package com.teamtea.eclipticseasons.data.api.provider;

import com.teamtea.eclipticseasons.api.data.client.model.ESBlockModelDefinition;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientDataMapProvider;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.multipart.Condition;
import net.minecraft.client.renderer.block.dispatch.multipart.Selector;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class AbstractModelDefinitionProvider extends ESClientDataMapProvider<ESModelLoadedJson> {

    private final ExtraModelProvider blockModels;
    private final PackOutput.PathProvider modelPathProvider;

    public AbstractModelDefinitionProvider(PackOutput output, String modid, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, registries, ClientJsonCacheListener.DIRECTORY_MODEL_DEFINITION, ESModelLoadedJson.CODEC.codec());
        this.modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        this.blockModels = new ExtraModelProvider();
    }

    protected ExtraModelProvider models() {
        return blockModels;
    }

    @Override
    protected abstract void gather(HolderLookup.Provider provider);


    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        return
                CompletableFuture.allOf(
                        super.run(output, provider),
                        blockModels.save(output, modelPathProvider));
    }

    public static class ExtraModelProvider extends ModelProvider.SimpleModelCollector {
        public ExtraModelProvider() {
            super();
        }

        @Override
        public CompletableFuture<?> save(CachedOutput cache, PackOutput.PathProvider pathProvider) {
            return super.save(cache, pathProvider);
        }
    }

    public ModelDefinitionBuilder addModelDefinition(Identifier defLoc) {
        ModelDefinitionBuilder builder = new ModelDefinitionBuilder(blockModels, defLoc);
        add(defLoc, builder::build);
        return builder;
    }

    public ModelDefinitionBuilder simple(Identifier location) {
        return addModelDefinition(location).singleWithExist();
    }

    public ModelDefinitionBuilder addSnowy(Block block) {
        return addModelDefinition(block.builtInRegistryHolder().getKey().identifier().withPrefix("snowy/"));
    }

    public class ModelDefinitionBuilder {
        private final Identifier defLoc;
        protected final ExtraModelProvider models;
        protected final List<Selector> selectorLikes = new ArrayList<>();
        protected boolean replace = false;
        protected final Set<String> requirement = new LinkedHashSet<>();
        protected Map<String, BlockStateModel.Unbaked> multiVariants = new LinkedHashMap<>();

        private ModelDefinitionBuilder(ExtraModelProvider models, Identifier defLoc) {
            this.defLoc = defLoc;
            this.models = models;
        }

        public ModelDefinitionBuilder replace(boolean replace) {
            this.replace = replace;
            return this;
        }

        public ModelDefinitionBuilder requireMod(String modid) {
            requirement.add(modid);
            return this;
        }


        public ModelDefinitionBuilder stagedVariants(String variantKey, int count) {
            clearCache();
            for (int i = 0; i < count; i++) {
                Identifier stageId = withBlockFolder(defLoc).withSuffix("_stage" + i);
                BlockStateModel.Unbaked variant = new SingleVariant.Unbaked(new Variant(stageId));
                variant(variantKey + "=" + i, variant);
                ModelTemplates.CROSS.create(stageId, TextureMapping.cross(new Material(stageId)), models);
            }
            return this;
        }

        public ModelDefinitionBuilder multiPartWithGenerateSingle(Condition condition, Supplier<BlockStateModel.Unbaked> modelGenerator) {
            return multiPart(condition, modelGenerator.get());
        }

        public ModelDefinitionBuilder multiPart(@Nullable Condition condition, BlockStateModel.Unbaked variant) {
            selectorLikes.add(new Selector(Optional.ofNullable(condition), variant));
            return this;
        }

        public ModelDefinitionBuilder variant(BlockStateModel.Unbaked variant) {
            return variant(ESModelLoadedJson.ALL_VARIANT, variant);
        }

        public ModelDefinitionBuilder variant(@Nullable String condition, BlockStateModel.Unbaked variant) {
            condition = condition == null ? ESModelLoadedJson.ALL_VARIANT : condition;
            multiVariants.put(condition, variant);
            return this;
        }

        protected void clearVariantCache() {
            this.multiVariants.clear();
        }

        protected void clearCache() {
            // modelsToGenerated.clear();
            clearVariantCache();
            this.selectorLikes.clear();
        }

        public ModelDefinitionBuilder singleCross() {
            Identifier withPrefix = withBlockFolder(defLoc);
            return singleCross(withPrefix);
        }

        public ModelDefinitionBuilder singleCross(Identifier texturePath) {
            clearCache();
            Identifier withPrefix = withBlockFolder(defLoc);
            variant(new SingleVariant.Unbaked(new Variant(withPrefix)));
            ModelTemplates.CROSS.create(withPrefix, TextureMapping.cross(new Material(texturePath)), models);
            return this;
        }

        public ModelDefinitionBuilder singleWithExist() {
            clearCache();
            Identifier withPrefix = withBlockFolder(defLoc);
            variant(new SingleVariant.Unbaked(new Variant(withPrefix)));
            return this;
        }


        public ESModelLoadedJson build() {

            return ESModelLoadedJson.builder()
                    .customDefinition(ESBlockModelDefinition.builder()
                            .replace(replace)
                            .require(requirement.stream().toList())
                            .build())
                    .variants(Optional.ofNullable(multiVariants.isEmpty() ? null :
                            new BlockStateModelDispatcher.SimpleModelSelectors(multiVariants))
                    )
                    .multiPart(Optional.ofNullable(selectorLikes.isEmpty() ? null :
                                    new BlockStateModelDispatcher.MultiPartDefinition(selectorLikes)
                            )
                    )
                    .build();

        }


    }

    public static final String BLOCK_FOLDER = "block";

    public static Identifier withBlockFolder(Identifier rl) {
        String prefix = BLOCK_FOLDER + "/";
        if (rl.getPath().startsWith(prefix)) {
            return rl;
        }
        return Identifier.fromNamespaceAndPath(rl.getNamespace(), BLOCK_FOLDER + "/" + rl.getPath());
    }

    public Identifier snow_rl(String path) {
        String prefix = "snowy/";
        if (path.startsWith(prefix)) {
            return withBlockFolder(Identifier.fromNamespaceAndPath(modid, path));
        }
        return withBlockFolder(Identifier.fromNamespaceAndPath(modid, "snowy/" + path));
    }
}
