package com.teamtea.eclipticseasons.compat.modernui.base;

import icyllis.modernui.widget.TabLayout;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SimpleOnTabSelectedListener extends TabLayout.OnTabSelectedListener {
    @Override
    default void onTabSelected(TabLayout.@NotNull Tab tab) {
        on(tab);
    }

    void on(TabLayout.Tab tab);
}
