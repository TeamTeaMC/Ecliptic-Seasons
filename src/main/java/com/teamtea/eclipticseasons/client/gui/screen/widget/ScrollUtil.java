package com.teamtea.eclipticseasons.client.gui.screen.widget;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.ScrollableLayout;

import java.lang.reflect.Field;

public class ScrollUtil {
    public static final AbstractScrollArea.ScrollbarSettings scrollbarSettings =
            new AbstractScrollArea.ScrollbarSettings(
                    EclipticSeasons.rl("widget/es_scroller"),
                    null,
                    EclipticSeasons.rl("widget/es_scroller_background"),
                    6, 32,
                    10, true);

    public static ScrollableLayout setScrollbarSettings(ScrollableLayout layout) {
        try {
            Field containerField = ScrollableLayout.class.getDeclaredField("container");
            containerField.setAccessible(true);
            Object container = containerField.get(layout);
            Field settingsField = AbstractScrollArea.class.getDeclaredField("scrollbarSettings");
            settingsField.setAccessible(true);
            settingsField.set(container, scrollbarSettings);
        } catch (ReflectiveOperationException _) {
        }
        return layout;
    }

    public static double scrollAmount(ScrollableLayout layout) {
        if (layout == null) return 0;
        double[] amount = {0};
        layout.visitChildren(child -> {
            if (child instanceof AbstractScrollArea scrollArea) amount[0] = scrollArea.scrollAmount();
        });
        return amount[0];
    }

    public static void setScrollAmount(ScrollableLayout layout, double amount) {
        if (layout == null) return;
        layout.visitChildren(child -> {
            if (child instanceof AbstractScrollArea scrollArea) scrollArea.setScrollAmount(amount);
        });
    }
}
