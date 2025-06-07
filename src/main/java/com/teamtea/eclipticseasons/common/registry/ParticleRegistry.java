package com.teamtea.eclipticseasons.common.registry;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Function;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class ParticleRegistry {
    public static final SimpleParticleType FIREFLY = new SimpleParticleType(false);
    public static final SimpleParticleType WILD_GOOSE = new SimpleParticleType(false);
    public static final SimpleParticleType BUTTERFLY = new SimpleParticleType(false);
    public static final ParticleType<ColorParticleOption> GREENHOUSE =  create(false, ColorParticleOption::codec, ColorParticleOption::streamCodec);
    public static final ParticleType<ColorParticleOption> FALLEN_LEAVES = create(false, ColorParticleOption::codec, ColorParticleOption::streamCodec);
    public static final ParticleType<ColorParticleOption> FLYING_BLOOM = create(false, ColorParticleOption::codec, ColorParticleOption::streamCodec);

    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        event.register(Registries.PARTICLE_TYPE, particleTypeRegisterHelper -> {
            particleTypeRegisterHelper.register(EclipticSeasons.rl("firefly"), FIREFLY);
            particleTypeRegisterHelper.register(EclipticSeasons.rl("wild_goose"), WILD_GOOSE);
            particleTypeRegisterHelper.register(EclipticSeasons.rl("butterfly"), BUTTERFLY);
            particleTypeRegisterHelper.register(EclipticSeasons.rl("fallen_leaves"), FALLEN_LEAVES);
            particleTypeRegisterHelper.register(EclipticSeasons.rl("flying_bloom"), FLYING_BLOOM);
            particleTypeRegisterHelper.register(EclipticSeasons.rl("greenhouse"), GREENHOUSE);
        });
    }

    private static <T extends ParticleOptions> ParticleType<T> create(
            boolean pOverrideLimitter,
            final Function<ParticleType<T>, MapCodec<T>> pCodecGetter,
            final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> pStreamCodecGetter
    ) {
        return new ParticleType<T>(pOverrideLimitter) {
            @Override
            public MapCodec<T> codec() {
                return pCodecGetter.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return pStreamCodecGetter.apply(this);
            }
        };
    }


}
