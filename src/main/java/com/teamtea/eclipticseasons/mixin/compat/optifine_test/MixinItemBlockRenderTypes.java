package com.teamtea.eclipticseasons.mixin.compat.optifine_test;


import org.spongepowered.asm.mixin.Mixin;

@Mixin({net.minecraft.client.renderer.ItemBlockRenderTypes.class})
public abstract class MixinItemBlockRenderTypes {

    // ctx.world().world.getBlockState(ctx.pos)
    // @Inject(at = {@At("HEAD")}, method = {"getRenderLayers"}, cancellable = true, remap = false)
    // private static void eclipticseasons$getRenderLayers(BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
    //     if (ModelManager.shouldCutoutMipped(state)) {
    //         cir.setReturnValue(ChunkRenderTypeSet.of(RenderType.cutoutMipped()));
    //     }
    //
    // }


}
