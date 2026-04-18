package com.teamtea.eclipticseasons.mixin.common.level;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.misc.IBiomeWeatherProvider;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(Level.class)
public class MixinLevel implements IBiomeWeatherProvider {


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
                cir.setReturnValue(WeatherManager.isEffectiveRaining(serverLevel));
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
                cir.setReturnValue(WeatherManager.getAverageRainLevel(serverLevel, p_46723_));
            }
        }
    }

    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")}, method = {"precipitationAt"})
    private boolean eclipticseasons$precipitationAt_skipRainCheck(Level instance, Operation<Boolean> original) {
        return (EclipticUtil.hasLocalWeather(instance)
                && instance instanceof ServerLevel) || original.call(instance);
    }

    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")}, method = {"precipitationAt"}, cancellable = true)
    private void eclipticseasons$precipitationAt_endBiomeCheck(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if ((Object) this instanceof ServerLevel level) {
            if (EclipticUtil.hasLocalWeather(level)) {
                cir.setReturnValue(WeatherManager.getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos));
            }
        }
    }

    /**
     * 当使用原版天气时需要判断
     **/
    @WrapOperation(
            method = "precipitationAt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation eclipticseasons$isRainingAt_getPrecipitationAt(Biome instance, BlockPos pos, int seaLevel, Operation<Biome.Precipitation> original) {
        return VanillaWeather.handlePrecipitationAt((Level) (Object) this, instance, pos);
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
                cir.setReturnValue(WeatherManager.isEffectiveThundering(serverLevel));
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
                cir.setReturnValue(WeatherManager.getAverageThunderLevel(serverLevel, p_46723_));
            }
        }
    }

    @Unique
    private ArrayList<WeatherManager.BiomeWeather> eclipticseasons$biomeWeathers;

    @Override
    public ArrayList<WeatherManager.BiomeWeather> es$get() {
        return this.eclipticseasons$biomeWeathers;
    }

    @Override
    public void es$set(ArrayList<WeatherManager.BiomeWeather> biomeWeathers) {
        this.eclipticseasons$biomeWeathers = biomeWeathers;
    }


    @Unique
    float es$averageRainLevel;
    @Unique
    float es$averageThunderLevel;

    @Override
    public float es$getAverageRainLevel(float delta) {
        return es$averageRainLevel;
    }

    @Override
    public float es$getAverageThunderLevel(float delta) {
        return es$averageThunderLevel;
    }

    @Unique
    Holder<Biome> es$coreBiome;

    @Override
    public Holder<Biome> es$getCoreBiome() {
        return es$coreBiome;
    }

    @Override
    public void es$setCoreBiome(Holder<Biome> biomeHolder) {
        this.es$coreBiome = biomeHolder;
    }

    @Override
    public void es$setAverageRainLevel(float value) {
        this.es$averageRainLevel = value;
    }

    @Override
    public void es$setAverageThunderLevel(float value) {
        this.es$averageThunderLevel = value;
    }
}
