package com.teamtea.eclipticseasons.client.render.ber;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.common.block.blockentity.QuestHangingSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.HangingSignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class QuestSignRenderer extends HangingSignRenderer {
    private final Map<WoodType, HangingSignRenderer.Models> signModels;
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, -0.32F, 0.073F);
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private final Font font;
    private final SpriteId materail;
    private final SpriteGetter sprites;

    public QuestSignRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.signModels = WoodType.values()
                .collect(ImmutableMap.toImmutableMap(type -> (WoodType) type, type -> HangingSignRenderer.Models.create(context, type)));
        this.sprites = context.sprites();
        this.font = context.font();
        this.materail = createHangingSignMaterial(EclipticSeasons.rl("frame"));
    }

    @Override
    public void extractRenderState(SignBlockEntity blockEntity, HangingSignRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        if (blockEntity instanceof QuestHangingSignBlockEntity questHangingSignBlockEntity) {
            state.woodType = questHangingSignBlockEntity.getSignType().type();
            state.drawOutline = true;
            // state.frontText=questHangingSignBlockEntity.getSeasonQuest().;
        }
    }

    @Override
    public void submit(HangingSignRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        Model.Simple bodyModel = this.getSignModel(state);
        poseStack.pushPose();
        poseStack.mulPose(state.transformations.body());
        this.submitSign(poseStack, state.lightCoords, state.woodType, bodyModel, state.breakProgress, submitNodeCollector);
        poseStack.popPose();
        if (state.frontText != null) {
            poseStack.pushPose();
            poseStack.mulPose(state.transformations.frontText());
            poseStack.scale(0.8f, 0.8f, 0.8f);
            this.submitSignText(state, poseStack, submitNodeCollector, state.frontText);
            poseStack.popPose();
        }
    }

    @Override
    protected void submitSign(
            @NonNull PoseStack poseStack,
            int lightCoords,
            @NonNull WoodType type,
            Model.@NonNull Simple signModel,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress,
            SubmitNodeCollector submitNodeCollector
    ) {
        SpriteId sprite = this.getSignSprite(type);
        submitNodeCollector.submitModel(signModel, Unit.INSTANCE, poseStack, lightCoords, OverlayTexture.NO_OVERLAY, -1, sprite, this.sprites, 0, breakProgress);

        sprite = materail;
        submitNodeCollector.submitModel(signModel, Unit.INSTANCE, poseStack, lightCoords, OverlayTexture.NO_OVERLAY, -1, sprite, this.sprites, 0, breakProgress);
    }


    private static SpriteId createHangingSignMaterial(Identifier Identifier) {
        return new SpriteId(Sheets.SIGN_SHEET, Identifier.fromNamespaceAndPath(Identifier.getNamespace(), "entity/signs/hanging/" + Identifier.getPath()));
    }
}
