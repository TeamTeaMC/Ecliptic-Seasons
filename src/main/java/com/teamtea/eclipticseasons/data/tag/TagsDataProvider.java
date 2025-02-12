package com.teamtea.eclipticseasons.data.tag;

import com.teamtea.eclipticseasons.api.constant.tag.SeasonTypeBiomeTags;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TagsDataProvider extends TagsProvider<Biome> {


    public TagsDataProvider(DataGenerator generator, String modid, ExistingFileHelper helper) {
        super(generator, BuiltinRegistries.BIOME, modid, helper);
    }

    @Override
    protected void addTags() {
        this.tag(SeasonTypeBiomeTags.SEASONAL).addTags(BiomeTags.IS_OVERWORLD);
        this.tag(SeasonTypeBiomeTags.MONSOONAL).addTags(BiomeTags.IS_SAVANNA);
        this.tag(SeasonTypeBiomeTags.RAINLESS).addTags(Tags.Biomes.IS_CAVE,BiomeTags.IS_NETHER,BiomeTags.IS_END);
        this.tag(SeasonTypeBiomeTags.ARID).addTags(Tags.Biomes.IS_SANDY);
        this.tag(SeasonTypeBiomeTags.DROUGHTY).addTags(BiomeTags.IS_BADLANDS, Tags.Biomes.IS_PEAK, Tags.Biomes.IS_COLD_OVERWORLD);
        this.tag(SeasonTypeBiomeTags.SOFT).addTags(BiomeTags.IS_BEACH, BiomeTags.IS_OCEAN);
        this.tag(SeasonTypeBiomeTags.RAINY).addTags(BiomeTags.IS_JUNGLE);
    }
}
