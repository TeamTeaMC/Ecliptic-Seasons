package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({net.minecraft.client.renderer.ItemBlockRenderTypes.class})
public abstract class MixinItemBlockRenderTypes {

    // ctx.world().world.getBlockState(ctx.pos)
    @Inject(at = {@At("HEAD")}, method = {"getRenderLayers"}, cancellable = true, remap = false)
    private static void ecliptic$getRenderLayers(BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        if (ModelManager.shouldCutoutMipped(state)) {
            cir.setReturnValue(ChunkRenderTypeSet.of(RenderType.cutoutMipped()));
        }

    }


}
