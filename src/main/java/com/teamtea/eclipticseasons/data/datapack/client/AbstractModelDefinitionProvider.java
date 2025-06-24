package com.teamtea.eclipticseasons.data.datapack.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.api.data.client.model.multipart.*;
import com.teamtea.eclipticseasons.api.data.client.model.variant.MultiVariantLike;
import com.teamtea.eclipticseasons.api.data.client.model.variant.VariantLike;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.datapack.client.base.ESClientDataMapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractModelDefinitionProvider extends ESClientDataMapProvider<ESModelLoadedJson> {

    public AbstractModelDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_MODEL_DEFINITION, ESModelLoadedJson.CODEC);
        this.blockModels = new ExtraModelProvider(output, modid, helper);
    }

    private final ExtraModelProvider blockModels;

    protected ExtraModelProvider models() {
        return blockModels;
    }

    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        return CompletableFuture.allOf(
                super.run(output, provider),
                models().generateAll(output));
    }

    protected static class ExtraModelProvider extends ModelProvider<ExtraModelBuilder> {

        public ExtraModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
            super(output, modid, BLOCK_FOLDER, ExtraModelBuilder::new, existingFileHelper);
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
            return CompletableFuture.allOf();
        }

        @Override
        public @NotNull String getName() {
            return "ES Block Model : " + modid;
        }

        @Override
        protected void registerModels() {
        }

        // If set folder would not use folder
        // private ResourceLocation extendWithFolder(ResourceLocation rl) {
        //     if (rl.getPath().contains("/")) {
        //         return rl;
        //     }
        //     return new ResourceLocation(rl.getNamespace(), folder + "/" + rl.getPath());
        // }

        @Override
        public ExtraModelBuilder getBuilder(String path) {
            Preconditions.checkNotNull(path, "Path must not be null");
            ResourceLocation outputLoc = withBlockFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path));
            this.existingFileHelper.trackGenerated(outputLoc, MODEL);
            return generatedModels.computeIfAbsent(outputLoc, factory);
        }

        public @NotNull ExtraModelBuilder getBuilder(@NotNull ResourceLocation path) {
            Preconditions.checkNotNull(path, "Path must not be null");
            ResourceLocation outputLoc = withBlockFolder(path);
            this.existingFileHelper.trackGenerated(outputLoc, MODEL);
            return generatedModels.computeIfAbsent(outputLoc, factory);
        }

        @Override
        public CompletableFuture<?> generateAll(CachedOutput cache) {
            return super.generateAll(cache);
        }


    }

    public static ResourceLocation withBlockFolder(ResourceLocation rl) {
        String prefix = ExtraModelProvider.BLOCK_FOLDER + "/";
        if (rl.getPath().startsWith(prefix)) {
            return rl;
        }
        return new ResourceLocation(rl.getNamespace(), ExtraModelProvider.BLOCK_FOLDER + "/" + rl.getPath());
    }

    protected VariantLike.VariantBuilder variant(ResourceLocation resourceLocation) {
        return VariantLike.builder(withBlockFolder(resourceLocation));
    }

    protected static @NotNull ConditionLike condition(String key, String value) {
        return new KeyValueConditionLike(key, value);
    }

    public static @NotNull ConditionLike condition(Property<?> property, Object object) {
        return new KeyValueConditionLike(property, object);
    }

    protected static @NotNull ConditionLike and(ConditionLike... conditionLikes) {
        return new AndConditionLike(List.of(conditionLikes));
    }

    protected static @NotNull ConditionLike or(ConditionLike... conditionLikes) {
        return new OrConditionLike(List.of(conditionLikes));
    }

    @SafeVarargs
    protected static @NotNull String conditionString(Pair<Property<?>, Comparable<?>>... conditions) {
        return Arrays.stream(conditions)
                .collect(Collectors.groupingBy(
                        Pair::getFirst,
                        Collectors.mapping(Pair::getSecond, Collectors.toList())
                )).entrySet().stream()
                .map(e -> e.getKey().getName() + "=" + e.getValue()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("|")))
                .collect(Collectors.joining(","));
    }

    protected static @NotNull String conditionString(Map<Property<?>, Comparable<?>> conditions) {
        return conditions.entrySet().stream()
                .map(e -> e.getKey().getName() + "=" + e.getValue())
                .collect(Collectors.joining(","));
    }

    protected BlockModelDefinitionBuilder addSnowyBlockModelDefinition(Block block) {
        return addBlockModelDefinition(block, block.builtInRegistryHolder().key().location().withPrefix("snowy/"));
    }

    public BlockModelDefinitionBuilder addBlockModelDefinition(Block block, ResourceLocation location) {
        BlockModelDefinitionBuilder variantModelBuilder = new BlockModelDefinitionBuilder(models(), block, location);
        add(location, variantModelBuilder::build);
        return variantModelBuilder;
    }

    public PartialStateModelDefinitionBuilder addPartialStateModelDefinition(ResourceLocation location, Block... blocks) {
        Map<Property<?>, List<? extends Comparable<?>>> collect = Arrays.stream(blocks)
                .map(block -> block.getStateDefinition().getProperties())
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(
                        e -> e,
                        e -> new ArrayList<>(e.getPossibleValues())
                ));
        return addPartialStateModelDefinition(location, collect);
    }

    public PartialStateModelDefinitionBuilder addPartialStateModelDefinition(ResourceLocation location, Map<Property<?>, List<? extends Comparable<?>>> allowValues) {
        PartialStateModelDefinitionBuilder variantModelBuilder = new PartialStateModelDefinitionBuilder(models(), allowValues, location);
        add(location, variantModelBuilder::build);
        return variantModelBuilder;
    }

    public ModelDefinitionBuilder addModelDefinition(ResourceLocation location) {
        ModelDefinitionBuilder variantModelBuilder = new ModelDefinitionBuilder(models(), location);
        add(location, variantModelBuilder::build);
        return variantModelBuilder;
    }

    public ModelDefinitionBuilder simple(ResourceLocation location) {
        return addModelDefinition(location).singleWithExist();
    }

    public static class BlockModelDefinitionBuilder extends ModelDefinitionBuilder {

        protected final Block owner;

        protected BlockModelDefinitionBuilder(ExtraModelProvider models, Block block, ResourceLocation defLoc) {
            super(models, defLoc);
            this.owner = block;
        }


        public BlockModelDefinitionBuilder variantsForAllStatesExcept(Function<BlockState, VariantLike[]> mapper, Property<?>... ignored) {
            Set<PartialState> seen = new HashSet<>();
            for (BlockState fullState : owner.getStateDefinition().getPossibleStates()) {
                Map<Property<?>, Comparable<?>> propertyValues = Maps.newLinkedHashMap(fullState.getValues());
                for (Property<?> p : ignored) {
                    propertyValues.remove(p);
                }
                PartialState partialState = new PartialState(propertyValues);
                if (seen.add(partialState)) {
                    VariantLike[] variantLikes = mapper.apply(fullState);
                    if (variantLikes.length > 0) {
                        variant(conditionString(propertyValues), variantLikes);
                    }
                }
            }
            return this;
        }

    }

    public static class PartialStateModelDefinitionBuilder extends ModelDefinitionBuilder {

        private final Map<Property<?>, List<? extends Comparable<?>>> allowValues;

        protected PartialStateModelDefinitionBuilder(ExtraModelProvider models, Map<Property<?>, List<? extends Comparable<?>>> allowValues, ResourceLocation defLoc) {
            super(models, defLoc);
            this.allowValues = allowValues;
        }

        public PartialStateModelDefinitionBuilder variantsForAllStatesExcept(Function<PartialState, VariantLike[]> mapper, Property<?>... ignored) {
            Set<PartialState> seen = new HashSet<>();
            for (PartialState partialState : PartialState.combine(allowValues)) {
                if (seen.add(partialState)) {
                    VariantLike[] variantLikes = mapper.apply(partialState);
                    if (variantLikes.length > 0) {
                        variant(conditionString(partialState.map), variantLikes);
                    }
                }
            }
            return this;
        }
    }

    public record PartialState(Map<Property<?>, Comparable<?>> map) {

        public static List<PartialState> combine(Map<Property<?>, List<? extends Comparable<?>>> map) {
            List<Property<?>> properties = new ArrayList<>(map.keySet());
            List<PartialState> result = new ArrayList<>();
            backtrack(properties, map, 0, new LinkedHashMap<>(), result);
            return result;
        }

        private static void backtrack(
                List<Property<?>> properties,
                Map<Property<?>, List<? extends Comparable<?>>> map,
                int index,
                LinkedHashMap<Property<?>, Comparable<?>> current,
                List<PartialState> result) {

            if (index == properties.size()) {
                result.add(new PartialState(new LinkedHashMap<>(current)));
                return;
            }

            Property<?> prop = properties.get(index);
            List<? extends Comparable<?>> values = map.get(prop);

            if (values == null || values.isEmpty()) {
                backtrack(properties, map, index + 1, current, result);
                return;
            }

            for (Comparable<?> val : values) {
                current.put(prop, val);
                backtrack(properties, map, index + 1, current, result);
                current.remove(prop);
            }
        }
    }

    public static class ModelDefinitionBuilder {
        // private final ESModelLoadedJson.ESModelLoadedJsonBuilder builder;
        private final ResourceLocation defLoc;
        protected final ExtraModelProvider models;
        // protected final List<Runnable> modelsToGenerated = new ArrayList<>();
        protected final Map<ConditionLike, List<VariantLike>> selectorLikes = new LinkedHashMap<>();
        protected boolean replace = false;
        protected final Set<String> requirement = new LinkedHashSet<>();
        protected Map<String, List<VariantLike>> multiVariants = new LinkedHashMap<>();

        private ModelDefinitionBuilder(ExtraModelProvider models, ResourceLocation defLoc) {
            this.defLoc = defLoc;
            this.models = models;
            // this.builder = ESModelLoadedJson.builder();
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
                ResourceLocation stageId = withBlockFolder(defLoc).withSuffix("_stage" + i);
                VariantLike variant = VariantLike.builder(stageId).build();
                variant(variantKey + "=" + i, variant);
                // this.crossModelsToGenerated.add(stageId);
                // this.modelsToGenerated.add(() -> cross(stageId));
                models.cross(stageId.toString(), stageId);
            }
            return this;
        }


        @SafeVarargs
        protected final ModelDefinitionBuilder simple(Function<ResourceLocation, ExtraModelBuilder>... model) {
            clearCache();
            ResourceLocation withPrefix = withBlockFolder(defLoc);
            if (model.length == 1) {
                variant(VariantLike.builder(withPrefix).build());
                // this.modelsToGenerated.add(() -> model[0].accept(models().getBuilder(withPrefix.toString())));
                // model[0].accept(models().getBuilder(withPrefix.toString()));
                model[0].apply(withPrefix);
            } else {
                for (int i = 0; i < model.length; i++) {
                    ResourceLocation variantId = defLoc.withSuffix("_" + i);
                    int finalI = i;
                    // this.modelsToGenerated.add(() -> model[finalI].accept(models().getBuilder(variantId.toString())));
                    // model[finalI].accept(models().getBuilder(variantId.toString()));
                    model[finalI].apply(variantId);

                    variant(VariantLike.builder(variantId).build());
                }
            }
            return this;
        }

        public ModelDefinitionBuilder multiPartWithGenerate(VariantLike variant, Function<ResourceLocation, ExtraModelBuilder> modelGenerator) {
            return multiPartWithGenerate(SelectorLike.EMPTY_CONDITION, modelGenerator, variant);
        }

        public ModelDefinitionBuilder multiPartWithGenerate(ConditionLike condition, Function<ResourceLocation, ExtraModelBuilder> modelGenerator, VariantLike variant) {
            return multiPartWithGenerate(condition, variant.getModelLocation(), modelGenerator, variant);
        }

        public ModelDefinitionBuilder multiPartWithGenerate(ConditionLike condition, ResourceLocation modelLoc, Function<ResourceLocation, ExtraModelBuilder> modelGenerator, VariantLike... variants) {
            // this.modelsToGenerated.add(() -> modelGenerator.accept(models().getBuilder(modelLoc)));
            // modelGenerator.accept(models().getBuilder(modelLoc));
            modelGenerator.apply(modelLoc);
            return multiPart(condition, variants);
        }

        public ModelDefinitionBuilder multiPart(VariantLike... variant) {
            return multiPart(SelectorLike.EMPTY_CONDITION, variant);
        }

        public ModelDefinitionBuilder multiPart(@Nullable ConditionLike condition, VariantLike... variant) {
            condition = condition == null ? SelectorLike.EMPTY_CONDITION : condition;
            selectorLikes
                    .computeIfAbsent(condition, k -> new ArrayList<>())
                    .addAll(List.of(variant));
            return this;
        }

        public ModelDefinitionBuilder variantWithGenerate(VariantLike variant, Function<ResourceLocation, ExtraModelBuilder> modelGenerator) {
            return variantWithGenerate(ESModelLoadedJson.ALL_VARIANT, modelGenerator, variant);
        }

        public ModelDefinitionBuilder variantWithGenerate(String condition, Function<ResourceLocation, ExtraModelBuilder> modelGenerator, VariantLike variant) {
            return variantWithGenerate(condition, variant.getModelLocation(), modelGenerator, variant);
        }

        public ModelDefinitionBuilder variantWithGenerate(String condition, ResourceLocation modelLoc, Function<ResourceLocation, ExtraModelBuilder> modelGenerator, VariantLike... variants) {
            // this.modelsToGenerated.add(() -> modelGenerator.accept(models().getBuilder(modelLoc)));
            // modelGenerator.accept(models().getBuilder(modelLoc));
            modelGenerator.apply(modelLoc);
            return variant(condition, variants);
        }

        public ModelDefinitionBuilder variant(VariantLike... variant) {
            return variant(ESModelLoadedJson.ALL_VARIANT, variant);
        }

        public ModelDefinitionBuilder variant(@Nullable String condition, VariantLike... variant) {
            condition = condition == null ? ESModelLoadedJson.ALL_VARIANT : condition;
            multiVariants
                    .computeIfAbsent(condition, k -> new ArrayList<>())
                    .addAll(List.of(variant));
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
            clearCache();
            ResourceLocation withPrefix = withBlockFolder(defLoc);
            variant(VariantLike.builder(withPrefix).build());
            // this.modelsToGenerated.add(() -> cross(withPrefix));
            models.cross(withPrefix.toString(), withPrefix);
            return this;
        }

        public ModelDefinitionBuilder singleWithExist() {
            clearCache();
            ResourceLocation withPrefix = withBlockFolder(defLoc);
            variant(VariantLike.builder(withPrefix).build());
            return this;
        }

        // public ModelDefinitionBuilder toModels() {
        //     modelsToGenerated.forEach(Runnable::run);
        //     return this;
        // }

        public ESModelLoadedJson build() {
            return
                    ESModelLoadedJson.builder()
                            .replace(replace)
                            .require(requirement.stream().toList())
                            .variants(multiVariants
                                    .entrySet()
                                    .stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> new MultiVariantLike(e.getValue())
                                    ))
                            )
                            .multiPartLike(
                                    new MultiPartLike(selectorLikes.entrySet()
                                            .stream()
                                            .map(e -> new SelectorLike(e.getKey(), new MultiVariantLike(e.getValue())))
                                            .toList())
                            )
                            .build();
        }


    }


    public static class ExtraModelBuilder extends ModelBuilder<ExtraModelBuilder> {
        public ExtraModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
            super(outputLocation, existingFileHelper);
        }

        private ExtraModelBuilder self() {
            return this;
        }

        // @Override
        // public SlefBlockModelBuilder texture(String key, String texture) {
        //     Preconditions.checkNotNull(key, "Key must not be null");
        //     Preconditions.checkNotNull(texture, "Texture must not be null");
        //     if (texture.charAt(0) == '#') {
        //         this.textures.put(key, texture);
        //         return self();
        //     } else {
        //         ResourceLocation asLoc;
        //         if (texture.contains(":")) {
        //             asLoc = new ResourceLocation(texture);
        //         } else {
        //             asLoc = new ResourceLocation(getLocation().getNamespace(), texture);
        //         }
        //         return texture(key, asLoc);
        //     }
        // }


        @Override
        public ExtraModelBuilder texture(String key, ResourceLocation texture) {
            Preconditions.checkNotNull(key, "Key must not be null");
            Preconditions.checkNotNull(texture, "Texture must not be null");
            // Preconditions.checkArgument(existingFileHelper.exists(texture, ModelProvider.TEXTURE),
            //         "Texture %s does not exist in any known resource pack", texture);
            this.textures.put(key, texture.toString());
            return self();
        }
    }
}
