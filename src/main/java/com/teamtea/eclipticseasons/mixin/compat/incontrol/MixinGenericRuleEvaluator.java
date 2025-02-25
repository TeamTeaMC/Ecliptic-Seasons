package com.teamtea.eclipticseasons.mixin.compat.incontrol;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import mcjty.incontrol.rules.support.GenericRuleEvaluator;
import mcjty.incontrol.tools.rules.IEventQuery;
import mcjty.incontrol.tools.varia.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BiFunction;

@Mixin({GenericRuleEvaluator.class})
public abstract class MixinGenericRuleEvaluator {

    @Shadow(remap = false) @Final private List<BiFunction<Object, IEventQuery, Boolean>> checks;

    // @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")},
    //         method = {"lambda$addWeatherCheck$44"},
    //         remap = false)
    // private static boolean eclipticseasons$lambda$addWeatherCheck$44(Level instance, Operation<Boolean> original, @Local(argsOnly = true) Object event, @Local(argsOnly = true) IEventQuery query) {
    //     BlockPos pos = query.getPos(event);
    //     return EclipticSeasonsApi.getInstance().isRainOrSnowAt(instance,pos);
    // }
    //
    // @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isThundering()Z")},
    //         method = {"lambda$addWeatherCheck$45"},
    //         remap = false)
    // private static boolean eclipticseasons$lambda$addWeatherCheck$45(Level instance, Operation<Boolean> original, @Local(argsOnly = true) Object event, @Local(argsOnly = true) IEventQuery query) {
    //     BlockPos pos = query.getPos(event);
    //     return EclipticSeasonsApi.getInstance().isRainOrSnowAt(instance,pos);
    // }

    @Inject(at = {@At(value = "HEAD")},
            method = {"addSpringCheck"},
            remap = false)
    private void eclipticseasons$addSpringCheck(Boolean s, CallbackInfo ci) {
        this.checks.add((event, query) -> Season.SPRING == EclipticSeasonsApi.getInstance().getSolarTerm(Tools.getServerWorld(query.getWorld(event))).getSeason());
    }

    @Inject(at = {@At(value = "HEAD")},
            method = {"addSummerCheck"},
            remap = false)
    private void eclipticseasons$addSummerCheck(Boolean s, CallbackInfo ci) {
        this.checks.add((event, query) -> Season.SUMMER == EclipticSeasonsApi.getInstance().getSolarTerm(Tools.getServerWorld(query.getWorld(event))).getSeason());
    }

    @Inject(at = {@At(value = "HEAD")},
            method = {"addAutumnCheck"},
            remap = false)
    private void eclipticseasons$addAutumnCheck(Boolean s, CallbackInfo ci) {
        this.checks.add((event, query) -> Season.AUTUMN == EclipticSeasonsApi.getInstance().getSolarTerm(Tools.getServerWorld(query.getWorld(event))).getSeason());
    }

    @Inject(at = {@At(value = "HEAD")},
            method = {"addWinterCheck"},
            remap = false)
    private void eclipticseasons$addWinterCheck(Boolean s, CallbackInfo ci) {
        this.checks.add((event, query) -> Season.WINTER == EclipticSeasonsApi.getInstance().getSolarTerm(Tools.getServerWorld(query.getWorld(event))).getSeason());
    }

}
