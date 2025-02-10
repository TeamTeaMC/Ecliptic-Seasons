package com.teamtea.eclipticseasons.common.core.solar;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import oculus.org.antlr.v4.runtime.misc.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class ClientSolarDataManager extends SolarDataManager {

    public ClientSolarDataManager(World level) {
        super(level);
        this.levelWeakReference = new WeakReference<>(level);
    }


    @Override
    public @NotNull CompoundNBT save(CompoundNBT compound) {
        compound.putInt("SolarTermsDay", getSolarTermsDay());
        compound.putInt("SolarTermsTicks", getSolarTermsTicks());
        ListNBT listTag = new ListNBT();
        if (levelWeakReference.get() != null) {
            ArrayList<WeatherManager.BiomeWeather> list = WeatherManager.getBiomeList(levelWeakReference.get());
            for (WeatherManager.BiomeWeather biomeWeather : list) {
                listTag.add(biomeWeather.serializeNBT());
            }
        }
        compound.put("biomes", listTag);
        return compound;
    }

    public static ClientSolarDataManager get(World level) {
        if (level instanceof ServerWorld ) {
            return get((ServerWorld)level);
        }
        if (level instanceof ClientWorld ) {
            return get((ClientWorld) level);
        }
        return null;
    }


    public static ClientSolarDataManager get(ServerWorld serverLevel) {
        DimensionSavedDataManager storage = serverLevel.getDataStorage();
        return storage.computeIfAbsent(() -> new ClientSolarDataManager(serverLevel), EclipticSeasons.MODID);
    }


    public static ClientSolarDataManager get(ClientWorld clientLevel) {
        return new ClientSolarDataManager(clientLevel);
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

}
