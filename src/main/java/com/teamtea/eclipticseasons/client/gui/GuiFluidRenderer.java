package com.teamtea.eclipticseasons.client.gui;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class GuiFluidRenderer extends PictureInPictureRenderer<GuiFluidRenderState> {
    protected final ModelBlockRenderer modelBlockRenderer;

    public GuiFluidRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
        modelBlockRenderer = new ModelBlockRenderer(false, false, Minecraft.getInstance().getBlockColors());
    }

    private static final float RENDER_SIZE = 24F;
    private static final ItemTransform DEFAULT_TRANSFORM = new ItemTransform(
            new Vector3f(30, 225, 0), new Vector3f(), new Vector3f(0.625F, 0.625F, 0.625F)
    );
    private static final Quaternionfc LIGHT_FIX_ROT = Axis.YP.rotationDegrees(285);

    @Override
    protected void renderToTexture(GuiFluidRenderState renderState, PoseStack poseStack) {

        float scale = renderState.scale();
        FluidState state = renderState.state();
        // state = Blocks.LAVA.defaultBlockState().getFluidState();
        poseStack.pushPose();
        poseStack.scale(RENDER_SIZE * scale, -RENDER_SIZE * scale, -RENDER_SIZE * scale);
        DEFAULT_TRANSFORM.apply(false, poseStack.last());

        poseStack.translate(.5, .5, .5);
        poseStack.last().normal().rotate(LIGHT_FIX_ROT);
        poseStack.translate(-.5, -.5, -.5);
        FluidModel fluidModel = Minecraft.getInstance().getModelManager()
                .getFluidStateModelSet().get(state);

        TextureAtlasSprite sprite = fluidModel.stillMaterial().sprite();
        // FluidRenderer.Output fluidOutput = this::getOrBeginLayer;
        renderFluidInGui(poseStack, state,
                fluidModel, 1f,
                0, 0, 1f);

        poseStack.popPose();
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return (float) height / ((float) 2);
    }

    @Override
    protected @NonNull String getTextureLabel() {
        return EclipticSeasons.rl("fluid_in_gui").toString();
    }

    @Override
    public @NonNull Class<GuiFluidRenderState> getRenderStateClass() {
        return GuiFluidRenderState.class;
    }


    public void renderFluidInGui(PoseStack pose,
                                 // FluidRenderer.Output output,
                                 FluidState state,
                                 FluidModel model,
                                 float fluidHeight,
                                 float x, float y, float size) {
        Minecraft mc = Minecraft.getInstance();

        TextureAtlasSprite sprite = model.stillMaterial().sprite();
        int color = model.fluidTintSource() == null ? -1 : model.fluidTintSource().color(state);

        VertexConsumer buffer = bufferSource.getBuffer(
                RenderTypes.translucentMovingBlock()
                // RenderTypes.entityTranslucent(
                // sprite.contents().name().withPrefix("textures/").withSuffix(".png")
        // )
        );

        float y0 = 0;
        float yMax = 0.872f;
        float yTop = y0 + fluidHeight * (yMax - y0);

        float x0 = 0, x1 = 1;
        float z0 = 0, z1 = 1;

        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();
        float vTop = v1 - (v1 - v0) * fluidHeight;

        pose.pushPose();

        pose.translate(x, y, 0);
        pose.scale(size, size, size);


        int light = LightCoordsUtil.FULL_BRIGHT;
        GlStateManager._disableCull();

        // Bottom
        addVertex(buffer, pose, x1, y0, z1, u1, v1, color, 1f, light);
        addVertex(buffer, pose, x0, y0, z1, u0, v1, color, 1f, light);
        addVertex(buffer, pose, x0, y0, z0, u0, v0, color, 1f, light);
        addVertex(buffer, pose, x1, y0, z0, u1, v0, color, 1f, light);

        // Top
        addVertex(buffer, pose, x0, yTop, z1, u0, vTop, color, 1f, light);
        addVertex(buffer, pose, x1, yTop, z1, u0, v1, color, 1f, light);
        addVertex(buffer, pose, x1, yTop, z0, u1, v1, color, 1f, light);
        addVertex(buffer, pose, x0, yTop, z0, u1, vTop, color, 1f, light);

        // Front
        addVertex(buffer, pose, x0, yTop, z0, u1, v1, color, 1f, light);
        addVertex(buffer, pose, x1, yTop, z0, u1, vTop, color, 1f, light);
        addVertex(buffer, pose, x1, y0, z0, u0, vTop, color, 1f, light);
        addVertex(buffer, pose, x0, y0, z0, u0, v1, color, 1f, light);

        // Back
        addVertex(buffer, pose, x0, yTop, z1, u0, v1, color, 1f, light);
        addVertex(buffer, pose, x0, y0, z1, u0, vTop, color, 1f, light);
        addVertex(buffer, pose, x1, y0, z1, u1, vTop, color, 1f, light);
        addVertex(buffer, pose, x1, yTop, z1, u1, v1, color, 1f, light);

        // Left
        addVertex(buffer, pose, x0, y0, z1, u0, vTop, color, 1f, light);
        addVertex(buffer, pose, x0, yTop, z1, u1, vTop, color, 1f, light);
        addVertex(buffer, pose, x0, yTop, z0, u1, v1, color, 1f, light);
        addVertex(buffer, pose, x0, y0, z0, u0, v1, color, 1f, light);

        // Right
        addVertex(buffer, pose, x1, yTop, z0, u1, vTop, color, 1f, light);
        addVertex(buffer, pose, x1, yTop, z1, u1, v1, color, 1f, light);
        addVertex(buffer, pose, x1, y0, z1, u0, v1, color, 1f, light);
        addVertex(buffer, pose, x1, y0, z0, u0, vTop, color, 1f, light);

        GlStateManager._enableCull();
        pose.popPose();

        // guiGraphics.flush();
    }

    public static void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, int RGBA, float alpha, int brightness) {
        float red = ((RGBA >> 16) & 0xFF) / 255f;
        float green = ((RGBA >> 8) & 0xFF) / 255f;
        float blue = ((RGBA >> 0) & 0xFF) / 255f;
        //		renderer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880)/*.lightmap(0, 240)*/.normal(stack.last().normal(), 0, 1.0F, 0).endVertex();
        int light1 = brightness & '\uffff';
        int light2 = brightness >> 16 & '\uffff';
        renderer.addVertex(stack.last(), x, y, z).setColor(red, green, blue, alpha).setUv(u, v).setUv2(light1, light2).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(stack.last(), 0, 1.0F, 0);
    }


}
