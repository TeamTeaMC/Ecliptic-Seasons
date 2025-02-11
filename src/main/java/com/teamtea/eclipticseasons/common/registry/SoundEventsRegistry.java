package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundEventsRegistry {
    public final static SoundEvent spring_forest = new SoundEvent(EclipticSeasons.rl("ambient.spring_forest"));
    public final static SoundEvent garden_wind = new SoundEvent(EclipticSeasons.rl("ambient.garden_wind"));
    public final static SoundEvent night_river = new SoundEvent(EclipticSeasons.rl("ambient.night_river"));
    public final static SoundEvent windy_leave = new SoundEvent(EclipticSeasons.rl("ambient.windy_leave"));
    public final static SoundEvent winter_forest = new SoundEvent(EclipticSeasons.rl("ambient.winter_forest"));
    public final static SoundEvent winter_cold = new SoundEvent(EclipticSeasons.rl("ambient.winter_cold"));

    @SubscribeEvent
    public static void blockRegister(RegistryEvent.Register<SoundEvent> event) {
        // MultiPackResourceManager
        // event.register(Registry.SOUND_EVENT.key(), soundEventRegisterHelper -> {
        //     soundEventRegisterHelper.register(spring_forest.getLocation(), spring_forest);
        //     soundEventRegisterHelper.register(garden_wind.getLocation(), garden_wind);
        //     soundEventRegisterHelper.register(night_river.getLocation(), night_river);
        //     soundEventRegisterHelper.register(windy_leave.getLocation(), windy_leave);
        //     soundEventRegisterHelper.register(winter_forest.getLocation(), winter_forest);
        //     soundEventRegisterHelper.register(winter_cold.getLocation(), winter_cold);
        // });
        spring_forest.setRegistryName(EclipticSeasons.rl("spring_forest"));
        garden_wind.setRegistryName(EclipticSeasons.rl("garden_wind"));
        night_river.setRegistryName(EclipticSeasons.rl("night_river"));
        windy_leave.setRegistryName(EclipticSeasons.rl("windy_leave"));
        winter_forest.setRegistryName(EclipticSeasons.rl("winter_forest"));
        winter_cold.setRegistryName(EclipticSeasons.rl("winter_cold"));
        event.getRegistry().registerAll(spring_forest,
                garden_wind,
                night_river,
                windy_leave,
                winter_forest,
                winter_cold
        );
    }


    @SubscribeEvent
    public static void onServerAboutToStartEvent(FMLCommonSetupEvent event) {
        WeatherManager.BIOME_WEATHER_LIST.clear();
        WeatherManager.NEXT_CHECK_BIOME_MAP.clear();
    }
}
