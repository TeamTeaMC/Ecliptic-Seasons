package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.compat.voxy.VoxyClientTool;
import com.teamtea.eclipticseasons.compat.voxy.helper.IVoxyModelController;
import me.cortex.voxy.client.core.model.bakery.ReuseVertexConsumer;
import me.cortex.voxy.client.core.model.bakery.SoftwareModelTextureBakery;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SoftwareModelTextureBakery.class})
public abstract class MixinModelTextureBakery implements IVoxyModelController {

    @Shadow
    @Final
    private ReuseVertexConsumer translucentVC;

    @Shadow
    @Final
    private ReuseVertexConsumer opaqueVC;

    @Inject(
            remap = false,
            method = "bakeBlockModel",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$bakeBlockModel_pre(BlockState state, CallbackInfo ci) {

        if (isSnowyBlock())
            VoxyClientTool.renderToStream(state,translucentVC,opaqueVC);
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
