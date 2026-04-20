package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessChunkRendererRegion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class FabricRender {
    public static boolean render(RenderChunkRegion renderchunkregion, BlockState state, BlockPos pos, BakedModel bakedModel, PoseStack posestack, ModelData empty, RenderType renderType) {
        if (renderchunkregion instanceof AccessChunkRendererRegion ac
                && ac.fabric_getRenderer() != null) {
            ac.fabric_getRenderer().tessellateBlock(state, pos, bakedModel, posestack, empty, renderType);
            return true;
        }
        return false;
    }
}
