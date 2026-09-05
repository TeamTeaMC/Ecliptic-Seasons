package com.teamtea.eclipticseasons.mixin.client.model.material;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.model.block.quad.IExtendedMaterial;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MaterialBaker.class)
public abstract class MixinMaterialBaker {
    @ModifyReturnValue(
            method = "resolveSlot",
            at = @At(value = "RETURN"))
    private static Material.Baked eclipticseasons$parseForceCutout(
            Material.Baked original, @Local Material value
    ) {
        if (original != null && (Object) value instanceof IExtendedMaterial extendedMaterial
                && extendedMaterial.eclipticseasons$forceCutout()
                && (Object) original instanceof IExtendedMaterial extendedMaterialImpl)
            extendedMaterialImpl.eclipticseasons$setForceCutout(extendedMaterial.eclipticseasons$forceCutout());
        return original;
    }
}
