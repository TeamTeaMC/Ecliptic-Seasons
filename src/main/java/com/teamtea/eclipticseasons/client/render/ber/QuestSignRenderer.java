package com.teamtea.eclipticseasons.client.render.ber;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.common.block.blockentity.QuestHangingSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class QuestSignRenderer extends HangingSignRenderer {
    private final Map<WoodType, HangingSignModel> hangingSignModels;
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, -0.32F, 0.073F);
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private final Font font;
    private final Material materail;

    public QuestSignRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.hangingSignModels = WoodType.values()
                .collect(
                        ImmutableMap.toImmutableMap(
                                p_249901_ -> p_249901_,
                                p_251956_ -> new HangingSignRenderer.HangingSignModel(context.bakeLayer(ModelLayers.createHangingSignModelName(p_251956_)))
                        )
                );
        this.font = context.getFont();
        this.materail = createHangingSignMaterial(EclipticSeasons.rl("frame"));
    }


    @Override
    public void render(SignBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity instanceof QuestHangingSignBlockEntity questHangingSignBlockEntity) {
            // super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
            BlockState blockstate = blockEntity.getBlockState();
            SignBlock signblock = (SignBlock) blockstate.getBlock();
            WoodType woodtype = questHangingSignBlockEntity.getSignType().type();
            HangingSignRenderer.HangingSignModel hangingsignrenderer$hangingsignmodel = this.hangingSignModels.get(woodtype);
            hangingsignrenderer$hangingsignmodel.evaluateVisibleParts(blockstate);
            this.renderSignWithText(questHangingSignBlockEntity, poseStack, bufferSource, packedLight, packedOverlay, blockstate, signblock, woodtype, hangingsignrenderer$hangingsignmodel);
        }
    }

    void renderSignWithText(
            QuestHangingSignBlockEntity signEntity,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay,
            BlockState state,
            SignBlock signBlock,
            WoodType woodType,
            Model model
    ) {
        poseStack.pushPose();
        this.translateSign(poseStack, -signBlock.getYRotationDegrees(state), state);
        this.renderSign(poseStack, buffer, packedLight, packedOverlay, woodType, model);
        this.renderSignText(
                signEntity.getBlockPos(),
                signEntity.getFrontText(),
                poseStack,
                buffer,
                packedLight,
                signEntity.getTextLineHeight(),
                signEntity.getMaxTextLineWidth(),
                true,
                signEntity.getSeasonQuest()
        );
        this.renderSignDecoration(poseStack, buffer, packedLight, packedOverlay, woodType, model);
        poseStack.popPose();
    }

    void translateSign(PoseStack poseStack, float yRot, BlockState state) {
        poseStack.translate(0.5, 0.9375, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.translate(0.0F, -0.3125F, 0.0F);
    }

    void renderSign(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, WoodType woodType, Model model) {
        poseStack.pushPose();
        float f = this.getSignModelRenderScale();
        poseStack.scale(f, -f, -f);
        Material material = this.getSignMaterial(woodType);
        VertexConsumer vertexconsumer = material.buffer(buffer, model::renderType);
        this.renderSignModel(poseStack, packedLight, packedOverlay, model, vertexconsumer);
        poseStack.popPose();
    }

    void renderSignDecoration(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, WoodType woodType, Model model) {
        poseStack.pushPose();
        float f = this.getSignModelRenderScale();
        poseStack.scale(f, -f, -f);

        VertexConsumer vertexconsumer = this.materail.buffer(buffer, model::renderType);
        this.renderSignModel(poseStack, LightTexture.pack(8, 15), packedOverlay, model, vertexconsumer);
        poseStack.popPose();
    }


    void renderSignModel(PoseStack poseStack, int packedLight, int packedOverlay, Model model, VertexConsumer vertexConsumer) {
        HangingSignRenderer.HangingSignModel hangingsignrenderer$hangingsignmodel = (HangingSignRenderer.HangingSignModel) model;
        hangingsignrenderer$hangingsignmodel.root.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }

    Material getSignMaterial(WoodType woodType) {
        return Sheets.getHangingSignMaterial(woodType);
    }

    Vec3 getTextOffset() {
        return TEXT_OFFSET;
    }

    void renderSignText(
            BlockPos pos,
            SignText text,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int lineHeight,
            int maxWidth,
            boolean isFrontText,
            SeasonQuest seasonQuest
    ) {
        poseStack.pushPose();
        this.translateSignText(poseStack, isFrontText, this.getTextOffset());
        boolean b = text.hasGlowingText();
        b = seasonQuest != null && seasonQuest.glowing().isPresent();
        int textColor = text.getColor().getTextColor();
        textColor = seasonQuest != null && seasonQuest.color().isPresent() ? seasonQuest.color().get() : Color.WHITE.getRGB();
        int i = getDarkColor(textColor, b);
        int j = 4 * lineHeight / 2;
        FormattedCharSequence[] aformattedcharsequence = text.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), component -> {
            List<FormattedCharSequence> list = this.font.split(component, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });
        int k;
        boolean flag;
        int l;
        if (b) {
            k = Color.WHITE.getRGB();
            flag = isOutlineVisible(pos, k);
            l = 15728880;
        } else {
            k = i;
            flag = false;
            l = packedLight;
        }

        poseStack.scale(0.8f, 0.8f, 0.8f);
        for (int i1 = 0; i1 < 4; i1++) {
            FormattedCharSequence formattedcharsequence = aformattedcharsequence[i1];
            float f = (float) (-this.font.width(formattedcharsequence) / 2);
            poseStack.pushPose();
            float abs = Mth.abs(f);
            if (abs > 32) {
                poseStack.scale(32 / abs * 0.97f, 1f, 1f);
            }
            if (flag) {
                this.font.drawInBatch8xOutline(formattedcharsequence, f, (float) (i1 * lineHeight - j), k, i, poseStack.last().pose(), buffer, l);
            } else {
                this.font
                        .drawInBatch(
                                formattedcharsequence,
                                f,
                                (float) (i1 * lineHeight - j),
                                k,
                                false,
                                poseStack.last().pose(),
                                buffer,
                                Font.DisplayMode.POLYGON_OFFSET,
                                0,
                                l
                        );
            }
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void translateSignText(PoseStack poseStack, boolean isFrontText, Vec3 offset) {
        if (!isFrontText) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }

        float f = 0.015625F * this.getSignTextRenderScale();
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.scale(f, -f, f);
    }

    static boolean isOutlineVisible(BlockPos pos, int textColor) {
        if (textColor == DyeColor.BLACK.getTextColor()) {
            return true;
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localplayer = minecraft.player;
            if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping()) {
                return true;
            } else {
                Entity entity = minecraft.getCameraEntity();
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(pos)) < (double) OUTLINE_RENDER_DISTANCE;
            }
        }
    }

    private static Material createHangingSignMaterial(ResourceLocation resourceLocation) {
        return new Material(Sheets.SIGN_SHEET, ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), "entity/signs/hanging/" + resourceLocation.getPath()));
    }

    public static int getDarkColor(int i, boolean hasGlowingText) {
        if (i == DyeColor.BLACK.getTextColor()) {
            return -988212;
        } else {
            double d0 = 0.4;
            int j = (int) ((double) FastColor.ARGB32.red(i) * 0.4);
            int k = (int) ((double) FastColor.ARGB32.green(i) * 0.4);
            int l = (int) ((double) FastColor.ARGB32.blue(i) * 0.4);
            return FastColor.ARGB32.color(0, j, k, l);
        }
    }
}
