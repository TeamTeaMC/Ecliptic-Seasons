package com.teamtea.eclipticseasons.data.extend.example;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.TemperateSolarTermColors;
import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.api.data.client.ColorMode;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientDataMapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BiomeColorProvider extends ESClientDataMapProvider<BiomeColor> {
    public BiomeColorProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider>  registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_BIOME, BiomeColor.CODEC);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<Biome> biomeRegistryLookup = provider.lookupOrThrow(Registries.BIOME);
        add("plains",new BiomeColor(
                biomeRegistryLookup.getOrThrow(Tags.Biomes.IS_PLAINS),
                SolarTermValueMap.<ColorMode>builder()
                        .putSeason(Season.SPRING,new ColorMode(Optional.of(Color.PINK.getRGB()),Optional.empty(),Optional.empty()))
                        .putSeason(Season.SUMMER,new ColorMode(Optional.of(Color.GREEN.getRGB()),Optional.empty(),Optional.empty()))
                        .putSeason(Season.AUTUMN,new ColorMode(Optional.of(Color.ORANGE.getRGB()),Optional.empty(),Optional.empty()))
                        .putSeason(Season.WINTER,new ColorMode(Optional.of(Color.BLUE.getRGB()),Optional.empty(),Optional.empty()))
                        .putSeason(Season.NONE,new ColorMode(Optional.of(Color.WHITE.getRGB()),Optional.empty(),Optional.empty()))
                        .ofBuild(),Optional.empty()
        ));

        SolarTermValueMap.Builder<ColorMode> builder = SolarTermValueMap.builder();
        for (TemperateSolarTermColors colors : TemperateSolarTermColors.collectValues()) {
            builder.putSolarTerm(SolarTerm.get(colors.ordinal()),
                    new ColorMode(Optional.of(colors.getLeaveColor()), Optional.of(colors.getMix()), Optional.empty()));
        }
        add("plains_2", new BiomeColor(
                HolderSet.direct(biomeRegistryLookup.getOrThrow(Biomes.PLAINS)),
                Optional.empty(), builder.ofBuild()
        ));
    }
}
