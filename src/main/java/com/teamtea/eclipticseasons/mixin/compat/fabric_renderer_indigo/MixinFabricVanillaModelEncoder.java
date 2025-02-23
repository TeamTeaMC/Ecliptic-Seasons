package com.teamtea.eclipticseasons.mixin.compat.fabric_renderer_indigo;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.TerrainRenderContextLevelGetter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.renderer.VanillaModelEncoder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Supplier;

@Mixin({VanillaModelEncoder.class})
public abstract class MixinFabricVanillaModelEncoder {

    @ModifyExpressionValue(
            remap = false,
            method = "emitBlockQuads",
            at = @At(value = "INVOKE",  target = "Lnet/minecraft/client/resources/model/BakedModel;getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;")
    )
    private static List<BakedQuad> eclipticseasons$emitBlockQuads_getQuads_cancel_top(List<BakedQuad> original,
                                                                               @Local(argsOnly = true) BakedModel bakedModel,
                                                                               @Local(argsOnly = true) RenderContext renderContext,
                                                                               @Local(argsOnly = true) Supplier<RandomSource> randomSourceSupplier,
                                                                               @Local(argsOnly = true) BlockState state,
                                                                               @Local Direction direction) {
        if (renderContext instanceof TerrainRenderContextLevelGetter terrainRenderContextLevelGetter) {
            BlockAndTintGetter blockAndTintGetter = terrainRenderContextLevelGetter.eclipticseasons$get(); {
                return ModelManager.cancelTop(bakedModel, blockAndTintGetter, state, terrainRenderContextLevelGetter.eclipticseasons$getPos(), direction, randomSourceSupplier.get(), state.getSeed(terrainRenderContextLevelGetter.eclipticseasons$getPos()), original);
                }
        }
        return original;
    }


}
