package com.teamtea.eclipticseasons.mixin.common;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
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

    @Shadow
    @Final
    private Biome.ClimateSettings climateSettings;

    // 阻止非寒冷群系结冰
    @Inject(at = {@At("HEAD")}, method = {"shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"}, cancellable = true)
    public void ecliptic$shouldFreeze(LevelReader p_47520_, BlockPos p_47521_, CallbackInfoReturnable<Boolean> cir) {
        // if (p_47520_ instanceof ServerLevel level) {
        //     // 目前设置为不生成雪，根据香草判断一下了
        //     if ((this.getTemperature(p_47521_) >= 0.15F))
        //         cir.setReturnValue(false);
        // }
    }


    @Inject(at = {@At("HEAD")}, method = {"getPrecipitation"}, cancellable = true)
    public void ecliptic$getPrecipitationAt(CallbackInfoReturnable<Biome.Precipitation> cir) {
        cir.setReturnValue(WeatherManager.getPrecipitationAt((Biome) (Object) this, BlockPos.ZERO));
    }

    @ModifyExpressionValue(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getTemperature(Lnet/minecraft/core/BlockPos;)F")}, method = {"warmEnoughToRain"})
    public float ecliptic$warmEnoughToRain(float original) {
        return original - SimpleUtil.getNowSolarTerm(WeatherManager.fetchLevelIfNull(null)).getTemperatureChange();
    }

    @ModifyExpressionValue(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canSurvive(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")}, method = {"shouldSnow"})
    public boolean ecliptic$shouldSnow(boolean original) {
        return original;
    }

    @Inject(at = {@At("HEAD")}, method = {"getBaseTemperature"}, cancellable = true)
    public void ecliptic$getBaseTemperature(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(BiomeClimateManager.agent$GetBaseTemperature((Biome) (Object) this));
    }


}
