package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;//package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessChunkRendererRegion;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.chunk.RenderSectionRegion;
//import net.minecraft.client.resources.model.BakedModel;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.block.state.BlockState;
//import net.neoforged.neoforge.model.data.ModelData;
//
//public class FabricRender {
//    public static void render(RenderSectionRegion renderchunkregion, BlockState state, BlockPos pos, BakedModel bakedModel, PoseStack posestack, ModelData empty, RenderType renderType) {
//        ((AccessChunkRendererRegion) renderchunkregion).fabric_getRenderer().tessellateBlock(state, pos, bakedModel, posestack, empty, renderType);
//    }
//}
