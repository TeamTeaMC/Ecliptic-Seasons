package com.teamtea.eclipticseasons.data.general.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.common.block.HygrometerBlock;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import lombok.Builder;
import lombok.Data;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.common.util.ConcatenatedListView;

import java.util.List;


@Data
@Builder
public class ESBlockModelGenerators {
    private final BlockModelGenerators models;


    public static final TextureSlot TS_1 = TextureSlot.create("1");

    public static final ModelTemplate HYGROMETER = ModelTemplates.create(EclipticSeasons.rl("hygrometer").toString(), TS_1);
    public static final ModelTemplate GRASS_FLOWER = ModelTemplates.create(EclipticSeasons.rl("grass_flower").toString(), TS_1);
    public static final ModelTemplate TINTED_GRASS_FLOWER = ModelTemplates.create(EclipticSeasons.rl("tinted_grass_flower").toString(), TS_1);


    public void run() {
        addSimple(BlockRegistry.wind_chimes.get());
        addSimple(BlockRegistry.paper_wind_chimes.get());
        addSimple(BlockRegistry.bamboo_wind_chimes.get());

        addCopperGrate(BlockRegistry.block_in_copper_grate_block.get());
        addCopperGrate(BlockRegistry.block_in_exposed_copper_grate_block.get());
        addCopperGrate(BlockRegistry.block_in_weathered_copper_grate_block.get());
        addCopperGrate(BlockRegistry.block_in_oxidized_copper_grate_block.get());
        addCopperGrate(BlockRegistry.block_in_waxed_copper_grate_block.get());
        addCopperGrate(BlockRegistry.block_in_waxed_exposed_copper_grate_block.get());
        addCopperGrate(BlockRegistry.block_in_waxed_weathered_copper_grate_block.get());
        addCopperGrate(BlockRegistry.block_in_waxed_oxidized_copper_grate_block.get());

        simpleBlockItem(BlockRegistry.block_in_wooden_grate_block.get(), EclipticSeasons.rl("block/wooden_grate"), true);


        addCauldron(BlockRegistry.ice_cauldron.get(), Blocks.ICE);
        addCauldron(BlockRegistry.snow_cauldron.get(), Blocks.SNOW);

        addRotationHorizontalBlock(BlockRegistry.calendar.get());
        addRotationHorizontalBlock(BlockRegistry.pinwheel_blue.get());
        addRotationHorizontalBlock(BlockRegistry.pinwheel_orange.get());
        addRotationHorizontalBlock(BlockRegistry.pinwheel_lime.get());

        addHygrometerBlock();

        for (Block block : List.of(BlockRegistry.greenhouse_core_container.get(),
                BlockRegistry.spring_greenhouse_core.get(),
                BlockRegistry.summer_greenhouse_core.get(),
                BlockRegistry.autumn_greenhouse_core.get(),
                BlockRegistry.winter_greenhouse_core.get())) {
            simpleBlockItem(block, EclipticSeasons.rl("block/green_house_core_particle"), false);
        }

        for (Block block : List.of(BlockRegistry.season_quest_ceiling_hanging_sign.get(),
                BlockRegistry.season_quest_wall_hanging_sign.get())) {
            simpleBlockItem(block, Identifier.parse("minecraft:block/oak_planks"), ModelTemplates.create("air", TextureSlot.PARTICLE), false);
        }

        generateFlowers();
    }

    private void simpleBlockItem(Block block, Identifier textureid, boolean item) {
        simpleBlockItem(block, textureid, ModelTemplates.CUBE_ALL, item);
    }

    private void simpleBlockItem(Block block, Identifier textureid, ModelTemplate modelTemplate, boolean item) {
        models.createTrivialBlock(block,
                (b) -> TexturedModel.createDefault((_) ->
                        TextureMapping.cube(new Material(textureid))
                                .put(TextureSlot.PARTICLE, new Material(textureid)), modelTemplate).get(b));
        if (item && block.asItem() != Items.AIR)
            models.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block));
    }

    public void addRotationHorizontalBlock(Block block) {
        MultiVariant model = BlockModelGenerators.plainVariant(
                block.builtInRegistryHolder().key().identifier().withPrefix("block/"));
        models.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, model).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
    }

    public void addHygrometerBlock() {
        Block block = BlockRegistry.hygrometer.get();
        models.blockStateOutput.accept(MultiVariantGenerator
                .dispatch(block)
                .with(PropertyDispatch.initial(HygrometerBlock.POWER)
                        .generate(integer ->
                                {
                                    Identifier rl = EclipticSeasons.rl("block/" + BlockRegistry.hygrometer.getId().getPath() + "_light_" + HygrometerBlock.getHumidityLevelFromPower(integer));
                                    TextureMapping textureMapping = new TextureMapping().put(TS_1, new Material(rl));
                                    Identifier identifier;
                                    try {
                                        identifier = HYGROMETER.create(rl, textureMapping, models.modelOutput);
                                    } catch (Exception e) {
                                        identifier = rl;
                                    }
                                    return BlockModelGenerators.plainVariant(identifier);
                                }
                        ))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
    }

    public void addSimple(Block block) {
        models.createNonTemplateModelBlock(block);
    }

    public void addSimple(Block block, Block parent) {
        models.createNonTemplateModelBlock(block, parent);
    }


    public void addCopperGrate(Block block) {
        addSimple(block, BlockRegistry.getOriginalCopperGrateBlockNotWaxed(block));
    }

    public void addCauldron(Block block, Block content) {
        models.blockStateOutput
                .accept(
                        BlockModelGenerators.createSimpleBlock(
                                block,
                                BlockModelGenerators.plainVariant(
                                        ModelTemplates.CAULDRON_FULL
                                                .create(block, TextureMapping.cauldron(TextureMapping.getBlockTexture(content)), models.modelOutput)
                                )
                        )
                );
    }


    protected void generateFlowers() {
        for (var flowerOnGrass : ConcatenatedListView.of(
                ExtraModelManager.flower_on_grass,
                ExtraModelManager.snow_edge_overlays
        )) {
            makeStandAloneModel(flowerOnGrass, GRASS_FLOWER);
        }

        for (var flowerOnGrass : ConcatenatedListView.of(
                ExtraModelManager.fourleaf_clovers,
                ExtraModelManager.leaf_piles
        )) {
            makeStandAloneModel(flowerOnGrass, TINTED_GRASS_FLOWER);
        }
    }

    private void makeStandAloneModel(StandaloneModelKey<BlockStateModel> flowerOnGrass, ModelTemplate grassFlower) {
        TextureMapping textureMapping = new TextureMapping().put(TS_1, new Material(id(flowerOnGrass.getName())));
        grassFlower.create(id(flowerOnGrass.getName()), textureMapping, models.modelOutput);
    }

    public Identifier id(String id) {
        return EclipticSeasons.parse(id);
    }

    public Identifier resource(String path) {
        return EclipticSeasons.rl("block/" + path);
    }
}
