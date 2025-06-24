package com.teamtea.eclipticseasons.data.datapack.client;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.api.data.client.model.multipart.KeyValueConditionLike;
import com.teamtea.eclipticseasons.api.data.client.model.multipart.MultiPartLike;
import com.teamtea.eclipticseasons.api.data.client.model.multipart.SelectorLike;
import com.teamtea.eclipticseasons.api.data.client.model.variant.MultiVariantLike;
import com.teamtea.eclipticseasons.api.data.client.model.variant.VariantLike;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.model.SnowModelConstant;
import com.teamtea.eclipticseasons.common.core.snow.ClientModelDefinitions;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientModelDefinitionProvider extends AbstractModelDefinitionProvider {


    public ClientModelDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries);
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
                .requireMod(EclipticSeasonsApi.MODID)
                .requireMod(ResourceLocation.DEFAULT_NAMESPACE)
                .replace(false)
                .multiPart(condition(GrassBlock.SNOWY, false), variant(ClientModelDefinitions.GRASS_BLOCK_SNOW).rotationY(0).build(),
                        variant(ClientModelDefinitions.GRASS_BLOCK_SNOW).rotationY(90).build(),
                        variant(ClientModelDefinitions.GRASS_BLOCK_SNOW).rotationY(180).build(),
                        variant(ClientModelDefinitions.GRASS_BLOCK_SNOW).rotationY(270).build())
                .multiPart(variant(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(0).build(),
                        variant(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(90).build(),
                        variant(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(180).build(),
                        variant(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(270).build())
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
