package com.teamtea.eclipticseasons.common.core.solar;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.SolarTermsMessage;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SolarDataManager extends SavedData {

    protected int solarTermsDay = (ServerConfig.Season.initialSolarTermIndex.get() - 1) * ServerConfig.Season.lastingDaysOfEachTerm.get();
    protected int solarTermsTicks = 0;

    protected WeakReference<Level> levelWeakReference;

    public SolarDataManager(Level level) {
        levelWeakReference = new WeakReference<>(level);
    }

    public SolarDataManager(Level level, CompoundTag nbt) {
        this(level);
        setSolarTermsDay(nbt.getInt("SolarTermsDay"));
        setSolarTermsTicks(nbt.getInt("SolarTermsTicks"));
        var listTag = nbt.getList("biomes", Tag.TAG_COMPOUND);
        if (levelWeakReference.get() != null) {
            var biomeWeathers =WeatherManager.getBiomeList(levelWeakReference.get());
            for (int i = 0; i < listTag.size(); i++) {
                var location = listTag.getCompound(i).getString("biome");
                for (WeatherManager.BiomeWeather biomeWeather : biomeWeathers) {
                    if (location.equals(biomeWeather.location.toString()))
                    {
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
        if (solarTermsTicks > dayTime + 100) {
            solarTermsDay++;
            solarTermsDay %= 24 * ServerConfig.Season.lastingDaysOfEachTerm.get();

            BiomeClimateManager.updateTemperature(world, getSolarTerm());
            sendUpdateMessage(world);
        }
        solarTermsTicks = dayTime;

        // 强制刷新，由于服务器区块是悲观锁，所以不能强刷
        // if (world.getRandom().nextBoolean() && world.getDayTime() % 1000 == 0) {
        //     // player.connection.send();
        //     var a = new ArrayList<ChunkAccess>();
        //     for (ChunkHolder chunk : (world).getChunkSource().chunkMap.getChunks()) {
        //         var cs = chunk.getLastAvailable();
        //         if (cs != null)
        //             a.add(chunk.getLastAvailable());
        //     }
        //
        //     world.getChunkSource().chunkMap.resendBiomesForChunks(a);
        // }

        setDirty();
    }

    public int getSolarTermIndex() {
        return solarTermsDay / ServerConfig.Season.lastingDaysOfEachTerm.get();
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
        this.solarTermsDay = Math.max(solarTermsDay, 0) % (24 * ServerConfig.Season.lastingDaysOfEachTerm.get());
        setDirty();
    }

    public void setSolarTermsTicks(int solarTermsTicks) {
        this.solarTermsTicks = solarTermsTicks;
        setDirty();
    }

    public void sendUpdateMessage(ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            SimpleNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SolarTermsMessage(this.getSolarTermsDay()));
            if (getSolarTermsDay() % ServerConfig.Season.lastingDaysOfEachTerm.get() == 0) {
                player.sendSystemMessage(Component.translatable("info.teastory.environment.solar_term.message", SolarTerm.get(getSolarTermIndex()).getAlternationText()), false);
            }
        }
    }


    public void resendBiomesForChunks(ServerLevel serverLevel, ChunkMap chunkMap, List<ChunkAccess> chunkAccessList) {
        Map<ServerPlayer, List<LevelChunk>> map = new HashMap<>();

        for(ChunkAccess chunkaccess : chunkAccessList) {
            ChunkPos chunkpos = chunkaccess.getPos();
            LevelChunk levelchunk;
            if (chunkaccess instanceof LevelChunk levelchunk1) {
                levelchunk = levelchunk1;
            } else {
                levelchunk = serverLevel.getChunk(chunkpos.x, chunkpos.z);
            }

            for(ServerPlayer serverplayer : chunkMap.getPlayers(chunkpos, false)) {
                map.computeIfAbsent(serverplayer, (p_274834_) -> new ArrayList<>()).add(levelchunk);
            }
        }

        map.forEach((player, levelChunks) -> {
            player.connection.send(ClientboundChunksBiomesPacket.forChunks(levelChunks));
        });
    }

}
