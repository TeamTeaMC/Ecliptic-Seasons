package com.teamtea.eclipticseasons.data.datapack.client;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBiomeAmbient;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.SoundEventsRegistry;
import com.teamtea.eclipticseasons.data.datapack.client.base.ESClientBiomeDataMapProvider;
import com.teamtea.eclipticseasons.data.datapack.client.base.ESClientDataMapProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class SeasonalBiomeAmbientProvider extends ESClientBiomeDataMapProvider<SeasonalBiomeAmbient> {
    public SeasonalBiomeAmbientProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_AMBIENT, SeasonalBiomeAmbient.CODEC);
    }


    @Override
    protected void gather(HolderLookup.Provider provider, HolderGetter<Biome> biomeHolderGetter){

        HolderLookup.RegistryLookup<SoundEvent> slp = provider.lookupOrThrow(Registries.SOUND_EVENT);

        add("spring", SeasonalBiomeAmbient.builder().season(Season.SPRING).biomes(
                and(or(get(Biomes.CHERRY_GROVE),
                                get(BiomeTags.IS_FOREST),
                                get(Tags.Biomes.IS_PLAINS)),
                        not(or(get(Tags.Biomes.IS_COLD),get(BiomeTags.IS_OCEAN),get(BiomeTags.IS_BEACH))))
        ).sound(getSoundHolder(slp, SoundEventsRegistry.spring_forest)).build());

        add("summer_day", SeasonalBiomeAmbient.builder().season(Season.SUMMER).biomes(
                and(or(get(Biomes.CHERRY_GROVE),
                        get(BiomeTags.IS_FOREST),
                        get(Tags.Biomes.IS_PLAINS),
                        get(BiomeTags.IS_RIVER)))
        ).ignore_time(false).day(true).sound(getSoundHolder(slp, SoundEventsRegistry.garden_wind)).build());

        add("summer_night", SeasonalBiomeAmbient.builder().season(Season.SUMMER).biomes(
                not(or(get(BiomeTags.IS_SAVANNA),
                        get(Tags.Biomes.IS_CAVE),
                        get(Tags.Biomes.IS_DESERT),
                        get(BiomeTags.IS_BADLANDS),
                        get(Tags.Biomes.IS_PEAK),
                        get(BiomeTags.IS_OCEAN)))
        ).ignore_time(false).day(false).sound(getSoundHolder(slp, SoundEventsRegistry.night_river)).build());

        add("autumn", SeasonalBiomeAmbient.builder().season(Season.AUTUMN).biomes(
                or(get(Biomes.CHERRY_GROVE),
                        get(BiomeTags.IS_FOREST))
        ).sound(getSoundHolder(slp, SoundEventsRegistry.windy_leave)).build());


        add("winter_snow", SeasonalBiomeAmbient.builder().season(Season.WINTER).biomes(
                and(not(get(Tags.Biomes.IS_CAVE)),
                        or(get(Biomes.CHERRY_GROVE),
                                get(BiomeTags.IS_FOREST)))
        ).sound(getSoundHolder(slp, SoundEventsRegistry.winter_forest)).rain(true).priority(950).build());

        add("winter_wind", SeasonalBiomeAmbient.builder().season(Season.WINTER).biomes(
                not(get(Tags.Biomes.IS_CAVE))
        ).rain(true).sound(getSoundHolder(slp, SoundEventsRegistry.winter_cold)).build());

    }

    protected Holder<SoundEvent> getSoundHolder(HolderLookup.RegistryLookup<SoundEvent> lookup, SoundEvent soundEvent) {
        return lookup.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, soundEvent.getLocation()));
    }
}