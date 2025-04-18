package com.teamtea.eclipticseasons.data.tag;


import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;


import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;


public final class ESBlockTagProvider extends BlockTagsProvider {
    public ESBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(EclipticBlockTags.NONE_FALLEN_LEAVES).add(Blocks.CHERRY_LEAVES, Blocks.SPRUCE_LEAVES);
        tag(EclipticBlockTags.HABITAT_BUTTERFLY).addTag(BlockTags.FLOWERS);
        tag(EclipticBlockTags.HABITAT_FIREFLY).addTag(BlockTags.SMALL_FLOWERS).add(Blocks.GRASS, Blocks.TALL_GRASS);

        tag(CropHumidityType.AVERAGE_MOIST.getBlockTag()).addOptional(fd_rl("tomatoes"));

        tag(EclipticBlockTags.SOFT_HEAT_SOURCES).add(Blocks.CAMPFIRE).add(Blocks.MAGMA_BLOCK);
        tag(EclipticBlockTags.DARK_GROW_PLANTS)
                .add(Blocks.BROWN_MUSHROOM_BLOCK)
                .add(Blocks.RED_MUSHROOM_BLOCK)
                .addOptional(fd_rl("brown_mushroom_colony"))
                .addOptional(fd_rl("red_mushroom_colony"));

        tag(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON)
                .addTag(BlockTags.SNOW)
                .addTag(BlockTags.ICE)
                .addTag(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON);

        for (CropSeasonType cropSeasonType : CropSeasonType.collectValues()) {
            tag(cropSeasonType.getBlockTag());
        }
        for (CropHumidityType cropHumidityType : CropHumidityType.collectValues()) {
            tag(cropHumidityType.getBlockTag());
        }

        tag(EclipticBlockTags.NATURAL_PLANTS);

        tag(BlockTags.CEILING_HANGING_SIGNS).add(BlockRegistry.season_quest_ceiling_hanging_sign.get());
        tag(BlockTags.WALL_HANGING_SIGNS).add(BlockRegistry.season_quest_wall_hanging_sign.get());


        tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.calendar.get(),
                BlockRegistry.season_quest_wall_hanging_sign.get(),
                BlockRegistry.season_quest_ceiling_hanging_sign.get(),
                BlockRegistry.block_in_wooden_grate_block.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.spring_greenhouse_core.get(),
                BlockRegistry.summer_greenhouse_core.get(),
                BlockRegistry.autumn_greenhouse_core.get(),
                BlockRegistry.winter_greenhouse_core.get(),
                BlockRegistry.greenhouse_core_container.get());


    }


    public ResourceLocation fd_rl(String name) {
        return new ResourceLocation("farmersdelight", name);
    }
}
