package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public interface ITranslatable extends StringRepresentable {
    Component getTranslation();

    String getName();

    int ordinal();

    @Override
    default @NotNull String getSerializedName() {
        return getName();
    }
}
