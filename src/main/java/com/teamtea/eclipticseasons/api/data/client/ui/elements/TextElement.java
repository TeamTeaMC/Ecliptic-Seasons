package com.teamtea.eclipticseasons.api.data.client.ui.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.Identifier;

@Data
@Builder
public class TextElement implements UIElement {

    public static final MapCodec<TextElement> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                    Codec.STRING.fieldOf("id").forGetter(o -> o.id))
            .apply(ins, TextElement::new));


    private final String id;

    @Override
    public Identifier getType() {
        return UIElements.NUMBER;
    }

    @Override
    public MapCodec<? extends UIElement> codec() {
        return CODEC;
    }

}
