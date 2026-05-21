package com.teamtea.eclipticseasons.common.core.solar;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.season.SpecialDays;
import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.block.blockentity.GreenHouseCoreBlockEntity;
import com.teamtea.eclipticseasons.common.core.biome.BiomeRainDispatcher;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.GreenHouseCoreProvider;
import com.teamtea.eclipticseasons.common.core.crop.HumidityControlProvider;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.extra.SpecialDaysManager;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.SolarTermsMessage;
import com.teamtea.eclipticseasons.common.network.message.UpdateTempChangeMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;


public class SolarDataManager extends SavedData {

    protected final int startSolarTermsDay = (CommonConfig.Season.initialSolarTermIndex.get() - 1) * CommonConfig.Season.lastingDaysOfEachTerm.get();
    protected int solarTermsDay = startSolarTermsDay;
    protected int solarTermsTicks = 0;
    private int biomeDataVersion = 0;
    protected boolean isValidDimension = false;
    protected float solarTempChange = 0;

    protected WeakReference<Level> levelWeakReference;
    protected List<Holder<SpecialDays>> CACHE;
    protected long lastSpecialDaysQueryTime;

    // Long2ObjectOpenHashMap<List<T>>
    private final Long2ObjectOpenHashMap<List<Pair<BlockPos, HumidityControlProvider>>> humidityCoreMap;
    private final Long2ObjectOpenHashMap<List<Pair<BlockPos, GreenHouseCoreProvider>>> greenHouseCoreMap;
    private final Long2LongOpenHashMap needTickMap;
    private final Long2ObjectOpenHashMap<BlockState> skipNextCheckInTickPosMap;

    public SolarDataManager(Level level) {
        levelWeakReference = new WeakReference<>(level);
        humidityCoreMap = new Long2ObjectOpenHashMap<>();
        greenHouseCoreMap = new Long2ObjectOpenHashMap<>();
        needTickMap = new Long2LongOpenHashMap();
        isValidDimension = MapChecker.isValidDimension(level);
        skipNextCheckInTickPosMap = new Long2ObjectOpenHashMap<>();
        solarTempChange = createTempChange(level);
    }

    protected float createTempChange(Level level) {
        return isValidDimension() ?
                (float) Mth.clamp((level.getRandom().nextGaussian() * (0.25f / 2f)), -0.25f, 0.25f) : 0;
    }

    public SolarDataManager(Level level, CompoundTag nbt) {
        this(level);
        setSolarTermsDay(nbt.getInt("SolarTermsDay"));
        setSolarTermsTicks(nbt.getInt("SolarTermsTicks"));
        if (nbt.contains("SolarTempChange")) {
            setSolarTempChange(nbt.getFloat("SolarTempChange"));
        }
        this.biomeDataVersion = nbt.getInt("BiomeDataVersion");
        setLevelData(nbt);
    }

    protected void setLevelData(CompoundTag nbt) {
        Level level = levelWeakReference.get();
        if (level != null) {
            var listTag = nbt.getList("biomes", Tag.TAG_COMPOUND);
            var biomeWeathers = WeatherManager.getBiomeList(level);
            int countCheck = 0;
            long hash = nbt.getLong("BiomeRainHashRecord");
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag compound = listTag.getCompound(i);
                var location = compound.getString("biome");
                if (biomeWeathers != null) {
                    for (int j = 0; j < biomeWeathers.size(); j++) {
                        WeatherManager.BiomeWeather biomeWeather = biomeWeathers.get(j);
                        if (location.equals(biomeWeather.location.toString())) {
                            biomeWeather.deserializeNBT(compound, level.registryAccess(), hash);
                            // 这里必须要id相等，不然缓存全部失效
                            if (i == j) {
                                countCheck++;
                            }
                            break;
                        }
                    }
                }
            }
            if (countCheck != listTag.size()) {
                updateBiomeVersion();
                EclipticSeasons.logger("Warning for biome date need to be update with", listTag.size(), biomeWeathers == null ? 0 : biomeWeathers.size(), " new version is", biomeDataVersion);
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
        return CommonConfig.Season.realWorldSolarTerms.get() ?
                new FixedSolarDataManagerLocal(serverLevel, compoundTag) : new SolarDataManager(serverLevel, compoundTag);
    }

    private static SolarDataManager create(ServerLevel serverLevel) {
        SolarDataManager manager = CommonConfig.Season.realWorldSolarTerms.get() ?
                new FixedSolarDataManagerLocal(serverLevel) : new SolarDataManager(serverLevel);
        WeatherManager.initNewWorldWeather(serverLevel, serverLevel.random, manager.getSolarTerm());
        return manager;
    }


    public void updateTicks(ServerLevel level) {
        solarTermsTicks++;
        int dayTime = Math.toIntExact(level.getDayTime() % EclipticUtil.getDayLengthInMinecraft(level));
        if (solarTermsTicks > dayTime + 100) {
            setSolarTermsDay((getSolarTermsDay() + 1));
            sendAndUpdate(level);
        }
        solarTermsTicks = dayTime;

        setDirty();
    }

    public boolean isValidDimension() {
        return this.isValidDimension;
    }

    public int getSolarTermIndex() {
        if (!isValidDimension()) return SolarTerm.NONE.ordinal();
        return ((getSolarTermsDay() / CommonConfig.Season.lastingDaysOfEachTerm.get()) % 24 + 24) % 24;
    }

    public SolarTerm getSolarTerm() {
        return SolarTerm.get(this.getSolarTermIndex());
    }

    public SolarTerm getNextSolarTerm() {
        if (!isValidDimension()) return SolarTerm.NONE;
        return SolarTerm.get((this.getSolarTermIndex() + 1) % 24);
    }

    public int getSolarTermDaysInPeriod() {
        return Math.abs(getSolarTermsDay() % getSolarTermLastingDays());
    }

    public int getSolarTermLastingDays() {
        return CommonConfig.Season.lastingDaysOfEachTerm.get();
    }

    public boolean isTodayLastDay() {
        int longTime = getSolarTermLastingDays();
        return (getSolarTermsDay() + 1) % longTime == 0;
    }

    public int getSolarYear() {
        return !isValidDimension() ? 0 :
                ((getSolarTermsDay() - 0) / (24 * getSolarTermLastingDays())) + 1;
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

    public float getSolarTempChange() {
        return solarTempChange;
    }

    public void setSolarTermsDay(int solarTermsDay) {
        // this.solarTermsDay = Math.maxTime(solarTermsDay, 0) % (24 * CommonConfig.Season.lastingDaysOfEachTerm.get());
        this.solarTermsDay = solarTermsDay;
        setDirty();
    }

    public void setSolarTermsTicks(int solarTermsTicks) {
        this.solarTermsTicks = solarTermsTicks;
        setDirty();
    }

    public void setSolarTempChange(float solarTempChange) {
        this.solarTempChange = solarTempChange;
        setDirty();
    }

    public List<Holder<SpecialDays>> getSpecialDays(Level level, BlockPos pos) {
        long gameTime = level.getGameTime();
        if (gameTime - lastSpecialDaysQueryTime > 40) {
            CACHE = SpecialDaysManager.getSpecialDays(level, pos);
            lastSpecialDaysQueryTime = gameTime;
        }
        return CACHE == null ? List.of() : CACHE;
    }

    public void updateBiomeVersion() {
        this.biomeDataVersion++;
    }

    public void addHumidityControlProvider(BlockPos pos, HumidityControlProvider humidityControlProvider) {
        ChunkPos chunkPos = new ChunkPos(pos);
        List<Pair<BlockPos, HumidityControlProvider>> blockPosBlockStateMap = this.humidityCoreMap.get(chunkPos.toLong());
        if (blockPosBlockStateMap == null) {
            blockPosBlockStateMap = new ArrayList<>();
            this.humidityCoreMap.put(chunkPos.toLong(), blockPosBlockStateMap);
        }

        for (int i = 0; i < blockPosBlockStateMap.size(); i++) {
            Pair<BlockPos, HumidityControlProvider> p = blockPosBlockStateMap.get(i);
            if (p.first().equals(pos)) {
                if (p.second() != humidityControlProvider) {
                    blockPosBlockStateMap.set(i, Pair.of(pos, humidityControlProvider));
                }
                return;
            }
        }
        blockPosBlockStateMap.add(Pair.of(pos, humidityControlProvider));
    }


    public void addGreenHouseCoreProvider(BlockPos pos, GreenHouseCoreProvider provider) {
        addGreenHouseCoreProvider(pos, provider, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
    }

    @ApiStatus.Experimental
    private void addGreenHouseCoreProvider(BlockPos pos, GreenHouseCoreProvider provider, int chunkX, int chunkZ) {
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        List<Pair<BlockPos, GreenHouseCoreProvider>> blockPosBlockStateMap = this.greenHouseCoreMap.get(chunkPos.toLong());
        if (blockPosBlockStateMap == null) {
            blockPosBlockStateMap = new ArrayList<>();
            this.greenHouseCoreMap.put(chunkPos.toLong(), blockPosBlockStateMap);
        }

        for (int i = 0; i < blockPosBlockStateMap.size(); i++) {
            Pair<BlockPos, GreenHouseCoreProvider> p = blockPosBlockStateMap.get(i);
            if (p.first().equals(pos)) {
                if (p.second().getSeason() == provider.getSeason()) {
                    p.second().addAvailCost(provider.getAvailCost());
                }
                return;
            }
        }
        blockPosBlockStateMap.add(Pair.of(pos, provider));
    }

    public void unloadChunk(ChunkPos chunkPos) {
        this.humidityCoreMap.remove(chunkPos.toLong());
        this.greenHouseCoreMap.remove(chunkPos.toLong());
        this.needTickMap.remove(chunkPos.toLong());
    }

    public HumidityControlProvider queryHumidityControlProvider(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        List<Pair<BlockPos, HumidityControlProvider>> lis = this.humidityCoreMap.getOrDefault(chunkPos.toLong(), null);
        if (lis != null) {
            for (int i = 0, lisSize = lis.size(); i < lisSize; i++) {
                Pair<BlockPos, HumidityControlProvider> p = lis.get(i);
                if (p.first().equals(blockPos)) {
                    return p.second();
                }
            }
        }
        return null;
    }

    public HumidityControlProvider removeHumidityControlProvider(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        List<Pair<BlockPos, HumidityControlProvider>> lis = this.humidityCoreMap.getOrDefault(chunkPos.toLong(), null);
        if (lis != null) {
            for (int i = 0, lisSize = lis.size(); i < lisSize; i++) {
                Pair<BlockPos, HumidityControlProvider> p = lis.get(i);
                if (p.first().equals(blockPos)) {
                    lis.remove(i);
                    if (lis.isEmpty()) {
                        greenHouseCoreMap.remove(chunkPos.toLong());
                    }
                    return p.second();
                }
            }
        }
        return null;
    }

    public float calculateHumidityModification(BlockPos blockPos) {
        return calculateHumidityModification(blockPos, true);
    }

    public float calculateHumidityModification(BlockPos blockPos, boolean growPlus) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        // if (growPlus) {
        //     needTickMap.put(chunkPos.toLong(), levelWeakReference.get() != null ?
        //             levelWeakReference.get().getGameTime() : 0);
        // }

        Vec3 center = blockPos.getCenter();

        int localX = blockPos.getX() & 15;
        int localZ = blockPos.getZ() & 15;

        boolean isLeftBorder = localX <= 6;
        boolean isRightBorder = localX >= 7;
        boolean isFrontBorder = localZ <= 6;
        boolean isBackBorder = localZ >= 7;

        float result = 0f;
        for (int dx = isLeftBorder ? -1 : 0; dx <= (isRightBorder ? 1 : 0); dx++) {
            for (int dz = isFrontBorder ? -1 : 0; dz <= (isBackBorder ? 1 : 0); dz++) {
                ChunkPos currentChunkPos = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                List<Pair<BlockPos, HumidityControlProvider>> lis = this.humidityCoreMap.getOrDefault(currentChunkPos.toLong(), null);

                if (lis != null) {
                    for (Pair<BlockPos, HumidityControlProvider> p : lis) {
                        if (
                            // p.first().getY() > blockPos.getY()
                            // &&
                                CropGrowthHandler.isWithinDistanceForGreenHouseWorker(center, p.first().getCenter(), p.second().getRange())
                            // p.first().getCenter().distanceToSqr(center) < (p.second().getRange() + 0.1)
                        ) {
                            result += p.second().getLevel();
                        }
                    }
                }
            }
        }

        return result;
    }

    public GreenHouseCoreProvider queryGreenHouseProvider(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        List<Pair<BlockPos, GreenHouseCoreProvider>> lis = this.greenHouseCoreMap.getOrDefault(chunkPos.toLong(), null);
        if (lis != null) {
            for (int i = 0, lisSize = lis.size(); i < lisSize; i++) {
                Pair<BlockPos, GreenHouseCoreProvider> p = lis.get(i);
                if (p.first().equals(blockPos)) {
                    return p.second();
                }
            }
        }
        return null;
    }

    public GreenHouseCoreProvider removeGreenHouseProvider(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        List<Pair<BlockPos, GreenHouseCoreProvider>> lis = this.greenHouseCoreMap.getOrDefault(chunkPos.toLong(), null);
        if (lis != null) {
            for (int i = 0, lisSize = lis.size(); i < lisSize; i++) {
                Pair<BlockPos, GreenHouseCoreProvider> p = lis.get(i);
                if (p.first().equals(blockPos)) {
                    lis.remove(i);
                    if (lis.isEmpty()) {
                        greenHouseCoreMap.remove(chunkPos.toLong());
                    }
                    return p.second();
                }
            }
        }
        return null;
    }

    public GreenHouseCoreProvider findNearGreenHouseProvider(BlockPos blockPos, List<Season> seasons) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        Vec3 center = blockPos.getCenter();
        int d = CommonConfig.Crop.seasonCoreRange.get() / 16 + 1;

        for (int r = 0; r <= d; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx == -r || dx == r || dz == -r || dz == r) {
                        ChunkPos currentChunkPos = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                        GreenHouseCoreProvider greenHouseCoreProvider = checkSeasonProviderInChunk(seasons, currentChunkPos, center);
                        if (greenHouseCoreProvider instanceof GreenHouseCoreBlockEntity.Consumer consumer) {
                            consumer.addEnergy(1);
                            return null;
                        }
                        if (greenHouseCoreProvider != null) return greenHouseCoreProvider;
                    }
                }
            }
        }

        return null;
    }


    public GreenHouseCoreProvider findNearGreenHouseConsumer(BlockPos blockPos, List<Season> seasons) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        Vec3 center = blockPos.getCenter();
        int d = CommonConfig.Crop.seasonCoreRange.get() / 16 + 1;

        for (int r = 0; r <= d; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx == -r || dx == r || dz == -r || dz == r) {
                        ChunkPos currentChunkPos = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                        GreenHouseCoreProvider greenHouseCoreProvider = checkSeasonProviderInChunk(seasons, currentChunkPos, center);
                        if (greenHouseCoreProvider instanceof GreenHouseCoreBlockEntity.Consumer consumer) {
                            return consumer;
                        }
                        if (greenHouseCoreProvider != null) return null;
                    }
                }
            }
        }
        return null;
    }

    protected GreenHouseCoreProvider checkSeasonProviderInChunk(List<Season> seasons, ChunkPos currentChunkPos, Vec3 center) {
        GreenHouseCoreProvider greenHouseCoreProvider = null;
        List<Pair<BlockPos, GreenHouseCoreProvider>> lis = this.greenHouseCoreMap.getOrDefault(currentChunkPos.toLong(), null);
        if (lis != null) {
            int seasonCoreRange = CommonConfig.Crop.seasonCoreRange.get();
            for (Pair<BlockPos, GreenHouseCoreProvider> p : lis) {
                if (seasons.contains(p.second().getSeason()) &&
                        CropGrowthHandler.isWithinDistanceForGreenHouseWorker(center, p.first().getCenter(), seasonCoreRange)) {
                    greenHouseCoreProvider = p.second();
                    break;
                }
            }
        }
        return greenHouseCoreProvider;
    }

    public void tickChunk(LevelChunk chunk) {
        // if (!this.needTickMap.isEmpty()) {
        //     long longPos = chunk.getPos().toLong();
        //     long startTime = needTickMap.get(longPos);
        //     if (startTime > 1)
        //         needTickMap.put(longPos, startTime - 1);
        //     else needTickMap.remove(longPos);
        // }
        if (this.humidityCoreMap.isEmpty()) return;
        ChunkPos pos = chunk.getPos();
        List<Pair<BlockPos, HumidityControlProvider>> list = this.humidityCoreMap.get(pos.toLong());
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Pair<BlockPos, HumidityControlProvider> pair = list.get(i);
                if (pair.second().getRemainTime() <= 0) {
                    list.remove(i);
                    i--;
                } else {
                    pair.second().addRemainTime(-1);
                }
            }
            if (list.isEmpty()) this.humidityCoreMap.remove(pos.toLong());
        }
    }

    public void sendAndUpdate(ServerLevel level) {
        boolean changeSolarTerm = getSolarTermsDay() % CommonConfig.Season.lastingDaysOfEachTerm.get() == 0;
        boolean updateTempChange = false;
        SolarTerm solarTerm = getSolarTerm();
        if (changeSolarTerm) {
            // note 不再需要更新
            // BiomeClimateManager.updateTemperature(level, getSolarTerm());
            SolarTerm old = SolarTerm.collectValues()[(getSolarTermIndex() + 24) % 24];
            NeoForge.EVENT_BUS.post(new SolarTermChangeEvent(old, solarTerm, level, solarTermsDay));
            if (solarTerm == SolarTerm.SUMMER_SOLSTICE) {
                setSolarTempChange(createTempChange(level));
                updateTempChange = true;
            }
        }

        if (solarTerm != SolarTerm.NONE) {
            for (ServerPlayer player : level.players()) {
                SimpleNetworkHandler.send(player, new SolarTermsMessage(this.getSolarTermsDay()));
                if (changeSolarTerm && CommonConfig.Season.enableInform.get()) {
                    SimpleUtil.sendSolarTermMessage(player, solarTerm, false);
                }
                if (updateTempChange) {
                    SimpleNetworkHandler.send(player, new UpdateTempChangeMessage(getSolarTempChange()));
                }
                if (changeSolarTerm) {
                    WeatherManager.tickPlayerForSeasonCheck(player, solarTerm);
                }
            }
        }
    }


    @Override
    public @NotNull CompoundTag save(CompoundTag compound, HolderLookup.@NotNull Provider pRegistries) {
        compound.putInt("SolarTermsDay", getSolarTermsDay());
        compound.putInt("SolarTermsTicks", getSolarTermsTicks());
        compound.putFloat("SolarTempChange", getSolarTempChange());
        ListTag listTag = new ListTag();
        if (levelWeakReference.get() != null) {
            var list = WeatherManager.getBiomeList(levelWeakReference.get());
            if (list != null) {
                for (WeatherManager.BiomeWeather biomeWeather : list) {
                    listTag.add(biomeWeather.serializeNBT());
                }
            }
        }
        compound.put("biomes", listTag);
        compound.putInt("BiomeDataVersion", biomeDataVersion);
        compound.putLong("BiomeRainHashRecord", BiomeRainDispatcher.hash_cache);
        return compound;
    }


    public boolean shouldTickChunk(ChunkPos chunkPos) {
        return this.needTickMap.containsKey(chunkPos.toLong());
    }


    public void tickLevel(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            this.skipNextCheckInTickPosMap.clear();
            if (MapChecker.isValidDimension(serverLevel)) {
                this.updateTicks(serverLevel);
            }
        }
    }

    public BlockState addSkipNextCheck(BlockPos blockPos, BlockState blockState) {
        return this.skipNextCheckInTickPosMap.put(blockPos.asLong(), blockState);
    }

    public boolean shouldSkipNextCheck(BlockPos blockPos) {
        return this.skipNextCheckInTickPosMap.containsKey(blockPos.asLong());
    }

    public static final String KEY_ATTACH_SOLAR_DATA = EclipticSeasons.rl("attach_solar_data").toString();

    public void saveChunk(ChunkPos pos, CompoundTag data) {
        Level level = levelWeakReference.get();
        if (level == null) return;
        if (!CommonConfig.Crop.saveChunkEnvironmentalHumidity.get()) return;

        List<Pair<BlockPos, HumidityControlProvider>> list = this.humidityCoreMap.getOrDefault(pos.toLong(), null);
        if (list != null) {
            ListTag compoundTag = new ListTag();
            for (Pair<BlockPos, HumidityControlProvider> pair : list) {
                if (!pair.right().shouldSave()) continue;
                CompoundTag ct = new CompoundTag();
                ct.putLong("pos", pair.left().asLong());
                ct.put("humidity_modifiers", pair.right().serializeNBT(level.registryAccess()));
                compoundTag.add(ct);
            }
            if (!compoundTag.isEmpty())
                data.put(KEY_ATTACH_SOLAR_DATA, compoundTag);
        }
    }

    public void loadChunk(ChunkPos pos, CompoundTag data) {
        Level level = levelWeakReference.get();
        if (level == null) return;
        if (!data.contains(KEY_ATTACH_SOLAR_DATA)) return;
        ListTag attachSolarData = data.getList(KEY_ATTACH_SOLAR_DATA, Tag.TAG_COMPOUND);
        for (Tag attachSolarDatum : attachSolarData) {
            CompoundTag compoundTag = (CompoundTag) attachSolarDatum;
            long aLong = compoundTag.getLong("pos");
            BlockPos blockPos = BlockPos.of(aLong);
            CompoundTag humidity_modifier = compoundTag.getCompound("humidity_modifiers");
            if (humidity_modifier.isEmpty()) continue;
            HumidityControlProvider humidityControlProvider = new HumidityControlProvider(0, 0, 0, true);
            humidityControlProvider.deserializeNBT(level.registryAccess(), humidity_modifier);
            addHumidityControlProvider(blockPos, humidityControlProvider);
        }
    }
}
