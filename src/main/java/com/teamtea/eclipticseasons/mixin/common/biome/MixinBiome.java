package com.teamtea.eclipticseasons.mixin.common.biome;


import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
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

    @Inject(at = {@At("HEAD")}, method = {"getPrecipitationAt"}, cancellable = true)
    public void eclipticseasons$getPrecipitationAt(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (EclipticUtil.useSolarWeather())
            cir.setReturnValue(WeatherManager.getPrecipitationAt((Biome) (Object) this, pos));
        else {
            cir.setReturnValue(VanillaWeather.handlePrecipitationAt((Biome) (Object) this, pos));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"hasPrecipitation"}, cancellable = true)
    public void eclipticseasons$hasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
        if (EclipticUtil.useSolarWeather())
            cir.setReturnValue(BiomeClimateManager.agent$hasPrecipitation((Biome) (Object) this));
        else {
            if (BiomeClimateManager.getTag((Biome) (Object) this).equals(ClimateTypeBiomeTags.MONSOONAL)) {
                cir.setReturnValue(VanillaWeather.hasMonsoonalPrecipitation((Biome) (Object) this));
            }
        }
    }

    //
    // @Inject(at = {@At("HEAD")}, method = {"getBaseTemperature"}, cancellable = true)
    // public void eclipticseasons$getBaseTemperature(CallbackInfoReturnable<Float> cir) {
    //     cir.setReturnValue(BiomeClimateManager.agent$GetBaseTemperature((Biome) (Object) this));
    // }
    //
    //

    //
    // @ModifyExpressionValue(at = {@At(value = "INVOKE",
    //         target = "Lnet/minecraft/world/level/biome/Biome;getTemperature(Lnet/minecraft/core/BlockPos;)F")},
    //         method = {"warmEnoughToRain"})
    // public float eclipticseasons$warmEnoughToRain(float original) {
    //     Level level = WeatherManager.fetchLevelIfNull(null);
    //     if (level != null)
    //         original = BiomeClimateManager.fixTemp(level, (Biome) (Object) this, original);
    //     return original;
    // }
    //
    // @WrapOperation(at = {@At(value = "INVOKE",
    //         target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z")},
    //         method = {"shouldSnow", "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z"})
    // public boolean eclipticseasons$fixTempWithoutSeason(Biome instance, BlockPos pPos, Operation<Boolean> original, @Local(argsOnly = true) LevelReader levelReader) {
    //     Level level = null;
    //     if (levelReader instanceof WorldGenLevel worldGenLevel) {
    //         level = worldGenLevel.getLevel();
    //     } else if (levelReader instanceof Level level1) {
    //         level = level1;
    //     } else {
    //         level = WeatherManager.fetchLevelIfNull(null);
    //     }
    //     if (level != null) {
    //         return BiomeClimateManager.fixTemp(level, (Biome) (Object) this, getTemperature(pPos)) >= BiomeClimateManager.SNOW_LEVEL;
    //     }
    //     return original.call(instance, pPos);
    // }
}
