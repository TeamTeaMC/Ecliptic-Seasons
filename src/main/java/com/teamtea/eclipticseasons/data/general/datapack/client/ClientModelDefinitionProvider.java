package com.teamtea.eclipticseasons.data.general.datapack.client;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.api.data.client.model.variant.MultiVariantLike;
import com.teamtea.eclipticseasons.api.data.client.model.variant.VariantLike;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.common.core.snow.ClientModelDefinitions;
import com.teamtea.eclipticseasons.common.registry.SnowDefinitionsRegistry;
import com.teamtea.eclipticseasons.data.api.provider.AbstractModelDefinitionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class ClientModelDefinitionProvider extends AbstractModelDefinitionProvider {


    public ClientModelDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries);
    }

    public ExtraModelBuilder getModel(String resourceLocation) {
        return new ExtraModelBuilder(withBlockFolder(EclipticSeasons.rl(resourceLocation)), helper)
                .parent(new ModelFile.ExistingModelFile(withBlockFolder(EclipticSeasons.rl(resourceLocation)), helper));
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        simple(ClientModelDefinitions.SNOWY_LEAVES_TOP).requireMod(modid);
        simple(ClientModelDefinitions.SNOWY_LEAVES_ATTACH).requireMod(modid);
        simple(ClientModelDefinitions.OVERLAY_TINY).requireMod(modid);
        simple(ClientModelDefinitions.OVERLAY).requireMod(modid);

        // add(getPath(ClientModelDefinitions.OVERLAY_TINY), ESModelLoadedJson.builder()
        //         .multiPartLike(new MultiPartLike(List.of(
        //                 new SelectorLike((new MultiVariantLike(List.of(
        //                         new VariantLike.VariantBuilder(SnowModelConstant.OVERLAY_TINY).build()
        //
        //                 ))))
        //         )))
        //         .requirement(EclipticSeasonsApi.MODID)
        //         .build());



        addModelDefinition(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY)
                .replace(true)
                .variant(variant(models().withExistingParent("snowy_grass_block", "block/block")
                        .customLoader(CompositeModelBuilder::begin)
                        .child("snowy_grass_block_no_top",models().withExistingParent("snowy_grass_block_no_top", "grass_block_snow",true)
                                .texture("top", new ResourceLocation("block/air")))
                        .child("top_snow",getModel("grass_block_overlay"))
                        .end()).build())
        // .requireMod(EclipticSeasonsApi.MODID)
                // .requireMod(ResourceLocation.DEFAULT_NAMESPACE)
                // .replace(false)
                // .multiPart(condition(GrassBlock.SNOWY, false), variant(ClientModelDefinitions.GRASS_BLOCK_SNOW).rotationY(0).build(),
                //         variant(ClientModelDefinitions.GRASS_BLOCK_SNOW).rotationY(90).build(),
                //         variant(ClientModelDefinitions.GRASS_BLOCK_SNOW).rotationY(180).build(),
                //         variant(ClientModelDefinitions.GRASS_BLOCK_SNOW).rotationY(270).build())
                // .multiPart(variant(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(0).build(),
                //         variant(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(90).build(),
                //         variant(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(180).build(),
                //         variant(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(270).build())
        ;

        addFlower();
        addModelDefinition(ClientModelDefinitions.SNOWY_SWEET_BERRY_BUSH)
                .replace(true)
                .stagedVariants(SweetBerryBushBlock.AGE.getName(), 4);

        addModelDefinition(ClientModelDefinitions.SNOWY_DEAD_BUSH)
                .singleCross()
                .replace(true);

        addModelDefinition(ClientModelDefinitions.SNOWY_SUGAR_CANE)
                .singleCross()
                .replace(true);

        addSnowyPlant(Blocks.BAMBOO_SAPLING, "bamboo_stage0");

        addSnowyBlockModelDefinition(Blocks.BAMBOO)
                .multiPartWithGenerate(condition(BambooStalkBlock.AGE, BambooStalkBlock.AGE.getName(0)),
                        () -> IntStream.range(1, 5)
                                .mapToObj(i -> models().snowyWithExistingParent("bamboo%s_age0".formatted(i))
                                        .texture("all", snow_rl("bamboo_stalk"))).toList()
                )
                .multiPartWithGenerate(condition(BambooStalkBlock.AGE, BambooStalkBlock.AGE.getName(1)),
                        () -> IntStream.range(1, 5)
                                .mapToObj(i -> models().snowyWithExistingParent("bamboo%s_age1".formatted(i))
                                        .texture("all", snow_rl("bamboo_stalk"))).toList()
                )
                .multiPartWithGenerateSingle(condition(BambooStalkBlock.LEAVES, BambooStalkBlock.LEAVES.getName(BambooLeaves.LARGE)),
                        () -> models().snowyWithExistingParent("bamboo_large_leaves").texture("texture", snow_rl("bamboo_large_leaves"))
                )
                .multiPartWithGenerateSingle(condition(BambooStalkBlock.LEAVES, BambooStalkBlock.LEAVES.getName(BambooLeaves.SMALL)),
                        () -> models().snowyWithExistingParent("bamboo_small_leaves").texture("texture", snow_rl("bamboo_small_leaves"))
                )
                .replace(true);

        addBlockModelDefinition(Blocks.BAMBOO, SnowDefinitionsRegistry.getSnowModelPath(modid, Blocks.BAMBOO).withSuffix("_top"))
                .multiPartWithGenerate(condition(BambooStalkBlock.AGE, BambooStalkBlock.AGE.getName(0)),
                        () -> IntStream.range(1, 5)
                                .mapToObj(i -> models().snowyWithExistingParent("bamboo%s_age0_top".formatted(i), "bamboo%s_age0".formatted(i))
                                        .texture("all", snow_rl("bamboo_stalk_top"))).toList()
                )
                .multiPartWithGenerate(condition(BambooStalkBlock.AGE, BambooStalkBlock.AGE.getName(1)),
                        () -> IntStream.range(1, 5)
                                .mapToObj(i -> models().snowyWithExistingParent("bamboo%s_age1_top".formatted(i), "bamboo%s_age1".formatted(i))
                                        .texture("all", snow_rl("bamboo_stalk_top"))).toList()
                )
                .multiPart(condition(BambooStalkBlock.LEAVES, BambooStalkBlock.LEAVES.getName(BambooLeaves.LARGE)),
                        variant(snow_rl("bamboo_large_leaves")).build()
                )
                .multiPart(condition(BambooStalkBlock.LEAVES, BambooStalkBlock.LEAVES.getName(BambooLeaves.SMALL)),
                        variant(snow_rl("bamboo_small_leaves")).build())
                .replace(true);
    }

    protected void addSnowyPlant(Block plant) {
        addModelDefinition(SnowDefinitionsRegistry.getSnowModelPath(plant))
                .singleCross()
                .replace(true);
    }

    protected void addSnowyPlant(Block plant, String texture) {
        addModelDefinition(SnowDefinitionsRegistry.getSnowModelPath(plant))
                .singleCross(withBlockFolder(EclipticSeasons.rl(texture).withPrefix("snowy/")))
                .replace(true);
    }

    protected ModelDefinitionBuilder addSnowyCrossDoublePlant(Block lilac) {
        String path = getPath(lilac.builtInRegistryHolder().key().location());
        return addSnowyBlockModelDefinition(lilac)
                .variantsForAllStatesExceptExact(state ->
                {
                    if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                        return models().snowyWithExistingParent(path + "_bottom")
                                .texture("cross", "block/snowy/%s_bottom".formatted(path));
                    } else {
                        return models().snowyWithExistingParent(path + "_top")
                                .texture("cross", "block/snowy/%s_top".formatted(path));
                    }
                })
                .replace(true);
    }

    private void addFlower() {

        for (SolarTerm solarTerm : SolarTerm.collectValues()) {
            if (solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SPRING, SolarTerm.BEGINNING_OF_SUMMER)) {
                int weight = (Math.abs(solarTerm.ordinal() - 3) + 1) * 56 * 2;
                add(getPath(EclipticSeasons.rl("flower_on_grass_" + solarTerm.getName())), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT,
                        buildMultiVariantLikeFromList(ExtraModelManager.flower_on_grass, weight)).build());
            }
            if (solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                int weight = (Math.abs(solarTerm.ordinal() - 7) + 1) * 42 * 2;
                add(getPath(EclipticSeasons.rl("fourleaf_clovers_" + solarTerm.getName())), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT,
                        buildMultiVariantLikeFromList(ExtraModelManager.fourleaf_clovers, weight)).build());
            }
        }


    }

    private MultiVariantLike buildMultiVariantLikeFromList(List<ResourceLocation> modelResourceLocations, int emptyWeight) {
        List<VariantLike> list = new ArrayList<>(modelResourceLocations.stream()
                .map(r -> new VariantLike.VariantBuilder(r).weight(1).build())
                .toList());
        if (emptyWeight > 0)
            list.add(variant(new ResourceLocation("block/air")).weight(emptyWeight).build());
        return new MultiVariantLike(list);
    }


    private static String getPath(ResourceLocation overlay) {
        return overlay.getPath();
    }


}
