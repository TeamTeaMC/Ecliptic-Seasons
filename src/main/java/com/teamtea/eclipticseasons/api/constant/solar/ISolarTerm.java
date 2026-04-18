package com.teamtea.eclipticseasons.api.constant.solar;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public interface ISolarTerm {
    MutableComponent getTranslation();
    MutableComponent getTittleTranslation();
    MutableComponent getPatternTranslation();
    MutableComponent getAlternationText();
    ChatFormatting getColor();
    Identifier getIconFont();
    Identifier getIcon();
    String getFontLabel();
    Pair<Integer, Integer> getIconPosition();
    int getIconAtlasSize();
    int getIconWidth();
    int getIconHeight();
    Season getSeason();
}
