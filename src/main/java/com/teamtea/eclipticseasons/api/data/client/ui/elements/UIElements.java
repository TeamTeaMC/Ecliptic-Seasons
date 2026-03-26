package com.teamtea.eclipticseasons.api.data.client.ui.elements;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class UIElements {
    public static final Map<Identifier, MapCodec<? extends UIElement>> EFFECTS = new HashMap<>();

    public static final Identifier AUTO_COMPLETION = EclipticSeasons.rl("auto_completion");
    public static final Identifier BOOL = EclipticSeasons.rl("bool");
    public static final Identifier NUMBER = EclipticSeasons.rl("number");
    public static final Identifier STRING = EclipticSeasons.rl("text");
    public static final Identifier LIST = EclipticSeasons.rl("list");

    public static void register(Identifier id, MapCodec<? extends UIElement> codec) {
        EFFECTS.put(id, codec);
    }

    static {
        register(AUTO_COMPLETION, AutoCompletionElement.CODEC);
        register(BOOL, BoolElement.CODEC);
        register(NUMBER, NumberElement.CODEC);
        register(STRING, TextElement.CODEC);
        register(LIST, ListElement.CODEC);
    }
}
