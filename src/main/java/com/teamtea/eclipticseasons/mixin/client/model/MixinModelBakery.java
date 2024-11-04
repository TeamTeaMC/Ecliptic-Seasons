package com.teamtea.eclipticseasons.mixin.client.model;


import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.BiFunction;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
    @Shadow
    private @Final Map<ResourceLocation, BakedModel> bakedTopLevelModels;
    @Shadow
    private @Final Map<ResourceLocation, UnbakedModel> topLevelModels;


    @Inject(method = "bakeModels", at = @At("RETURN"))
    public void ecliptic$bakeModels(BiFunction<ResourceLocation, Material, TextureAtlasSprite> atlasSpriteGetter, CallbackInfo ci) {
        topLevelModels.forEach(((location, unbakedModel) -> {
            // 这里检查需要烘焙哪些面
            if (unbakedModel instanceof BlockModel blockModel ) {
                // EclipticSeasons.logger(blockModel.getRootModel(),location);

            }
        }));
    }
}
