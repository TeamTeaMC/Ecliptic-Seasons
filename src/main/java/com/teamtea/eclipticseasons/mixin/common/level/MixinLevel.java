package com.teamtea.eclipticseasons.mixin.common.level;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
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
    private void eclipticseasons$isRaining(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            if (EclipticUtil.hasLocalWeather(serverLevel)) {
                if (CommonConfig.Debug.logIllegalUse.get()) {
                    try {
                        throw new IllegalCallerException("Use isRainAt to check if rain");
                    } catch (IllegalCallerException e) {
                        e.printStackTrace();
                    }
                }
                cir.setReturnValue(WeatherManager.isRainingEverywhere(serverLevel));
            }
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getRainLevel"}, cancellable = true)
    private void eclipticseasons$getRainLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            if (EclipticUtil.hasLocalWeather(serverLevel)) {
                if (CommonConfig.Debug.logIllegalUse.get()) {
                    try {
                        throw new IllegalCallerException("Shouldn't call getRainLevel now");
                    } catch (IllegalCallerException e) {
                        e.printStackTrace();
                    }
                }
                cir.setReturnValue(WeatherManager.getMinRainLevel(serverLevel, p_46723_));
            }
        }
    }

    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")}, method = {"isRainingAt"})
    private boolean eclipticseasons$isRainingAt_skipRainCheck(Level instance, Operation<Boolean> original) {
        return EclipticUtil.hasLocalWeather(instance) || original.call(instance);
    }

    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")}, method = {"isRainingAt"}, cancellable = true)
    private void eclipticseasons$isRainingAt_endBiomeCheck(BlockPos p_46759_, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Level level) {
            if (EclipticUtil.hasLocalWeather(level)) {
                cir.setReturnValue(WeatherManager.isRainingUnderSky(level, p_46759_));
            }
        }
    }


    @Inject(at = {@At("HEAD")}, method = {"isThundering"}, cancellable = true)
    private void eclipticseasons$isThundering(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            if (EclipticUtil.hasLocalWeather(serverLevel)) {
                if (CommonConfig.Debug.logIllegalUse.get()) {
                    try {
                        throw new IllegalCallerException("Use isThunderingAt to check if rain");
                    } catch (IllegalCallerException e) {
                        e.printStackTrace();
                    }
                }
                cir.setReturnValue(WeatherManager.isThunderEverywhere(serverLevel));
            }
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getThunderLevel"}, cancellable = true)
    private void eclipticseasons$getThunderLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ServerLevel serverLevel) {
            if (EclipticUtil.hasLocalWeather(serverLevel)) {
                if (CommonConfig.Debug.logIllegalUse.get()) {
                    try {
                        throw new IllegalCallerException("Shouldn't call getThunderLevel now");
                    } catch (IllegalCallerException e) {
                        e.printStackTrace();
                    }
                }
                cir.setReturnValue(WeatherManager.getMinThunderLevel(serverLevel, p_46723_));
            }
        }
    }
}
