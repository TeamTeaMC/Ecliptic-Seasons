package com.teamtea.eclipticseasons.data.general.datapack.client;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBiomeAmbient;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.common.registry.SoundEventsRegistry;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientBiomeDataMapProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class SeasonalBiomeAmbientProvider extends ESClientBiomeDataMapProvider<SeasonalBiomeAmbient> {
    public SeasonalBiomeAmbientProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_AMBIENT, SeasonalBiomeAmbient.CODEC);
    }


    @Override
    protected void gather(HolderLookup.Provider provider, HolderGetter<Biome> biomeHolderGetter) {

        HolderLookup.RegistryLookup<SoundEvent> slp = provider.lookupOrThrow(Registries.SOUND_EVENT);

        add("spring", SeasonalBiomeAmbient.builder().season(Season.SPRING)
                .biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/spring"))))
                .ignored_biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/spring_negate"))))
                .sound(getSoundHolder(slp, SoundEventsRegistry.spring_forest)).build());

        add("summer_day", SeasonalBiomeAmbient.builder().season(Season.SUMMER)
                .biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/summer_day"))))
                .ignore_time(false).day(true).sound(getSoundHolder(slp, SoundEventsRegistry.garden_wind)).build());

        add("summer_night", SeasonalBiomeAmbient.builder().season(Season.SUMMER)
                .ignored_biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/summer_night_negate"))))
                .ignore_time(false).day(false).sound(getSoundHolder(slp, SoundEventsRegistry.night_river)).day(false).build());

        add("autumn", SeasonalBiomeAmbient.builder().season(Season.AUTUMN)
                .biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/autumn"))))
                .sound(getSoundHolder(slp, SoundEventsRegistry.windy_leave)).build());


        add("winter_snow", SeasonalBiomeAmbient.builder().season(Season.WINTER)
                .biomes(get(TagKey.create(Registries.BIOME,
                        EclipticSeasons.rl("misc/ambient/winter_snow"))))
                .ignored_biomes(get(Tags.Biomes.IS_CAVE))
                .sound(getSoundHolder(slp, SoundEventsRegistry.winter_forest)).rain(true).priority(950).build());

        add("winter_wind", SeasonalBiomeAmbient.builder().season(Season.WINTER)
                .ignored_biomes(get(Tags.Biomes.IS_CAVE))
                .rain(true).sound(getSoundHolder(slp, SoundEventsRegistry.winter_cold)).build());

    }

    protected ResourceLocation getSoundHolder(HolderLookup.RegistryLookup<SoundEvent> lookup, SoundEvent soundEvent) {
        return (soundEvent.getLocation());
    }
}