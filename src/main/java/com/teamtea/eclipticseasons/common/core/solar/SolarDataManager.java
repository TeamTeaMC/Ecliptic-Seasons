package com.teamtea.eclipticseasons.common.core.solar;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.SolarTermsMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;


public class SolarDataManager extends SavedData {

    protected int solarTermsDay = (CommonConfig.Season.initialSolarTermIndex.get() - 1) * CommonConfig.Season.lastingDaysOfEachTerm.get();
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
            solarTermsDay %= 24 * CommonConfig.Season.lastingDaysOfEachTerm.get();

            BiomeClimateManager.updateTemperature(world, getSolarTermIndex());
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
        return solarTermsDay / CommonConfig.Season.lastingDaysOfEachTerm.get();
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
        this.solarTermsDay = Math.max(solarTermsDay, 0) % (24 * CommonConfig.Season.lastingDaysOfEachTerm.get());
        setDirty();
    }

    public void setSolarTermsTicks(int solarTermsTicks) {
        this.solarTermsTicks = solarTermsTicks;
        setDirty();
    }

    public void sendUpdateMessage(ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            SimpleNetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SolarTermsMessage(this.getSolarTermsDay()));
            if (CommonConfig.Season.enableInform.get()
                    &&getSolarTermsDay() % CommonConfig.Season.lastingDaysOfEachTerm.get() == 0) {
                player.sendMessage(new TranslatableComponent("info.teastory.environment.solar_term.message", SolarTerm.get(getSolarTermIndex()).getAlternationText()),  Util.NIL_UUID);
            }
        }
    }



}
