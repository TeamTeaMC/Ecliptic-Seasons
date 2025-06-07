package com.teamtea.eclipticseasons.api.data.climate;


import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.TestOnly;

import java.util.Optional;


// TODO这些东西应该进行拆分
// note 比如，biome的patch类修饰温度变化和降水，下雨和打雷概率以及时长修饰，雪期和雪线修饰，群系颜色
// note 以及设置群系组

@TestOnly
public record BiomeClimate(
        Optional<Float> tempChange,
        Optional<Float> rainChange,
        Optional<Float> rainChance,
        Optional<Either<String, Integer>> grassColor,
        Optional<Float> grassColorMix,
        Optional<Either<String, Integer>> foliageColor,
        Optional<Float> foliageColorMix
) {

    public static final Codec<BiomeClimate> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.FLOAT.optionalFieldOf("tempChange").forGetter(BiomeClimate::tempChange),
            Codec.FLOAT.optionalFieldOf("rainChange").forGetter(BiomeClimate::rainChange),
            Codec.FLOAT.optionalFieldOf("rainChance").forGetter(BiomeClimate::rainChance),
            Codec.either(Codec.STRING, Codec.INT).optionalFieldOf("grassColor").forGetter(BiomeClimate::grassColor),
            Codec.FLOAT.optionalFieldOf("grassColorMix").forGetter(BiomeClimate::grassColorMix),
            Codec.either(Codec.STRING, Codec.INT).optionalFieldOf("foliageColor").forGetter(BiomeClimate::foliageColor),
            Codec.FLOAT.optionalFieldOf("foliageColorMix").forGetter(BiomeClimate::foliageColorMix)
    ).apply(ins, BiomeClimate::new));

}
