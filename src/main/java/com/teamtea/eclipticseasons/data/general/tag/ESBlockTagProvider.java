package com.teamtea.eclipticseasons.data.general.tag;


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
        // special

        tag(EclipticBlockTags.DARK_GROW_PLANTS)
                .add(Blocks.BROWN_MUSHROOM_BLOCK)
                .add(Blocks.RED_MUSHROOM_BLOCK)
                .addOptional(fd_rl("brown_mushroom_colony"))
                .addOptional(fd_rl("red_mushroom_colony"));

        tag(EclipticBlockTags.NATURAL_PLANTS).add(Blocks.BAMBOO_SAPLING);
        tag(EclipticBlockTags.VOLATILE).add(Blocks.BUBBLE_COLUMN).addTag(EclipticBlockTags.VOLATILE_PLANTS);
        tag(EclipticBlockTags.VOLATILE_PLANTS);
        tag(EclipticBlockTags.UNAFFECTED_BY_HUMIDITY);
        tag(EclipticBlockTags.UNAFFECTED_BY_SEASONS);
        tag(EclipticBlockTags.NOT_KILLED_BY_CLIMATE);

        // add crop info
        tag(CropSeasonType.SUMMER.getBlockTag()).add(Blocks.MELON_STEM, Blocks.COCOA, Blocks.CACTUS);
        tag(CropSeasonType.AUTUMN.getBlockTag()).add(Blocks.PUMPKIN_STEM);
        tag(CropSeasonType.SP_AU.getBlockTag()).add(Blocks.POTATOES, Blocks.BEETROOTS, Blocks.CARROTS);
        tag(CropSeasonType.SP_SU_AU.getBlockTag()).add(Blocks.KELP, Blocks.KELP_PLANT, Blocks.TORCHFLOWER);
        tag(CropSeasonType.SP_SU.getBlockTag()).add(Blocks.WHEAT).add(Blocks.SUGAR_CANE);
        tag(CropSeasonType.ALL.getBlockTag()).add(Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT);
        tag(CropSeasonType.SP_WI.getBlockTag()).add(Blocks.SWEET_BERRY_BUSH);
        tag(CropSeasonType.SPRING.getBlockTag()).add(Blocks.BAMBOO).add(Blocks.BAMBOO_SAPLING);

        tag(CropHumidityType.DRY_AVERAGE.getBlockTag()).add(Blocks.CACTUS);
        tag(CropHumidityType.DRY_MOIST.getBlockTag()).add(Blocks.SWEET_BERRY_BUSH);
        tag(CropHumidityType.DRY_HUMID.getBlockTag()).add(Blocks.MELON_STEM);
        tag(CropHumidityType.AVERAGE_HUMID.getBlockTag()).add(Blocks.CAVE_VINES, Blocks.CAVE_VINES_PLANT, Blocks.SUGAR_CANE);
        tag(CropHumidityType.AVERAGE_MOIST.getBlockTag()).add(Blocks.WHEAT, Blocks.CARROTS, Blocks.BEETROOTS, Blocks.POTATOES, Blocks.PUMPKIN_STEM);
        tag(CropHumidityType.AVERAGE_MOIST.getBlockTag()).add(Blocks.COCOA, Blocks.KELP, Blocks.KELP_PLANT, Blocks.TORCHFLOWER);
        tag(CropHumidityType.MOIST_HUMID.getBlockTag()).add(Blocks.BAMBOO).add(Blocks.BAMBOO_SAPLING).add(Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM);

        // others
        tag(CropHumidityType.AVERAGE_MOIST.getBlockTag()).addOptional(fd_rl("tomatoes")).addOptional(fd_rl("budding_tomatoes")).addOptional(fd_rl("cabbages")).addOptional(fd_rl("onions"));
        tag(CropHumidityType.MOIST_HUMID.getBlockTag()).addOptional(fd_rl("rice")).addOptional(fd_rl("rice_panicles")).addOptional(fd_rl("brown_mushroom_colony")).addOptional(fd_rl("red_mushroom_colony"));


        // stand
        for (CropSeasonType cropSeasonType : CropSeasonType.collectValues()) {
            tag(cropSeasonType.getBlockTag());
        }
        for (CropHumidityType cropHumidityType : CropHumidityType.collectValues()) {
            tag(cropHumidityType.getBlockTag());
        }

        tag(EclipticBlockTags.NONE_FALLEN_LEAVES).add(Blocks.CHERRY_LEAVES, Blocks.SPRUCE_LEAVES);
        tag(EclipticBlockTags.HABITAT_BUTTERFLY).addTag(BlockTags.FLOWERS);
        tag(EclipticBlockTags.HABITAT_FIREFLY).addTag(BlockTags.SMALL_FLOWERS).add(Blocks.GRASS, Blocks.TALL_GRASS);

        // add common
        tag(EclipticBlockTags.SOFT_HEAT_SOURCES).add(Blocks.CAMPFIRE).add(Blocks.MAGMA_BLOCK);

        tag(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON)
                .addTag(BlockTags.SNOW)
                .addTag(BlockTags.ICE)
                .addTag(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON);

        tag(EclipticBlockTags.SNOW_LAYER_CANNOT_SURVIVE_IN)
                .addTags(BlockTags.DOORS, BlockTags.TRAPDOORS,
                        BlockTags.BUTTONS,
                        BlockTags.CANDLE_CAKES,
                        BlockTags.BEDS,
                        BlockTags.RAILS,
                        BlockTags.ANVIL,
                        BlockTags.PRESSURE_PLATES
                );

        // add mc
        tag(BlockTags.CEILING_HANGING_SIGNS).add(BlockRegistry.season_quest_ceiling_hanging_sign.get());
        tag(BlockTags.WALL_HANGING_SIGNS).add(BlockRegistry.season_quest_wall_hanging_sign.get());


        tag(BlockTags.MINEABLE_WITH_AXE).add(BlockRegistry.calendar.get(),
                BlockRegistry.season_quest_wall_hanging_sign.get(),
                BlockRegistry.season_quest_ceiling_hanging_sign.get(),
                BlockRegistry.wind_chimes.get(),
                BlockRegistry.bamboo_wind_chimes.get(),
                BlockRegistry.wind_chimes.get(),
                BlockRegistry.pinwheel_blue.get(),
                BlockRegistry.pinwheel_lime.get(),
                BlockRegistry.pinwheel_orange.get(),
                BlockRegistry.block_in_wooden_grate_block.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BlockRegistry.hygrometer.get(),
                BlockRegistry.spring_greenhouse_core.get(),
                BlockRegistry.summer_greenhouse_core.get(),
                BlockRegistry.autumn_greenhouse_core.get(),
                BlockRegistry.winter_greenhouse_core.get(),
                BlockRegistry.greenhouse_core_container.get(),
                BlockRegistry.ice_cauldron.get(),
                BlockRegistry.snow_cauldron.get());

        tag(BlockTags.CAULDRONS).add(BlockRegistry.ice_cauldron.get(),
                BlockRegistry.snow_cauldron.get());

    }


    public ResourceLocation fd_rl(String name) {
        return new ResourceLocation("farmersdelight", name);
    }
}
