package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WeatherEffectRenderer.class)
public abstract class MixinWeatherEffectRenderer {


    @Shadow
    @Final
    private static Identifier RAIN_LOCATION;

    @ModifyArg(
            method = {"render(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;Lnet/minecraft/client/renderer/state/level/LevelRenderState;)V"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;getTexture(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/texture/AbstractTexture;")
    )
    private Identifier eclipticseasons$renderSnowAndRain_rebindingTexture(Identifier location) {
        return ClientWeatherChecker.modifyRainAmount3(location, location == RAIN_LOCATION);
    }
}
