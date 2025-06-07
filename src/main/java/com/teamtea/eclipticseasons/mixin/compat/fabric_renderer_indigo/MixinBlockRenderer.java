package com.teamtea.eclipticseasons.mixin.compat.fabric_renderer_indigo;


import com.mojang.blaze3d.vertex.PoseStack;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.TerrainRenderContextLevelGetter;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TerrainRenderContext.class})
public abstract class MixinBlockRenderer extends AbstractBlockRenderContext implements TerrainRenderContextLevelGetter {


    @Inject(
            remap = false,
            method = "tessellateBlock",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/resources/model/BakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V")
    )
    private void eclipticseasons$tessellateBlock(
            BlockState blockState, BlockPos blockPos, BakedModel model, PoseStack matrixStack, ModelData modelData, RenderType renderType, CallbackInfo ci) {

    }


    @Override
    public BlockAndTintGetter eclipticseasons$get() {
        return blockInfo.blockView;
    }

    @Override
    public BlockPos eclipticseasons$getPos() {
        return blockInfo.blockPos;
    }
}
