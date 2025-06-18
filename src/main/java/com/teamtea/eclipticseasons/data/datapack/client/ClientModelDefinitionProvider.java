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
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.model.SnowModelConstant;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.common.core.snow.ClientModelDefinitions;
import com.teamtea.eclipticseasons.data.datapack.client.base.ESClientBiomeDataMapProvider;
import com.teamtea.eclipticseasons.data.datapack.client.base.ESClientDataMapProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientModelDefinitionProvider extends AbstractModelDefinitionProvider {


    public ClientModelDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        add(getPath(ClientModelDefinitions.OVERLAY), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT, new MultiVariantLike(List.of(
                new VariantLike.VariantBuilder(SnowModelConstant.OVERLAY).build()
        ))).requirement(EclipticSeasonsApi.MODID).build());

        add(getPath(ClientModelDefinitions.SNOWY_LEAVES_TOP), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT, new MultiVariantLike(List.of(
                new VariantLike.VariantBuilder(SnowModelConstant.SNOWY_LEAVES_TOP).build()
        ))).requirement(EclipticSeasonsApi.MODID).build());
        add(getPath(ClientModelDefinitions.SNOWY_LEAVES_ATTACH), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT, new MultiVariantLike(List.of(
                new VariantLike.VariantBuilder(SnowModelConstant.SNOWY_LEAVES_ATTACH).build()
        ))).requirement(EclipticSeasonsApi.MODID).build());

        add(getPath(ClientModelDefinitions.OVERLAY_TINY), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT, new MultiVariantLike(List.of(
                new VariantLike.VariantBuilder(SnowModelConstant.OVERLAY_TINY).build()
        ))).requirement(EclipticSeasonsApi.MODID).build());

        // add(getPath(ClientModelDefinitions.OVERLAY_TINY), ESModelLoadedJson.builder()
        //         .multiPartLike(new MultiPartLike(List.of(
        //                 new SelectorLike((new MultiVariantLike(List.of(
        //                         new VariantLike.VariantBuilder(SnowModelConstant.OVERLAY_TINY).build()
        //                 ))))
        //         )))
        //         .requirement(EclipticSeasonsApi.MODID)
        //         .build());

        add(getPath(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY), ESModelLoadedJson.builder()
                .replace(false)
                .multiPartLike(new MultiPartLike(List.of(
                        new SelectorLike(new KeyValueConditionLike(GrassBlock.SNOWY, false), new MultiVariantLike(
                                List.of(
                                        VariantLike.builder(SnowModelConstant.GRASS_BLOCK_SNOW).rotationY(0).build(),
                                        VariantLike.builder(SnowModelConstant.GRASS_BLOCK_SNOW).rotationY(90).build(),
                                        VariantLike.builder(SnowModelConstant.GRASS_BLOCK_SNOW).rotationY(180).build(),
                                        VariantLike.builder(SnowModelConstant.GRASS_BLOCK_SNOW).rotationY(270).build()
                                ))),
                        new SelectorLike(new MultiVariantLike(List.of(
                                VariantLike.builder(SnowModelConstant.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(0).build(),
                                VariantLike.builder(SnowModelConstant.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(90).build(),
                                VariantLike.builder(SnowModelConstant.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(180).build(),
                                VariantLike.builder(SnowModelConstant.SNOWY_GRASS_BLOCK_OVERLAY).rotationY(270).build()
                        )))
                )))
                .requirement(EclipticSeasonsApi.MODID)
                .requirement(ResourceLocation.DEFAULT_NAMESPACE)
                .build());

        addFlower();

    }

    private void addFlower() {

        for (SolarTerm solarTerm : SolarTerm.collectValues()) {
            if (solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SPRING, SolarTerm.BEGINNING_OF_SUMMER)) {
                int weight = (Math.abs(solarTerm.ordinal() - 3) + 1) * 56 * 2;
                add(getPath(EclipticSeasons.rl("flower_on_grass_" + solarTerm.getName())), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT,
                        buildMultiVariantLikeFromList(ModelManager.flower_on_grass, weight)).build());
            }
            if (solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                int weight = (Math.abs(solarTerm.ordinal() - 7) + 1) * 42 * 2;
                add(getPath(EclipticSeasons.rl("fourleaf_clovers_" + solarTerm.getName())), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT,
                        buildMultiVariantLikeFromList(ModelManager.fourleaf_clovers, weight)).build());
            }
        }


    }

    private MultiVariantLike buildMultiVariantLikeFromList(List<ResourceLocation> modelResourceLocations, int emptyWeight) {
        List<VariantLike> list = new ArrayList<>(modelResourceLocations.stream()
                .map(r -> new VariantLike.VariantBuilder(r).weight(1).build())
                .toList());
        if (emptyWeight > 0)
            list.add(VariantLike.builder(new ResourceLocation("block/air")).weight(emptyWeight).build());
        return new MultiVariantLike(list);
    }


    private static String getPath(ResourceLocation overlay) {
        return overlay.getPath();
    }


}
