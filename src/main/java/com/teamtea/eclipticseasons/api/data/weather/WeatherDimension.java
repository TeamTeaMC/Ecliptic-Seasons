package com.teamtea.eclipticseasons.api.data.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record WeatherDimension(Holder<Biome> core,
                               ResourceKey<Level> dimension) {

    public static final Codec<WeatherDimension> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderCodec(Registries.BIOME).fieldOf("core").forGetter(WeatherDimension::core),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(WeatherDimension::dimension)
    ).apply(ins, WeatherDimension::new));
}
