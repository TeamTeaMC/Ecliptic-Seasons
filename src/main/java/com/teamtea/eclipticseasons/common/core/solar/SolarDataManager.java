package com.teamtea.eclipticseasons.common.core.solar;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.GreenHouseCoreProvider;
import com.teamtea.eclipticseasons.common.core.crop.HumidityControlProvider;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.SolarTermsMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SolarDataManager extends SavedData {

    protected int solarTermsDay = (CommonConfig.Season.initialSolarTermIndex.get() - 1) * CommonConfig.Season.lastingDaysOfEachTerm.get();
    protected int solarTermsTicks = 0;
    protected boolean isValidDimension = false;

    protected WeakReference<Level> levelWeakReference;
    private final Map<ChunkPos, List<Pair<BlockPos, HumidityControlProvider>>> serverLevelMapMap;
    private final Map<ChunkPos, List<Pair<BlockPos, GreenHouseCoreProvider>>> serverLevelMapMap2;
    private final Long2ObjectOpenHashMap<BlockState> onceCheck;

    public SolarDataManager(Level level) {
        levelWeakReference = new WeakReference<>(level);
        serverLevelMapMap = new HashMap<>();
        serverLevelMapMap2 = new HashMap<>();
        isValidDimension = MapChecker.isValidDimension(level);
        onceCheck = new Long2ObjectOpenHashMap<>();
    }

    public SolarDataManager(Level level, CompoundTag nbt) {
        this(level);
        setSolarTermsDay(nbt.getInt("SolarTermsDay"));
        setSolarTermsTicks(nbt.getInt("SolarTermsTicks"));
        var listTag = nbt.getList("biomes", Tag.TAG_COMPOUND);
        if (levelWeakReference.get() != null) {
            var biomeWeathers = WeatherManager.getBiomeList(levelWeakReference.get());
            for (int i = 0; i < listTag.size(); i++) {
                var location = listTag.getCompound(i).getString("biome");
                for (WeatherManager.BiomeWeather biomeWeather : biomeWeathers) {
                    if (location.equals(biomeWeather.location.toString())) {
                        biomeWeather.deserializeNBT(listTag.getCompound(i));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compound) {
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
        return compound;
    }

    public static SolarDataManager get(ServerLevel serverLevel) {
        DimensionDataStorage storage = serverLevel.getDataStorage();
        return storage.computeIfAbsent((compoundTag) -> new SolarDataManager(serverLevel, compoundTag),
                () -> {
                    SolarDataManager manager = new SolarDataManager(serverLevel);
                    WeatherManager.initNewWorldWeather(serverLevel, serverLevel.random, manager.getSolarTerm());
                    return manager;
                }, EclipticSeasons.MODID);
    }


    public void updateTicks(ServerLevel world) {
        solarTermsTicks++;
        int dayTime = Math.toIntExact(world.getDayTime() % 24000);
        // 这里是当累计计数，dayTime会重置接近0，但是solarTermsTicks不会，因此有所差异
        if (solarTermsTicks > dayTime + 100)
        // if (dayTime % 100 == 0)
        {
            solarTermsDay++;

            sendAndUpdate(world);
        }
        solarTermsTicks = dayTime;

        setDirty();
    }

    public boolean isValidDimension() {
        return this.isValidDimension;
    }

    public int getSolarTermIndex() {
        if (!isValidDimension()) return SolarTerm.NONE.ordinal();
        return (getSolarTermsDay() / CommonConfig.Season.lastingDaysOfEachTerm.get() + 24) % 24;
    }

    public SolarTerm getSolarTerm() {
        return SolarTerm.get(this.getSolarTermIndex());
    }

    public SolarTerm getNextSolarTerm() {
        if (!isValidDimension()) return SolarTerm.NONE;
        return SolarTerm.get((this.getSolarTermIndex() + 1) % 24);
    }


    public int getSolarTermLastingDays() {
        return CommonConfig.Season.lastingDaysOfEachTerm.get();
    }

    public boolean isTodayLastDay() {
        int longTime = getSolarTermLastingDays();
        return (getSolarTermsDay() + 1) % longTime == 0;
    }


    public int getSolarTermsDay() {
        return solarTermsDay;
    }

    public int getSolarTermsTicks() {
        return solarTermsTicks;
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

    public void addMap(BlockPos pos, HumidityControlProvider humidityControlProvider) {
        ChunkPos chunkPos = new ChunkPos(pos);
        List<Pair<BlockPos, HumidityControlProvider>> blockPosBlockStateMap = this.serverLevelMapMap.get(chunkPos);
        if (blockPosBlockStateMap == null) {
            blockPosBlockStateMap = new ArrayList<>();
            this.serverLevelMapMap.put(chunkPos, blockPosBlockStateMap);
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

    public void addMap(BlockPos pos, GreenHouseCoreProvider provider) {
        ChunkPos chunkPos = new ChunkPos(pos);
        List<Pair<BlockPos, GreenHouseCoreProvider>> blockPosBlockStateMap = this.serverLevelMapMap2.get(chunkPos);
        if (blockPosBlockStateMap == null) {
            blockPosBlockStateMap = new ArrayList<>();
            this.serverLevelMapMap2.put(chunkPos, blockPosBlockStateMap);
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
        this.serverLevelMapMap.remove(chunkPos);
        this.serverLevelMapMap2.remove(chunkPos);
    }

    public HumidityControlProvider queryHumidityControlProvider(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        List<Pair<BlockPos, HumidityControlProvider>> lis = this.serverLevelMapMap.getOrDefault(chunkPos, null);
        if (lis != null) {
            for (Pair<BlockPos, HumidityControlProvider> p : lis) {
                if (p.first().equals(blockPos)) {
                    return p.second();
                }
            }
        }
        return null;
    }

    public HumidityControlProvider removeHumidityControlProvider(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        List<Pair<BlockPos, HumidityControlProvider>> lis = this.serverLevelMapMap.getOrDefault(chunkPos, null);
        if (lis != null) {
            for (int i = 0, lisSize = lis.size(); i < lisSize; i++) {
                Pair<BlockPos, HumidityControlProvider> p = lis.get(i);
                if (p.first().equals(blockPos)) {
                    lis.remove(i);
                    if (lis.isEmpty()) {
                        serverLevelMapMap2.remove(chunkPos);
                    }
                    return p.second();
                }
            }
        }
        return null;
    }

    public int calculateHumidityModification(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        Vec3 center = blockPos.getCenter();

        int localX = blockPos.getX() & 15;
        int localZ = blockPos.getZ() & 15;

        boolean isLeftBorder = localX <= 3;
        boolean isRightBorder = localX >= 12;
        boolean isFrontBorder = localZ <= 3;
        boolean isBackBorder = localZ >= 12;

        int result = 0;
        for (int dx = isLeftBorder ? -1 : 0; dx <= (isRightBorder ? 1 : 0); dx++) {
            for (int dz = isFrontBorder ? -1 : 0; dz <= (isBackBorder ? 1 : 0); dz++) {
                ChunkPos currentChunkPos = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                List<Pair<BlockPos, HumidityControlProvider>> lis = this.serverLevelMapMap.getOrDefault(currentChunkPos, null);

                if (lis != null) {
                    for (Pair<BlockPos, HumidityControlProvider> p : lis) {
                        if (
                            // p.first().getY() > blockPos.getY()
                            // &&
                                p.first().getCenter().distanceToSqr(center) < (p.second().getRange() + 0.1)) {
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
        List<Pair<BlockPos, GreenHouseCoreProvider>> lis = this.serverLevelMapMap2.getOrDefault(chunkPos, null);
        if (lis != null) {
            for (Pair<BlockPos, GreenHouseCoreProvider> p : lis) {
                if (p.first().equals(blockPos)) {
                    return p.second();
                }
            }
        }
        return null;
    }

    public GreenHouseCoreProvider removeGreenHouseProvider(BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        List<Pair<BlockPos, GreenHouseCoreProvider>> lis = this.serverLevelMapMap2.getOrDefault(chunkPos, null);
        if (lis != null) {
            for (int i = 0, lisSize = lis.size(); i < lisSize; i++) {
                Pair<BlockPos, GreenHouseCoreProvider> p = lis.get(i);
                if (p.first().equals(blockPos)) {
                    lis.remove(i);
                    if (lis.isEmpty()) {
                        serverLevelMapMap2.remove(chunkPos);
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

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                ChunkPos currentChunkPos = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
                List<Pair<BlockPos, GreenHouseCoreProvider>> lis = this.serverLevelMapMap2.getOrDefault(currentChunkPos, null);

                if (lis != null) {
                    for (Pair<BlockPos, GreenHouseCoreProvider> p : lis) {
                        if (seasons.contains(p.second().getSeason())
                                // && p.first().getY() >= blockPos.getY()
                                && p.first().getCenter().distanceToSqr(center) < (15 * 15 + 0.1)) {
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
        List<Pair<BlockPos, HumidityControlProvider>> list = this.serverLevelMapMap.get(pos);
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
            if (list.isEmpty()) this.serverLevelMapMap.remove(pos);
        }
    }

    public void sendAndUpdate(ServerLevel world) {
        boolean changeSolarTerm = getSolarTermsDay() % CommonConfig.Season.lastingDaysOfEachTerm.get() == 0;

        SolarTerm solarTerm = getSolarTerm();

        if (changeSolarTerm) {
            // BiomeClimateManager.updateTemperature(world, getSolarTerm());
            SolarTerm old = SolarTerm.collectValues()[(getSolarTermIndex() + 24) % 24];

            MinecraftForge.EVENT_BUS.post(new SolarTermChangeEvent(old, solarTerm, world, solarTermsDay));
        }

        if (solarTerm != SolarTerm.NONE) {
            for (ServerPlayer player : world.players()) {
                SimpleNetworkHandler.send(player, new SolarTermsMessage(this.getSolarTermsDay()));
                if (changeSolarTerm && CommonConfig.Season.enableInform.get()) {
                    player.sendSystemMessage(SimpleUtil.getSolarTermMessage(solarTerm), false);
                }
                WeatherManager.tickPlayerForSeasonCheck(player);
            }
        }
    }


    public void resendBiomesForChunks(ServerLevel serverLevel, ChunkMap chunkMap, List<ChunkAccess> chunkAccessList) {
        Map<ServerPlayer, List<LevelChunk>> map = new HashMap<>();

        for (ChunkAccess chunkaccess : chunkAccessList) {
            ChunkPos chunkpos = chunkaccess.getPos();
            LevelChunk levelchunk;
            if (chunkaccess instanceof LevelChunk levelchunk1) {
                levelchunk = levelchunk1;
            } else {
                levelchunk = serverLevel.getChunk(chunkpos.x, chunkpos.z);
            }

            for (ServerPlayer serverplayer : chunkMap.getPlayers(chunkpos, false)) {
                map.computeIfAbsent(serverplayer, (p_274834_) -> new ArrayList<>()).add(levelchunk);
            }
        }

        map.forEach((player, levelChunks) -> {
            player.connection.send(ClientboundChunksBiomesPacket.forChunks(levelChunks));
        });
    }

    public void tickLevel(ServerLevel level) {
        this.onceCheck.clear();
    }

    public BlockState addSkipNextCheck(BlockPos blockPos, BlockState blockState) {
        return this.onceCheck.put(blockPos.asLong(),blockState);
    }

    public boolean shouldSkipNextCheck(BlockPos blockPos) {
        return this.onceCheck.containsKey(blockPos.asLong());
    }
}
