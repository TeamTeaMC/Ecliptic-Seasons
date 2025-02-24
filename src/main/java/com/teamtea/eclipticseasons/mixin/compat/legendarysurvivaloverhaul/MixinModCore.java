package com.teamtea.eclipticseasons.mixin.compat.legendarysurvivaloverhaul;


import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_ESUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sfiomn.legendarysurvivaloverhaul.LegendarySurvivalOverhaul;

@Mixin({LegendarySurvivalOverhaul.class})
public abstract class MixinModCore {

    @Inject(
            remap = false,
            method = "serverStarted",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$lambda$onLoadComplete$1(CallbackInfo ci) {
        LSO_ESUtil.initAverageTemperatures();
    }

}
