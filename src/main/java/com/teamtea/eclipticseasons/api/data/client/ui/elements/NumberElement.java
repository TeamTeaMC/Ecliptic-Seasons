package com.teamtea.eclipticseasons.api.data.client.ui.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@Data
@Builder
public class NumberElement implements UIElement {

    public static final MapCodec<NumberElement> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                    Codec.STRING.fieldOf("id").forGetter(o -> o.id))
            .apply(ins, NumberElement::new));


    private final String id;

    @Override
    public Identifier getType() {
        return UIElements.NUMBER;
    }

    @Override
    public MapCodec<? extends UIElement> codec() {
        return CODEC;
    }


    @Override
    public boolean isNumber() {
        return true;
    }
}
