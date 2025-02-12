package com.teamtea.eclipticseasons.api.misc;


import net.minecraft.util.text.ITextComponent;


public interface ITranslatable {
    ITextComponent getTranslation();

    String getName();

    int ordinal();


}
