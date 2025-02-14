package com.teamtea.eclipticseasons.common.core.solar;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.WetterStructure;
import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.SolarTermsMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

import java.lang.ref.WeakReference;
import java.util.*;


public class SolarDataManager extends SavedData {

    protected int solarTermsDay = (CommonConfig.Season.initialSolarTermIndex.get() - 1) * CommonConfig.Season.lastingDaysOfEachTerm.get();
    protected int solarTermsTicks = 0;
    private int biomeDataVersion = 0;

    protected WeakReference<Level> levelWeakReference;

    private final Map<ChunkPos, List<Pair<BlockPos, WetterStructure>>> serverLevelMapMap;

    public SolarDataManager(Level level) {
        levelWeakReference = new WeakReference<>(level);
        serverLevelMapMap = new HashMap<>();
    }

    public SolarDataManager(Level level, CompoundTag nbt) {
        this(level);
        setSolarTermsDay(nbt.getInt("SolarTermsDay"));
        setSolarTermsTicks(nbt.getInt("SolarTermsTicks"));
        this.biomeDataVersion = nbt.getInt("BiomeDataVersion");
        setLevelData(nbt);
    }

    protected void setLevelData(CompoundTag nbt) {
        if (levelWeakReference.get() != null) {
            var listTag = nbt.getList("biomes", Tag.TAG_COMPOUND);
            var biomeWeathers = WeatherManager.getBiomeList(levelWeakReference.get());
            int countCheck = 0;
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag compound = listTag.getCompound(i);
                var location = compound.getString("biome");
                for (int j = 0; j < biomeWeathers.size(); j++) {
                    WeatherManager.BiomeWeather biomeWeather = biomeWeathers.get(j);
                    if (location.equals(biomeWeather.location.toString())) {
                        biomeWeather.deserializeNBT(compound);
                        // 这里必须要id相等，不然缓存全部失效
                        if (i == j) {
                            countCheck++;
                        }
                        break;
                    }
                }
            }
            // TODO：如果存在未命中，说明更新了，检查是否真的有效
            if (countCheck != listTag.size()) {
                this.biomeDataVersion++;
                EclipticSeasons.logger("Warning for biome date need to be update with", listTag.size(), biomeWeathers.size(), " new version is", biomeDataVersion);
            }
        }
    }


    public static SolarDataManager get(ServerLevel serverLevel) {
        DimensionDataStorage storage = serverLevel.getDataStorage();
        return storage.computeIfAbsent(
                new Factory<>(() -> create(serverLevel),
                        ((compoundTag, provider) -> load(serverLevel, compoundTag, provider))),
                EclipticSeasonsApi.MODID);
    }

    private static SolarDataManager load(ServerLevel serverLevel, CompoundTag compoundTag, HolderLookup.Provider provider) {
        return new SolarDataManager(serverLevel, compoundTag);
    }

    private static SolarDataManager create(ServerLevel serverLevel) {
        return new SolarDataManager(serverLevel);
    }


    public void updateTicks(ServerLevel world) {
        solarTermsTicks++;
        int dayTime = Math.toIntExact(world.getDayTime() % 24000);
        if (solarTermsTicks > dayTime + 100) {
            setSolarTermsDay((getSolarTermsDay() + 1));
            sendAndUpdate(world);
        }
        solarTermsTicks = dayTime;

        setDirty();
    }

    public int getSolarTermIndex() {
        return (getSolarTermsDay() / CommonConfig.Season.lastingDaysOfEachTerm.get()) % 24;
    }

    public SolarTerm getSolarTerm() {
        return SolarTerm.get(this.getSolarTermIndex());
    }

    public int getSolarTermsDay() {
        return solarTermsDay;
    }

    public int getSolarTermsTicks() {
        return solarTermsTicks;
    }

    public int getBiomeDataVersion() {
        return biomeDataVersion;
    }

    public void setSolarTermsDay(int solarTermsDay) {
        // this.solarTermsDay = Math.max(solarTermsDay, 0) % (24 * CommonConfig.Season.lastingDaysOfEachTerm.get());
        this.solarTermsDay = solarTermsDay;
        setDirty();
    }

    public void setSolarTermsTicks(int solarTermsTicks) {
        this.solarTermsTicks = solarTermsTicks;
        setDirty();
    }

    public void addMap(BlockPos pos, WetterStructure state) {
        ChunkPos chunkPos = new ChunkPos(pos);
        List<Pair<BlockPos, WetterStructure>> blockPosBlockStateMap = this.serverLevelMapMap.get(chunkPos);
        if (blockPosBlockStateMap == null) {
            blockPosBlockStateMap = new ArrayList<>();
            this.serverLevelMapMap.put(chunkPos, blockPosBlockStateMap);
        }

        for (int i = 0; i < blockPosBlockStateMap.size(); i++) {
            Pair<BlockPos, WetterStructure> p = blockPosBlockStateMap.get(i);
            if (p.first().equals(pos)) {
                if (p.second() != state) {
                    blockPosBlockStateMap.set(i, Pair.of(pos, state));
                }
                return;
            }
        }
        blockPosBlockStateMap.add(Pair.of(pos, state));
    }

    public void unloadChunk(ChunkPos chunkPos) {
        this.serverLevelMapMap.remove(chunkPos);
    }

    public WetterStructure findNearPos(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        Vec3 center = blockPos.getCenter();

        int localX = blockPos.getX() & 15;
        int localZ = blockPos.getZ() & 15;

        boolean isLeftBorder = localX <= 3;
        boolean isRightBorder = localX >= 12;
        boolean isFrontBorder = localZ <= 3;
        boolean isBackBorder = localZ >= 12;

        for (int dx = isLeftBorder ? -1 : 0; dx <= (isRightBorder ? 1 : 0); dx++) {
            for (int dz = isFrontBorder ? -1 : 0; dz <= (isBackBorder ? 1 : 0); dz++) {
                ChunkPos currentChunkPos = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                List<Pair<BlockPos, WetterStructure>> lis = this.serverLevelMapMap.getOrDefault(currentChunkPos, null);

                if (lis != null) {
                    for (Pair<BlockPos, WetterStructure> p : lis) {
                        if (p.first().getY() < blockPos.getY()
                                && p.first().getCenter().distanceToSqr(center) < (p.second().range() * p.second().range() + 0.1)) {
                            return p.second();
                        }
                    }
                }
            }
        }

        return null;
    }

    public void randomClearSome(ChunkPos pos, RandomSource randomSource) {
        if (this.serverLevelMapMap.isEmpty()) return;
        List<Pair<BlockPos, WetterStructure>> list = this.serverLevelMapMap.get(pos);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (randomSource.nextInt(5) == 0) {
                    list.remove(i);
                    i--;
                }
            }
            if (list.isEmpty()) this.serverLevelMapMap.remove(pos);
        }
    }

    public void sendAndUpdate(ServerLevel world) {
        boolean changeSolarTerm = getSolarTermsDay() % CommonConfig.Season.lastingDaysOfEachTerm.get() == 0;

        if (changeSolarTerm) {
            BiomeClimateManager.updateTemperature(world, getSolarTerm());
            SolarTerm old = SolarTerm.collectValues()[(getSolarTermIndex() + 24) % 24];
            NeoForge.EVENT_BUS.post(new SolarTermChangeEvent(old, getSolarTerm(), world, solarTermsDay));
        }

        for (ServerPlayer player : world.players()) {
            SimpleNetworkHandler.send(player, new SolarTermsMessage(this.getSolarTermsDay()));
            if (changeSolarTerm && CommonConfig.Season.enableInform.get()) {
                player.sendSystemMessage(SimpleUtil.getSolarTermMessage(getSolarTerm()), false);
            }
            WeatherManager.tickPlayerForSeasonCheck(player);
        }
    }


    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider pRegistries) {
        compound.putInt("SolarTermsDay", getSolarTermsDay());
        compound.putInt("SolarTermsTicks", getSolarTermsTicks());
        ListTag listTag = new ListTag();
        if (levelWeakReference.get() != null) {
            var list = WeatherManager.getBiomeList(levelWeakReference.get());
            for (WeatherManager.BiomeWeather biomeWeather : list) {
                listTag.add(biomeWeather.serializeNBT());
            }
        }
        compound.put("biomes", listTag);
        compound.putInt("BiomeDataVersion", biomeDataVersion);

        // TODO：这里为将来写缓存做准备
        if (false) {
            CompoundTag test = new CompoundTag();
            long a = System.currentTimeMillis();
            if (levelWeakReference.get() instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().chunkMap.getChunks().forEach(chunkHolder ->
                {
                    ChunkAccess latestChunk = chunkHolder.getLatestChunk();
                    if (latestChunk != null) {
                        ChunkPos chunkPos = latestChunk.getPos();
                        String vs = chunkPos.toString();
                        CompoundTag chunk = new CompoundTag();
                        int[] biomes = new int[256];
                        Object2IntArrayMap<ResourceLocation> platte = new Object2IntArrayMap<>();
                        int idn = 0;
                        for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); i++) {
                            for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); j++) {
                                int y = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING,
                                        i, j);
                                Holder<Biome> biome = serverLevel.getBiome(new BlockPos(i, y, j));
                                ResourceLocation location = biome.getKey().location();
                                if (!platte.containsKey(location)) {
                                    platte.put(location, idn);
                                    idn++;
                                }
                                int id = platte.getInt(location);
                                biomes[(i & 15) * 16 + (j & 15)] = id;
                            }
                        }
                        ListTag platteTag = new ListTag();

                        for (ResourceLocation resourceLocation : platte.object2IntEntrySet().stream().map(
                                Map.Entry::getKey
                        ).toList()) {
                            CompoundTag ss = new CompoundTag();
                            ss.putString("id", resourceLocation.toString());
                            platteTag.add(ss);
                        }
                        chunk.putIntArray("matrix", biomes);
                        chunk.put("platte", platteTag);
                        test.put(vs, chunk);
                    }
                });
            }
            compound.put("test", test);
            long c = System.currentTimeMillis();
            EclipticSeasons.logger("Test Biome data backup", c - a);
        }
        return compound;
    }


}
