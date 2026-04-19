package com.teamtea.eclipticseasons.mixin.compat.iris;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.uniforms.BiomeUniforms;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

@Mixin({BiomeUniforms.class})
public abstract class MixinBiomeUniforms {

    @WrapOperation(
            remap = false,
            method = "lambda$addBiomeUniforms$2",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private static Biome.Precipitation eclipticseasons$addBiomeUniforms$2_precipitation(Biome instance, BlockPos pos, Operation<Biome.Precipitation> original, @Local(argsOnly = true) LocalPlayer localPlayer) {
        if (EclipticUtil.hasLocalWeather(localPlayer.level())) {
            return WeatherManager.getPrecipitationAt(localPlayer.level(), MapChecker.getSurfaceBiome(localPlayer.level(), pos).value(), pos);
        } else return original.call(instance, pos);
    }
}