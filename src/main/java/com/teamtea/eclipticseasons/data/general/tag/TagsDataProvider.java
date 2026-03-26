package com.teamtea.eclipticseasons.data.general.tag;

import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TagsDataProvider extends KeyTagProvider<Biome> {


    public TagsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, Registries.BIOME, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        for (TagKey<Biome> biomeType : ClimateTypeBiomeTags.OVERWORLD_AGRO_BIOME_TYPES) {
            tag(biomeType);
        }


        for (TagKey<Biome> biomeType : ClimateTypeBiomeTags.BIOME_TYPES) {
            TagKey<Biome> oldTag = ClimateTypeBiomeTags.create(biomeType.location().getPath().replace("rain/", ""));
            tag(oldTag);
            tag(biomeType).addTag(oldTag);
        }

        for (TagKey<Biome> biomeType : ClimateTypeBiomeTags.BIOME_COLOR_TYPES) {
            tag(biomeType);
        }

        tag(ClimateTypeBiomeTags.IS_SMALL).addTags(Tags.Biomes.IS_RIVER);

        tag(ClimateTypeBiomeTags.EXTREME_COLD).addTags(Tags.Biomes.IS_ICY, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_MOUNTAIN_PEAK);
        // tag(ClimateTypeBiomeTags.SEASONAL).addTags(Tags.Biomes.IS_OVERWORLD, Tags.Biomes.IS_VOID);
        // tag(ClimateTypeBiomeTags.SEASONAL_HOT).addTags(Tags.Biomes.IS_HOT_OVERWORLD);
        // tag(ClimateTypeBiomeTags.SEASONAL_COLD).addTags(Tags.Biomes.IS_MOUNTAIN_PEAK, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_ICY);
        //
        // tag(ClimateTypeBiomeTags.MONSOONAL).addTags(Tags.Biomes.IS_SAVANNA);
        // tag(ClimateTypeBiomeTags.RAINLESS).addTags(Tags.Biomes.IS_CAVE);
        // tag(ClimateTypeBiomeTags.ARID).addTags(Tags.Biomes.IS_BADLANDS, Tags.Biomes.IS_DESERT);
        // tag(ClimateTypeBiomeTags.DROUGHTY).addTags();
        // tag(ClimateTypeBiomeTags.SOFT).addTags(Tags.Biomes.IS_BEACH, Tags.Biomes.IS_OCEAN);
        // tag(ClimateTypeBiomeTags.RAINY).add(Biomes.JUNGLE);
        //
        // tag(ClimateTypeBiomeTags.IS_SMALL).addTags(Tags.Biomes.IS_RIVER);
        //
        // // Biome Color
        // tag(ClimateTypeBiomeTags.SEASONAL_COLOR_CHANGE).addTags(Tags.Biomes.IS_OVERWORLD);
        // tag(ClimateTypeBiomeTags.SEASONAL_HOT_COLOR_CHANGE).addTags(Tags.Biomes.IS_HOT_OVERWORLD);
        // tag(ClimateTypeBiomeTags.SEASONAL_COLD_COLOR_CHANGE).addTags(Tags.Biomes.IS_MOUNTAIN_PEAK, Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_ICY);
        //
        // tag(ClimateTypeBiomeTags.MONSOONAL_COLOR_CHANGE).addTags(ClimateTypeBiomeTags.MONSOONAL);
        // tag(ClimateTypeBiomeTags.NONE_COLOR_CHANGE).addTags(Tags.Biomes.IS_CAVE, Tags.Biomes.IS_BADLANDS, Tags.Biomes.IS_DESERT, Tags.Biomes.IS_VOID);
        // tag(ClimateTypeBiomeTags.SLIGHTLY_COLOR_CHANGE).addTags(Tags.Biomes.IS_BEACH, Tags.Biomes.IS_OCEAN).add(Biomes.JUNGLE);


    }
}
