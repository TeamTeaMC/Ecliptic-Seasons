package com.teamtea.eclipticseasons.api.misc;

import com.teamtea.eclipticseasons.common.misc.SimplePair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public interface ISolarTerm {
    MutableComponent getTranslation();
    MutableComponent getTittleTranslation();
    MutableComponent getPatternTranslation();
    MutableComponent getAlternationText();
    ChatFormatting getColor();
    ResourceLocation getIconFont();
    ResourceLocation getIcon();
    String getFontLabel();
    SimplePair<Integer, Integer> getIconPosition();
    int getIconAtlasSize();
    int getIconWidth();
    int getIconHeight();
}
