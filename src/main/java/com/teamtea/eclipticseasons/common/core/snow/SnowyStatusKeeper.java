package com.teamtea.eclipticseasons.common.core.snow;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import lombok.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 一个可能的设计是我们需要记录所有可能下雪的位置，至少是ymax和ymin。
 * 那么此时我们就需要追踪所有的方块变化，即LevelChunk的setState变化
 * 同时统计群系与百分比的关系
 * <p>
 * 注意方块变化也应该对覆雪状态产生影响，例如，摧毁应该remove，上方放置了方块导致固体高度更新也应该remove
 * 其余情况可以忽略
 * <p>
 * 目前一个问题在tick可能会忽略底下的覆雪方块，我们也可以将其作为特性，意为不受到阳光直射，因此保持特性。
 **/

@Data
public class SnowyStatusKeeper implements Cloneable {

    public static final int FLAG_NONE = 0;
    public static final int FLAG_SNOW = 1;
    public static final int FLAG_NOT_RECORD = -1;

    public static final MapCodec<SnowyStatusKeeper> MAP_CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.LONG.listOf()
                    .fieldOf("map")
                    .forGetter(o -> new ArrayList<>(o.posMap.keySet())),
            Codec.BYTE.listOf()
                    .optionalFieldOf("status", List.of())
                    .forGetter(o -> o.posMap.values().stream().map(Integer::byteValue).toList())
    ).apply(ins, SnowyStatusKeeper::new));

    public static final Codec<SnowyStatusKeeper> CODEC = MAP_CODEC.codec();

    protected SnowyStatusKeeper() {
        this(List.of(), List.of());
    }

    public static SnowyStatusKeeper create() {
        return new SnowyStatusKeeper();
    }

    public SnowyStatusKeeper(List<Long> posList,
                             List<Byte> status) {
        posMap.defaultReturnValue(FLAG_NOT_RECORD);
        if (posList.size() == status.size() || status.isEmpty()) {
            for (int i = 0, pairsSize = posList.size(); i < pairsSize; i++) {
                var pair = posList.get(i);
                this.posMap.put(pair, status.isEmpty() ? FLAG_SNOW : status.get(i));
            }
        }

        stepCount.defaultReturnValue(-1);
    }

    private final Long2IntLinkedOpenHashMap posMap = new Long2IntLinkedOpenHashMap();
    private final Long2IntLinkedOpenHashMap stepCount = new Long2IntLinkedOpenHashMap();

    // private final Short2ObjectLinkedOpenHashMap<IntArrayList> xzPosList = new Short2ObjectLinkedOpenHashMap<>();

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

    public void updateAndSend(ServerLevel serverLevel, LevelChunk chunk) {
        if (EclipticUtil.canSnowyBlockInteract()) {
            if (change) {
                chunk.markUnsaved();
                ;
                if (!posListUpdate.isEmpty()) {
                    chunk.syncData(AttachmentRegistry.SNOWY_STATUS_KEEPER);
                    posListUpdate.clear();
                    statusListUpdate.clear();
                }
            }
        }
        change = false;
    }

    protected void setChange() {
        change = true;
        cacheTag = null;
    }

    public void tickChunk(ServerLevel level, LevelChunk chunk, ChunkPos chunkPos, BlockPos checkPos, ChunkInfoMap chunkInfoMap,
                          WeatherStatusKeeper weatherStatusKeeper) {
        if (!EclipticUtil.canSnowyBlockInteract()) return;
        int height = chunkInfoMap.getHeight(checkPos);
        int surfaceHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, checkPos.getX(), checkPos.getZ());
        checkPos = new BlockPos(checkPos.getX(), height < surfaceHeight ?
                (level.getRandom().nextIntBetweenInclusive(0, surfaceHeight - height) + height) : height
                , checkPos.getZ());

        if (height <= checkPos.getY()) {
            Holder<Biome> biome = MapChecker.getSurfaceBiomeByChunk(level, chunk, checkPos);
            weatherStatusKeeper.getBiomeUse().add(biome);

            BlockState state = chunk.getBlockState(checkPos);
            int flag = MapChecker.getDefaultBlockTypeFlag(state);
            WeatherManager.SnowRenderStatus snowStatus =
                    flag == MapChecker.FLAG_NONE ?
                            WeatherManager.SnowRenderStatus.SNOW_MELT :
                            WeatherManager.getSnowStatus(level, biome, checkPos, EclipticUtil.isRainingOrSnowingWithSurfaceBiome(level, biome, checkPos));

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
        int minBuildHeight = chunk.getMinY();
        int maxBuildHeight = chunk.getMaxY();

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    private transient CompoundTag cacheTag = null;

    public static class Serializer implements IAttachmentSerializer<SnowyStatusKeeper> {

        @Override
        public SnowyStatusKeeper read(IAttachmentHolder holder, ValueInput input) {
            if (!EclipticUtil.canSnowyBlockInteract()) return create();
            Optional<SnowyStatusKeeper> snowyStatus = input.read("snowy_status", CODEC);
            return snowyStatus.orElseGet(SnowyStatusKeeper::create);
        }

        @Override
        public boolean write(SnowyStatusKeeper attachment, ValueOutput output) {
            if (!EclipticUtil.canSnowyBlockInteract()) return false;
            output.storeNullable("snowy_status", CODEC, attachment);
            return false;
        }

        //
        //@Override
        //public Tag write(@NotNull SnowyStatusKeeper attachment, HolderLookup.@NotNull Provider provider) {
        //    if (!EclipticUtil.canSnowyBlockInteract()) new CompoundTag();
        //    if (attachment.cacheTag != null)
        //        return attachment.cacheTag;
        //    Optional<Tag> result = CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), attachment).result();
        //    if (result.orElse(null) instanceof CompoundTag compoundTag) {
        //        attachment.cacheTag = compoundTag;
        //        return compoundTag;
        //    }
        //    return new CompoundTag();
        //}


    }
}
