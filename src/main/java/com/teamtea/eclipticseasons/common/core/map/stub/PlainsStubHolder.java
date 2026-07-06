package com.teamtea.eclipticseasons.common.core.map.stub;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

/**
 * A dummy {@link Holder.Reference} implementation used strictly as a compile-time or runtime 
 * safety net (stub/fallback). This prevents NullPointerExceptions or game crashes when an abnormal 
 * or uninitialized {@link net.minecraft.world.level.Level} context fails to provide a valid registry access.
 */
public class PlainsStubHolder extends Holder.Reference<Biome> {

    /**
     * Emergency fallback instance representing the standard Plains biome.
     */
    public static final Holder.Reference<Biome> PLAINS = new PlainsStubHolder(
            Type.STAND_ALONE,
            new DummyHolderOwner<>(),
            Biomes.PLAINS,
            createPlainsBiomeBehavior().build()
    );

    /**
     * Emergency fallback instance representing the Void (The Void) biome, 
     * typically used for empty dimensions or unconstructed world contexts.
     */
    public static final Holder.Reference<Biome> VOID = new PlainsStubHolder(
            Type.STAND_ALONE,
            new DummyHolderOwner<>(),
            Biomes.THE_VOID,
            createVoidBiomeBehavior().build()
    );

    private PlainsStubHolder(Type type, HolderOwner<Biome> owner,
                                @Nullable ResourceKey<Biome> key,
                                @Nullable Biome value) {
        super(type, owner, key, value);
    }

    @Override
    public @NonNull Stream<TagKey<Biome>> tags() {
        return Stream.empty();
    }

    @Override
    public boolean is(@NonNull TagKey<Biome> tag) {
        return false;
    }

    // ========= STATIC DUMMY BIOME BUILDERS =========

    private static final int DEFAULT_WATER_COLOR = 4159204;

    /**
     * Constructs a minimal, hardcoded Plains biome behavior for safety fallback.
     */
    private static Biome.BiomeBuilder createPlainsBiomeBehavior() {
        float temperature = 0.5f;
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(temperature)
                .downfall(0.5f)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, OverworldBiomes.calculateSkyColor(temperature))
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(DEFAULT_WATER_COLOR).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(new BiomeGenerationSettings(HolderSet.empty(), List.of()));
    }

    /**
     * Constructs a minimal, hardcoded Void biome behavior for safety fallback.
     */
    private static Biome.BiomeBuilder createVoidBiomeBehavior() {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.5f)
                .downfall(0.0f)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, 8103167) // Standard sky color for the void
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(DEFAULT_WATER_COLOR).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(new BiomeGenerationSettings(HolderSet.empty(), List.of()));
    }
}