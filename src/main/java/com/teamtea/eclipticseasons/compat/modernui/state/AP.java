package com.teamtea.eclipticseasons.compat.modernui.state;

import org.jetbrains.annotations.NotNull;

public record AP(Object resourceLocation, String text, boolean isTag) {
    public AP(Object resourceLocation, String text) {
        this(resourceLocation, text, false);
    }

    public String getText() {
        return isTag ? "#" + resourceLocation.toString() : resourceLocation.toString();
    }

    @Override
    public @NotNull String toString() {
        return text;
    }
}
