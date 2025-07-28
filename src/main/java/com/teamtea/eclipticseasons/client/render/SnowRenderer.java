package com.teamtea.eclipticseasons.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class SnowRenderer {

    public static void renderSnowyBlock(BakedModel bakedModel, VertexConsumer bufferbuilder2, BlockPos pos, BlockState state, PoseStack posestack, RenderChunkRegion renderchunkregion, RandomSource random, long seed, RenderType renderType) {
        // long seed = state.getSeed(pos);
        posestack.pushPose();
        posestack.translate((float) (pos.getX() & 15), (float) (pos.getY() & 15), (float) (pos.getZ() & 15));
        if (!CompatModule.isFabric_renderer_indigo()) {
            Minecraft.getInstance().getBlockRenderer().getModelRenderer()
                    .tesselateBlock(renderchunkregion,
                            bakedModel,
                            state,
                            pos,
                            posestack,
                            bufferbuilder2,
                            true,
                            random,
                            seed,
                            OverlayTexture.NO_OVERLAY,
                            ModelData.EMPTY,
                            renderType);
        } else {
            FabricRender.render(renderchunkregion, state, pos, bakedModel, posestack, ModelData.EMPTY, renderType);
        }

        posestack.popPose();

    }
}
