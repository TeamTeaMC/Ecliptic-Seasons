package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ParticleRegistry {
    public static final BasicParticleType FIREFLY = new BasicParticleType(false);
    public static final BasicParticleType WILD_GOOSE = new BasicParticleType(false);

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
