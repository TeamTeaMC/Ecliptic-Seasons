package com.teamtea.eclipticseasons.client.gui.screen.widget;

import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

public class ColorStringWidget extends StringWidget {
    private final SyncType syncType;

    public ColorStringWidget(Component message, Font font, SyncType syncType) {
        super(message, font);
        this.syncType = syncType;
    }

    @Override
    public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        final int MARKER_HEIGHT = 12;
        int markerX = getX();
        int markerY = getY() + (getHeight() - MARKER_HEIGHT) / 2;
        renderTypeMarker(graphics, markerX, markerY);
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(12, 1);
        int color = getMainColor() & 0xFFFFFF;
        graphics.fillGradient(
                getX() - 8, getY() - 1,
                getX() + width - 8, getY() + height - 1,
                0x38505E66, 0x10505E66
        );
        graphics.fillGradient(
                getX() - 8, getY() - 1,
                getX() + width - 8, getY() + height - 1,
                0x28000000 | color, color
        );
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        pose.popMatrix();
    }

    private void renderTypeMarker(GuiGraphicsExtractor graphics, int x, int y) {
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
