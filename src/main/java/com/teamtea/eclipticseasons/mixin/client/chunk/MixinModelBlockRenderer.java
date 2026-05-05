package com.teamtea.eclipticseasons.mixin.client.chunk;


import com.teamtea.eclipticseasons.api.misc.client.ISeasonRendererContextOwner;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.core.SeasonRenderDispatcher;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ModelBlockRenderer.class)
public abstract class MixinModelBlockRenderer {


    @Shadow
    @Final
    private RandomSource random;

    @Shadow
    @Final
    private List<BlockStateModelPart> parts;

    @Inject(
            method = "tesselateBlock",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/List;isEmpty()Z")
    )
    private void eclipticseasons$tesselateBlock_extra(
            BlockQuadOutput output,
            float x, float y, float z,
            BlockAndTintGetter level,
            BlockPos pos, BlockState blockState,
            BlockStateModel model, long seed,
            CallbackInfo ci) {
        if (level instanceof IMapSlice mapSlice) {
            SeasonRenderDispatcher.findModel(ISeasonRendererContextOwner.of(mapSlice), mapSlice,
                            pos,
                            blockState,
                            this.random,
                            blockState.getSeed(pos),
                            mapSlice.getModelCheckPos(),
                            this.parts)
                    .apply(level, pos, blockState, random, this.parts)
                    .resetAll();
        }
    }


}
