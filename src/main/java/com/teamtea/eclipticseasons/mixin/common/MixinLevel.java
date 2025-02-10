package com.teamtea.eclipticseasons.mixin.common;


import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinLevel {


    @Inject(at = {@At("HEAD")}, method = {"isRaining"}, cancellable = true)
    private void ecliptic$isRaining(CallbackInfoReturnable<Boolean> cir) throws IllegalAccessException {
        if ((Object) this instanceof ServerWorld ) {
            if (CommonConfig.Debug.debugMode.get()){
                throw new IllegalAccessException("Use isRainAt to check if rain");
            }
            cir.setReturnValue(WeatherManager.isRainingEverywhere((ServerWorld)(Object) this));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getRainLevel"}, cancellable = true)
    private void ecliptic$getRainLevel(float p_46723_, CallbackInfoReturnable<Float> cir) throws IllegalAccessException {
        if ((Object) this instanceof ServerWorld ) {
            if (CommonConfig.Debug.debugMode.get()){
                throw new IllegalAccessException("Shouldn't call getRainLevel now");
            }
            cir.setReturnValue(WeatherManager.getMinRainLevel( (ServerWorld)(Object) this,p_46723_));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"isRainingAt"}, cancellable = true)
    private void ecliptic$isRainingAt(BlockPos p_175727_1_, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerWorld ) {
            cir.setReturnValue(WeatherManager.isRainingAt((ServerWorld)(Object) this, p_175727_1_));
        }
    }


    @Inject(at = {@At("HEAD")}, method = {"isThundering"}, cancellable = true)
    private void ecliptic$isThundering(CallbackInfoReturnable<Boolean> cir) throws IllegalAccessException {
        if ((Object) this instanceof ServerWorld ) {
            if (CommonConfig.Debug.debugMode.get()){
                throw new IllegalAccessException("Use isThunderingAt to check if rain");
            }
            cir.setReturnValue(WeatherManager.isThunderEverywhere((ServerWorld)(Object) this));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getThunderLevel"}, cancellable = true)
    private void ecliptic$getThunderLevel(float p_46723_, CallbackInfoReturnable<Float> cir) throws IllegalAccessException {
        if ((Object) this instanceof ServerWorld ) {
            if (CommonConfig.Debug.debugMode.get()){
                throw new IllegalAccessException("Shouldn't call getThunderLevel now");
            }
            cir.setReturnValue(WeatherManager.getMinThunderLevel( (ServerWorld)(Object) this,p_46723_));
        }
    }
}
