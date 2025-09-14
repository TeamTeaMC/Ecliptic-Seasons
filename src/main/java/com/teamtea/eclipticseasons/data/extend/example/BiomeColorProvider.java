package com.teamtea.eclipticseasons.data.extend.example;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.TemperateSolarTermColors;
import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.api.data.client.ColorMode;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientDataMapProvider;
import com.teamtea.eclipticseasons.data.general.datapack.DatapackRegistryGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BiomeColorProvider extends ESClientDataMapProvider<BiomeColor> {
    public BiomeColorProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_BIOME, BiomeColor.CODEC);
        this.registries = registries.thenApply(r -> DatapackRegistryGenerator.constructRegistries(r, DatapackRegistryGenerator.REGISTRY_SET_BUILDER));
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<Biome> biomeRegistryLookup = provider.lookupOrThrow(Registries.BIOME);
        var BIOME_REGISTRY_LOOKUP = new AgroClimateRegistry.BiomeRegistryLookup(biomeRegistryLookup);
        var aThrow = provider.lookupOrThrow(ESRegistries.AGRO_CLIMATE);

        add("plains", new BiomeColor(
                HolderSet.direct(Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.PLAINS)),
                SolarTermValueMap.<ColorMode>builder()
                        .putSeason(Season.SPRING, new ColorMode(Optional.of(Color.PINK.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.SUMMER, new ColorMode(Optional.of(Color.GREEN.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.AUTUMN, new ColorMode(Optional.of(Color.ORANGE.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.WINTER, new ColorMode(Optional.of(Color.BLUE.getRGB()), Optional.empty(), Optional.empty()))
                        .putSeason(Season.NONE, new ColorMode(Optional.of(Color.WHITE.getRGB()), Optional.empty(), Optional.empty()))
                        .ofBuild(), Optional.empty()
        ));

        SolarTermValueMap.Builder<ColorMode> builder = SolarTermValueMap.builder();
        for (TemperateSolarTermColors colors : TemperateSolarTermColors.collectValues()) {
            builder.putSolarTerm(SolarTerm.get(colors.ordinal()),
                    new ColorMode(Optional.of(colors.getLeaveColor()), Optional.of(colors.getMix()), Optional.empty()));
        }
        add("plains_2", new BiomeColor(
                HolderSet.direct(Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.PLAINS)),
                Optional.empty(), builder.ofBuild()
        ));

        SolarTermValueMap.Builder<ColorMode> builder2 = SolarTermValueMap.<ColorMode>builder()
                .putSeason(Season.SPRING, new ColorMode(Optional.of(Color.PINK.getRGB()), Optional.empty(), Optional.empty()))
                .putSeason(Season.SUMMER, new ColorMode(Optional.of(Color.GREEN.getRGB()), Optional.empty(), Optional.empty()))
                .putSeason(Season.AUTUMN, new ColorMode(Optional.of(Color.ORANGE.getRGB()), Optional.empty(), Optional.empty()))
                .putSeason(Season.WINTER, new ColorMode(Optional.of(Color.BLUE.getRGB()), Optional.empty(), Optional.empty()))
                .putSeason(Season.NONE, new ColorMode(Optional.of(Color.WHITE.getRGB()), Optional.empty(), Optional.empty()));
        builder2.climate(aThrow.getOrThrow(AgroClimateRegistry.COLD));
        add("snowy_plains", new BiomeColor(
                HolderSet.direct(Holder.Reference.createStandAlone(BIOME_REGISTRY_LOOKUP, Biomes.SNOWY_PLAINS)),
                Optional.empty(), builder2.ofBuild(), builder2.ofBuild()
        ));

    }
}
