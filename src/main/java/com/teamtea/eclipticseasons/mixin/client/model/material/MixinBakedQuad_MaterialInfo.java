package com.teamtea.eclipticseasons.mixin.client.model.material;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Transparency;
import com.teamtea.eclipticseasons.client.model.block.quad.IExtendedMaterial;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BakedQuad.MaterialInfo.class)
public abstract class MixinBakedQuad_MaterialInfo implements IExtendedMaterial {
    @ModifyVariable(
            method = "of(Lnet/minecraft/client/resources/model/sprite/Material$Baked;Lcom/mojang/blaze3d/platform/Transparency;IZIZ)Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;",
            at = @At(
                    value = "HEAD"
            ),
            argsOnly = true)
    private static Transparency eclipticseasons$parseForceCutout(
            Transparency transparency,
            @Local(argsOnly = true) Material.Baked value
    ) {
        if ((Object) value instanceof IExtendedMaterial extendedMaterial
                && extendedMaterial.eclipticseasons$forceCutout())
            transparency = transparency.hasTranslucent() ? Transparency.TRANSPARENT_AND_TRANSLUCENT : Transparency.TRANSPARENT;
        return transparency;
    }
}
