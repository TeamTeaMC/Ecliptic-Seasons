package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.block.base.SimpleHorizontalEntityBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.CalendarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.awt.*;


public class CalendarBlockEntityRenderer implements BlockEntityRenderer<CalendarBlockEntity> {

    private final Font font;

    public CalendarBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
        this.font = pContext.getFont();
    }

    @Override
    public void render(CalendarBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {

        var facing = blockEntity.getBlockState().getValue(SimpleHorizontalEntityBlock.FACING).ordinal() * 90;
        SolarTerm st = ClientCon.nowSolarTerm;

        if (st != SolarTerm.NONE)
            drawText(2, Component.translatable("info.eclipticseasons.environment.solar_term.hint").getString(), Color.GRAY.getRGB(), blockEntity, poseStack, bufferIn, combinedLight);

        String string = st.getTranslation().getString() +
                (st == SolarTerm.NONE ? "" :" (%s)".formatted(st.getSeason().getTranslation().getString()));

        drawText(1, string, st.getSeason().getColor().getColor(), blockEntity, poseStack, bufferIn, combinedLight);

        // drawText(2, st.getAlternationText().getString().substring(0,5), st.getSeason().getColor().getColor(), blockEntity, poseStack, bufferIn, combinedLight);
        //
        // drawText(1, st.getAlternationText().getString().substring(5), st.getSeason().getColor().getColor(), blockEntity, poseStack, bufferIn, combinedLight);

    }

    private void drawText(int line, String label, int color, BlockEntity tile, PoseStack matrixStackIn, MultiBufferSource txtBuffer, int combinedLightIn) {

        matrixStackIn.pushPose();


        Font fontRenderer = this.font;
        // MultiBufferSource.BufferSource txtBuffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        int textWidth = fontRenderer.width(label);
        var lh = font.lineHeight;

        LocalPlayer player = Minecraft.getInstance().player;

        var d = tile.getBlockState().getValue(SimpleHorizontalEntityBlock.FACING);
        handleMatrixAngle(matrixStackIn, player, tile.getBlockPos(), d);
        float x = 0;
        float y = 0;
        float z = 0;
        float scale_x = 0.007f;
        float scale_y = 0.007f;
        float scale_z = 0.007f;

        float extraHeight = 0f;

        matrixStackIn.translate(0, -0.125f / 2f, 0);
        matrixStackIn.translate(x, y, z + 0.74f);
        matrixStackIn.pushPose();
        matrixStackIn.scale(scale_x, scale_y, scale_z);
        if(textWidth>80){
            float re=80f/textWidth;
            matrixStackIn.scale(re, re, re);
            matrixStackIn.translate(0, -4f, 0);
        }
        fontRenderer.drawInBatch(label
                , (float) (-textWidth) / 2.0F, -18F - lh * 1.2f * line - 1.2f * extraHeight, color, false, matrixStackIn.last().pose(), txtBuffer, Font.DisplayMode.NORMAL, 0, combinedLightIn);

        // txtBuffer.endBatch();
        matrixStackIn.popPose();
        // matrixStackIn.scale(20, 20, 20);
        matrixStackIn.scale(0.2f, 0.2f, 0.2f);
        int size = 16;


        if (line == 1) {
            // Lighting.setupForFlatItems();
            // GlStateManager._disableCull();
            VertexConsumer builder = txtBuffer.getBuffer(RenderType.entitySmoothCutout(SolarTerm.getFullIcon().withPrefix("textures/").withSuffix(".png")));
            // builder = txtBuffer.getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(null, false));
            blitRect(matrixStackIn, builder,combinedLightIn, OverlayTexture.NO_OVERLAY,
                    size / 2f,
                    (float) -size * 0.6f,
                    size * ClientCon.nowSolarTerm.getIconPosition().getKey(),
                    size * ClientCon.nowSolarTerm.getIconPosition().getValue(),
                    size,
                    size,
                    (int) (180 / (30f / size)),
                    (int) (120 / (30f / size)),
                    false);
            // Lighting.setupFor3DItems();
        } else {
            // matrixStackIn.translate(0, 5f, -4f);
            // matrixStackIn.scale(4f, 4f, 4f);
            //
            // Minecraft.getInstance().getBlockRenderer().renderSingleBlock(EclipticSeasons.ModContents.calendar.get().defaultBlockState(),matrixStackIn,txtBuffer,combinedLightIn,OverlayTexture.NO_OVERLAY);
        }
        matrixStackIn.popPose();

    }


    private void handleMatrixAngle(PoseStack matrixStackIn, LocalPlayer player, BlockPos pos, Direction d) {
        Vector3d vector3d = new Vector3d(player.getPosition(1.0f).x() - pos.getX() - 0.5
                , player.getPosition(0f).y() - pos.getY()
                , player.getPosition(0f).z() - pos.getZ() - 0.5);

        if (d == Direction.DOWN || d == Direction.UP) {
            if (vector3d.x > 0 && Math.abs(vector3d.x) > Math.abs(vector3d.z)) d = Direction.EAST;
            if (vector3d.x < 0 && Math.abs(vector3d.x) > Math.abs(vector3d.z)) d = Direction.WEST;
            if (vector3d.x > 0 && Math.abs(vector3d.x) < Math.abs(vector3d.z)) d = Direction.SOUTH;
            if (vector3d.x < 0 && Math.abs(vector3d.x) < Math.abs(vector3d.z)) d = Direction.NORTH;
        }
        switch (d) {
            case SOUTH:
                matrixStackIn.translate(0.5, 0.15, 1);
                // matrixStackIn.mulPose(new Quaternion(0, 180, 180, true));
                matrixStackIn.mulPose(XYZ.deg_to_rad(0, 180, 180));
                break;
            case NORTH:
                // matrixStackIn.mulPose(new Quaternion(0, 0, 180, true));
                matrixStackIn.mulPose(XYZ.deg_to_rad(0, 0, 180));
                matrixStackIn.translate(-0.5, -0.15, 0);
                break;
            case EAST:
                // matrixStackIn.mulPose(new Quaternion(0, 270, 180, true));
                matrixStackIn.mulPose(XYZ.deg_to_rad(0, 270, 180));
                matrixStackIn.translate(-0.5, -0.15, -1);
                break;
            case WEST:
                // matrixStackIn.mulPose(new Quaternion(0, 90, 180, true));
                matrixStackIn.mulPose(XYZ.deg_to_rad(0, 90, 180));
                matrixStackIn.translate(0.5, -0.15, 0);
                break;
            default:
                matrixStackIn.scale(0.01f, 0.01f, 0.01f);
                break;
        }
    }

    /**
     * @param x0      渲染起点x
     * @param y0      渲染起点y
     * @param xt      图上起点y
     * @param yt      图上起点y
     * @param width   图上宽度
     * @param height  图上高度
     * @param tWidth  图片长度
     * @param tHeight 图片高度
     **/
    protected static void blitRect(PoseStack matrixStack, VertexConsumer builder, int packedLight, int overlay, float x0, float y0, float xt, float yt, float width, float height, int tWidth, int tHeight, boolean mirrored) {

        float pixelScale = 0.0625f;

        x0 = x0 * pixelScale;
        y0 = y0 * pixelScale;
        xt = xt * pixelScale;
        yt = yt * pixelScale;
        width = width * pixelScale;
        height = height * pixelScale;


        float tx0 = xt / (tWidth * pixelScale);
        float ty0 = yt / (tHeight * pixelScale);
        float tx1 = tx0 + width / (tWidth * pixelScale);
        float ty1 = ty0 + height / (tHeight * pixelScale);

        float x1 = x0 - width;
        float y1 = y0 + height;

        // TextureAtlasSprite sprite = ModelManager.getSprite(EclipticSeasons.rl("block/seasons_icons"));
        // tx0=sprite.getU0();
        // tx1=sprite.getU(16d/6d);
        // ty0=sprite.getV0();
        // ty1=sprite.getV(16d/4d);

        if (mirrored) {
            x1 *= -1;
        }

        Matrix4f matrix = matrixStack.last().pose();
        var normal = matrixStack.last();


        builder.vertex(matrix, x0, y1, 0.0f).color(Color.WHITE.getRGB()).uv(tx0, ty1).overlayCoords(overlay).uv2(packedLight).normal(normal.normal(), 0.0F, 0, 1.0F).endVertex();
        builder.vertex(matrix, x1, y1, 0.0f).color(Color.WHITE.getRGB()).uv(tx1, ty1).overlayCoords(overlay).uv2(packedLight).normal(normal.normal(), 0.0F, 0, 1.0F).endVertex();
        builder.vertex(matrix, x1, y0, 0.0f).color(Color.WHITE.getRGB()).uv(tx1, ty0).overlayCoords(overlay).uv2(packedLight).normal(normal.normal(), 0.0F, 0, 1.0F).endVertex();
        builder.vertex(matrix, x0, y0, 0.0f).color(Color.WHITE.getRGB()).uv(tx0, ty0).overlayCoords(overlay).uv2(packedLight).normal(normal.normal(), 0.0F, 0, 1.0F).endVertex();


    }
}
