package com.teamtea.eclipticseasons.mixin.client.model.material;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import com.teamtea.eclipticseasons.client.model.block.quad.IExtendedMaterial;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureSlots.class)
public abstract class MixinTextureSlots {

    @ModifyExpressionValue(
            method = "parseEntry",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"
            )
    )
    private static DataResult<Material> eclipticseasons$parseForceCutout(
            DataResult<Material> original, @Local(argsOnly = true) JsonElement input
    ) {
        if (input instanceof JsonObject object
                && GsonHelper.getAsBoolean(object, "eclipticseasons:force_cutout", false)) {
            if (original.isSuccess()) {
                return original.map(material -> {
                    if ((Object) original.getOrThrow() instanceof IExtendedMaterial extendedMaterial)
                        extendedMaterial.eclipticseasons$setForceCutout(true);
                    return material;
                });
            }
        }
        return original;
    }
}
