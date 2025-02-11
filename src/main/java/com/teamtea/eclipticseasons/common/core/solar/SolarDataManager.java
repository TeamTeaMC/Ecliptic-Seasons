package com.teamtea.eclipticseasons.common.core.solar;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.SolarTermsMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.Pair;
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
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SolarDataManager extends SavedData {

    protected int solarTermsDay = (CommonConfig.Season.initialSolarTermIndex.get() - 1) * CommonConfig.Season.lastingDaysOfEachTerm.get();
    protected int solarTermsTicks = 0;

    protected WeakReference<Level> levelWeakReference;
    private final Map<ChunkPos, List<Pair<BlockPos, BlockState>>> serverLevelMapMap;

    public SolarDataManager(Level level) {
        levelWeakReference = new WeakReference<>(level);
        serverLevelMapMap = new HashMap<>();
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
                () -> new SolarDataManager(serverLevel), EclipticSeasons.MODID);
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

    public int getSolarTermIndex() {
        return (solarTermsDay / CommonConfig.Season.lastingDaysOfEachTerm.get()) % 24;
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

    public void setSolarTermsDay(int solarTermsDay) {
        // this.solarTermsDay = Math.max(solarTermsDay, 0) % (24 * CommonConfig.Season.lastingDaysOfEachTerm.get());
        this.solarTermsDay = solarTermsDay;
        setDirty();
    }

    public void setSolarTermsTicks(int solarTermsTicks) {
        this.solarTermsTicks = solarTermsTicks;
        setDirty();
    }

    public void addMap(BlockPos pos, BlockState state) {
        ChunkPos chunkPos = new ChunkPos(pos);
        List<Pair<BlockPos, BlockState>> blockPosBlockStateMap = this.serverLevelMapMap.get(chunkPos);
        if (blockPosBlockStateMap == null) {
            blockPosBlockStateMap = new ArrayList<Pair<BlockPos, BlockState>>();
            this.serverLevelMapMap.put(chunkPos, blockPosBlockStateMap);
        }

        for (int i = 0; i < blockPosBlockStateMap.size(); i++) {
            Pair<BlockPos, BlockState> p = blockPosBlockStateMap.get(i);
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

    public BlockState findNearPos(BlockPos blockPos) {
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
                List<Pair<BlockPos, BlockState>> lis = this.serverLevelMapMap.getOrDefault(currentChunkPos, null);

                if (lis != null) {
                    for (Pair<BlockPos, BlockState> p : lis) {
                        if (p.first().getY() < blockPos.getY()
                                && p.first().getCenter().distanceToSqr(center) < 16.1) {
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
        List<Pair<BlockPos, BlockState>> list = this.serverLevelMapMap.get(pos);
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

            MinecraftForge.EVENT_BUS.post(new SolarTermChangeEvent(old, getSolarTerm(), world, solarTermsDay));
        }

        for (ServerPlayer player : world.players()) {
            SimpleNetworkHandler.send(player, new SolarTermsMessage(this.getSolarTermsDay()));
            if (changeSolarTerm && CommonConfig.Season.enableInform.get()) {
                player.sendSystemMessage(SimpleUtil.getSolarTermMessage(getSolarTerm()), false);
            }
            WeatherManager.tickPlayerForSeasonCheck(player);
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

}
