package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ParticleRegistry {
    public static final SimpleParticleType FIREFLY = new SimpleParticleType(false);
    public static final SimpleParticleType WILD_GOOSE = new SimpleParticleType(false);

    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        event.register(Registry.PARTICLE_TYPE.key(), particleTypeRegisterHelper -> {
            particleTypeRegisterHelper.register(EclipticSeasons.rl("firefly"), FIREFLY);
            particleTypeRegisterHelper.register(EclipticSeasons.rl("wild_goose"), WILD_GOOSE);
        });
    }


}
