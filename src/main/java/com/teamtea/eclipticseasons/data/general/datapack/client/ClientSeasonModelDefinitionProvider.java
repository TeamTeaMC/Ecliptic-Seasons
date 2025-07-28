package com.teamtea.eclipticseasons.data.general.datapack.client;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonBlockDefinition;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientBiomeDataMapProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientSeasonModelDefinitionProvider extends ESClientBiomeDataMapProvider<SeasonBlockDefinition> {


    public ClientSeasonModelDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_SEASON_DEFINITION, SeasonBlockDefinition.CODEC);
    }

    @Override
    protected void gather(HolderLookup.Provider provider, HolderGetter<Biome> biomeRegistryLookup) {
        HolderLookup.RegistryLookup<Block> blockRegistryLookup = provider.lookupOrThrow(Registries.BLOCK);

        List<SeasonBlockDefinition.Slice> slices = new ArrayList<>();
        for (SolarTerm solarTerm : SolarTerm.collectValues()) {
            if (solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SPRING, SolarTerm.GRAIN_RAIN)) {
                ResourceLocation rl = EclipticSeasons.rl("flower_on_grass_" + solarTerm.getName());
                slices.add(SeasonBlockDefinition.Slice.builder().solarTerm(solarTerm).mid(rl).build());
            }
            if (solarTerm.isInTerms(SolarTerm.LESSER_FULLNESS, SolarTerm.BEGINNING_OF_AUTUMN)) {
                ResourceLocation rl = EclipticSeasons.rl("fourleaf_clovers_" + solarTerm.getName());
                slices.add(SeasonBlockDefinition.Slice.builder().solarTerm(solarTerm).mid(rl).build());
            }
        }

        SolarTerm beginningOfSummer = SolarTerm.BEGINNING_OF_SUMMER;
        slices.add(SeasonBlockDefinition.Slice.builder().solarTerm(beginningOfSummer)
                .transitionModels(
                        Pair.of(EclipticSeasons.rl("flower_on_grass_" + beginningOfSummer.getName()),
                                EclipticSeasons.rl("fourleaf_clovers_" + beginningOfSummer.getName()))).build());

        add(SeasonBlockDefinition.GRASS_BLOCK, new SeasonBlockDefinition(HolderSet.direct(Blocks.GRASS_BLOCK.builtInRegistryHolder()),
                and(get(ClimateTypeBiomeTags.SEASONAL),
                        not(get(Tags.Biomes.IS_HOT)),
                        not(get(Tags.Biomes.IS_COLD))),
                slices
        ));
    }


    private static String getPath(ResourceLocation overlay) {
        return overlay.getPath();
    }


}
