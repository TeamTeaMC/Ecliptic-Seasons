package com.teamtea.eclipticseasons.mixin.common;


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
    public void ecliptic$getPrecipitationAt(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (EclipticUtil.useSolarWeather())
            cir.setReturnValue(WeatherManager.getPrecipitationAt((Biome) (Object) this, pos));
        else {
            cir.setReturnValue(VanillaWeather.handlePrecipitationAt((Biome) (Object) this, pos));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getBaseTemperature"}, cancellable = true)
    public void ecliptic$getBaseTemperature(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(BiomeClimateManager.agent$GetBaseTemperature((Biome) (Object) this));
    }


    @Inject(at = {@At("HEAD")}, method = {"hasPrecipitation"}, cancellable = true)
    public void ecliptic$hasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
        if (EclipticUtil.useSolarWeather())
            cir.setReturnValue(BiomeClimateManager.agent$hasPrecipitation((Biome) (Object) this));
        else {
            if (BiomeClimateManager.getTag((Biome) (Object) this).equals(ClimateTypeBiomeTags.MONSOONAL)) {
                cir.setReturnValue(BiomeClimateManager.agent$hasPrecipitation((Biome) (Object) this));
            }
        }
    }
}
