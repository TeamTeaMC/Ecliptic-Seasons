package com.teamtea.eclipticseasons.data.datapack.client;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.client.ColorMode;
import com.teamtea.eclipticseasons.api.data.client.LeafColor;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.datapack.client.base.ESClientDataMapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

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
                HolderSet.direct(Blocks.PUMPKIN.builtInRegistryHolder()),
                Optional.of(HolderSet.direct(biomeRegistryLookup.getOrThrow(Biomes.PLAINS))),
                SolarTermValueMap.<ColorMode>builder()
                        .putSeason(Season.SPRING, new ColorMode(Optional.of(Color.PINK.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.SUMMER, new ColorMode(Optional.of(Color.GREEN.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.AUTUMN, new ColorMode(Optional.of(Color.ORANGE.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.WINTER, new ColorMode(Optional.of(Color.BLUE.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.NONE, new ColorMode(Optional.of(Color.WHITE.getRGB()), Optional.empty(), Optional.empty()))
                        .ofBuild(),
                SolarTermValueMap.<List<ResourceLocation>>builder()
                        .defaultValue(List.of(new ResourceLocation("spark_1"))).ofBuild(),
                SolarTermValueMap.<Integer>builder()
                        .defaultValue(100000).ofBuild()
        ));
    }
}
