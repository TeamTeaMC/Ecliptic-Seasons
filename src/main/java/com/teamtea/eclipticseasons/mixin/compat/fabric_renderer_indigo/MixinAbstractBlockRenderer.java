package com.teamtea.eclipticseasons.mixin.compat.fabric_renderer_indigo;


import com.google.common.annotations.Beta;
import com.teamtea.eclipticseasons.compat.vanilla.ExtendBlockView;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Beta
@Mixin({AbstractBlockRenderContext.class})
public abstract class MixinAbstractBlockRenderer {

    @Shadow(remap = false)
    @Final
    protected BlockRenderInfo blockInfo;

    //@Inject(
    //        remap = false,
    //        method = "renderQuad",
    //        at = @At(value = "INVOKE",
    //                // shift = At.Shift.AFTER,
    //                target = "Lnet/fabricmc/fabric/impl/client/indigo/renderer/mesh/MutableQuadViewImpl;material()Lnet/fabricmc/fabric/impl/client/indigo/renderer/material/RenderMaterialImpl;"),
    //        cancellable = true)
    //private void eclipticseasons$renderQuad_cache(
    //        MutableQuadViewImpl quad, CallbackInfo ci) {
    //    if (blockInfo.blockView instanceof ExtendBlockView extendBlockView) {
    //        if(extendBlockView.getShouldCollectBakeQuads()) {
    //            SpriteFinder spriteFinder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS));
    //            extendBlockView
    //                    .addCacheBakeQuad(quad.toBakedQuad(spriteFinder.find(quad)));
    //        }
    //    }
    //}


}
