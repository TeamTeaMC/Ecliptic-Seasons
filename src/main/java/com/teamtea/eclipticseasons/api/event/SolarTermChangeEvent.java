package com.teamtea.eclipticseasons.api.event;


import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import lombok.Getter;
import net.minecraft.world.level.Level;


/**
 * The event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}
 * **/
@Getter
public class SolarTermChangeEvent extends net.minecraftforge.eventbus.api.Event implements IESEvent {
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

}
