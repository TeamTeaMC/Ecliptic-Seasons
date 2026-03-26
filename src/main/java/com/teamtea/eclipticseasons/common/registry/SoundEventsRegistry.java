package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@SuppressWarnings("removal")
@EventBusSubscriber
public class SoundEventsRegistry {
    public final static SoundEvent spring_forest = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.spring_forest"));
    public final static SoundEvent garden_wind = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.garden_wind"));
    public final static SoundEvent night_river = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.night_river"));
    public final static SoundEvent windy_leave = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.windy_leave"));
    public final static SoundEvent winter_forest = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.winter_forest"));
    public final static SoundEvent winter_cold = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.winter_cold"));
    public final static SoundEvent wind_chimes = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("block.wind_chimes"));
    public final static SoundEvent bamboo_wind_chimes = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("block.bamboo_wind_chimes"));
    public final static SoundEvent paper_wind_chimes = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("block.paper_wind_chimes"));

    public final static SoundEvent snowless_hometown = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl( "record.snowless_hometown"));

    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        // MultiPackResourceManager
        event.register(Registries.SOUND_EVENT, soundEventRegisterHelper -> {
            soundEventRegisterHelper.register(spring_forest.location(), spring_forest);
            soundEventRegisterHelper.register(garden_wind.location(), garden_wind);
            soundEventRegisterHelper.register(night_river.location(), night_river);
            soundEventRegisterHelper.register(windy_leave.location(), windy_leave);
            soundEventRegisterHelper.register(winter_forest.location(), winter_forest);
            soundEventRegisterHelper.register(winter_cold.location(), winter_cold);
            soundEventRegisterHelper.register(wind_chimes.location(), wind_chimes);
            soundEventRegisterHelper.register(bamboo_wind_chimes.location(), bamboo_wind_chimes);
            soundEventRegisterHelper.register(paper_wind_chimes.location(), paper_wind_chimes);
            soundEventRegisterHelper.register(snowless_hometown.location(), snowless_hometown);
        });
    }
}
