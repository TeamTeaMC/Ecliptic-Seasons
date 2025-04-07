package com.teamtea.eclipticseasons.mixin.client.model;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
    @Shadow
    private @Final Map<ResourceLocation, BakedModel> bakedTopLevelModels;
    @Shadow
    private @Final Map<ResourceLocation, UnbakedModel> topLevelModels;


    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Inject(method = "bakeModels", at = @At("RETURN"))
    public void eclipticseasons$bakeModels(BiFunction<ResourceLocation, Material, TextureAtlasSprite> atlasSpriteGetter, CallbackInfo ci) {
        if (true)
        {
            return;
        }
        // List<Block> blocks = BuiltInRegistries.BLOCK.stream().filter(block -> block.defaultBlockState().isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)).toList();
        List<Block> blocks = BuiltInRegistries.BLOCK.stream().toList();
        for (Block block : blocks) {
            for (BlockState possibleState : block.getStateDefinition().getPossibleStates()) {
                UnbakedModel unbakedModel = topLevelModels.getOrDefault(BlockModelShaper.stateToModelLocation(possibleState), null);
                if (unbakedModel != null) {
                    if (unbakedModel instanceof MultiVariant multiVariant) {
                        for (Variant variant : multiVariant.getVariants()) {
                            UnbakedModel unbakedModelVariant = unbakedCache.get(variant.getModelLocation());
                            if (unbakedModelVariant instanceof BlockModel blockModel) {
                                blockModel.textureMap.forEach(
                                        (s, materialStringEither) -> {
                                            materialStringEither.left().ifPresent(
                                                    material -> {
                                                        ModelManager.blocksCache.put(material.texture(), null);
                                                    }
                                            );
                                        }
                                );
                            }
                        }
                    }
                }
            }
        }

    }
}
