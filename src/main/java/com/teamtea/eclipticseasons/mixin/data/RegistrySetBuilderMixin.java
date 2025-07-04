package com.teamtea.eclipticseasons.mixin.data;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.data.datapack.DatapackRegistryGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(targets = "net.minecraft.core.RegistrySetBuilder$BuildState")
public abstract class RegistrySetBuilderMixin {

    @ModifyExpressionValue(method = {"reportRemainingUnreferencedValues"},
            at = {@At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;")})
    private Set<ResourceKey<Object>> eclipticseasons$buildPatch$fixError(Set<ResourceKey<Object>> original) {
        // because we not generate feature by dg, so it is not registered.
        if ((Object) this instanceof DatapackRegistryGenerator)
            if (original.equals(Biomes.PLAINS)) {
                HashSet<ResourceKey<Object>> resourceKeys = new HashSet<>(original);
                resourceKeys.remove(Biomes.PLAINS);
                return resourceKeys;
            }
        return original;
    }
}