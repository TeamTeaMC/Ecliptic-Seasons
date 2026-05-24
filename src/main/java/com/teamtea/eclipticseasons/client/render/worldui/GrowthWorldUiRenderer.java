package com.teamtea.eclipticseasons.client.render.worldui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.teamtea.eclipticseasons.client.render.worldui.state.GrowthWorldUiFrameState;
import com.teamtea.eclipticseasons.client.render.worldui.state.GrowthWorldUiState;
import com.teamtea.eclipticseasons.common.item.info.GrowthInfo;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public final class GrowthWorldUiRenderer {

    public static GrowthWorldUiFrameState extractLevelRenderState(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null || mc.options.hideGui) {
            return null;
        }

        if (!mc.player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemRegistry.growth_detector)) {
            return null;
        }

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit)) {
            return null;
        }

        Level level = event.getLevelRenderer().level;
        BlockPos pos = blockHit.getBlockPos();
        BlockState blockState = level.getBlockState(pos);

        GrowthInfo info = GrowthInfoClientCache.get(level, pos, blockState);
        if (info == null) {
            return null;
        }

        GrowthWorldUiState uiState = GrowthWorldUiState.from(info);

        Vec3 cameraPos = event.getCamera().getPosition();
        Quaternionf cameraRotation = new Quaternionf(event.getCamera().rotation());

        Vec3 uiPos = calculateUiPos(level, pos, blockState, cameraPos, uiState.yOffset());

        double dx = cameraPos.x - uiPos.x;
        double dz = cameraPos.z - uiPos.z;
        float yaw = (float) Math.atan2(dx, dz);

        return new GrowthWorldUiFrameState(
                cameraPos,
                cameraRotation,
                uiPos,
                yaw,
                uiState
        );
    }


    private static Vec3 calculateUiPos(
            Level level,
            BlockPos pos,
            BlockState state,
            Vec3 cameraPos,
            float yOffset
    ) {
        VoxelShape shape = state.getShape(level, pos);
        double height = shape.isEmpty() ? 1.0D : shape.max(Direction.Axis.Y);

        double yUIOffset = (height + 0.35D);
        Vec3 base = new Vec3(
                pos.getX() + 0.5D,
                pos.getY() + yUIOffset,
                pos.getZ() + 0.5D
        );

        if (!isUiBlocked(level, base, pos)) {
            return base;
        }

        base = base.add(0, -0.35D, 0);

        Vec3 right = getCameraRight(pos, cameraPos);

        Vec3 rightPos = base.add(right.scale(0.85D));
        if (!isUiBlocked(level, rightPos, pos)) {
            return rightPos;
        }

        Vec3 leftPos = base.add(right.scale(-0.85D));
        if (!isUiBlocked(level, leftPos, pos)) {
            return leftPos;
        }

        for (int i = 1; i <= 4; i++) {
            Vec3 upPos = base.add(0.0D, i * 0.5D, 0.0D);
            if (!isUiBlocked(level, upPos, pos)) {
                return upPos;
            }
        }

        return base;
    }

    private static Vec3 getCameraRight(BlockPos pos, Vec3 cameraPos) {
        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 toCamera = cameraPos.subtract(center);
        Vec3 horizontal = new Vec3(toCamera.x, 0.0D, toCamera.z);

        if (horizontal.lengthSqr() < 1.0E-6D) {
            horizontal = new Vec3(0.0D, 0.0D, 1.0D);
        } else {
            horizontal = horizontal.normalize();
        }

        return new Vec3(-horizontal.z, 0.0D, horizontal.x);
    }

    private static boolean isUiBlocked(Level level, Vec3 uiPos, BlockPos targetPos) {
        BlockPos checkPos = BlockPos.containing(uiPos.x, uiPos.y + 0.15D, uiPos.z);

        if (checkPos.equals(targetPos)) {
            return false;
        }

        BlockState state = level.getBlockState(checkPos);

        if (state.isAir()) {
            return false;
        }

        VoxelShape shape = state.getShape(level, checkPos);
        return !shape.isEmpty();
    }

    // =============================
    // Renderer.
    // =============================

    public static void renderLevelStage(RenderLevelStageEvent event) {
        if (CommonConfig.Crop.growthDetectorClassicMode.get()) return;

        GrowthWorldUiFrameState frame =
                extractLevelRenderState(event);

        if (frame == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null || mc.options.hideGui) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        Vec3 cameraPos = frame.cameraPos();
        Vec3 uiPos = frame.uiPos();
        GrowthWorldUiState uiState = frame.uiState();

        poseStack.pushPose();

        poseStack.translate(
                uiPos.x - cameraPos.x,
                uiPos.y - cameraPos.y,
                uiPos.z - cameraPos.z
        );

        poseStack.mulPose(Axis.YP.rotation(frame.yaw()));
        poseStack.scale(uiState.scale(), -uiState.scale(), uiState.scale());

        renderGrowthText(mc.font, poseStack, buffer, uiState);

        poseStack.popPose();
    }

    public static void renderGrowthText(
            Font font,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffer,
            GrowthWorldUiState state
    ) {
        List<Component> texts = new ArrayList<>();
        texts.add(state.title());
        texts.addAll(state.lines());

        int lineHeight = 10;
        int paddingX = 6;
        int paddingY = 4;

        int width = 0;
        for (Component text : texts) {
            width = Math.max(width, font.width(text));
        }

        int totalHeight = texts.size() * lineHeight;

        int left = -width / 2 - paddingX;
        int right = width / 2 + paddingX;
        int top = -totalHeight / 2 - paddingY;
        int bottom = totalHeight / 2 + paddingY;

        renderBorderedBackgroundBox(
                poseStack,
                buffer,
                left,
                top,
                right,
                bottom,
                state.backgroundColor(),
                state.borderColor()
        );

        int y = -totalHeight / 2;

        for (int i = 0; i < texts.size(); i++) {
            Component text = texts.get(i);
            int color = i == 0 ? state.titleColor() : state.textColor();

            font.drawInBatch(
                    text,
                    -font.width(text) / 2.0F,
                    y,
                    color,
                    false,
                    poseStack.last().pose(),
                    buffer,
                    Font.DisplayMode.NORMAL,
                    0,
                    0xF000F0
            );

            y += lineHeight;
        }
    }

    public static void renderBorderedBackgroundBox(
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffer,
            int left,
            int top,
            int right,
            int bottom,
            int backgroundColor,
            int borderColor
    ) {
        poseStack.pushPose();
        poseStack.translate(0, 0, -5);

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.textBackground());

        int light = LightTexture.FULL_BRIGHT;
        float z = 0.01F;
        int border = 2;

        fillRect(consumer, matrix, left, top, right, bottom, z, borderColor, light);

        fillRect(
                consumer,
                matrix,
                left + border,
                top + border,
                right - border,
                bottom - border,
                z + 0.02F,
                backgroundColor,
                light
        );

        poseStack.popPose();
    }

    private static void fillRect(
            VertexConsumer consumer,
            Matrix4f matrix,
            int left,
            int top,
            int right,
            int bottom,
            float z,
            int color,
            int light
    ) {
        consumer.addVertex(matrix, left, bottom, z).setColor(color).setLight(light);
        consumer.addVertex(matrix, right, bottom, z).setColor(color).setLight(light);
        consumer.addVertex(matrix, right, top, z).setColor(color).setLight(light);
        consumer.addVertex(matrix, left, top, z).setColor(color).setLight(light);
    }
}