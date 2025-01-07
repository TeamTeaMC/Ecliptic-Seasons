package com.teamtea.eclipticseasons.mixin.compat.iris;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.BiomeUniforms;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

@Mixin({BiomeUniforms.class})
public abstract class MixinBiomeUniforms {

    @Shadow
    static IntSupplier playerI(ToIntFunction<LocalPlayer> function) {
        return null;
    }

    @Inject(
            remap = false,
            method = "addBiomeUniforms",
            at = @At(value = "HEAD")
    )
    private static void ecliptic$addBiomeUniforms_pre(UniformHolder uniforms, CallbackInfo ci) {
        uniforms.uniform1i(UniformUpdateFrequency.PER_TICK, "biome_precipitation", playerI((player) -> {
            Biome.Precipitation precipitation =
                    EclipticSeasonsApi.getInstance().getPrecipitationAt(player.level(), player.blockPosition());
            byte var10000;
            switch (precipitation) {
                case NONE -> var10000 = 0;
                case RAIN -> var10000 = 1;
                case SNOW -> var10000 = 2;
                default -> throw new MatchException(null, null);
            }
            return var10000;
        }));
        EclipticSeasons.LOGGER.warn("Allow Iris to use the biome_precipitation agent from %s ".formatted(EclipticSeasonsApi.MODID));
    }
}
