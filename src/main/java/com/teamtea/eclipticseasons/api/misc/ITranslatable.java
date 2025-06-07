package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface ITranslatable extends StringRepresentable {
    Component getTranslation();

    default String getName(){
        return this.toString().toLowerCase(Locale.ROOT);
    }

    int ordinal();

    @Override
    default @NotNull String getSerializedName() {
        return getName();
    }
}
