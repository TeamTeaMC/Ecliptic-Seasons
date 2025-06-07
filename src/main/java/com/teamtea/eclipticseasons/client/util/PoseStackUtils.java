package com.teamtea.eclipticseasons.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

public class PoseStackUtils {
    public static void withPose(GuiGraphics guiGraphics, Consumer<GuiGraphics> operation) {
        guiGraphics.pose().pushPose();
        try {
            operation.accept(guiGraphics);
        } finally {
            guiGraphics.pose().popPose();
        }
    }

    public static void withPose(PoseStack pose, Consumer<PoseStack> operation) {
        pose.pushPose();
        try {
            operation.accept(pose);
        } finally {
            pose.popPose();
        }
    }

    public static void withTranslatedPose(GuiGraphics guiGraphics, float x, float y, float z, Consumer<GuiGraphics> operation) {
        withPose(guiGraphics, g -> {
            g.pose().translate(x, y, z);
            operation.accept(g);
        });
    }

    public static void withTranslatedPose(PoseStack pose, float x, float y, float z, Runnable operation) {
        withPose(pose, p -> {
            p.translate(x, y, z);
            operation.run();
        });
    }

    public static void withScaledPose(GuiGraphics guiGraphics, float x, float y, float z, Consumer<GuiGraphics> operation) {
        withPose(guiGraphics, g -> {
            g.pose().scale(x, y, z);
            operation.accept(g);
        });
    }

    public static void withScaledPose(PoseStack pose, float x, float y, float z, Runnable operation) {
        withPose(pose, p -> {
            p.scale(x, y, z);
            operation.run();
        });
    }
}
