package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.model.SeparatePerspectiveModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderTypeLookup.class})
public abstract class MixinItemBlockRenderTypes {

    // ctx.world().world.getBlockState(ctx.pos)
    @Inject(at = {@At("HEAD")}, method = {"canRenderInLayer(Lnet/minecraft/block/BlockState;Lnet/minecraft/client/renderer/RenderType;)Z"}, cancellable = true, remap = false)
    private static void eclipticseasons$getRenderLayers(BlockState state, RenderType type, CallbackInfoReturnable<Boolean> cir) {
        if (ModelManager.shouldCutoutMipped(state)) {
            if(type==RenderType.solid())
                cir.setReturnValue(false);
            if(type==RenderType.cutoutMipped())
                cir.setReturnValue(true);
            // cir.setReturnValue(ChunkRenderTypeSet.of(RenderType.cutoutMipped()));
        }

    }


}
