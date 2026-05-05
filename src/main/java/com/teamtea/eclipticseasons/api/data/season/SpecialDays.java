package com.teamtea.eclipticseasons.api.data.season;


import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

@Data
@Builder
public class SpecialDays {

    public static final JsonElement JSON_ELEMENT_EMPTY =Component.Serializer.toJsonTree(Component.empty());

    public static final Codec<SpecialDays> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.floatRange(0, 1).optionalFieldOf("start", 0f).forGetter(o -> o.start),
            Codec.floatRange(0, 1).optionalFieldOf("end", 0f).forGetter(o -> o.end),
            Codec.INT.optionalFieldOf("lasting_days", 0).forGetter(o -> o.lastingDays),
            ESExtraCodec.SOLAR_TERM.fieldOf("term").forGetter(o -> o.term),
            ExtraCodecs.JSON.fieldOf("title").forGetter(o -> Component.Serializer.toJsonTree(o.title)),
            ExtraCodecs.JSON.optionalFieldOf("alternation", JSON_ELEMENT_EMPTY).forGetter(o -> Component.Serializer.toJsonTree(o.alternation))
    ).apply(ins, (start1, end1, lastingDays1, term1, title1, alternation1) ->
            new SpecialDays(start1, end1, lastingDays1, term1, Component.Serializer.fromJson(title1), Component.Serializer.fromJson(alternation1))));

    @Builder.Default
    public final float start = 0;
    @Builder.Default
    public final float end = 0;
    @Builder.Default
    public final int lastingDays = 0;
    @NonNull
    public final SolarTerm term;
    // @NonNull
    // public final Identifier icon;
    @Builder.Default
    public final Component title = Component.empty();
    @Builder.Default
    public final Component alternation = Component.empty();

}
