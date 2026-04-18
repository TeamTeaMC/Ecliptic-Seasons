package com.teamtea.eclipticseasons.api.data.client.ui.elements;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;

@Data
@Builder
public class AutoCompletionElement implements UIElement {

    public static final MapCodec<AutoCompletionElement> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.STRING.fieldOf("id").forGetter(o -> o.id),
            Provide.CODEC.fieldOf("provide").forGetter(o -> o.provide),
            Codec.STRING.optionalFieldOf("key", Registries.BLOCK.identifier().toString()).forGetter(o -> o.key),
            (Codec.either(ExtraInfoHolder.CODEC,Codec.STRING))
                    .xmap(
                            e->e.left().orElse(new ExtraInfoHolder(e.right().orElse(""),e.right().orElse(""))),
                            Either::left
                    )
                    .listOf().optionalFieldOf("extra_info", List.of()).forGetter(o -> o.extraInfo)
    ).apply(ins, AutoCompletionElement::new));

    private final String id;
    private final Provide provide;
    private final String key;
    private final List<ExtraInfoHolder> extraInfo;

    @Override
    public Identifier getType() {
        return UIElements.AUTO_COMPLETION;
    }

    @Override
    public MapCodec<? extends UIElement> codec() {
        return CODEC;
    }


    public enum Provide implements StringRepresentable {
        HOLDER_SET, CLIENT_RESOURCE, BUILTIN, EXTRA;

        public static final Codec<Provide> CODEC = StringRepresentable.fromEnum(Provide::values);

        @Override
        public @NonNull String getSerializedName() {
            return toString().toLowerCase(Locale.ROOT);
        }
    }

    @Data
    public static class ExtraInfoHolder {
        public static final Codec<ExtraInfoHolder> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Codec.STRING.fieldOf("value").forGetter(o -> o.value),
                Codec.STRING.fieldOf("display").forGetter(o -> o.display)
        ).apply(ins, ExtraInfoHolder::new));

        private final String value;
        private final String display;
    }
}
