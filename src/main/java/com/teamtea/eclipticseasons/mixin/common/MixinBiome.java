package com.teamtea.eclipticseasons.mixin.common;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
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
    public void eclipticseasons$getPrecipitationAt(CallbackInfoReturnable<Biome.RainType> cir) {
        cir.setReturnValue(WeatherManager.getPrecipitationAt((Biome) (Object) this, BlockPos.ZERO));
    }


    @ModifyExpressionValue(at = {@At(value = "INVOKE",
            target = "Lnet/minecraft/world/biome/Biome;getTemperature(Lnet/minecraft/util/math/BlockPos;)F")},
            method = {"shouldSnow", "shouldFreeze(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Z)Z"})
    public float eclipticseasons$fixTempWithoutSeason(float original, @Local(argsOnly = true) IWorldReader iWorldReader, @Local(argsOnly = true) BlockPos pos) {
        if (iWorldReader instanceof World) {
            if (iWorldReader instanceof ServerWorld) {
                if (CommonConfig.Temperature.snowDown.get())
                    return WeatherManager.getSnowStatus((ServerWorld) iWorldReader, (Biome) (Object) this, pos) != WeatherManager.SnowRenderStatus.SNOW ? 1f : 0f;
            }
            original -= EclipticUtil.getNowSolarTerm((World) iWorldReader).getTemperatureChange();
        }
        return original;
    }


    @Inject(at = {@At("HEAD")}, method = {"getBaseTemperature"}, cancellable = true)
    public void eclipticseasons$getBaseTemperature(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(BiomeClimateManager.agent$GetBaseTemperature((Biome) (Object) this));
    }


}
