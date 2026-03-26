package com.teamtea.eclipticseasons.api.data.client.ui;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.client.ui.elements.UIElement;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

@Data
@Builder
public class UIParser {
    public static final Codec<UIParser> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.BOOL.optionalFieldOf("sever_data",true).forGetter(o -> o.severData),
            Codec.BOOL.optionalFieldOf("stack", false).forGetter(o -> o.stack),
            Identifier.CODEC.optionalFieldOf("key").forGetter(o -> o.key),
            Identifier.CODEC.optionalFieldOf("file").forGetter(o -> o.file),
            UIElement.CODEC.listOf().fieldOf("fields").forGetter(o -> o.fields)
    ).apply(ins, UIParser::new));

    @Builder.Default
    private final boolean severData = true;
    private final boolean stack;

    private final Optional<Identifier> key;
    private final Optional<Identifier> file;

    @Singular
    private final List<UIElement> fields;

}
