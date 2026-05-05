package com.teamtea.eclipticseasons.data.extend.example;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBackgroundMusic;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.common.registry.SpecialDaysRegistry;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientBiomeDataMapProvider;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.concurrent.CompletableFuture;

public class SeasonalBackgroundMusicProvider extends ESClientBiomeDataMapProvider<SeasonalBackgroundMusic> {
    public SeasonalBackgroundMusicProvider(PackOutput output, String modid, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, registries, ClientJsonCacheListener.DIRECTORY_BACKGROUND_MUSIC, SeasonalBackgroundMusic.CODEC);
        CompletableFuture<RegistrySetBuilder.PatchedRegistries> lookup = RegistryPatchGenerator
                .createLookup(registries,
                        new RegistrySetBuilder()
                                .add(ESRegistries.SPECIAL_DAYS, (c) -> {
                                })
                );
        lookup.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        this.registries = lookup.thenApply(RegistrySetBuilder.PatchedRegistries::full);
    }

    @Override
    protected void gather(HolderLookup.Provider provider, HolderGetter<Biome> biomeRegistryLookup) {

        var slp = new AgroClimateRegistry.BiomeRegistryLookup<>(provider.lookupOrThrow(Registries.SOUND_EVENT), Registries.SOUND_EVENT);
        var cck = new AgroClimateRegistry.BiomeRegistryLookup<>(provider.lookupOrThrow(ESRegistries.SPECIAL_DAYS), ESRegistries.SPECIAL_DAYS);

        add("mid_autumn", SeasonalBackgroundMusic.builder()
                // .season(Season.SPRING)
                .biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/spring"))))
                .ignore_time(false)
                .day(false)
                .ignored_biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/spring_negate"))))
                .specialDays(HolderSet.direct(cck.getOrThrow(SpecialDaysRegistry.MID_AUTUMN)))
                .music(new SeasonalBackgroundMusic.BackgroundMusicBuilder(new SeasonalBackgroundMusic.MusicBuilder(EclipticSeasons.rl("music.mid_autumn"), 1000, 25000, false))).build());
        add("christmas", SeasonalBackgroundMusic.builder()
                // .season(Season.SPRING)
                .biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/spring"))))
                .ignore_time(false)
                .day(false)
                .ignored_biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/spring_negate"))))
                .specialDays(HolderSet.direct(cck.getOrThrow(SpecialDaysRegistry.CHRISTMAS)))
                .music(new SeasonalBackgroundMusic.BackgroundMusicBuilder(new SeasonalBackgroundMusic.MusicBuilder(EclipticSeasons.rl("music.gacha_bells"), 1000, 25000, false))).build());

    }

    private static ResourceKey<SoundEvent> createKey(String name) {
        return ResourceKey.create(Registries.SOUND_EVENT, EclipticSeasons.rl(name));
    }

    protected Holder<SoundEvent> getSoundHolder(HolderLookup.RegistryLookup<SoundEvent> lookup, SoundEvent soundEvent) {
        return lookup.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, soundEvent.location()));
    }
}