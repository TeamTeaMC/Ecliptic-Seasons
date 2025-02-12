package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.network.chat.Component;

public interface ITranslatable {
    Component getTranslation();

    String getName();

    int ordinal();


}
