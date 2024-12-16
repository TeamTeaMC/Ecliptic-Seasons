package com.teamtea.eclipticseasons.mixin.compat.fabric_renderer_indigo;


import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.compat.vanilla.ExtendBlockView;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractBlockRenderContext.class})
public abstract class MixinAbstractBlockRenderer {

    @Shadow
    @Final
    protected BlockRenderInfo blockInfo;

    @Inject(
            remap = false,
            method = "renderQuad",
            at = @At(value = "TAIL"
                    // shift = At.Shift.AFTER,
            )
    )
    private void eclipticseasons$renderQuad(
            MutableQuadViewImpl quad, CallbackInfo ci) {
        if (blockInfo.blockView instanceof ExtendBlockView extendBlockView) {
            extendBlockView
                    .addCacheBakeQuad(quad.toBakedQuad(ModelManager.getSprite(ModelManager.snow)));

        }
    }


}
