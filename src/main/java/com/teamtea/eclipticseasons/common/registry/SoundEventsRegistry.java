package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundEventsRegistry {
    public final static SoundEvent spring_forest = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.spring_forest"));
    public final static SoundEvent garden_wind = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.garden_wind"));
    public final static SoundEvent night_river = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.night_river"));
    public final static SoundEvent windy_leave = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.windy_leave"));
    public final static SoundEvent winter_forest = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.winter_forest"));
    public final static SoundEvent winter_cold = SoundEvent.createVariableRangeEvent(EclipticSeasons.rl("ambient.winter_cold"));

    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        // MultiPackResourceManager
        event.register(Registries.SOUND_EVENT, soundEventRegisterHelper -> {
            soundEventRegisterHelper.register(spring_forest.getLocation(), spring_forest);
            soundEventRegisterHelper.register(garden_wind.getLocation(), garden_wind);
            soundEventRegisterHelper.register(night_river.getLocation(), night_river);
            soundEventRegisterHelper.register(windy_leave.getLocation(), windy_leave);
            soundEventRegisterHelper.register(winter_forest.getLocation(), winter_forest);
            soundEventRegisterHelper.register(winter_cold.getLocation(), winter_cold);
        });
    }
}
