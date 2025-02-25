package com.teamtea.eclipticseasons.mixin.common;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Biome.class})
public abstract class MixinBiome {
    @Shadow
    @Deprecated
    public abstract float getTemperature(BlockPos p_47506_);


    @Inject(at = {@At("HEAD")}, method = {"getPrecipitation"}, cancellable = true)
    public void eclipticseasons$getPrecipitationAt(CallbackInfoReturnable<Biome.Precipitation> cir) {
        cir.setReturnValue(WeatherManager.getPrecipitationAt((Biome) (Object) this, BlockPos.ZERO));
    }

    @ModifyExpressionValue(at = {@At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;getTemperature(Lnet/minecraft/core/BlockPos;)F")},
            method = {"warmEnoughToRain"})
    public float eclipticseasons$warmEnoughToRain(float original) {
        Level level = WeatherManager.fetchLevelIfNull(null);
        if (level != null)
            original -= EclipticUtil.getNowSolarTerm(level).getTemperatureChange();
        return original;
    }

    @WrapOperation(at = {@At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z")},
            method = {"shouldSnow", "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z"})
    public boolean eclipticseasons$fixTempWithoutSeason(Biome instance, BlockPos pPos, Operation<Boolean> original, @Local(argsOnly = true) LevelReader levelReader) {
        if (levelReader instanceof Level) {
            if(levelReader instanceof ServerLevel){
                if (CommonConfig.Temperature.snowDown.get())
                    return WeatherManager.getSnowStatus((ServerLevel) levelReader, instance, pPos) != WeatherManager.SnowRenderStatus.SNOW;
            }
            return this.getTemperature(pPos) - EclipticUtil.getNowSolarTerm((Level) levelReader).getTemperatureChange() >= 0.15F;
        }
        return original.call(instance, pPos);
    }


    @Inject(at = {@At("HEAD")}, method = {"getBaseTemperature"}, cancellable = true)
    public void eclipticseasons$getBaseTemperature(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(BiomeClimateManager.agent$GetBaseTemperature((Biome) (Object) this));
    }


}
