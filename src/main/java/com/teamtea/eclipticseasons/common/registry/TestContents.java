package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.misc.BasicWeather;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * Not really use.
 * **/
@Deprecated
@SuppressWarnings("removal")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class TestContents {
    public static final ResourceKey<Registry<BasicWeather>> WEATHER = createRegistryKey("weather");
    public static final Registry<BasicWeather> BASIC_WEATHERS = new RegistryBuilder<>(WEATHER).sync(true).create();
    public static final DeferredRegister<BasicWeather> weathers = DeferredRegister.create(WEATHER, EclipticSeasonsApi.MODID);

    static {
        // ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(p_320192_, this.type);
        if (FMLLoader.getDist() == Dist.CLIENT) {
            weathers.register("test", () -> new BasicWeather() {
                @Override
                protected Object clone() {
                    return this;
                }
            });
            weathers.register("ss", () -> new BasicWeather() {
                @Override
                protected Object clone() {
                    return this;
                }
            });
        } else {
            weathers.register("ss", () -> new BasicWeather() {
                @Override
                protected Object clone() {
                    return this;
                }
            });
            weathers.register("test", () -> new BasicWeather() {
                @Override
                protected Object clone() {
                    return this;
                }
            });
        }


    }

    @SubscribeEvent
    public static void newRegistryEvent(NewRegistryEvent event) {
        event.register(BASIC_WEATHERS);
    }

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String pName) {
        return ResourceKey.createRegistryKey(EclipticSeasons.rl(pName));
    }
}
