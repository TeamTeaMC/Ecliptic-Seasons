package com.teamtea.eclipticseasons.data.general.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.HygrometerBlock;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

public class BlockStatesDataProvider extends BlockStateProvider {


    private final ExistingFileHelper existingFileHelper;

    public BlockStatesDataProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EclipticSeasonsApi.MODID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        for (DeferredHolder<Block, Block> holder : List.of(BlockRegistry.calendar, BlockRegistry.pinwheel_blue, BlockRegistry.pinwheel_orange, BlockRegistry.pinwheel_lime)) {
            getVariantBuilder(holder.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(resourceBlock(holder.getId().getPath())))
                    .rotationY(getRotateYByFacing(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
                    .build());
        }

        for (DeferredHolder<Block, Block> holder : List.of(BlockRegistry.hygrometer)) {
            getVariantBuilder(holder.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(models()
                            .withExistingParent(resourceBlock(holder.getId().getPath()).getPath() + "_" + HygrometerBlock.getHumidityLevelFromPower(state.getValue(HygrometerBlock.POWER)), resourceBlock(holder.getId().getPath()))
                            .texture("1", resourceBlock(holder.getId().getPath() + "_light_" + HygrometerBlock.getHumidityLevelFromPower(state.getValue(HygrometerBlock.POWER))))
                    )
                    .rotationY(getRotateYByFacing(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
                    .build());
        }

        for (DeferredHolder<Block, Block> holder : List.of(BlockRegistry.ice_cauldron, BlockRegistry.snow_cauldron)) {
            getVariantBuilder(holder.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(models()
                            .withExistingParent(resourceBlock(holder.getId().getPath()).getPath(), "minecraft:block/powder_snow_cauldron_full")
                            .texture("content", holder == BlockRegistry.ice_cauldron ? "minecraft:block/ice" : "minecraft:block/snow")
                    )
                    .build());
        }


        addSimple(BlockRegistry.wind_chimes.get());
        addSimple(BlockRegistry.paper_wind_chimes.get());
        addSimple(BlockRegistry.bamboo_wind_chimes.get());

        addSimple(BlockRegistry.block_in_copper_grate_block.get());
        addSimple(BlockRegistry.block_in_exposed_copper_grate_block.get());
        addSimple(BlockRegistry.block_in_weathered_copper_grate_block.get());
        addSimple(BlockRegistry.block_in_oxidized_copper_grate_block.get());
        addSimple(BlockRegistry.block_in_waxed_copper_grate_block.get());
        addSimple(BlockRegistry.block_in_waxed_exposed_copper_grate_block.get());
        addSimple(BlockRegistry.block_in_waxed_weathered_copper_grate_block.get());
        addSimple(BlockRegistry.block_in_waxed_oxidized_copper_grate_block.get());
        addSimple(BlockRegistry.block_in_wooden_grate_block.get());

        addSimple(BlockRegistry.humidity_tank.get());

        // we need the rotate info
        for (Block block : List.of(BlockRegistry.greenhouse_core_container.get(),
                BlockRegistry.spring_greenhouse_core.get(),
                BlockRegistry.summer_greenhouse_core.get(),
                BlockRegistry.autumn_greenhouse_core.get(),
                BlockRegistry.winter_greenhouse_core.get())) {
            simpleBlockWithItem(block, models()
                    .cubeAll(blockName(block), EclipticSeasons.rl("block/green_house_core_particle"))
                    .texture("particle", EclipticSeasons.rl("block/green_house_core_particle"))
            );
        }

        for (Block block : List.of(BlockRegistry.season_quest_ceiling_hanging_sign.get(),
                BlockRegistry.season_quest_wall_hanging_sign.get())) {
            simpleBlockWithItem(block, models()
                    .withExistingParent(blockName(block), ResourceLocation.withDefaultNamespace("block/air"))
                    .texture("particle", ResourceLocation.withDefaultNamespace("block/oak_planks"))
            );
        }

    }

    public void addSimple(Block block) {
        simpleBlock(block, models().getExistingFile(resourceBlock(blockName(block))));
    }


    private String blockName(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    public static ResourceLocation resourceBlock(String path) {
        return EclipticSeasons.rl("block/" + path);
    }


    public static int getRotateYByFacing(Direction state) {
        switch (state) {
            case EAST -> {
                return 90;
            }
            case SOUTH -> {
                return 180;
            }
            case WEST -> {
                return 270;
            }
            default -> {
                return 0;
            }
        }
    }

}
