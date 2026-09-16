package com.teamtea.eclipticseasons.mixin.data;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.List;

@Mixin(targets = "net.minecraft.core.RegistrySetBuilder$BootstrappedRegistryState")
public abstract class RegistrySetBuilderMixin {

    @WrapOperation(method = {"lambda$errorOnMissingHolders$0"},
            at = {@At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")})
    private static <E> boolean eclipticseasons$buildPatch$fixError(List instance, E e, Operation<Boolean> original) {
        if ("true".equals(System.getProperty("eclipticseasons.runs.runData"))) {
            return true;
        }
        return original.call(instance, e);
    }

    @WrapOperation(method = {"lambda$validatePatchHolders$0"},
            at = {@At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")})
    private static <E> boolean eclipticseasons$buildPatch$fixError2(List instance, E e, Operation<Boolean> original) {
        if ("true".equals(System.getProperty("eclipticseasons.runs.runData"))) {
            return true;
        }
        return original.call(instance, e);
    }
}