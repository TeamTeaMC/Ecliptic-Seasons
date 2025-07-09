package com.teamtea.eclipticseasons.data.general.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.block.HygrometerBlock;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;


public class BlockStatesDataProvider extends BlockStateProvider {


    private final ExistingFileHelper existingFileHelper;

    public BlockStatesDataProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EclipticSeasonsApi.MODID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        for (var holder : List.of(BlockRegistry.calendar)) {
            getVariantBuilder(holder.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(resourceBlock(holder.getId().getPath())))
                    .rotationY(getRotateYByFacing(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
                    .build());
        }

        for (var holder : List.of(BlockRegistry.hygrometer)) {
            getVariantBuilder(holder.get()).forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(models()
                            .withExistingParent(resourceBlock(holder.getId().getPath()).getPath() + "_" + HygrometerBlock.getHumidityLevelFromPower(state.getValue(HygrometerBlock.POWER)), resourceBlock(holder.getId().getPath()))
                            .texture("1", resourceBlock(holder.getId().getPath() + "_light_" + HygrometerBlock.getHumidityLevelFromPower(state.getValue(HygrometerBlock.POWER))))
                    )
                    .rotationY(getRotateYByFacing(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
                    .build());
        }


        addSimple(BlockRegistry.block_in_wooden_grate_block.get());

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
                    .withExistingParent(blockName(block), new ResourceLocation("block/air"))
                    .texture("particle", new ResourceLocation("block/oak_planks"))
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
