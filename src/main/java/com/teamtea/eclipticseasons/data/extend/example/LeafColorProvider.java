package com.teamtea.eclipticseasons.data.extend.example;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.client.ColorMode;
import com.teamtea.eclipticseasons.api.data.client.LeafColor;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientDataMapProvider;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LeafColorProvider extends ESClientDataMapProvider<LeafColor> {
    public LeafColorProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_LEAF, LeafColor.CODEC);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<Biome> biomeRegistryLookup = provider.lookupOrThrow(Registries.BIOME);

        add("test", new LeafColor(
                LeafColor.ColorSource.CUSTOM,
                BlockPredicate.Builder.block().of(Blocks.PUMPKIN).build(),
                Optional.of(LocationPredicate.Builder.inBiome(biomeRegistryLookup.getOrThrow(Biomes.PLAINS)).build()),
                SolarTermValueMap.<ColorMode>builder()
                        .putSeason(Season.SPRING, new ColorMode(Optional.of(Color.PINK.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.SUMMER, new ColorMode(Optional.of(Color.GREEN.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.AUTUMN, new ColorMode(Optional.of(Color.ORANGE.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.WINTER, new ColorMode(Optional.of(Color.BLUE.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.NONE, new ColorMode(Optional.of(Color.WHITE.getRGB()), Optional.empty(), Optional.empty()))
                        .ofBuild(),
                SolarTermValueMap.<List<ResourceLocation>>builder()
                        .defaultValue(List.of(ResourceLocation.withDefaultNamespace("flash_test"))).ofBuild(),
                SolarTermValueMap.<Integer>builder()
                        .defaultValue(100000).ofBuild(),
                Optional.of(false)
        ));
    }
}
