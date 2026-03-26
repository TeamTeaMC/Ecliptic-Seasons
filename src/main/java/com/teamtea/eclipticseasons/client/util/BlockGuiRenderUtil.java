package com.teamtea.eclipticseasons.client.util;//package com.teamtea.eclipticseasons.client.util;
//
//import com.mojang.blaze3d.platform.GlStateManager;
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.renderer.ItemBlockRenderTypes;
//import net.minecraft.util.LightCoordsUtil;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.block.BlockRenderDispatcher;
//import net.minecraft.client.renderer.texture.OverlayTexture;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.resources.model.BakedModel;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.inventory.InventoryMenu;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.material.FluidState;
//import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
//import net.neoforged.neoforge.model.data.ModelData;
//import net.neoforged.neoforge.fluids.FluidStack;
//
//public class BlockGuiRenderUtil {
//    public static void renderBlockInGui(GuiGraphics guiGraphics, BlockState state) {
//        //ChunkRenderTypeSet renderLayers = ItemBlockRenderTypes.getRenderLayers(state);
//        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
//        BakedModel bakedmodel = dispatcher.getBlockModel(state);
//        int i = Minecraft.getInstance().getBlockColors().getColor(state, Minecraft.getInstance().level, Minecraft.getInstance().getCameraEntity().blockPosition(), 0);
//        float r = (float) (i >> 16 & 0xFF) / 255.0F;
//        float g = (float) (i >> 8 & 0xFF) / 255.0F;
//        float b = (float) (i & 0xFF) / 255.0F;
//        for (RenderType rt : bakedmodel.getRenderTypes(state, RandomSource.create(42), ModelData.EMPTY))
//            dispatcher.getModelRenderer()
//                    .renderModel(guiGraphics.pose().last(), guiGraphics.bufferSource().getBuffer(rt),
//                            state, bakedmodel,
//                            r, g, b,
//                            LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
//                            ModelData.EMPTY, rt
//                    );
//    }
//
//    public static void renderFluidInGui(GuiGraphics guiGraphics, FluidState fluidstate, float fluidHeight, float x, float y, float size) {
//        Minecraft mc = Minecraft.getInstance();
//
//        FluidStack fluidStack = new FluidStack(fluidstate.getType(), 1000);
//        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
//        TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
//                .apply(extensions.getStillTexture(fluidStack));
//        int color = extensions.getTintColor(fluidStack);
//        RenderType rendertype = ItemBlockRenderTypes.getRenderLayer(fluidstate);
//
//        float y0 = 0;
//        float yMax = 0.872f;
//        float yTop = y0 + fluidHeight * (yMax - y0);
//
//        float x0 = 0, x1 = 1;
//        float z0 = 0, z1 = 1;
//
//        float u0 = sprite.getU0(), u1 = sprite.getU1();
//        float v0 = sprite.getV0(), v1 = sprite.getV1();
//        float vTop = v1 - (v1 - v0) * fluidHeight;
//
//        PoseStack pose = guiGraphics.pose();
//        pose.pushPose();
//
//        pose.translate(x, y , 0);
//        pose.scale(size, size, size);
//
//        VertexConsumer buffer = guiGraphics.bufferSource().getBuffer(rendertype);
//        int light = LightCoordsUtil.FULL_BRIGHT;
//        GlStateManager._disableCull();
//
//        // Bottom
//        addVertex(buffer, pose, x1, y0, z1, u1, v1, color, 1f, light);
//        addVertex(buffer, pose, x0, y0, z1, u0, v1, color, 1f, light);
//        addVertex(buffer, pose, x0, y0, z0, u0, v0, color, 1f, light);
//        addVertex(buffer, pose, x1, y0, z0, u1, v0, color, 1f, light);
//
//        // Top
//        addVertex(buffer, pose, x0, yTop, z1, u0, vTop, color, 1f, light);
//        addVertex(buffer, pose, x1, yTop, z1, u0, v1, color, 1f, light);
//        addVertex(buffer, pose, x1, yTop, z0, u1, v1, color, 1f, light);
//        addVertex(buffer, pose, x0, yTop, z0, u1, vTop, color, 1f, light);
//
//        // Front
//        addVertex(buffer, pose, x0, yTop, z0, u1, v1, color, 1f, light);
//        addVertex(buffer, pose, x1, yTop, z0, u1, vTop, color, 1f, light);
//        addVertex(buffer, pose, x1, y0, z0, u0, vTop, color, 1f, light);
//        addVertex(buffer, pose, x0, y0, z0, u0, v1, color, 1f, light);
//
//        // Back
//        addVertex(buffer, pose, x0, yTop, z1, u0, v1, color, 1f, light);
//        addVertex(buffer, pose, x0, y0, z1, u0, vTop, color, 1f, light);
//        addVertex(buffer, pose, x1, y0, z1, u1, vTop, color, 1f, light);
//        addVertex(buffer, pose, x1, yTop, z1, u1, v1, color, 1f, light);
//
//        // Left
//        addVertex(buffer, pose, x0, y0, z1, u0, vTop, color, 1f, light);
//        addVertex(buffer, pose, x0, yTop, z1, u1, vTop, color, 1f, light);
//        addVertex(buffer, pose, x0, yTop, z0, u1, v1, color, 1f, light);
//        addVertex(buffer, pose, x0, y0, z0, u0, v1, color, 1f, light);
//
//        // Right
//        addVertex(buffer, pose, x1,  yTop, z0, u1, vTop, color, 1f, light);
//        addVertex(buffer, pose, x1, yTop, z1, u1, v1, color, 1f, light);
//        addVertex(buffer, pose, x1, y0, z1, u0, v1, color, 1f, light);
//        addVertex(buffer, pose, x1, y0, z0, u0, vTop, color, 1f, light);
//
//        GlStateManager._enableCull();
//        pose.popPose();
//
//        guiGraphics.flush();
//    }
//
//    public static void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, int RGBA, float alpha, int brightness) {
//        float red = ((RGBA >> 16) & 0xFF) / 255f;
//        float green = ((RGBA >> 8) & 0xFF) / 255f;
//        float blue = ((RGBA >> 0) & 0xFF) / 255f;
//        //		renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)/*.lightmap(0, 240)*/.normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
//        int light1 = brightness & '\uffff';
//        int light2 = brightness >> 16 & '\uffff';
//        renderer.addVertex(stack.last().pose(), x, y, z).setColor(red, green, blue, alpha).setUv(u, v).setUv2(light1, light2).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(stack.last(), 0, 1.0F, 0);
//    }
//}
