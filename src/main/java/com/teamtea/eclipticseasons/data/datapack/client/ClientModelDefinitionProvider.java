package com.teamtea.eclipticseasons.data.datapack.client;

import com.google.common.base.Preconditions;
import com.teamtea.eclipticseasons.EclipticSeasons;
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
import com.teamtea.eclipticseasons.data.datapack.client.base.ESClientDataMapProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrassBlock;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ClientModelDefinitionProvider extends ESClientDataMapProvider<ESModelLoadedJson> {


    private final MyBlockModelProvider blockModels;

    public ClientModelDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_MODEL_DEFINITION, ESModelLoadedJson.CODEC);
        this.blockModels = new MyBlockModelProvider(output, modid, helper);
    }

    public MyBlockModelProvider models() {
        return blockModels;
    }


    @Override
    protected void gather(HolderLookup.Provider provider) {
        add(getPath(ClientModelDefinitions.OVERLAY), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT, new MultiVariantLike(List.of(
                new VariantLike.VariantBuilder(SnowModelConstant.OVERLAY).build()
        ))).build());

        add(getPath(ClientModelDefinitions.SNOWY_LEAVES_TOP), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT, new MultiVariantLike(List.of(
                new VariantLike.VariantBuilder(SnowModelConstant.SNOWY_LEAVES_TOP).build()
        ))).build());
        add(getPath(ClientModelDefinitions.SNOWY_LEAVES_ATTACH), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT, new MultiVariantLike(List.of(
                new VariantLike.VariantBuilder(SnowModelConstant.SNOWY_LEAVES_ATTACH).build()
        ))).build());

        add(getPath(ClientModelDefinitions.OVERLAY_TINY), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT, new MultiVariantLike(List.of(
                new VariantLike.VariantBuilder(SnowModelConstant.OVERLAY_TINY).build()
        ))).build());

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

            // if (solarTerm == SolarTerm.BEGINNING_OF_SUMMER) {
            //     int weight = ((Math.abs(solarTerm.ordinal() - 7) + 1) * 42 + (Math.abs(solarTerm.ordinal() - 3) + 1) * 56);
            //     ArrayList<ModelResourceLocation> modelResourceLocations = new ArrayList<>(ModelManager.flower_on_grass);
            //     modelResourceLocations.addAll(ModelManager.fourleaf_clovers);
            //     add(getPath(EclipticSeasons.rl("flower_on_grass_summer_start")), ESModelLoadedJson.builder().variant(ESModelLoadedJson.ALL_VARIANT,
            //             buildMultiVariantLikeFromList(modelResourceLocations, weight)).build());
            // }
        }


    }

    private MultiVariantLike buildMultiVariantLikeFromList(List<ModelResourceLocation> modelResourceLocations, int emptyWeight) {
        List<VariantLike> list = new java.util.ArrayList<>(modelResourceLocations.stream()
                .map(ModelResourceLocation::id)
                .map(r -> new VariantLike.VariantBuilder(r).weight(1).build())
                .toList());
        if (emptyWeight > 0)
            list.add(VariantLike.builder(ResourceLocation.withDefaultNamespace("block/air")).weight(emptyWeight).build());
        return new MultiVariantLike(list);
    }


    private static String getPath(ResourceLocation overlay) {
        return overlay.getPath();
    }


    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        return CompletableFuture.allOf(
                super.run(output, provider),
                models().generateAll(output));
    }

    private class MyBlockModelProvider extends BlockModelProvider {
        public MyBlockModelProvider(PackOutput output, String modid, ExistingFileHelper helper) {
            super(output, modid, helper);
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
            return CompletableFuture.allOf();
        }

        @Override
        protected void registerModels() {
        }


        @Override
        public CompletableFuture<?> generateAll(CachedOutput cache) {
            return super.generateAll(cache);
        }
    }
}
