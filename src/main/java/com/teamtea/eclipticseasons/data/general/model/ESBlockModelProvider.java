package com.teamtea.eclipticseasons.data.general.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ESBlockModelProvider extends BlockModelProvider {


    public static final String BLOCK = "block/block";
    public static final String HANDHELD = "item/handheld";

    public ESBlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }


    @Override
    protected void registerModels() {
        for (ModelResourceLocation flowerOnGrass : ExtraModelManager.flower_on_grass) {
            withExistingParent(flowerOnGrass.id().getPath(), resource("grass_flower"))
                    .texture("1", flowerOnGrass.id().getPath());
        }

        for (ModelResourceLocation flowerOnGrass : ExtraModelManager.fourleaf_clovers) {
            withExistingParent(flowerOnGrass.id().getPath(), resource("tinted_grass_flower"))
                    .texture("1", flowerOnGrass.id().getPath());
        }

        for (ModelResourceLocation flowerOnGrass : ExtraModelManager.snow_edge_overlays) {
            withExistingParent(flowerOnGrass.id().getPath(), resource("grass_flower"))
                    .texture("1", flowerOnGrass.id().getPath());
        }

        withExistingParent(BlockRegistry.block_in_copper_grate_block.getId().getPath(), blockName(Blocks.COPPER_GRATE));
        withExistingParent(BlockRegistry.block_in_exposed_copper_grate_block.getId().getPath(), blockName(Blocks.EXPOSED_COPPER_GRATE));
        withExistingParent(BlockRegistry.block_in_weathered_copper_grate_block.getId().getPath(), blockName(Blocks.WEATHERED_COPPER_GRATE));
        withExistingParent(BlockRegistry.block_in_oxidized_copper_grate_block.getId().getPath(), blockName(Blocks.OXIDIZED_COPPER_GRATE));
        withExistingParent(BlockRegistry.block_in_waxed_copper_grate_block.getId().getPath(), blockName(Blocks.COPPER_GRATE));
        withExistingParent(BlockRegistry.block_in_waxed_exposed_copper_grate_block.getId().getPath(), blockName(Blocks.EXPOSED_COPPER_GRATE));
        withExistingParent(BlockRegistry.block_in_waxed_weathered_copper_grate_block.getId().getPath(), blockName(Blocks.WEATHERED_COPPER_GRATE));
        withExistingParent(BlockRegistry.block_in_waxed_oxidized_copper_grate_block.getId().getPath(), blockName(Blocks.OXIDIZED_COPPER_GRATE));

        cubeAll(resource(BlockRegistry.block_in_wooden_grate_block.getId().getPath()).getPath(), EclipticSeasons.rl("block/wooden_grate"));


        // withExistingParent("snowy_grass_block","grass_block_snow")
        //         .texture("top",ResourceLocation.withDefaultNamespace("block/snow"));

    }


    public ResourceLocation resource(String path) {
        return EclipticSeasons.rl("block/" + path);
    }

    private String blockName(Block block) {
        return "block/" + BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}
