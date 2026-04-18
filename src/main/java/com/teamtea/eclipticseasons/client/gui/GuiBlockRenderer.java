package com.teamtea.eclipticseasons.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public class GuiBlockRenderer extends PictureInPictureRenderer<GuiBlockRenderState> {
    protected final ModelBlockRenderer modelBlockRenderer;

    public GuiBlockRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
        modelBlockRenderer = new ModelBlockRenderer(false, false, Minecraft.getInstance().getBlockColors());
    }

    private static final float RENDER_SIZE = 24F;
    private static final ItemTransform DEFAULT_TRANSFORM = new ItemTransform(
            new Vector3f(30, 225, 0), new Vector3f(), new Vector3f(0.625F, 0.625F, 0.625F)
    );
    private static final Quaternionfc LIGHT_FIX_ROT = Axis.YP.rotationDegrees(285);

    @Override
    protected void renderToTexture(GuiBlockRenderState renderState, PoseStack poseStack) {
        float scale = renderState.scale();
        BlockState state = renderState.state();

        poseStack.pushPose();
        poseStack.scale(RENDER_SIZE * scale, -RENDER_SIZE * scale, -RENDER_SIZE * scale);
        DEFAULT_TRANSFORM.apply(false, poseStack.last());

        // poseStack.translate(.5, .5, .5);
        // poseStack.last().normal().rotate(LIGHT_FIX_ROT);
        // poseStack.translate(-.5, -.5, -.5);

        BlockQuadOutput output = (x, y, z, quad, instance) -> putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, quad.materialInfo().layer());
        BlockQuadOutput solidOutput = (x, y, z, quad, instance) -> putBakedQuad(poseStack, bufferSource, x, y, z, quad, instance, ChunkSectionLayer.SOLID);
        BlockQuadOutput blockQuadOutput = ModelBlockRenderer.forceOpaque(Minecraft.getInstance().options.cutoutLeaves().get(), state) ? output : solidOutput;
        // BlockModel blockModel = Minecraft.getInstance().getModelManager().getBlockModelSet().get(state);
        modelBlockRenderer.tesselateBlock(
                blockQuadOutput,0,0,0,Minecraft.getInstance().level,Minecraft.getInstance().player.getOnPos(),
                state,Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state),42L
        );
        poseStack.popPose();
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return (float) height / ((float)2);
    }

    @Override
    protected @NonNull String getTextureLabel() {
        return EclipticSeasons.rl("block_in_gui").toString();
    }

    @Override
    public @NonNull Class<GuiBlockRenderState> getRenderStateClass() {
        return GuiBlockRenderState.class;
    }


    private static void putBakedQuad(
            PoseStack poseStack,
            MultiBufferSource.BufferSource bufferSource,
            float x,
            float y,
            float z,
            BakedQuad quad,
            QuadInstance instance,
            ChunkSectionLayer layer
    ) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        VertexConsumer buffer = bufferSource.getBuffer(switch (layer) {
            case SOLID -> RenderTypes.solidMovingBlock();
            case CUTOUT -> RenderTypes.cutoutMovingBlock();
            case TRANSLUCENT -> RenderTypes.translucentMovingBlock();
        });
        buffer.putBakedQuad(poseStack.last(), quad, instance);
        poseStack.popPose();
    }
}
