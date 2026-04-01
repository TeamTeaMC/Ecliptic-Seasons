package com.teamtea.eclipticseasons.data.general.datapack.client;

import com.mojang.math.Quadrant;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.common.core.snow.ClientModelDefinitions;
import com.teamtea.eclipticseasons.common.registry.SnowDefinitionsRegistry;
import com.teamtea.eclipticseasons.data.api.provider.AbstractModelDefinitionProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ClientModelDefinitionProvider extends AbstractModelDefinitionProvider {


    public ClientModelDefinitionProvider(PackOutput output, String modid, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, registries);
    }


    public static final ModelTemplate snowy_grass_block = ModelTemplates.create("grass_block_snow", TextureSlot.TOP);

    @Override
    protected void gather(HolderLookup.Provider provider) {
        simple(ClientModelDefinitions.SNOWY_LEAVES_TOP).requireMod(modid);
        simple(ClientModelDefinitions.SNOWY_LEAVES_ATTACH).requireMod(modid);
        simple(ClientModelDefinitions.OVERLAY_TINY).requireMod(modid);
        simple(ClientModelDefinitions.OVERLAY).requireMod(modid);

        snowy_grass_block.create(withBlockFolder(EclipticSeasons.rl("snowy_grass_block")),
                new TextureMapping().put(TextureSlot.TOP, new Material(Identifier.withDefaultNamespace("block/snow"))),
                models());
        addModelDefinition(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY)
                .replace(true)
                .variant(new SingleVariant.Unbaked(new Variant(withBlockFolder(EclipticSeasons.rl("snowy_grass_block")))));

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


    private void addFlower() {
        for (SolarTerm solarTerm : SolarTerm.collectValues()) {
            if (solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SPRING, SolarTerm.BEGINNING_OF_SUMMER)) {
                int weight = (Math.abs(solarTerm.ordinal() - 3) + 1) * 56 * 2;
                add(getPath(EclipticSeasons.rl("flower_on_grass_" + solarTerm.getName())),

                        ESModelLoadedJson.builder().variants(
                                Optional.of(new BlockStateModelDispatcher.SimpleModelSelectors(
                                        Map.of(ESModelLoadedJson.ALL_VARIANT,
                                                buildMultiVariantLikeFromList(ExtraModelManager.flower_on_grass, weight)
                                        )
                                ))
                        ).build());
            }
            if (solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                int weight = (Math.abs(solarTerm.ordinal() - 7) + 1) * 42 * 2;
                add(getPath(EclipticSeasons.rl("fourleaf_clovers_" + solarTerm.getName())),
                        ESModelLoadedJson.builder().variants(Optional.of(new BlockStateModelDispatcher.SimpleModelSelectors(
                                Map.of(ESModelLoadedJson.ALL_VARIANT, buildMultiVariantLikeFromList(ExtraModelManager.fourleaf_clovers, weight))))).build());
            }
        }


    }

    private WeightedVariants.Unbaked buildMultiVariantLikeFromList(List<StandaloneModelKey<BlockStateModel>> modelIdentifiers, int emptyWeight) {
        WeightedList.Builder<BlockStateModel.Unbaked> builder = WeightedList.builder();
        for (var modelIdentifier : modelIdentifiers) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                builder.add(new Weighted<>(
                        new SingleVariant.Unbaked(new Variant(Identifier.parse(modelIdentifier.getName()),
                                new Variant.SimpleModelState(Quadrant.R0,
                                        Quadrant.values()[direction.ordinal() - 2], Quadrant.R0, false))), 1));
            }
            // builder.add(new Weighted<>(
            //         new SingleVariant.Unbaked(new Variant(Identifier.parse(modelIdentifier.getName()))), 1));
        }
        if (emptyWeight > 0)
            builder.add(new Weighted<>(new SingleVariant.Unbaked(new Variant(Identifier.withDefaultNamespace("block/air"))), emptyWeight*4));
        return new WeightedVariants.Unbaked(builder.build());
    }


    private static String getPath(Identifier overlay) {
        return overlay.getPath();
    }


}
