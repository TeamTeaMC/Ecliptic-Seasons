package com.teamtea.eclipticseasons.mixin.compat.embeddium;

import com.teamtea.eclipticseasons.common.mixin.injector.DirectInject;
import com.teamtea.eclipticseasons.compat.embeddium.EmbeddiumBlenderColorProvider;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.embeddedt.embeddium.api.render.chunk.EmbeddiumBlockAndTintGetter;
import org.embeddedt.embeddium.impl.model.quad.ModelQuadView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "org.embeddedt.embeddium.impl.model.color.DefaultColorProviders$VanillaAdapter")
public abstract class Mixin_ColorSource implements EmbeddiumBlenderColorProvider {


    @Shadow
    @Final
    private BlockColor provider;

    @DirectInject(
            remap = false,
            method = "getColors(Lorg/embeddedt/embeddium/api/render/chunk/EmbeddiumBlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lorg/embeddedt/embeddium/impl/model/quad/ModelQuadView;[I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/embeddedt/embeddium/impl/model/quad/ModelQuadView;getColorIndex()I"
            ),
            mode = DirectInject.Mode.CANCEL_IF_TRUE
    )
    private boolean eclipticseasons$getColorsForESLeaves(
            EmbeddiumBlockAndTintGetter view, BlockPos pos, BlockState state, ModelQuadView quad, int[] output
    ) {
        if (this.provider instanceof EmbeddiumBlenderColorProvider provider2) {
            provider2.getColors(view, pos, state, quad, output);
            return true;
        }

        return false;
    }
}
