package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.compat.voxy.IVoxyModelController;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.client.core.model.bakery.ModelTextureBakery;
import me.cortex.voxy.client.core.model.bakery.ReuseVertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelTextureBakery.class})
public abstract class MixinModelTextureBakery implements IVoxyModelController {

    @Shadow(remap = false)
    @Final
    private ReuseVertexConsumer vc;

    //@Shadow(remap = false)
    //public static int getMetaFromLayer(RenderType layer) {
    //    return 0;
    //}

    @Inject(
            remap = false,
            method = "bakeBlockModel",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$bakeBlockModel_pre(BlockState state, RenderType layer, CallbackInfo ci, @Share("snowy_model") LocalRef<BakedModel> modelLocalRef) {
        if (isSnowyBlock())
            VoxyTool.renderToStream(state, layer, vc);
    }


    @Unique
    boolean eclipticseasons$snowyBlock = false;

    @Override
    public void setSnowyBlock(boolean snowyBlock) {
        this.eclipticseasons$snowyBlock = snowyBlock;
    }

    @Override
    public boolean isSnowyBlock() {
        return eclipticseasons$snowyBlock;
    }
}
