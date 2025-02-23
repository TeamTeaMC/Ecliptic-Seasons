package com.teamtea.eclipticseasons.mixin.compat.hauntedharvest;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.compat.CompatModule;
import net.mehvahdjukaar.hauntedharvest.HauntedHarvest;
import net.mehvahdjukaar.hauntedharvest.SeasonManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SeasonManager.class})
public abstract class MixinHauntedHarvest {

    @Inject(at = {@At(value = "HEAD")},
            method = {"isHalloween"},
            remap = false, cancellable = true)
    private void eclipticseasons$isHalloween(Level level, CallbackInfoReturnable<Boolean> cir) {
        if (CompatModule.CommonConfig.hauntedharvest_enable.get())
            cir.setReturnValue(CompatModule.CommonConfig.hauntedharvest_halloween_time.get().contains(EclipticSeasonsApi.getInstance().getSolarTerm(level)));
    }

    @Inject(at = {@At(value = "HEAD")},
            method = {"shouldWearCustomPumpkin"},
            remap = false, cancellable = true)
    private void eclipticseasons$shouldWearCustomPumpkin(Level level, CallbackInfoReturnable<Boolean> cir) {
        if (CompatModule.CommonConfig.hauntedharvest_enable.get())
            cir.setReturnValue(CompatModule.CommonConfig.hauntedharvest_mobs_wear_pumpkins_time.get().contains(EclipticSeasonsApi.getInstance().getSolarTerm(level)));
    }

}
