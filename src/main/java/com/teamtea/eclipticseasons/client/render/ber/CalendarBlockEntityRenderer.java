package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.block.base.SimpleHorizontalEntityBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.CalendarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.Guardian;
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
        var st = ClientCon.nowSolarTerm;

        drawText(2, Component.translatable("info.eclipticseasons.environment.solar_term.hint").getString(), Color.GRAY.getRGB(), blockEntity, poseStack, bufferIn, combinedLight);

        drawText(1, st.getTranslation().getString(), st.getSeason().getColor().getColor(), blockEntity, poseStack, bufferIn, combinedLight);


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

        matrixStackIn.translate(0, -0.125f/2f, 0);
        matrixStackIn.translate(x, y, z + 0.74f);
        matrixStackIn.pushPose();
        matrixStackIn.scale(scale_x, scale_y, scale_z);
        fontRenderer.drawInBatch(label
                , (float) (-textWidth) / 2.0F, -18F - lh * 1.2f * line - 1.2f * extraHeight, color, false, matrixStackIn.last().pose(), txtBuffer, Font.DisplayMode.NORMAL, 0,  LightTexture.FULL_SKY);
        // txtBuffer.endBatch();
        matrixStackIn.popPose();

        if(line==1)
        {
            // 也许需要用renderModel的手段
            VertexConsumer builder = txtBuffer.getBuffer(RenderType.entitySmoothCutout(SolarTerm.getFullIcon().withPrefix("textures/").withSuffix(".png")));
            // matrixStackIn.scale(20, 20, 20);
            matrixStackIn.scale(0.2f, 0.2f, 0.2f);
            int size = 16;
            Lighting.setupLevel();
            blitRect(matrixStackIn, builder, LightTexture.FULL_SKY, OverlayTexture.NO_OVERLAY,
                    size / 2f,
                    (float) -size * 0.6f,
                    size * ClientCon.nowSolarTerm.getIconPosition().getKey(),
                    size * ClientCon.nowSolarTerm.getIconPosition().getValue(),
                    size,
                    size,
                    (int) (180 / (30f / size)),
                    (int) (120 / (30f / size)),
                    false);
            matrixStackIn.popPose();
        }
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

        packedLight= LightTexture.FULL_SKY;
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

        if (mirrored) {
            x1 *= -1;
        }

        Matrix4f matrix = matrixStack.last().pose();
        var normal = matrixStack.last();

        builder.addVertex(matrix, x0, y1, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(tx0, ty1).setOverlay(overlay).setLight(packedLight).setNormal(normal, 0.0F, -1.0F, 0.0F);
        builder.addVertex(matrix, x1, y1, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(tx1, ty1).setOverlay(overlay).setLight(packedLight).setNormal(normal, 0.0F, -1.0F, 0.0F);
        builder.addVertex(matrix, x1, y0, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(tx1, ty0).setOverlay(overlay).setLight(packedLight).setNormal(normal, 0.0F, -1.0F, 0.0F);
        builder.addVertex(matrix, x0, y0, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(tx0, ty0).setOverlay(overlay).setLight(packedLight).setNormal(normal, 0.0F, -1.0F, 0.0F);

    }
}
