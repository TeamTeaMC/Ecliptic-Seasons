package com.teamtea.eclipticseasons.api.data;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.TestOnly;

import java.util.Optional;

/**
 * {
 * type:khjxiaogu:datapack,
 * index:1,
 * biome:plains,
 * temp:0,
 * down:10
 * }
 **/
@TestOnly
public record BiomeClimate(
        int index,
        Optional<Float> temp,
        Optional<Float> rain
) {
    public static final Codec<BiomeClimate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            // Codec.BOOL.optionalFieldOf("apply", false).orElse(false).forGetter(BiomeClimate::apply),
            // RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biome").forGetter(BiomeClimate::biome),
            Codec.INT.fieldOf("index").forGetter(BiomeClimate::index),
            Codec.FLOAT.optionalFieldOf("temp").forGetter(BiomeClimate::temp),
            Codec.FLOAT.optionalFieldOf("rain").forGetter(BiomeClimate::rain)
    ).apply(builder, BiomeClimate::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, BiomeClimate> STREAM_CODEC = StreamCodec.composite(
            // ByteBufCodecs.BOOL,
            // solarTermsMessage -> solarTermsMessage.apply,
            // ByteBufCodecs.holderSet(Registries.BIOME),
            // solarTermsMessage -> solarTermsMessage.biome,
            ByteBufCodecs.INT,
            solarTermsMessage -> solarTermsMessage.index,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT),
            solarTermsMessage -> solarTermsMessage.temp,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT),
            solarTermsMessage -> solarTermsMessage.rain,
            BiomeClimate::new
    );

    public SolarTerm solarTerm() {
        return SolarTerm.collectValues()[index];
    }
}
