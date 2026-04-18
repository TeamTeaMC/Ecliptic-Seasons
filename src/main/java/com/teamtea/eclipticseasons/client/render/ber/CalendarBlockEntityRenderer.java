package com.teamtea.eclipticseasons.client.render.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.ISolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.render.ber.state.CalendarState;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.block.CalendarBlock;
import com.teamtea.eclipticseasons.common.block.base.SimpleHorizontalEntityBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.CalendarBlockEntity;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.solar.extra.CalendarAstronomer;
import com.teamtea.eclipticseasons.common.core.solar.extra.FixedSolarDataManagerLocal;
import com.teamtea.eclipticseasons.common.core.solar.SolarTermHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.jspecify.annotations.Nullable;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public class CalendarBlockEntityRenderer implements BlockEntityRenderer<CalendarBlockEntity, CalendarState> {

    private final Font font;

    public CalendarBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
        this.font = pContext.font();
    }


    @Override
    public CalendarState createRenderState() {
        return new CalendarState();
    }

    @Override
    public void extractRenderState(CalendarBlockEntity blockEntity, CalendarState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        if (!blockEntity.isInit()) {
            Holder<Biome> cropBiome = CropGrowthHandler.getCropBiome(blockEntity.getLevel(), blockEntity.getBlockPos());
            blockEntity.setBiome(cropBiome);
            blockEntity.setInit(true);
        }

        state.setBiome(blockEntity.getBiome());
        state.setDisplayMode(blockEntity.getBlockState().getValue(CalendarBlock.MODE));
        state.setFacing(blockEntity.getBlockState().getValue(SimpleHorizontalEntityBlock.FACING));
    }

    @Override
    public void submit(CalendarState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Direction facing = state.getFacing();
        CalendarBlock.DisplayMode displayMode = state.getDisplayMode();
        SolarTerm st = ClientCon.nowSolarTerm;

        ISolarTerm iSolarTermOriginal = SolarTermHelper.get(state.getBiome(), st);
        ISolarTerm seasonPhaseUsed = displayMode == CalendarBlock.DisplayMode.NEXT ?
                SolarTermHelper.getNext(state.getBiome(), st) : iSolarTermOriginal;


        setEnv(facing, state.blockPos, poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), state.lightCoords,
                (PoseStack matrixStackIn, MultiBufferSource multiBufferSource, Integer combinedLightIn) -> {

                    matrixStackIn.translate(0, -0.0f, 0);
                    drawIcon(seasonPhaseUsed.getIcon(),
                            seasonPhaseUsed.getIconWidth(),
                            seasonPhaseUsed.getIconHeight(),
                            seasonPhaseUsed.getIconAtlasSize(),
                            seasonPhaseUsed.getIconPosition().getFirst(),
                            seasonPhaseUsed.getIconPosition().getSecond(),
                            matrixStackIn, multiBufferSource, combinedLightIn);

                    drawText(1, seasonPhaseUsed.getPatternTranslation().getString(), Optional.ofNullable(seasonPhaseUsed.getColor().getColor()).orElse(-1), matrixStackIn, multiBufferSource);

                    if (st != SolarTerm.NONE) {
                        String string = "";
                        switch (displayMode) {
                            case YEAR ->
                                    string = Component.translatable("info.eclipticseasons.environment.solar_term.hint2", ClientCon.nowSolarYear).getString();
                            case NEXT -> {
                                Pair<SolarTerm, ISolarTerm> nextPair = SolarTermHelper.getNextTermAndStart(state.getBiome(), st);
                                int remain;
                                if (SolarHolders.getSaveData(ClientCon.getUseLevel()) instanceof FixedSolarDataManagerLocal fsl) {
                                    Date next = fsl.getNextSolarTermByDay(new CalendarAstronomer());
                                    LocalDate nextDay = Instant.ofEpochMilli(next.getTime())
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();

                                    LocalDate today = Instant.ofEpochMilli(new Date().getTime())
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
                                    remain = Math.toIntExact(ChronoUnit.DAYS.between(today, nextDay));
                                } else {
                                    int lastingDaysOfEachTerm = EclipticSeasonsApi.getInstance().getLastingDaysOfEachTerm(ClientCon.getUseLevel());
                                    remain = Mth.floor(((1 - ClientCon.progress / 100f) * lastingDaysOfEachTerm));
                                    remain += lastingDaysOfEachTerm * (
                                            ((nextPair.getFirst().ordinal() - st.getNextSolarTerm().ordinal() + 24) % 24));
                                    remain = iSolarTermOriginal == nextPair.getSecond() ? 0 : remain;
                                }
                                string = Component.translatable("info.eclipticseasons.environment.solar_term.hint3", remain).getString();
                            }
                            case DAY ->
                                    string = Component.translatable("info.eclipticseasons.environment.solar_term.hint4", EclipticUtil.getNowSolarDay(ClientCon.getUseLevel())).getString();
                            default -> string = seasonPhaseUsed.getTittleTranslation().getString();
                        }
                        drawText(2, string, Color.GRAY.getRGB(), matrixStackIn, multiBufferSource);
                    }

                });
    }

    private void setEnv(Direction d, BlockPos pos, PoseStack matrixStackIn, MultiBufferSource txtBuffer, int combinedLightIn, TBiConsumer<PoseStack, MultiBufferSource, Integer> runnable) {
        matrixStackIn.pushPose();
        LocalPlayer player = Minecraft.getInstance().player;
        handleMatrixAngle(matrixStackIn, player, pos, d);
        float x = 0;
        float y = 0;
        float z = 0;
        matrixStackIn.translate(0, -0.125f / 2f, 0);
        matrixStackIn.translate(x, y, z + 0.74f);
        runnable.accept(matrixStackIn, txtBuffer, combinedLightIn);
        matrixStackIn.popPose();
    }

    private void drawIcon(Identifier fullIcon,
                          int twidth, int theight, float isize, int x, int y,
                          PoseStack matrixStackIn, MultiBufferSource txtBuffer, int combinedLightIn) {
        matrixStackIn.pushPose();

        // matrixStackIn.scale(20, 20, 20);
        matrixStackIn.scale(0.2f, 0.2f, 0.2f);
        int size = 16;
        // Lighting.setupForFlatItems();
        // GlStateManager._disableCull();
        Identifier location = fullIcon.withPrefix("textures/").withSuffix(".png");
        VertexConsumer builder = txtBuffer.getBuffer(RenderTypes.entityCutout(location));

        // builder = txtBuffer.getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(null, false));
        blitRect(matrixStackIn, builder, combinedLightIn, OverlayTexture.NO_OVERLAY,
                size / 2f,
                (float) -size * 0.6f,
                size * x,
                size * y,
                size,
                size,
                (int) (twidth / (isize / size)),
                (int) (theight / (isize / size)),
                true);
        // Lighting.setupFor3DItems();

        matrixStackIn.popPose();
    }

    private void drawText(int line, String label, int color, PoseStack matrixStackIn, MultiBufferSource txtBuffer) {
        // Font fontRenderer = this.font;
        // int textWidth = fontRenderer.width(label);
        // var lh = font.lineHeight;
        // float scale_x = 0.007f;
        // float scale_y = 0.007f;
        // float scale_z = 0.007f;
        //
        // float extraHeight = 0f;
        // matrixStackIn.pushPose();
        // matrixStackIn.scale(scale_x, scale_y, scale_z);
        // if (textWidth > 80) {
        //     float re = 80f / textWidth;
        //     matrixStackIn.scale(re, re, re);
        //     matrixStackIn.translate(0, -4f, 0);
        // }
        // fontRenderer.drawInBatch(label
        //         , (float) (-textWidth) / 2.0F, -18F - lh * 1.2f * line - 1.2f * extraHeight, color, false, matrixStackIn.last().pose(), txtBuffer, Font.DisplayMode.NORMAL, 0, LightCoordsUtil.FULL_SKY);
        // // txtBuffer.endBatch();
        // matrixStackIn.popPose();
        Font font = this.font;
        int lineWidth = 80;
        float scale = 0.007f;
        float x = 0;
        float y = -0.3125f / scale;

        matrixStackIn.pushPose();
        matrixStackIn.scale(scale, scale, scale);

        List<FormattedCharSequence> lines = font.split(FormattedText.of(label), lineWidth);

        int lineHeight = font.lineHeight;

        y += lineHeight * (2 - line + 1);
        // float totalHeight = Math.maxTime(1, lines.size() - 1) * lineHeight;
        // if(lines.size()>2){
        //     totalHeight=1* lineHeight;;
        // }
        float startY = y + 0;

        for (FormattedCharSequence charSequence : lines.reversed()) {
            int textWidth = font.width(charSequence);
            float drawX = x - textWidth / 2.0f;
            font.drawInBatch(charSequence, drawX, startY, color, false, matrixStackIn.last().pose(), txtBuffer,
                    Font.DisplayMode.NORMAL, 0, LightCoordsUtil.FULL_SKY);
            startY -= lineHeight;
        }

        matrixStackIn.popPose();
        if (!lines.isEmpty()) {
            matrixStackIn.translate(0, -((lines.size() - 0.75) * lineHeight) * (scale), 0);
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

        packedLight = LightCoordsUtil.FULL_SKY;
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
            x0 = -x0;
            x1 = -x1;
        }

        Matrix4f matrix = matrixStack.last().pose();
        var normal = matrixStack.last();

        builder.addVertex(matrix, x0, y1, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(tx0, ty1).setOverlay(overlay).setLight(packedLight).setNormal(normal, 0.0F, -1.0F, 0.0F);
        builder.addVertex(matrix, x1, y1, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(tx1, ty1).setOverlay(overlay).setLight(packedLight).setNormal(normal, 0.0F, -1.0F, 0.0F);
        builder.addVertex(matrix, x1, y0, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(tx1, ty0).setOverlay(overlay).setLight(packedLight).setNormal(normal, 0.0F, -1.0F, 0.0F);
        builder.addVertex(matrix, x0, y0, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(tx0, ty0).setOverlay(overlay).setLight(packedLight).setNormal(normal, 0.0F, -1.0F, 0.0F);
    }


    @FunctionalInterface
    private interface TBiConsumer<T, U, R> {
        void accept(T var1, U var2, R var3);
    }
}
