package com.teamtea.eclipticseasons.api.event;


import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.world.level.Level;


/**
 * The event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * **/
public class SolarTermChangeEvent extends net.minecraftforge.eventbus.api.Event implements ISolarTermChangeEvent {
    private final SolarTerm oldSolarTerm;
    private final SolarTerm newSolarTerm;
    private final Level level;
    private final int solarDays;

    public SolarTermChangeEvent(SolarTerm oldSolarTerm, SolarTerm newSolarTerm, Level level, int solarDays) {
        this.oldSolarTerm = oldSolarTerm;
        this.newSolarTerm = newSolarTerm;
        this.level = level;
        this.solarDays = solarDays;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public SolarTerm getNewSolarTerm() {
        return newSolarTerm;
    }

    @Override
    public SolarTerm getOldSolarTerm() {
        return oldSolarTerm;
    }

    @Override
    public int getSolarDays() {
        return solarDays;
    }
}
