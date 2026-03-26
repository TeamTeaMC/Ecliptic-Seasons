package com.teamtea.eclipticseasons.api.data.client.ui.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.resources.Identifier;

import java.util.List;

@Data
@Builder
public class ListElement implements UIElement {

    public static final MapCodec<ListElement> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                    Codec.STRING.fieldOf("id").forGetter(o -> o.id),
                    Codec.BOOL.optionalFieldOf("map", false).forGetter(o -> o.map),
                    UIElement.CODEC.listOf().fieldOf("items").forGetter(o -> o.items))
            .apply(ins, ListElement::new));


    private final String id;
    private final boolean map;
    private final List<UIElement> items;

    @Override
    public Identifier getType() {
        return UIElements.LIST;
    }

    @Override
    public MapCodec<? extends UIElement> codec() {
        return CODEC;
    }


    @Override
    public boolean isList() {
        return !map;
    }
}
