package com.teamtea.eclipticseasons.mixin.data;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Set;

@Mixin(targets = "net.minecraft.core.RegistrySetBuilder$BuildState")
public abstract class RegistrySetBuilderMixin {

    @ModifyExpressionValue(method = {"reportRemainingUnreferencedValues"},
            at = {@At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;")})
    private Set<ResourceKey<Object>> eclipticseasons$buildPatch$fixError(Set<ResourceKey<Object>> original) {
        // because we not generate feature by dg, so it is not registered.
        // if ((Object) this instanceof DatapackRegistryGenerator)
        //if (original.contains(Biomes.PLAINS)
        //        || original.contains(Biomes.SUNFLOWER_PLAINS)
        //        || original.contains(Biomes.THE_VOID)
        //        || original.contains(VegetationPlacements.BAMBOO)
        if ( "true".equals(System.getProperty("eclipticseasons.runs.runData"))
        ) {
            //HashSet<ResourceKey<Object>> resourceKeys = new HashSet<>(original);
            //resourceKeys.remove(Biomes.PLAINS);
            //resourceKeys.remove(Biomes.SUNFLOWER_PLAINS);
            //resourceKeys.remove(Biomes.THE_VOID);
            //resourceKeys.remove(VegetationPlacements.BAMBOO);
            return new HashSet<>();
        }
        return original;
    }
}