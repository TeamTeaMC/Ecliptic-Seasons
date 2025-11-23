package com.teamtea.eclipticseasons.api.data.client.ui.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.resources.ResourceLocation;

public interface UIElement {
    Codec<UIElement> CODEC = Codec.lazyInitialized(() ->
            Codec.STRING
                    .xmap(s -> s.contains(":") ? ResourceLocation.parse(s) : EclipticSeasons.rl(s),
                            r -> r.getNamespace().equals(EclipticSeasonsApi.MODID) ? r.getPath() : r.toString())
                    .dispatch("type", UIElement::getType, UIElements.EFFECTS::get));

    ResourceLocation getType();

    MapCodec<? extends UIElement> codec();

    String getId();

    default boolean isNumber() {
        return false;
    }

    default boolean isList() {
        return false;
    }

    default boolean isMap() {
        return false;
    }
}
