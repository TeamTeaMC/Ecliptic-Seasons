package com.teamtea.eclipticseasons.mixin.client.model;


import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Mixin(value = ModelBakery.class,priority = 500)
public abstract class MixinModelBakery {
    @Shadow
    private @Final Map<ResourceLocation, BakedModel> bakedTopLevelModels;
    @Shadow
    private @Final Map<ResourceLocation, UnbakedModel> topLevelModels;


    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow
    public abstract UnbakedModel getModel(ResourceLocation p_119342_);

    @Deprecated(forRemoval = true)
    @Inject(method = "bakeModels", at = @At("RETURN"))
    public void eclipticseasons$bakeModels(BiFunction<ResourceLocation, Material, TextureAtlasSprite> atlasSpriteGetter, CallbackInfo ci) {
        if (true) {
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


    @Inject(at = {@At(value = "RETURN")}, method = {"<init>"})
    private void eclipticseasons$register_snowy_models(BlockColors pBlockColors,
                                                       ProfilerFiller pProfilerFiller,
                                                       Map<ResourceLocation, BlockModel> pModelResources,
                                                       Map<ResourceLocation, List<ModelBakery.LoadedJson>> pBlockStateResources,
                                                       CallbackInfo ci) {
        // sorry we can not inject in some place better
        Map<ResourceLocation, UnbakedModel> cache = new HashMap<>();
        ModelManager.registerExtraSnowyModels(cache::put);
        for (UnbakedModel value : cache.values()) {
            value.resolveParents(this::getModel);
        }
        cache.forEach(
                (pLocation, unbakedmodel) -> {
                    // this.getModel(pLocation);
                    this.unbakedCache.put(pLocation, unbakedmodel);
                    this.topLevelModels.put(pLocation, unbakedmodel);
                }
        );

        cache.clear();
    }
}
