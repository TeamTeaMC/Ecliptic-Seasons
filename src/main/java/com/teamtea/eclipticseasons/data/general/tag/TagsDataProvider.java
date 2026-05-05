package com.teamtea.eclipticseasons.data.general.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TagsDataProvider extends TagsProvider<Biome> {


    public TagsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.BIOME, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        for (TagKey<Biome> biomeType : ClimateTypeBiomeTags.OVERWORLD_AGRO_BIOME_TYPES) {
            tag(biomeType);
        }


        for (TagKey<Biome> biomeType : ClimateTypeBiomeTags.BIOME_TYPES) {
            TagKey<Biome> oldTag = ClimateTypeBiomeTags.create(biomeType.location().getPath().replace("rain/",""));
            tag(oldTag);
            tag(biomeType).addTag(oldTag);
        }

        for (TagKey<Biome> biomeType : ClimateTypeBiomeTags.BIOME_COLOR_TYPES) {
            tag(biomeType);
        }

        tag(ClimateTypeBiomeTags.IS_SMALL).addTags(BiomeTags.IS_RIVER);

        tag(ClimateTypeBiomeTags.EXTREME_COLD).addTags(Tags.Biomes.IS_SNOWY, Tags.Biomes.IS_PEAK);

        // tag(ClimateTypeBiomeTags.SEASONAL).addTags(BiomeTags.IS_OVERWORLD, Tags.Biomes.IS_VOID);
        // tag(ClimateTypeBiomeTags.SEASONAL_HOT).addTags(Tags.Biomes.IS_HOT_OVERWORLD);
        // tag(ClimateTypeBiomeTags.SEASONAL_COLD).addTags(Tags.Biomes.IS_PEAK, Tags.Biomes.IS_SNOWY);
        //
        // tag(ClimateTypeBiomeTags.MONSOONAL).addTags(BiomeTags.IS_SAVANNA);
        // tag(ClimateTypeBiomeTags.RAINLESS).addTags(Tags.Biomes.IS_CAVE);
        // tag(ClimateTypeBiomeTags.ARID).addTags(BiomeTags.IS_BADLANDS, Tags.Biomes.IS_DESERT);
        // tag(ClimateTypeBiomeTags.DROUGHTY).addTags();
        // tag(ClimateTypeBiomeTags.SOFT).addTags(BiomeTags.IS_BEACH, BiomeTags.IS_OCEAN);
        // tag(ClimateTypeBiomeTags.RAINY).add(Biomes.JUNGLE);
        //
        // tag(ClimateTypeBiomeTags.IS_SMALL).addTags(BiomeTags.IS_RIVER);
        //
        // // Biome Color
        // tag(ClimateTypeBiomeTags.SEASONAL_COLOR_CHANGE).addTags(BiomeTags.IS_OVERWORLD);
        // tag(ClimateTypeBiomeTags.SEASONAL_HOT_COLOR_CHANGE).addTags(Tags.Biomes.IS_HOT_OVERWORLD);
        // tag(ClimateTypeBiomeTags.SEASONAL_COLD_COLOR_CHANGE).addTags(Tags.Biomes.IS_PEAK, Tags.Biomes.IS_SNOWY);
        //
        // tag(ClimateTypeBiomeTags.MONSOONAL_COLOR_CHANGE).addTags(ClimateTypeBiomeTags.MONSOONAL);
        // tag(ClimateTypeBiomeTags.NONE_COLOR_CHANGE).addTags(Tags.Biomes.IS_CAVE, BiomeTags.IS_BADLANDS, Tags.Biomes.IS_DESERT, Tags.Biomes.IS_VOID);
        // tag(ClimateTypeBiomeTags.SLIGHTLY_COLOR_CHANGE).addTags(BiomeTags.IS_BEACH, BiomeTags.IS_OCEAN).add(Biomes.JUNGLE);


        // tag(TagKey.create(Registries.BIOME,
        //         EclipticSeasons.rl("misc/ambient/spring_negate"))).addTags(Tags.Biomes.IS_R);
    }
}
