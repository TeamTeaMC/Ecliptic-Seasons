package com.teamtea.eclipticseasons.mixin.common;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinLevel {


    @Inject(at = {@At("HEAD")}, method = {"isRaining"}, cancellable = true)
    private void ecliptic$isRaining(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            if (EclipticUtil.useSolarWeather()) {
                if (ServerConfig.Debug.debugMode.get()) {
                    throw new IllegalCallerException("Use isRainAt to check if rain");
                }
                cir.setReturnValue(WeatherManager.isRainingEverywhere(serverLevel));
            }
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getRainLevel"}, cancellable = true)
    private void ecliptic$getRainLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            if (EclipticUtil.useSolarWeather()) {
                if (ServerConfig.Debug.debugMode.get()) {
                    throw new IllegalCallerException("Shouldn't call getRainLevel now");
                }
                cir.setReturnValue(WeatherManager.getMinRainLevel(serverLevel, p_46723_));
            }
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"isRainingAt"}, cancellable = true)
    private void ecliptic$isRainingAt(BlockPos p_46759_, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            if (EclipticUtil.useSolarWeather()) {
                cir.setReturnValue(WeatherManager.isRainingAt(serverLevel, p_46759_));
            }
        }
    }


    @Inject(at = {@At("HEAD")}, method = {"isThundering"}, cancellable = true)
    private void ecliptic$isThundering(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            if (EclipticUtil.useSolarWeather()) {
                if (ServerConfig.Debug.debugMode.get()) {
                    throw new IllegalCallerException("Use isThunderingAt to check if rain");
                }
                cir.setReturnValue(WeatherManager.isThunderEverywhere(serverLevel));
            }
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getThunderLevel"}, cancellable = true)
    private void ecliptic$getThunderLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            if (EclipticUtil.useSolarWeather()) {
                if (ServerConfig.Debug.debugMode.get()) {
                    throw new IllegalCallerException("Shouldn't call getThunderLevel now");
                }
                cir.setReturnValue(WeatherManager.getMinThunderLevel(serverLevel, p_46723_));
            }
        }
    }
}
