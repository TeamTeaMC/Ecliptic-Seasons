package com.teamtea.eclipticseasons.api.data.client.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.client.model.multipart.MultiPartLike;
import com.teamtea.eclipticseasons.api.data.client.model.variant.MultiVariantLike;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class ESModelLoadedJson {
    public static final MapCodec<Map<String, MultiVariantLike>> VARIANTS = CodecUtil.mapCodec(Codec.STRING, MultiVariantLike.CODEC).optionalFieldOf("variants", ImmutableMap.of());
    public static final MapCodec<MultiPartLike> MULTIPART = MultiPartLike.CODEC.optionalFieldOf("multipart", MultiPartLike.EMPTY);
    // public static final Codec<ESModelLoadedJson> CODEC = RecordCodecBuilder.create(ins -> ins.group(
    //         Codec.BOOL.optionalFieldOf("replace", false).forGetter(o -> o.replace),
    //         VARIANTS.forGetter(o -> o.variants)
    // ).apply(ins, ESModelLoadedJson::new));

    public static final MapCodec<ESModelLoadedJson> MAP_CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            CodecUtil.listFrom(Codec.STRING).optionalFieldOf("require", List.of()).forGetter(c -> c.require),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(c -> c.replace),
            VARIANTS.forGetter(c -> c.variants),
            MULTIPART.forGetter(c -> c.multiPartLike)
    ).apply(ins, ESModelLoadedJson::new));

    public static final Codec<ESModelLoadedJson> CODEC = MAP_CODEC.codec();

    @Singular("requirement")
    private final List<String> require;

    @Builder.Default
    private final boolean replace = false;
    @Singular
    private final Map<String, MultiVariantLike> variants;
    @Builder.Default
    private final MultiPartLike multiPartLike = MultiPartLike.EMPTY;

    public static final String ALL_VARIANT = "";
}
