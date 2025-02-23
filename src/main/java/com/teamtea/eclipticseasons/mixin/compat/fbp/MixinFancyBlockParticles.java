package com.teamtea.eclipticseasons.mixin.compat.fbp;


import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import hantonik.fbp.FancyBlockParticles;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FancyBlockParticles.class})
public abstract class MixinFancyBlockParticles {

    @Inject(
            remap = false,
            method = "getBiomeTemperature",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void ecliptic$getBiomeTemperature(Biome biome, BlockPos pos, ClientWorld level, CallbackInfoReturnable<Float> cir) {
        float v =  WeatherManager.getPrecipitationAt(level, biome, pos) == Biome.RainType.SNOW ?
                0f : 1f;
        cir.setReturnValue(v);
    }

    @Inject(
            remap = false,
            method = "getBiomePrecipitation",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void ecliptic$getBiomePrecipitation(ClientWorld level, Biome biome, CallbackInfoReturnable<Biome.RainType> cir) {
        cir.setReturnValue(WeatherManager.getPrecipitationAt(level, biome, BlockPos.ZERO));
    }
}
