package com.teamtea.eclipticseasons.client.gui.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ColorStringWidget extends StringWidget {
    private final SyncType syncType;

    public ColorStringWidget(Component message, Font font, SyncType syncType) {
        super(message, font);
        this.syncType = syncType;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        final int MARKER_HEIGHT = 12;
        int markerX = getX();
        int markerY = getY() + (getHeight() - MARKER_HEIGHT) / 2;
        renderTypeMarker(graphics, markerX, markerY);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(10, 0, 0);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        pose.popPose();
    }

    private void renderTypeMarker(GuiGraphics graphics, int x, int y) {
        int mainColor = getMainColor();
        int shadowColor = getShadowColor();

        int width = 3;
        int height = 12;

        // 投影：向右下偏移 1px
        graphics.fill(x + 1, y + 1, x + width + 1, y + height + 1, 0x80000000);
        // 暗色底框
        graphics.fill(x, y, x + width, y + height, mainColor);
        // 中间亮色主体
        // graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, mainColor);
    }

    private int getMainColor() {
        return switch (syncType) {
            case COMMON -> 0xFFFFC928;
            case CLIENT -> 0xFF28D7EF;
            default -> 0xFFD65A4A;
        };
    }

    private int getShadowColor() {
        return switch (syncType) {
            case COMMON -> 0xFF704D08;
            case CLIENT -> 0xFF086D7A;
            default -> 0xFF713029;
        };
    }
}
