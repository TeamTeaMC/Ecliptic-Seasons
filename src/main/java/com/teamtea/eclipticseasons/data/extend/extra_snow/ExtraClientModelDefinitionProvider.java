package com.teamtea.eclipticseasons.data.extend.extra_snow;

import com.teamtea.eclipticseasons.data.general.datapack.client.ClientModelDefinitionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ExtraClientModelDefinitionProvider extends ClientModelDefinitionProvider {
    public ExtraClientModelDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {

        addSnowyBlockModelDefinition(Blocks.SUNFLOWER)
                .variantsForAllStatesExceptExact(state ->
                {
                    if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                        return models().withExistingParent("block/snowy/sunflower_bottom", "sunflower_bottom")
                                .texture("cross", "block/snowy/sunflower_bottom");
                    } else {
                        return models().withExistingParent("block/snowy/sunflower_top", "sunflower_top")
                                .texture("cross", "block/snowy/sunflower_top")
                                .texture("front", "block/snowy/sunflower_front")
                                .texture("back", "block/snowy/sunflower_back")
                                .texture("particle", "block/snowy/sunflower_front");
                    }
                })
                .replace(true);

        addSnowyPlant(Blocks.OAK_SAPLING);
        addSnowyPlant(Blocks.DARK_OAK_SAPLING);
        addSnowyPlant(Blocks.ACACIA_SAPLING);
        addSnowyPlant(Blocks.BIRCH_SAPLING);
        addSnowyPlant(Blocks.JUNGLE_SAPLING);
        addSnowyPlant(Blocks.SPRUCE_SAPLING);
        addSnowyPlant(Blocks.CHERRY_SAPLING);

        addSnowyBlockModelDefinition(Blocks.MANGROVE_PROPAGULE)
                .variantsForAllStatesExceptExact(state ->
                {
                    if (state.getValue(MangrovePropaguleBlock.HANGING)) {
                        return models().withExistingParent("block/snowy/mangrove_propagule_hanging_" + state.getValue(MangrovePropaguleBlock.AGE), "mangrove_propagule_hanging_" + state.getValue(MangrovePropaguleBlock.AGE))
                                .texture("propagule", "block/snowy/mangrove_propagule_hanging");
                    } else {
                        return models().withExistingParent("block/snowy/mangrove_propagule", "mangrove_propagule")
                                .texture("sapling", "block/snowy/mangrove_propagule");
                    }
                }, SaplingBlock.STAGE, BlockStateProperties.WATERLOGGED)
                .replace(true);

        addSnowyPlant(Blocks.RED_MUSHROOM);
        addSnowyPlant(Blocks.BROWN_MUSHROOM);
        addSnowyPlant(Blocks.ALLIUM);
        addSnowyPlant(Blocks.AZURE_BLUET);
        addSnowyPlant(Blocks.BLUE_ORCHID);
        addSnowyPlant(Blocks.CORNFLOWER);
        addSnowyPlant(Blocks.DANDELION);
        addSnowyPlant(Blocks.LILY_OF_THE_VALLEY);
        addSnowyPlant(Blocks.ORANGE_TULIP);
        addSnowyPlant(Blocks.PINK_TULIP);
        addSnowyPlant(Blocks.WHITE_TULIP);
        addSnowyPlant(Blocks.RED_TULIP);
        addSnowyPlant(Blocks.OXEYE_DAISY);
        addSnowyPlant(Blocks.POPPY);
        addSnowyPlant(Blocks.WITHER_ROSE);

        addSnowyCrossDoublePlant(Blocks.LILAC);
        addSnowyCrossDoublePlant(Blocks.PEONY);
        addSnowyCrossDoublePlant(Blocks.ROSE_BUSH);

        addSnowyPlant(Blocks.TORCHFLOWER);
        addSnowyBlockModelDefinition(Blocks.TORCHFLOWER_CROP)
                .variantsForAllStatesExceptExact(state ->
                        models().snowyWithExistingParent("torchflower_crop_stage" + state.getValue(TorchflowerCropBlock.AGE))
                                .cross())
                .replace(true);

        addSnowyBlockModelDefinition(Blocks.PITCHER_PLANT)
                .variantsForAllStatesExceptExact(state ->
                {
                    if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                        return models().snowyWithExistingParent("pitcher_plant_bottom")
                                .texture("bottom", "block/snowy/pitcher_crop_bottom_stage_4");
                    } else {
                        return models().snowyWithExistingParent("pitcher_plant_top")
                                .texture("top", "block/snowy/pitcher_crop_top_stage_4");
                    }
                })
                .replace(true);
        addSnowyBlockModelDefinition(Blocks.PITCHER_CROP)
                .variantsForAllStatesExceptExact(state ->
                {
                    if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                        int age = state.getValue(PitcherCropBlock.AGE);
                        ExtraModelBuilder pitcherBottom = models().snowyWithExistingParent("pitcher_crop_bottom_stage_" + age)
                                .texture("pitcher_top", "block/snowy/pitcher_crop_top")
                                .texture("pitcher_side", "block/snowy/pitcher_crop_side");
                        if (age > 0)
                            pitcherBottom.texture("stage_" + age + (age > 2 ? "_bottom" : ""), "block/snowy/pitcher_crop_bottom_stage_" + age);
                        return pitcherBottom;
                    } else {
                        int age = state.getValue(PitcherCropBlock.AGE);
                        ExtraModelBuilder pitcherBottom = models().snowyWithExistingParent("pitcher_crop_top_stage_" + age) ;
                        if (age > 2)
                            pitcherBottom.texture("stage_" + age + "_top", "block/snowy/pitcher_crop_top_stage_" + age);
                        return pitcherBottom;
                    }
                })
                .replace(true);
    }


    @Override
    public @NotNull String getName() {
        return super.getName() + "(Extra Version)";
    }
}
