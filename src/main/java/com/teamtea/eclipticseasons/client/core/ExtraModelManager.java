package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.client.model.ModelResolver;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyBlockState;
import com.teamtea.eclipticseasons.client.model.block.AutoSnowyBlockModel;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.compat.ctm.CTMSpriteChecker;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ExtraModelManager {
    public static int loadVersion = 0;
    public static ModelBakery.BakingResult models;

    public static BlockPos.MutableBlockPos posToMutable(BlockPos pos) {
        return new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isModelReplaceable(int flag) {
        return flag == MapChecker.FLAG_GRASS
                || flag == MapChecker.FLAG_GRASS_LARGE;
    }

    public static boolean isSpecialCTMSprite(TextureAtlasSprite textureAtlasSprite) {
        return textureAtlasSprite instanceof CTMSpriteChecker checker && checker.isCTMSprite();
    }

    // public static final Map<Identifier, ESModelLoadedJson> extraSnowModels = HashMap.newHashMap(1024);

    public static final Map<Identifier, ModelResolver> extraSnowModelBuilds = HashMap.newHashMap(1024);


    public static void clearForRebaked(ModelBakery.BakingResult modelRegistry) {
        AutoSnowyBlockModel.PART_MAP.clear();

        ExtraModelManager.models = modelRegistry;
        loadVersion++;
        // initCTMDetected();
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
        return new StandaloneModelKey<>(() -> EclipticSeasons.rl(s).toString());
    }

    public static StandaloneModelKey<BlockStateModel> extra_mrl(Identifier Identifier, String v) {
        return new StandaloneModelKey<>(() -> Identifier.withPrefix("extra/" + (v.isEmpty() ? "" : v + "/")).toString());
    }

    public static Identifier textureRL(String s) {
        return EclipticSeasons.rl( s);
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
                if (flag == MapChecker.FLAG_BLOCK || state.is(Blocks.GRASS_BLOCK)) {
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
                    snowModel = AutoSnowyBlockModel.CUSTOM;
                } else if (flag == MapChecker.FLAG_CUSTOM_AO) {
                    snowModel = AutoSnowyBlockModel.CUSTOM_AO;
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


            if (snowModel != null) {
                // stateModelsCache.putIfAbsent(snowState, snowModel);
                // SnowyBakedModelWrapper<?> bakedModel =
                //         snowModel instanceof SnowyBakedModelWrapper<?> ?
                //                 (SnowyBakedModelWrapper<?>) snowModel :
                //                 new SnowyBakedModelWrapper<>(snowModel);
                // bakedModel.setReplace(forceReplace);
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


    public static boolean isSpecialCTMBlock(BlockState state) {
        return false;
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
}
