package com.teamtea.eclipticseasons.mixin.compat.optifine;


import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin({net.minecraft.client.renderer.ItemBlockRenderTypes.class})
public abstract class MixinItemBlockRenderTypes {

    @Shadow
    private static boolean renderCutout;

    @Shadow
    @Final
    private static Map<Block, ChunkRenderTypeSet> BLOCK_RENDER_TYPES;

    // ctx.world().world.getBlockState(ctx.pos)
    @Inject(at = {@At("HEAD")}, method = {"getRenderLayers"}, cancellable = true, remap = false)
    private static void ecliptic$getRenderLayers(BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        if (renderCutout && BLOCK_RENDER_TYPES.get(state.getBlock()).contains(RenderType.SOLID))
            if (ModelManager.shouldCutoutMipped(state)) {
                cir.setReturnValue(ChunkRenderTypeSet.of(RenderType.cutoutMipped()));
            }
    }

    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "getRenderLayers",
    //         at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;")
    // )
    // private static V ecliptic$getRenderLayersM(V original, @Local BlockState blockState) {
    //     if (original instanceof ChunkRenderTypeSet)
    //         if (original.contains(RenderType.SOLID)) {
    //             if (ModelManager.shouldCutoutMipped(blockState))
    //                 return ChunkRenderTypeSet.of(RenderType.cutoutMipped());
    //         }
    //     // return ModelManager.appendOverlay(blockAndTintGetter, state, pos, direction, randomSource, seed, original);
    //     return original;
    // }

}
