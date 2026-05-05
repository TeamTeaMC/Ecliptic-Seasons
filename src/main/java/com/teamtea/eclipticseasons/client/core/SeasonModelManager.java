package com.teamtea.eclipticseasons.client.core;

import com.google.common.collect.ImmutableList;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.api.data.client.model.ModelResolver;
import com.teamtea.eclipticseasons.api.data.client.model.ModelTester;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyBlockState;
import com.teamtea.eclipticseasons.client.model.MyResolver;
import com.teamtea.eclipticseasons.client.model.block.DerivedSnowyBlockStateModel;
import com.teamtea.eclipticseasons.client.model.block.ExtendedMultiPartModel;
import com.teamtea.eclipticseasons.client.model.block.ReplacingBlockStateModel;
import com.teamtea.eclipticseasons.client.model.block.unbake.standalone.CustomUnbakeModel;
import com.teamtea.eclipticseasons.client.model.block.unbake.multipart.ConditionLike;
import com.teamtea.eclipticseasons.client.model.block.unbake.standalone.SeasonalUnbakeModel;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.compat.Platform;
import com.teamtea.eclipticseasons.compat.ctm.CtmLoader;
import com.teamtea.eclipticseasons.compat.ctm.CtmProperties;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.multipart.MultiPartModel;
import net.minecraft.client.renderer.block.dispatch.multipart.Selector;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SeasonModelManager {
    public static int loadVersion = 0;
    public static ModelBakery.BakingResult models;

    public static BlockPos.MutableBlockPos posToMutable(BlockPos pos) {
        return new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isModelReplaceable(BlockStateModel snowOverlay, int flag) {
        if (snowOverlay instanceof ReplacingBlockStateModel rp)
            return rp.replace();
        return flag == MapChecker.FLAG_GRASS
                || flag == MapChecker.FLAG_GRASS_LARGE;
    }

    public static Map<Block, CtmProperties> ctmStates = new IdentityHashMap<>();
    public static Map<Identifier, Void> ctmTiles = new HashMap<>();

    public static boolean isSpecialCTMBlock(BlockState blockState) {
        if (ctmStates.isEmpty()) return false;
        CtmProperties orDefault = ctmStates.getOrDefault(blockState.getBlock(), null);
        return orDefault != null && orDefault.matches(blockState);
    }

    public static boolean isSpecialCTMSprite(TextureAtlasSprite sprite) {
        if (ctmTiles.isEmpty()) return false;
        try {
            SpriteContents spriteContents = sprite.contents();
            return ctmTiles.containsKey(spriteContents.name());
        } catch (Exception exception) {
            EclipticSeasons.logger(exception);
        }
        return false;
    }

    public static void initCTMDetected() {
        long l = System.currentTimeMillis();
        ctmStates.clear();
        ctmTiles.clear();

        CtmLoader.CTMLoadingResult ctmLoadingResult = CtmLoader.loadAll(Minecraft.getInstance().getResourceManager());
        ctmStates = ctmLoadingResult.ctmStates;
        ctmTiles = ctmLoadingResult.ctmTiles;

        EclipticSeasons.logger("CTM detector cost %s ms".formatted(System.currentTimeMillis() - l));
    }
    // public static final Map<Identifier, ESModelLoadedJson> extraSnowModels = HashMap.newHashMap(1024);

    public static final Map<Identifier, ModelResolver> extraSnowModelBuilds = HashMap.newHashMap(1024);


    public static void clearForRebaked(ModelBakery.BakingResult modelRegistry) {
        SeasonModelManager.models = modelRegistry;

        for (Identifier identifier : SEASONAL_TEXTURE_HASH_MAP.keySet()) {
            Set<BlockState> blockStates = MyResolver.INSTANCE.getUsedModel().getOrDefault(identifier, Set.of());
            if (!blockStates.isEmpty()) {
                BlockStateModel blockStateModel = models.standaloneModels().get(SEASON_TEXTURE_MODEL_ID_MAPPER.get(identifier));
                if (blockStateModel != null) for (BlockState possibleState : blockStates) {
                    models.blockStateModels().put(
                            possibleState, blockStateModel
                    );
                }
            }
        }
        MyResolver.INSTANCE.clear();

        // replaceModelMap.clear();
        // replaceModelMap.defaultReturnValue(false);

        DerivedSnowyBlockStateModel.PART_CACHE_MAP.clear();

        loadVersion++;
        initCTMDetected();
        if (ClientCon.getUseLevel() != null) {
            ClientRef.updateClientSide(ClientCon.getUseLevel().registryAccess());
        }

        snowOverlayLeaves = modelRegistry.blockStateModels().get(BlockRegistry.snowyLeaves.get().defaultBlockState());
        snowySlabBottom = modelRegistry.blockStateModels().get(BlockRegistry.snowySlab.get().defaultBlockState()
                .setValue(SlabBlock.TYPE, SlabType.BOTTOM)
                .setValue(SlabBlock.WATERLOGGED, false)
        );
        snowOverlayBlock = modelRegistry.blockStateModels().get(BlockRegistry.snowyBlock.get().defaultBlockState());
    }

    public static void prepareTextureMapping() {
        ClientJsonCacheListener.textureReMappingsCache.prepareAsync(Minecraft.getInstance().getResourceManager());
        SeasonModelManager.SEASONAL_TEXTURE_HASH_MAP.clear();
        Map<Identifier, SeasonalTexture> build = ClientJsonCacheListener.textureReMappingsCache.build(SeasonalTexture.CODEC);
        build.forEach(
                (Identifier, seasonalTexture) -> {
                    seasonalTexture = seasonalTexture.build(Identifier);
                    if (seasonalTexture.getParent().isEmpty()) {
                        List<SeasonalTexture> seasonalTextures = SeasonModelManager.SEASONAL_TEXTURE_HASH_MAP.computeIfAbsent(
                                Identifier.withPrefix("block/"), (xx) -> new ArrayList<>());
                        seasonalTextures.add(seasonalTexture);
                    } else {
                        for (Identifier location : seasonalTexture.getParent()) {
                            List<SeasonalTexture> seasonalTextures = SeasonModelManager.SEASONAL_TEXTURE_HASH_MAP.computeIfAbsent(
                                    location, (xx) -> new ArrayList<>());
                            seasonalTextures.add(seasonalTexture);
                        }
                    }
                }
        );
    }


    public static Map<Identifier, StandaloneModelKey<BlockStateModel>> SEASON_TEXTURE_MODEL_ID_MAPPER = new HashMap<>();

    public static void registerExtraSnowyModels(BiConsumer<StandaloneModelKey<BlockStateModel>, UnbakedStandaloneModel<BlockStateModel>> registerModelAndDependenceMethod) {
        extraSnowModelBuilds.clear();
        // extraSnowModels.clear();
        // We need to load it before event
        ClientJsonCacheListener.modelDefCache.prepareAsync(Minecraft.getInstance().getResourceManager());
        prepareTextureMapping();

        Map<Identifier, ESModelLoadedJson> snowModelLoadedJsonMap = ClientJsonCacheListener.modelDefCache.build(ESModelLoadedJson.CODEC.codec());
        // extraSnowModels.putAll(snowModelLoadedJsonMap);
        EclipticSeasons.logger("Try to register extra model definitions with size %s.".formatted(snowModelLoadedJsonMap.size()));
        snowModelLoadedJsonMap.forEach(
                (resourceLocation, loadedJson) -> {
                    if (!loadedJson.getCustomDefinition().getRequire().isEmpty()) {
                        for (String modid : loadedJson.getCustomDefinition().getRequire()) {
                            if (!Platform.isModLoaded(modid)) {
                                return;
                            }
                        }
                    }
                    if (loadedJson.getMultiPart().isPresent()) {
                        BlockStateModelDispatcher.MultiPartDefinition multiPartDefinition = loadedJson.getMultiPart().get();
                        StandaloneModelKey<BlockStateModel> mrl = SeasonModelManager.extra_mrl(resourceLocation, "0");
                        // registerModelAndDependenceMethod.accept(mrl, loadedJson.getMultiPart().get());
                        registerModelAndDependenceMethod.accept(
                                mrl,
                                new CustomUnbakeModel<>(
                                        resolver -> {
                                            for (Selector selector : multiPartDefinition.selectors()) {
                                                selector.variant().resolveDependencies(resolver);
                                            }
                                        },
                                        ((resolvedModel, modelBaker) -> {
                                            ImmutableList.Builder<MultiPartModel.Selector<BlockStateModel.Unbaked>> instantiatedSelectors = ImmutableList.builderWithExpectedSize(multiPartDefinition.selectors().size());

                                            for (Selector selector : multiPartDefinition.selectors()) {
                                                instantiatedSelectors.add(new MultiPartModel.Selector<>(
                                                        ConditionLike.of(selector.condition()).instantiate(ExtendedMultiPartModel.FakeStateDefinition.of()), selector.variant()));
                                            }

                                            return new ExtendedMultiPartModel.Unbaked(instantiatedSelectors.build()).bake(Blocks.AIR.defaultBlockState(), modelBaker);
                                        })
                                ));
                        extraSnowModelBuilds.put(
                                resourceLocation, new ModelResolver(List.of(new ModelTester(
                                        mrl, loadedJson.getCustomDefinition().isReplace(), List.of()
                                )))
                        );
                    } else if (loadedJson.getVariants().isPresent()) {


                        loadedJson.getVariants().get().models().forEach(
                                (va, multiVariant) -> {
                                    StandaloneModelKey<BlockStateModel> mrl = SeasonModelManager.extra_mrl(resourceLocation, va.replaceAll("=", "_").replace(",", "_"));

                                    registerModelAndDependenceMethod.accept(
                                            mrl,
                                            new CustomUnbakeModel<>(
                                                    multiVariant,
                                                    ((resolvedModel, modelBaker) ->
                                                            multiVariant.bake(modelBaker))
                                            ));
                                    {
                                        extraSnowModelBuilds.compute(
                                                resourceLocation,
                                                (sss, solver) -> {
                                                    if (solver == null) {
                                                        solver = new ModelResolver(new ArrayList<>());
                                                    }
                                                    List<SnowDefinition.PropertyTester> test = new ArrayList<>();
                                                    for (String s : va.split(",")) {
                                                        String[] split = s.split("=");
                                                        if (split.length == 2) {
                                                            test.add(
                                                                    SnowDefinition.PropertyTester.builder().name(split[0])
                                                                            .matcher(SnowDefinition.ExactMatcher.builder().value(split[1]).build()).build()
                                                            );
                                                        }
                                                    }
                                                    solver.modelTesters().add(
                                                            new ModelTester(mrl, loadedJson.getCustomDefinition().isReplace(), test)
                                                    );
                                                    return solver;

                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );

        SEASONAL_TEXTURE_HASH_MAP.forEach((identifier, seasonalTextures) -> {
            StandaloneModelKey<BlockStateModel> season = extra_mrl(identifier, "season");
            SEASON_TEXTURE_MODEL_ID_MAPPER.put(identifier, season);
            registerModelAndDependenceMethod.accept(season, new SeasonalUnbakeModel<>(identifier, seasonalTextures)
            );
        });

    }


//     ==========================================

    public static BlockStateModel snowOverlayLeaves;
    public static BlockStateModel snowySlabBottom;
    public static BlockStateModel snowOverlayBlock;

    public static StandaloneModelKey<BlockStateModel> ice = mrl("block/ice");

    public static StandaloneModelKey<BlockStateModel> snowy_custom = mrl("block/snowy_custom");
    public static StandaloneModelKey<BlockStateModel> snowy_custom_ao = mrl("block/snowy_custom_ao");

    public static StandaloneModelKey<BlockStateModel> stairs_top = mrl("block/stairs_top");

    public static StandaloneModelKey<BlockStateModel> snowy_leaves_attach = mrl("block/snowy_leaves_attach");
    public static StandaloneModelKey<BlockStateModel> snowy_leaves_top = mrl("block/snowy_leaves_top");

    public static StandaloneModelKey<BlockStateModel> snowy_fern = mrl("block/snowy_fern");
    public static StandaloneModelKey<BlockStateModel> snowy_grass = mrl("block/snowy_grass");
    public static StandaloneModelKey<BlockStateModel> snowy_large_fern_bottom = mrl("block/snowy_large_fern_bottom");
    public static StandaloneModelKey<BlockStateModel> snowy_large_fern_top = mrl("block/snowy_large_fern_top");
    public static StandaloneModelKey<BlockStateModel> snowy_tall_grass_bottom = mrl("block/snowy_tall_grass_bottom");
    public static StandaloneModelKey<BlockStateModel> snowy_tall_grass_top = mrl("block/snowy_tall_grass_top");

    public static StandaloneModelKey<BlockStateModel> overlay_2 = mrl("block/overlay_2");
    public static StandaloneModelKey<BlockStateModel> snow_height2 = mrl("block/snow_height2");
    public static StandaloneModelKey<BlockStateModel> snow_height2_top = mrl("block/snow_height2_top");
    public static StandaloneModelKey<BlockStateModel> grass_flower = mrl("block/grass_flower");
    public static List<StandaloneModelKey<BlockStateModel>> flower_on_grass = Stream.of(1, 2, 3, 4, 5, 6).map(i -> mrl("block/grass_flower/flower_%s".formatted(i))).collect(Collectors.toCollection(ArrayList::new));
    public static List<StandaloneModelKey<BlockStateModel>> snow_edge_overlays = IntStream.rangeClosed(0, 18).mapToObj(i -> mrl("block/snow_edge/snow_edge_overlay_%s".formatted(i))).collect(Collectors.toCollection(ArrayList::new));
    public static List<StandaloneModelKey<BlockStateModel>> fourleaf_clovers = IntStream.rangeClosed(0, 6).mapToObj(i -> mrl("block/fourleaf_clover/fourleaf_clover_%s".formatted(i))).collect(Collectors.toCollection(ArrayList::new));
    public static List<StandaloneModelKey<BlockStateModel>> leaf_piles = IntStream.rangeClosed(0, 7).mapToObj(i -> mrl("block/leaf_pile/leaf_pile_%s".formatted(i))).collect(Collectors.toCollection(ArrayList::new));

    // public static StandaloneModelKey<BlockStateModel> fourleaf_clover = mrl("block/fourleaf_clover");

    public static Identifier snow = Identifier.withDefaultNamespace("snow");
    public static Identifier snow_overlay_half_left = textureRL("snow_overlay_half_left");
    public static Identifier snow_overlay_half_right = textureRL("snow_overlay_half_right");
    public static Identifier snow_overlay = textureRL("snow_overlay");
    public static Identifier snow_overlay_leaves = textureRL("snow_overlay_leaves");
    public static Identifier snow_overlay_tiny = textureRL("snow_overlay_tiny");
    public static Identifier snow_spot_overlay_leaves = textureRL("snow_spot_overlay_leaves");

    public static StandaloneModelKey<BlockStateModel> mrl(String s) {
        return new StandaloneModelKey<>(new ModelIDHolder(EclipticSeasons.rl(s)));
    }

    // Can not hash
    public static StandaloneModelKey<BlockStateModel> extra_mrl(Identifier Identifier, String v) {
        return new StandaloneModelKey<>(new ModelIDHolder(Identifier.withPrefix("extra/" + (v.isEmpty() ? "" : v + "/"))));
    }

    public static boolean isSpecialSnowySprite(TextureAtlasSprite textureAtlasSprite) {
        return textureAtlasSprite.contents().name().toString().contains("snow");
    }

    public record ModelIDHolder(Identifier id) implements ModelDebugName {
        @Override
        public @NonNull String debugName() {
            return id.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ModelIDHolder modelIDHolder = (ModelIDHolder) o;
            return Objects.equals(id, modelIDHolder.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    public static Identifier textureRL(String s) {
        return EclipticSeasons.rl(s);
    }

    // public static TextureAtlasSprite getSprite(Identifier Identifier) {
    //     return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Identifier);
    // }

    public static BlockStateModel getSnowyModel(BlockState state, BlockState snowState, int flag, int offset) {
        ISnowyBlockState snowyBlockState = (ISnowyBlockState) state;
        // BakedModel snowModel = stateModelsCache.getOrDefault(state, null);
        boolean notSpecialLeaves = !(
                (MapChecker.leaveLike(flag))
                        && snowState == null);
        BlockStateModel snowModel = notSpecialLeaves ?
                snowyBlockState.getSnowyModel(loadVersion) : snowyBlockState.getSnowyModel2(loadVersion);
        if (snowModel == null) {
            Block onBlock = state.getBlock();
            boolean forceReplace = false;

            // **************************
            // es patch for client override
            List<SnowDefinition> snowDefinitions = ClientRef.snowClientDef.get(onBlock);
            if (snowDefinitions != null && !snowDefinitions.isEmpty()) {
                for (SnowDefinition snowDefinition : snowDefinitions) {
                    Identifier cinfo =
                            notSpecialLeaves ? snowDefinition.getInfo().getMid() :
                                    snowDefinition.getInfo().getMid2();
                    ModelResolver smr = extraSnowModelBuilds.get(cinfo);
                    if (smr != null) {
                        var mmrl = smr.tryFind(state);
                        if (mmrl != null) {
                            snowModel = models.standaloneModels().get(mmrl.modelIdentifier());
                            forceReplace = mmrl.replace();
                            flag = snowDefinition.getInfo().getFlag();
                            break;
                        }
                    }
                }
            }
            // **************************

            if (snowModel == null) {
                if (flag == MapChecker.FLAG_BLOCK) {
                    snowModel = snowOverlayBlock;
                } else if (flag == MapChecker.FLAG_LEAVES) {
                    snowModel = !CommonConfig.Snow.snowyTree.get() ?
                            snowOverlayLeaves :
                            notSpecialLeaves ? models.standaloneModels().get(snowy_leaves_top) : models.standaloneModels().get(snowy_leaves_attach);
                } else if (flag == MapChecker.FLAG_SLAB) {
                    snowModel = snowySlabBottom;
                } else if (flag == MapChecker.FLAG_STAIRS_TOP) {
                    snowModel = models.standaloneModels().get(stairs_top);
                } else if (flag == MapChecker.FLAG_STAIRS) {
                    if (snowState != null)
                        snowModel = models.blockStateModels().get(snowState);
                } else if (flag == MapChecker.FLAG_GRASS) {
                    if (onBlock == Blocks.SHORT_GRASS) {
                        snowModel = models.standaloneModels().get(snowy_grass);
                    } else if (onBlock == Blocks.FERN) {
                        snowModel = models.standaloneModels().get(snowy_fern);
                    } else snowModel = models.standaloneModels().get(snowy_grass);
                } else if (flag == MapChecker.FLAG_GRASS_LARGE) {
                    if (onBlock == Blocks.TALL_GRASS) {
                        snowModel = models.standaloneModels().get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                    } else if (onBlock == Blocks.LARGE_FERN) {
                        snowModel = models.standaloneModels().get(offset == 1 ? snowy_large_fern_bottom : snowy_large_fern_top);
                    } else
                        snowModel = models.standaloneModels().get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                } else if (flag == MapChecker.FLAG_VINE) {
                    if (snowState != null)
                        snowModel = models.blockStateModels().get(snowState);
                } else if (flag == MapChecker.FLAG_FARMLAND) {
                    snowModel = models.standaloneModels().get(snow_height2_top);
                } else if (flag == MapChecker.FLAG_CUSTOM) {
                    // snowModel = models.standaloneModels().get(snowy_custom);
                    snowModel = DerivedSnowyBlockStateModel.CUSTOM;
                } else if (flag == MapChecker.FLAG_CUSTOM_AO) {
                    snowModel = DerivedSnowyBlockStateModel.CUSTOM_AO;
                    // snowModel = models.standaloneModels().get(snowy_custom_ao);
                } else if (flag == MapChecker.FLAG_CUSTOM_JSON
                        | flag == MapChecker.FLAG_CUSTOM_JSON_PLANTS
                        || flag == MapChecker.FLAG_CUSTOM_JSON_VINE_LIKE
                        || flag == MapChecker.FLAG_CUSTOM_JSON_WITH_TOP
                        || flag == MapChecker.FLAG_CUSTOM_JSON_WITH_TOP_LEAVES) {
                    SnowDefinition.Info uncacheSnow = SnowChecker.getUncacheSnow(state);
                    Identifier cinfo =
                            notSpecialLeaves ? uncacheSnow.getMid() : uncacheSnow.getMid2();
                    ModelResolver smr = extraSnowModelBuilds.get(cinfo);
                    if (smr != null) {
                        var mmrl = smr.tryFind(state);
                        if (mmrl != null) {
                            snowModel = models.standaloneModels().get(mmrl.modelIdentifier());
                            forceReplace = mmrl.replace();
                        }
                    }
                }
            }


            // reset replace
            if (snowModel != null) {
                // stateModelsCache.putIfAbsent(snowState, snowModel);
                // SnowyBakedModelWrapper<?> bakedModel =
                //         snowModel instanceof SnowyBakedModelWrapper<?> ?
                //                 (SnowyBakedModelWrapper<?>) snowModel :
                //                 new SnowyBakedModelWrapper<>(snowModel);
                // bakedModel.setReplace(forceReplace);
                // replaceModelMap.put(snowModel, forceReplace);
                if (forceReplace)
                    snowModel = new ReplacingBlockStateModel(snowModel, forceReplace);
                // if (ISnowyReplaceModel.isInvalid(bakedModel)) {
                //     bakedModel.updateBlockType(flag);
                //     bakedModel.setLowLayer(!notSpecialLeaves);
                // }
                // if (notSpecialLeaves)
                //     snowyBlockState.setSnowyModel(bakedModel, loadVersion);
                // else snowyBlockState.setSnowyModel2(bakedModel, loadVersion);

                if (notSpecialLeaves)
                    snowyBlockState.setSnowyModel(snowModel, loadVersion);
                else snowyBlockState.setSnowyModel2(snowModel, loadVersion);
            }

            // if (snowModel != null) {
            //     snowyModelsCache.putIfAbsent(snowModel, flag);
            // }
        }

        return snowModel;
    }


    public static ChunkSectionLayer getRenderType(BlockState state) {
        // TODO：加一个选择
        // if (!Minecraft.useFancyGraphics()) return RenderType.solid();
        // RenderType chunkRenderType = ItemBlockRenderTypes.getChunkRenderType(state);
        // ChunkRenderTypeSet chunkRenderTypeSet = ItemBlockRenderTypes.getRenderLayers(state);
        // if (chunkRenderTypeSet.contains(RenderType.translucent())) return RenderType.translucent();
        // else if (chunkRenderTypeSet.contains(RenderType.cutout())) return RenderType.cutout();
        // return
        //         // ( CompatModule.isContinuityLoad()||CompatModule.isCTMLoad())
        //         //         && !CompatModule.isSodiumLoad() ?
        //         //          RenderType.cutout() :
        //         Minecraft.useFancyGraphics()&& state.getBlock() instanceof LeavesBlock?
        //                 RenderType.cutoutMipped(): RenderType.cutout();
        return ChunkSectionLayer.CUTOUT;
    }


    public static BlockStateModel getSnowLayerModel(int layers) {
        int clampedLayers = Mth.clamp(layers, 1, 8);
        BlockState snowState = Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, clampedLayers);
        return models.getBlockStateModel(snowState);
    }

    public static TextureAtlasSprite getSprite(Identifier id) {
        SpriteId apply = Sheets.BLOCKS_MAPPER.apply(id);
        AtlasManager atlasManager = Minecraft.getInstance().getAtlasManager();
        return atlasManager.get(apply);
    }

    public static final Map<Identifier, List<SeasonalTexture>> SEASONAL_TEXTURE_HASH_MAP = new HashMap<>();

    public static List<SeasonalTexture> remappingSeasonTextures(Identifier resourceLocation) {
        if (SEASONAL_TEXTURE_HASH_MAP.containsKey(resourceLocation)) {
            return SEASONAL_TEXTURE_HASH_MAP.get(resourceLocation);
        }
        return null;
    }

    public static boolean renderAsSnowInShader(BlockState state, BlockGetter blockAndTintGetter, BlockPos pos) {
        int blockType = MapChecker.getDefaultBlockTypeFlag(state);
        return switch (blockType) {
            case MapChecker.FLAG_BLOCK,
                 MapChecker.FLAG_SLAB,
                 MapChecker.FLAG_STAIRS,
                 MapChecker.FLAG_STAIRS_TOP,
                 MapChecker.FLAG_FARMLAND,
                 MapChecker.FLAG_CUSTOM,
                 MapChecker.FLAG_CUSTOM_AO,
                 MapChecker.FLAG_CUSTOM_JSON,
                 MapChecker.FLAG_CUSTOM_JSON_WITH_TOP -> true;
            default -> false;
        };
    }
}
