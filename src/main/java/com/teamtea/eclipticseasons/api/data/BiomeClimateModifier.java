package com.teamtea.eclipticseasons.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.TestOnly;

import java.util.List;

@TestOnly
public record BiomeClimateModifier(
        boolean apply,
        HolderSet<Biome> biome,
        List<BiomeClimate> biomeClimateList
) {
    public static final Codec<BiomeClimateModifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.BOOL.optionalFieldOf("apply", false).orElse(false).forGetter(BiomeClimateModifier::apply),
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biome").forGetter(BiomeClimateModifier::biome),
            BiomeClimate.CODEC.listOf().fieldOf("modifiers").forGetter(BiomeClimateModifier::biomeClimateList)
    ).apply(builder, BiomeClimateModifier::new));

}
