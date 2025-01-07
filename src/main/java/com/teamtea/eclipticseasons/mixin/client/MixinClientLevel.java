package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinClientLevel {


    @Inject(at = {@At("HEAD")}, method = {"isRaining"}, cancellable = true)
    private void ecliptic$Client_isRaining(CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfig.Weather.useSolarWeather.get())
            if ((Object) this instanceof ClientLevel clientLevel) {
                cir.setReturnValue(ClientWeatherChecker.isRain(clientLevel));
            }
    }

    @Inject(at = {@At("HEAD")}, method = {"getRainLevel"}, cancellable = true)
    private void ecliptic$Client_getRainLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if (CommonConfig.Weather.useSolarWeather.get())
            if ((Object) this instanceof ClientLevel clientLevel) {
                cir.setReturnValue(ClientWeatherChecker.getRainLevel(clientLevel, p_46723_));
            }
    }

    @Inject(at = {@At("HEAD")}, method = {"isRainingAt"}, cancellable = true)
    private void ecliptic$Client_isRainingAt(BlockPos p_46759_, CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfig.Weather.useSolarWeather.get())
            if ((Object) this instanceof ClientLevel clientLevel) {
                cir.setReturnValue(ClientWeatherChecker.isRainingAt(clientLevel, p_46759_));
            }
    }

    @Inject(at = {@At("HEAD")}, method = {"isThundering"}, cancellable = true)
    private void ecliptic$isThundering(CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfig.Weather.useSolarWeather.get())
            if ((Object) this instanceof ClientLevel clientLevel) {
                cir.setReturnValue(ClientWeatherChecker.isThundering(clientLevel));
            }
    }


    @Inject(at = {@At("HEAD")}, method = {"getThunderLevel"}, cancellable = true)
    private void ecliptic$Client_getThunderLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if (CommonConfig.Weather.useSolarWeather.get())
            if ((Object) this instanceof ClientLevel clientLevel) {
                cir.setReturnValue(ClientWeatherChecker.getThunderLevel(clientLevel, p_46723_));
            }
    }
}
