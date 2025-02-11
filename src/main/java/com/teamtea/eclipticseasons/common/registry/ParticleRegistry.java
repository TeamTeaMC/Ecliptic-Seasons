package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ParticleRegistry {
    public static final SimpleParticleType FIREFLY = new SimpleParticleType(false);
    public static final SimpleParticleType WILD_GOOSE = new SimpleParticleType(false);

    @SubscribeEvent
    public static void blockRegister(RegistryEvent.Register<ParticleType<?>> event) {

        // event.getRegistry().register(Registry.PARTICLE_TYPE.key(), particleTypeRegisterHelper -> {
        //     particleTypeRegisterHelper.register(rl("firefly"), FIREFLY);
        //     particleTypeRegisterHelper.register(rl("wild_goose"), WILD_GOOSE);
        // });
        FIREFLY.setRegistryName(EclipticSeasons.rl("firefly"));
        WILD_GOOSE.setRegistryName(EclipticSeasons.rl("wild_goose"));
        event.getRegistry().registerAll(
                FIREFLY, WILD_GOOSE
        );
    }


}
