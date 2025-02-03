package com.teamtea.eclipticseasons.common.registry;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.particle.ColorParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ParticleRegistry {
    public static final SimpleParticleType FIREFLY = new SimpleParticleType(false);
    public static final SimpleParticleType WILD_GOOSE = new SimpleParticleType(false);
    public static final SimpleParticleType BUTTERFLY = new SimpleParticleType(false);
    public static final ParticleType<ColorParticleOptions> FALLEN_LEAVES = create(false, ColorParticleOptions.DESERIALIZER, (p_123819_) -> ColorParticleOptions.CODEC);

    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        event.register(Registries.PARTICLE_TYPE, particleTypeRegisterHelper -> {
            particleTypeRegisterHelper.register(EclipticSeasons.rl("firefly"), FIREFLY);
            particleTypeRegisterHelper.register(EclipticSeasons.rl("wild_goose"), WILD_GOOSE);
            particleTypeRegisterHelper.register(EclipticSeasons.rl("butterfly"), BUTTERFLY);
            particleTypeRegisterHelper.register(EclipticSeasons.rl("fallen_leaves"), FALLEN_LEAVES);
        });
    }

    private static <T extends ParticleOptions> ParticleType<T> create(boolean pOverrideLimiter, ParticleOptions.Deserializer<T> pDeserializer, final Function<ParticleType<T>, Codec<T>> pCodecFactory) {
        return new ParticleType<T>(pOverrideLimiter, pDeserializer) {
            public Codec<T> codec() {
                return pCodecFactory.apply(this);
            }
        };
    }
}
