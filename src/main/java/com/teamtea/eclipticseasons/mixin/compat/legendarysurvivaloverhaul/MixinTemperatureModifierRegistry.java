package com.teamtea.eclipticseasons.mixin.compat.legendarysurvivaloverhaul;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_ESModifier;
import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_ESUtil;
import net.minecraftforge.registries.DeferredRegister;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sfiomn.legendarysurvivaloverhaul.api.temperature.ModifierBase;
import sfiomn.legendarysurvivaloverhaul.registry.TemperatureModifierRegistry;

@Mixin({TemperatureModifierRegistry.class})
public abstract class MixinTemperatureModifierRegistry {

    @Shadow(remap = false) @Final public static DeferredRegister<ModifierBase> MODIFIERS;

    @Inject(
            remap = false,
            method = "<clinit>",
            at = @At(value = "TAIL")
    )
    private static void ecliptic$lambda$onLoadComplete$1(CallbackInfo ci) {
        LSO_ESUtil.ecliptic$EclipticSeasons = MODIFIERS.register("integration/" + EclipticSeasons.MODID, LSO_ESModifier::new);
    }
}
