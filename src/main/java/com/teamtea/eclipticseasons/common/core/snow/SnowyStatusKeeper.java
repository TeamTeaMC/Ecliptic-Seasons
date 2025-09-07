package com.teamtea.eclipticseasons.common.core.snow;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;


@Data
public class SnowyStatusKeeper implements Cloneable, ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final int FLAG_NONE = 0;
    public static final int FLAG_SNOW = 1;
    public static final int FLAG_NOT_RECORD = -1;

    public static final Codec<SnowyStatusKeeper> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.LONG.listOf()
                    .fieldOf("map")
                    .forGetter(o -> new ArrayList<>(o.posMap.keySet())),
            Codec.BYTE.listOf()
                    .optionalFieldOf("status", List.of())
                    .forGetter(o -> o.posMap.values().stream().map(Integer::byteValue).toList()),
            CodecUtil.holderCodec(Registries.BIOME).listOf().optionalFieldOf("biomes", List.of())
                    .forGetter(o -> new ArrayList<>(o.snowDepthRecord.keySet())),
            Codec.INT.listOf().optionalFieldOf("snow_depth", List.of())
                    .forGetter(o -> new ArrayList<>(o.snowDepthRecord.values()))
    ).apply(ins, SnowyStatusKeeper::new));

    protected SnowyStatusKeeper() {
        this(List.of(), List.of(), List.of(), List.of());
    }

    public static SnowyStatusKeeper create() {
        return new SnowyStatusKeeper();
    }

    public SnowyStatusKeeper(List<Long> posList,
                             List<Byte> status,
                             List<Holder<Biome>> biomes,
                             List<Integer> snow_depth) {
        posMap.defaultReturnValue(FLAG_NOT_RECORD);
        if (posList.size() == status.size() || status.isEmpty()) {
            for (int i = 0, pairsSize = posList.size(); i < pairsSize; i++) {
                var pair = posList.get(i);
                this.posMap.put(pair, status.isEmpty() ? FLAG_SNOW : status.get(i));
            }
        }

        if (biomes.size() == snow_depth.size()) {
            for (int i = 0, biomesSize = biomes.size(); i < biomesSize; i++) {
                Holder<Biome> biome = biomes.get(i);
                snowDepthRecord.put(biome, snow_depth.get(i));
            }
        }
        stepCount.defaultReturnValue(-1);
    }

    private final Long2IntLinkedOpenHashMap posMap = new Long2IntLinkedOpenHashMap();
    private final Long2IntLinkedOpenHashMap stepCount = new Long2IntLinkedOpenHashMap();
    // private final Short2ObjectLinkedOpenHashMap<IntArrayList> xzPosList = new Short2ObjectLinkedOpenHashMap<>();
    private final Map<Holder<Biome>, Integer> snowDepthRecord = new Reference2ObjectLinkedOpenHashMap<>();

    private final Set<Holder<Biome>> biomeUse = new ReferenceLinkedOpenHashSet<>();
    private final LongArrayList posListUpdate = new LongArrayList();
    private final IntArrayList statusListUpdate = new IntArrayList();

    private boolean change = false;

    public boolean isSnowyBlock(BlockPos pos) {
        return getPosMap().containsKey(pos.asLong());
    }

    public void set(BlockPos pos, int flag) {
        long aLong = pos.asLong();
        if (set(aLong, flag)) {
            posListUpdate.add(aLong);
            statusListUpdate.add(flag);
            setChange();
            if (!stepCount.isEmpty() && flag == FLAG_NONE) stepCount.remove(aLong);
        }
    }

    public boolean set(long pos, int flag) {
        return switch (flag) {
            case FLAG_NONE -> this.posMap.remove(pos) != FLAG_NOT_RECORD;
            case FLAG_SNOW -> this.posMap.put(pos, flag) != flag;
            default -> false;
        };
    }

    public void stepAndCheck(BlockPos pos) {
        long aLong = pos.asLong();
        int result = !isSnowyBlock(pos) ? 0 : stepCount.addTo(aLong, 1);
        if (result > 8) {
            set(pos, SnowyStatusKeeper.FLAG_NONE);
        }
    }

    public void removeBiomeRecord(Holder<Biome> biomeHolder) {
        this.snowDepthRecord.remove(biomeHolder);
        setChange();
    }

    public void updateAndSend(ServerLevel serverLevel, LevelChunk chunk) {
        if (EclipticUtil.canSnowyBlockInteract()) {
            updateBiomeWhenEndTick(serverLevel);
            if (change) {
                chunk.setUnsaved(true);
                if (!posListUpdate.isEmpty()) {
                    SimpleNetworkHandler.send(serverLevel.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false), new SnowyStatusHandler(false, this, chunk.getPos()));
                    posListUpdate.clear();
                    statusListUpdate.clear();
                }
            }
        }
        change = false;
    }

    protected void updateBiomeWhenEndTick(ServerLevel serverLevel) {
        for (Holder<Biome> biomeHolder : biomeUse) {
            int snowDepthAtBiome = WeatherManager.getSnowDepthAtBiome(serverLevel, biomeHolder.value());
            Integer put = snowDepthRecord.put(biomeHolder, snowDepthAtBiome);
            if (put != null && put != snowDepthAtBiome) {
                setChange();
            }
        }
    }

    protected void setChange() {
        change = true;
    }

    public Map<Holder<Biome>, IntIntImmutablePair> collectSnowyUpdate(Level level, @Nullable BiomeHolder biomeHolder) {
        Map<Holder<Biome>, IntIntImmutablePair> biomeSnowyUpdate = new IdentityHashMap<>();
        if (EclipticUtil.canSnowyBlockInteract() && !level.isClientSide) {
            // a chunk never tick without any record
            if (snowDepthRecord.isEmpty()) {
                Set<Holder<Biome>> biomeDetect = new ReferenceLinkedOpenHashSet<>();
                if (biomeHolder != null) {
                    Registry<Biome> biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
                    IntOpenHashSet ids = new IntOpenHashSet(biomeHolder.biomes());
                    for (int i : ids.toIntArray()) {
                        biomeDetect.add(MapChecker.idToBiome(biomes, i));
                    }
                }
                var ws = WeatherManager.getBiomeList(level);
                if (ws != null) {
                    // level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes()
                    for (WeatherManager.BiomeWeather w : ws) {
                        if (w.snowDepth > 0 && (biomeDetect.isEmpty() || biomeDetect.contains(w.biomeHolder)))
                            biomeSnowyUpdate.put(w.biomeHolder, IntIntImmutablePair.of(w.snowDepth, 0));
                    }
                }
            } else {
                snowDepthRecord.forEach((biome, oldBiomeSnowDepth) -> {
                    int snowDepthAtBiome = WeatherManager.getSnowDepthAtBiome(level, biome.value());
                    int snowDepthIncrease = snowDepthAtBiome - oldBiomeSnowDepth;
                    int offsetAbs = Mth.abs(snowDepthIncrease);
                    if ((snowDepthAtBiome < 3 || snowDepthAtBiome > 97) && offsetAbs != 0) {
                        biomeSnowyUpdate.put(biome, IntIntImmutablePair.of(snowDepthAtBiome, 0));
                    } else if (offsetAbs > 20) {
                        biomeSnowyUpdate.put(biome, IntIntImmutablePair.of(snowDepthAtBiome, snowDepthIncrease));
                    }
                });
            }
        }
        return biomeSnowyUpdate;
    }

    public void updateSnowDepthRecord(Map<Holder<Biome>, IntIntImmutablePair> map) {
        map.forEach((biomeHolder, immutablePair) ->
                snowDepthRecord.put(biomeHolder, immutablePair.leftInt())
        );
        setChange();
    }

    public void tickChunk(ServerLevel level, LevelChunk chunk, ChunkPos chunkPos, BlockPos checkPos, ChunkInfoMap chunkInfoMap) {
        if (!EclipticUtil.canSnowyBlockInteract()) return;
        int height = chunkInfoMap.getHeight(checkPos);
        int surfaceHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, checkPos.getX(), checkPos.getZ());
        checkPos = new BlockPos(checkPos.getX(), height < surfaceHeight ?
                (level.getRandom().nextIntBetweenInclusive(0, surfaceHeight - height) + height) : height
                , checkPos.getZ());

        if (height <= checkPos.getY()) {
            Holder<Biome> biome = MapChecker.getSurfaceBiomeByChunk(level, chunk, checkPos);
            biomeUse.add(biome);

            BlockState state = chunk.getBlockState(checkPos);
            int flag = MapChecker.getBlockTypeFlag(level, checkPos, state);
            WeatherManager.SnowRenderStatus snowStatus =
                    flag == MapChecker.FLAG_NONE ?
                            WeatherManager.SnowRenderStatus.SNOW_MELT :
                            WeatherManager.getSnowStatus(level, biome.value(), checkPos, EclipticUtil.isRainingOrSnowingWithSurfaceBiome(level, biome.value(), checkPos));

            boolean forceMelt = false;
            if (snowStatus != WeatherManager.SnowRenderStatus.SNOW_MELT) {
                if (SnowyMapChecker.isTooLight(level, checkPos, state, flag)) {
                    snowStatus = WeatherManager.SnowRenderStatus.SNOW_MELT;
                    forceMelt = true;
                }
                if (!forceMelt && !MapChecker.notLightAbove(level, checkPos, 4)) {
                    snowStatus = WeatherManager.SnowRenderStatus.SNOW_MELT;
                    forceMelt = true;
                }
            }

            switch (snowStatus) {
                case SNOW -> {
                    if (WeatherManager.getSnowDepthAtBiome(level, biome.value()) > Math.abs(state.getSeed(checkPos) % 100))
                        set(checkPos, FLAG_SNOW);
                }
                case SNOW_MELT -> {
                    if (forceMelt || WeatherManager.getSnowDepthAtBiome(level, biome.value()) <= Math.abs(state.getSeed(checkPos) % 100)) {
                        if (!chunk.getBlockState(checkPos).is(BlockTags.SNOW))
                            set(checkPos, FLAG_NONE);
                    }
                }
            }
        }
    }


    public boolean checkPosValid(ChunkAccess chunk) {
        boolean anyIllgegalPos = false;
        ChunkPos chunkPos = chunk.getPos();
        int startX = chunkPos.getMinBlockX();
        int startZ = chunkPos.getMinBlockZ();
        int endX = chunkPos.getMaxBlockX();
        int endZ = chunkPos.getMaxBlockZ();
        int minBuildHeight = chunk.getMinBuildHeight();
        int maxBuildHeight = chunk.getMaxBuildHeight();

        LongIterator it = getPosMap().keySet().longIterator();
        while (it.hasNext()) {
            long key = it.nextLong();
            int x = BlockPos.getX(key);
            int z = BlockPos.getZ(key);
            int y = BlockPos.getY(key);
            boolean valid = x >= startX && x <= endX &&
                    z >= startZ && z <= endZ &&
                    y >= minBuildHeight && y < maxBuildHeight;
            if (!valid) {
                it.remove();
                anyIllgegalPos = true;
            }
        }
        if (anyIllgegalPos) setChange();
        return !anyIllgegalPos;
    }

    /**
     * Only use it when render copy.
     **/
    @Override
    public SnowyStatusKeeper clone() {
        SnowyStatusKeeper newKepper = SnowyStatusKeeper.create();
        newKepper.posMap.putAll(this.posMap);
        return newKepper;
    }

    public static final SnowyStatusKeeper EMPTY = SnowyStatusKeeper.create();

    // ===================================================================
    // 1.20.1 use


    public static final Capability<SnowyStatusKeeper> SNOWY_STATUS_KEEPER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });


    private LazyOptional<SnowyStatusKeeper> cast = null;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        if (cap == SNOWY_STATUS_KEEPER_CAPABILITY) {
            if (cast == null) cast = LazyOptional.of(() -> this);
            return cast.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        if (!EclipticUtil.canSnowyBlockInteract()) new CompoundTag();
        Level level = WeatherManager.fetchLevelIfNull(null);
        if (level != null) {
            RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
            Optional<Tag> result = CODEC.encodeStart(registryOps, this).result();
            if (result.orElse(null) instanceof CompoundTag compoundTag) {
                return compoundTag;
            }
        }
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (!EclipticUtil.canSnowyBlockInteract()) return;

        Level level = WeatherManager.fetchLevelIfNull(null);
        if (level != null) {
            RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
            Optional<SnowyStatusKeeper> result = CODEC.parse(registryOps, nbt).result();
            result.ifPresent(this::copyFrom);
        }
    }

    public void copyFrom(SnowyStatusKeeper keeper) {
        this.posMap.clear();
        this.snowDepthRecord.clear();
        this.posMap.putAll(keeper.posMap);
        this.snowDepthRecord.putAll(keeper.snowDepthRecord);
    }
}
