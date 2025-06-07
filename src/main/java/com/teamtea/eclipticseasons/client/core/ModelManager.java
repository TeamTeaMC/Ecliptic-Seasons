package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonBlockDefinition;
import com.teamtea.eclipticseasons.api.data.client.model.ModelResolver;
import com.teamtea.eclipticseasons.api.data.client.model.ModelTester;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.misc.client.IMapSliceProvider;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyBlockState;
import com.teamtea.eclipticseasons.client.model.*;
import com.teamtea.eclipticseasons.client.model.bakequad.*;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.ctm.CtmLoader;
import com.teamtea.eclipticseasons.compat.ctm.CtmProperties;
import com.teamtea.eclipticseasons.config.ClientConfig;

import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// https://github.com/DoubleNegation/CompactOres/blob/1.18/src/main/java/doublenegation/mods/compactores/CompactOresResourcePack.java#L164

// 未来可以基于RepositorySource实现动态纹理生成（看情况，因为目前不需要，对内存消耗比较大）
public class ModelManager {

    public static List<BakedQuad> EMPTY_BAKED_QUAD_LIST = List.of();
    public static int loadVersion = 0;

    public static Map<ModelResourceLocation, BakedModel> models;

    public static ModelResourceLocation snowOverlayLeaves = new ModelResourceLocation(BlockRegistry.snowyLeaves.getId(), "");
    public static ModelResourceLocation snowySlabBottom = new ModelResourceLocation(BlockRegistry.snowySlab.getId(), "type=bottom,waterlogged=false");
    public static ModelResourceLocation snowOverlayBlock = new ModelResourceLocation(BlockRegistry.snowyBlock.getId(), "");


    public static ModelResourceLocation snowy_custom = mrl("block/snowy_custom");
    public static ModelResourceLocation stairs_top = mrl("block/stairs_top");

    public static ModelResourceLocation snowy_leaves_attach = mrl("block/snowy_leaves_attach");
    public static ModelResourceLocation snowy_leaves_top = mrl("block/snowy_leaves_top");

    public static ModelResourceLocation snowy_fern = mrl("block/snowy_fern");
    public static ModelResourceLocation snowy_grass = mrl("block/snowy_grass");
    public static ModelResourceLocation snowy_large_fern_bottom = mrl("block/snowy_large_fern_bottom");
    public static ModelResourceLocation snowy_large_fern_top = mrl("block/snowy_large_fern_top");
    public static ModelResourceLocation snowy_tall_grass_bottom = mrl("block/snowy_tall_grass_bottom");
    public static ModelResourceLocation snowy_tall_grass_top = mrl("block/snowy_tall_grass_top");

    public static ModelResourceLocation overlay_2 = mrl("block/overlay_2");
    public static ModelResourceLocation snow_height2 = mrl("block/snow_height2");
    public static ModelResourceLocation snow_height2_top = mrl("block/snow_height2_top");
    public static ModelResourceLocation grass_flower = mrl("block/grass_flower");
    public static List<ModelResourceLocation> flower_on_grass = Stream.of(1, 2, 3, 4, 5, 6).map(i -> mrl("block/flower_%s".formatted(i))).collect(Collectors.toCollection(ArrayList::new));
    public static List<ModelResourceLocation> snow_edge_overlays = IntStream.rangeClosed(0, 18).mapToObj(i -> mrl("block/snow_edge/snow_edge_overlay_%s".formatted(i))).collect(Collectors.toCollection(ArrayList::new));
    public static List<ModelResourceLocation> fourleaf_clovers = IntStream.rangeClosed(0, 6).mapToObj(i -> mrl("block/fourleaf_clover/fourleaf_clover_%s".formatted(i))).collect(Collectors.toCollection(ArrayList::new));

    // public static ModelResourceLocation fourleaf_clover = mrl("block/fourleaf_clover");

    public static ResourceLocation snow = ResourceLocation.withDefaultNamespace("block/snow");
    public static ResourceLocation snow_overlay_half_left = textureRL("snow_overlay_half_left");
    public static ResourceLocation snow_overlay_half_right = textureRL("snow_overlay_half_right");
    public static ResourceLocation snow_overlay = textureRL("snow_overlay");
    public static ResourceLocation snow_overlay_leaves = textureRL("snow_overlay_leaves");
    public static ResourceLocation snow_overlay_tiny = textureRL("snow_overlay_tiny");
    public static ResourceLocation snow_spot_overlay_leaves = textureRL("snow_spot_overlay_leaves");

    public static ModelResourceLocation mrl(String s) {
        return ModelResourceLocation.standalone(EclipticSeasons.rl(s));
    }

    public static ModelResourceLocation snow_mrl(ResourceLocation resourceLocation, String v) {
        return ModelResourceLocation.standalone(resourceLocation.withPrefix("extra/" + (v.isEmpty() ? "" : v + "/")));
    }

    public static ResourceLocation textureRL(String s) {
        return EclipticSeasons.rl("block/" + s);
    }

    public static TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(resourceLocation);
    }


    public static boolean shouldCutoutMipped(BlockState state) {
        if (Minecraft.getInstance().level != null) {
            var onBlock = state.getBlock();
            if (!(onBlock instanceof FenceBlock) && !(onBlock instanceof HalfTransparentBlock) && !(onBlock instanceof IronBarsBlock)) {
                // use EmptyBlockGetter.INSTANCE instead of null level
                if (onBlock instanceof SlabBlock || onBlock instanceof FarmBlock || onBlock instanceof DirtPathBlock || onBlock instanceof StairBlock
                        || state.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    // TODO：这里看能不能直接给model上标记，map还是耗时
    // public static Map<BakedModel, Integer> snowyModelsCache = new IdentityHashMap<>();
    // public static Map<BlockState, BakedModel> stateModelsCache = new IdentityHashMap<>();

    public static Map<Block, CtmProperties> ctmStates = new IdentityHashMap<>();
    public static Map<ResourceLocation, Void> ctmTiles = new HashMap<>();

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

    public static BakedModel getSnowyModel(BlockState state, BlockState snowState, int flag, int offset) {
        ISnowyBlockState snowyBlockState = (ISnowyBlockState) state;
        // BakedModel snowModel = stateModelsCache.getOrDefault(state, null);
        boolean notSpecialLeaves = !(
                (leaveLike(flag))
                        && snowState == null);
        BakedModel snowModel = notSpecialLeaves ?
                snowyBlockState.getSnowyModel(loadVersion) : snowyBlockState.getSnowyModel2(loadVersion);
        if (snowModel == null) {
            Block onBlock = state.getBlock();
            boolean forceReplace = false;

            // **************************
            // es patch for client override
            List<SnowDefinition> snowDefinitions = ClientRef.snowClientDef.get(onBlock);
            if (snowDefinitions != null && !snowDefinitions.isEmpty()) {
                for (SnowDefinition snowDefinition : snowDefinitions) {
                    ResourceLocation cinfo =
                            notSpecialLeaves ? snowDefinition.getInfo().getMid() :
                                    snowDefinition.getInfo().getMid2();
                    ModelResolver smr = extraSnowModelBuilds.get(cinfo);
                    if (smr != null) {
                        var mmrl = smr.tryFind(state);
                        if (mmrl != null) {
                            snowModel = models.get(mmrl.modelResourceLocation());
                            forceReplace = mmrl.replace();
                            break;
                        }
                    }
                }
            }
            // **************************

            if (snowModel == null) {
                if (flag == MapChecker.FLAG_BLOCK) {
                    snowModel = models.get(snowOverlayBlock);
                } else if (flag == MapChecker.FLAG_LEAVES) {
                    snowModel = !ClientConfig.Renderer.snowyTree.get() ?
                            models.get(snowOverlayLeaves) :
                            notSpecialLeaves ? models.get(snowy_leaves_top) : models.get(snowy_leaves_attach);
                } else if (flag == MapChecker.FLAG_SLAB) {
                    snowModel = models.get(snowySlabBottom);
                } else if (flag == MapChecker.FLAG_STAIRS_TOP) {
                    snowModel = models.get(stairs_top);
                } else if (flag == MapChecker.FLAG_STAIRS) {
                    if (snowState != null) snowModel = models.get(BlockModelShaper.stateToModelLocation(snowState));
                } else if (flag == MapChecker.FLAG_GRASS) {
                    if (onBlock == Blocks.SHORT_GRASS) {
                        snowModel = models.get(snowy_grass);
                    } else if (onBlock == Blocks.FERN) {
                        snowModel = models.get(snowy_fern);
                    } else snowModel = models.get(snowy_grass);
                } else if (flag == MapChecker.FLAG_GRASS_LARGE) {
                    if (onBlock == Blocks.TALL_GRASS) {
                        snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                    } else if (onBlock == Blocks.LARGE_FERN) {
                        snowModel = models.get(offset == 1 ? snowy_large_fern_bottom : snowy_large_fern_top);
                    } else snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                } else if (flag == MapChecker.FLAG_VINE) {
                    if (snowState != null) snowModel = models.get(BlockModelShaper.stateToModelLocation(snowState));
                } else if (flag == MapChecker.FLAG_FARMLAND) {
                    snowModel = models.get(snow_height2_top);
                    // snowModel = snowOverlayBlock.resolve().get();
                } else if (flag == MapChecker.FLAG_CUSTOM) {
                    snowModel = models.get(snowy_custom);
                } else if (flag == MapChecker.FLAG_CUSTOM_JSON
                        | flag == MapChecker.FLAG_CUSTOM_JSON_PLANTS
                        || flag == MapChecker.FLAG_CUSTOM_JSON_VINE_LIKE
                        || flag == MapChecker.FLAG_CUSTOM_JSON_WITH_TOP
                        || flag == MapChecker.FLAG_CUSTOM_JSON_WITH_TOP_LEAVES) {
                    SnowDefinition.Info uncacheSnow = SnowChecker.getUncacheSnow(state);
                    ResourceLocation cinfo =
                            notSpecialLeaves ? uncacheSnow.getMid() : uncacheSnow.getMid2();
                    ModelResolver smr = extraSnowModelBuilds.get(cinfo);
                    if (smr != null) {
                        var mmrl = smr.tryFind(state);
                        if (mmrl != null) {
                            snowModel = models.get(mmrl.modelResourceLocation());
                            forceReplace = mmrl.replace();
                        }
                    }
                }
            }


            if (snowModel != null) {
                // stateModelsCache.putIfAbsent(snowState, snowModel);
                SnowyBakedModelWrapper<?> bakedModel =
                        snowModel instanceof SnowyBakedModelWrapper<?> ?
                                (SnowyBakedModelWrapper<?>) snowModel :
                                new SnowyBakedModelWrapper<>(snowModel);
                bakedModel.setReplace(forceReplace);
                if (ISnowyReplaceModel.isInvalid(bakedModel)) {
                    bakedModel.updateBlockType(flag);
                    bakedModel.setLowLayer(!notSpecialLeaves);
                }
                if (notSpecialLeaves)
                    snowyBlockState.setSnowyModel(bakedModel, loadVersion);
                else snowyBlockState.setSnowyModel2(bakedModel, loadVersion);
            }

            // if (snowModel != null) {
            //     snowyModelsCache.putIfAbsent(snowModel, flag);
            // }
        }

        return snowModel;
    }

    private final static List<BakedQuad> EMPTY = List.of();

    // TODO：关于覆盖cutout面的问题，似乎可以给纹理加一个半透明像素，然后用cutout渲染就能正常覆盖了
    public static List<BakedQuad> cancelTop(@Nullable BakedModel bakedModel, @Nonnull BlockAndTintGetter blockAndTintGetter, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nullable Direction direction, @Nonnull RandomSource random, long seed, @Nonnull List<BakedQuad> original, @Nullable List<BakedQuad> cache, @Nullable BakedModel snowModel) {
        if (FMLEnvironment.production)
            if (bakedModel != null
                    && !original.isEmpty()
                    && (direction == Direction.UP || direction == null)
                    && !(IESReplaceModel.isInvalid(bakedModel))
                    && blockAndTintGetter instanceof IMapSliceProvider
            ) {
                random.setSeed(seed);
                // blockAndTintGetter 现在优化以后可以用来处理了
                if (snowModel == null)
                    snowModel = ModelManager.findModel(blockAndTintGetter, pos, state, random, seed, posToMutable(pos));

                if (snowModel instanceof SnowyBakedModelWrapper) {
                    int blockType = MapChecker.getBlockTypeFlag(blockAndTintGetter, pos, state);
                    if (blockType == MapChecker.FLAG_CUSTOM)
                        return original;
                    if (blockType == MapChecker.FLAG_LEAVES || blockType == MapChecker.FLAG_VINE)
                        return original;
                    if (direction == Direction.UP) {
                        if (blockType == MapChecker.FLAG_BLOCK) return EMPTY;
                    }


                    if (original.size() == 1) {
                        if (original.getFirst().getDirection() == Direction.UP) return EMPTY;
                    } else {
                        original = new ArrayList<>(original);
                        for (int i = 0; i < original.size(); i++) {
                            BakedQuad bakedQuad = original.get(i);
                            if (bakedQuad.getDirection() == Direction.UP) {
                                original.remove(i);
                                i--;
                            }
                        }
                    }

                }
            }


        if (bakedModel instanceof SnowyBakedModelWrapper) {


            int blockType = MapChecker.getBlockTypeFlag(blockAndTintGetter, pos, state);
            if (blockType == MapChecker.FLAG_CUSTOM) {
                original = new ArrayList<>();
            }


            // if (!FMLEnvironment.production)
            // if (blockType == MapChecker.FLAG_BLOCK) {
            //     {
            //         if (original.size() == 1) {
            //             if (blockAndTintGetter instanceof IMapSlice cmapSlice) {
            //                 byte snowDepth = WeatherManager.getBiomeList(ClientCon.getUseLevel()).get(cmapSlice.getSurfaceFaceBiomeId(pos)).snowDepth;
            //                 if (snowDepth < 51) {
            //
            //                     if (direction == Direction.UP) {
            //                         original = List.of(new BakedQuadRetextured(original.get(0),
            //                                 getSprite(textureRL(
            //                                         snowDepth < 26 ? "snow_small" : "snow_middle")
            //                                 )));
            //
            //                     } else {
            //                         original = EMPTY;
            //                     }
            //                 }
            //             }
            //         }
            //
            //     }
            // }

            boolean yuushyaBlock = false;
            // TODO:完善CTM支持
            yuushyaBlock |= cache != null && !cache.isEmpty();
            yuushyaBlock &= blockType != MapChecker.FLAG_LEAVES && blockType != MapChecker.FLAG_VINE;
            boolean sodiumStairs = ((blockType == MapChecker.FLAG_STAIRS || blockType == MapChecker.FLAG_STAIRS_TOP)
                    && CompatModule.isSodium() && state.getBlock() instanceof StairBlock);
            if (blockType == MapChecker.FLAG_CUSTOM
                    || yuushyaBlock
                    || sodiumStairs
                // && state.toString().contains("stairs_a_cherry_blindwall")
                // &&(state.hasProperty(StairBlock.SHAPE)&& state.getValue(StairBlock.SHAPE) == StairsShape.OUTER_RIGHT)
            ) {

                // 也许我们不需要这个，但是这样比较合适
                // if (yuushyaBlock
                //         && blockType == MapChecker.FLAG_STAIRS_TOP
                //         && state.getBlock() instanceof StairBlock)
                //     return original;
                // continuity裁剪面不一样
                if (yuushyaBlock
                        && blockType == MapChecker.FLAG_STAIRS
                        && state.getBlock() instanceof StairBlock
                ) {
                    if (direction != Direction.UP) {
                        // return original;
                    } else {
                        if (cache != null && !cache.isEmpty()) {
                            cache = new ArrayList<>(cache);
                            cache.removeIf(bakedQuad -> bakedQuad.getDirection() != Direction.UP);
                        }
                    }
                }
                // if(blockType==MapChecker.FLAG_STAIRS)
                {
                    if (blockType == MapChecker.FLAG_CUSTOM
                            || sodiumStairs
                            || (yuushyaBlock && (blockType == MapChecker.FLAG_STAIRS
                            || blockType == MapChecker.FLAG_STAIRS_TOP && direction != Direction.UP
                            || blockType == MapChecker.FLAG_SLAB
                            || blockType == MapChecker.FLAG_BLOCK && direction != Direction.UP))
                            || direction != null && direction.ordinal() > 1) {
                        ArrayList<BakedQuad> quadsCTM = null;

                        if (cache == null || cache.isEmpty()) {
                            BakedModel bakedModelCTM = models.get(BlockModelShaper.stateToModelLocation(state));
                            if (bakedModelCTM != null) {
                                ModelData modelDataCTM = bakedModelCTM.getModelData(blockAndTintGetter, pos, state, ModelData.EMPTY);
                                random.setSeed(seed);
                                ChunkRenderTypeSet renderTypes = bakedModelCTM.getRenderTypes(state, random, modelDataCTM);
                                quadsCTM = new ArrayList<>();
                                for (RenderType renderType : renderTypes.asList()) {
                                    random.setSeed(seed);
                                    quadsCTM.addAll(bakedModelCTM.getQuads(state, direction, random, modelDataCTM, renderType));
                                }
                            }
                        } else {
                            if (direction != null) {
                                // if(direction!=Direction.UP)
                                // quadsCTM = new ArrayList<>(EMPTY);
                                // else {
                                //     quadsCTM = new ArrayList<>(cache);
                                //     for (int i = 0; i < quadsCTM.size(); i++) {
                                //         BakedQuad bakedQuad = quadsCTM.get(i);
                                //         BakedQuadRetextured bakedQuadRetextured = new BakedQuadRetextured(bakedQuad, getSprite(snow_overlay));
                                //         quadsCTM.set(i, bakedQuadRetextured);
                                //     }
                                // }
                                quadsCTM = new ArrayList<>(EMPTY);
                            } else {
                                quadsCTM = new ArrayList<>(cache);
                                // for (int i = 0; i < quadsCTM.size(); i++) {
                                //     BakedQuad bakedQuad = quadsCTM.get(i);
                                //     BakedQuadRetextured bakedQuadRetextured = new BakedQuadRetextured(bakedQuad, getSprite(snow_overlay));
                                //     quadsCTM.set(i, bakedQuadRetextured);
                                // }
                                // return quadsCTM;
                            }
                        }

                        if (quadsCTM != null) {

                            boolean tooTiny = false;
                            tooTiny |= state.getBlock() instanceof FenceBlock;
                            tooTiny |= state.getBlock() instanceof FenceGateBlock;
                            tooTiny |= state.getBlock() instanceof IronBarsBlock;
                            tooTiny |= state.getBlock() instanceof StairBlock;
                            if (!tooTiny)
                                quadsCTM = QuadFixer.fixQuadCTM(quadsCTM);


                            // if (quadsCTM.isEmpty())
                            //     return original;
                            TextureAtlasSprite snow_overlay_sprite = getSprite(snow_overlay);
                            TextureAtlasSprite snow_overlay_tiny_sprite = getSprite(snow_overlay_tiny);
                            TextureAtlasSprite snow_sprite = getSprite(snow);
                            float offset = 0.5f;
                            boolean isSlabDown = blockType == MapChecker.FLAG_SLAB;
                            original = new ArrayList<>(quadsCTM.size());
                            // TODO:按高度清理连接面
                            for (BakedQuad bakedQuad : quadsCTM) {
                                Direction bakedQuadDirection = bakedQuad.getDirection();
                                if (bakedQuadDirection != Direction.DOWN) {
                                    TextureAtlasSprite spriteUse = snow_overlay_sprite;
                                    // if (blockType == MapChecker.FLAG_CUSTOM)
                                    {
                                        if (bakedQuadDirection != Direction.UP) {
                                            isSlabDown = true;
                                            float maxY = QuadFixer.getMaxY(bakedQuad);
                                            offset = 1 - maxY;
                                            if (offset < 0.00001f) {
                                                offset = 0;
                                                isSlabDown = false;
                                            }
                                            // if(maxY<0.75f)continue;
                                        }
                                    }

                                    if (bakedQuadDirection == Direction.UP) spriteUse = snow_sprite;
                                    else {
                                        if (state.getBlock() instanceof StairBlock
                                                && state.getValue(StairBlock.SHAPE) instanceof StairsShape stairsShape
                                                && state.getValue(StairBlock.HALF) instanceof Half half
                                                && state.getValue(StairBlock.FACING) instanceof Direction stairFaceDirection
                                        ) {
                                            spriteUse = QuadFixer.getMaxY(bakedQuad) - QuadFixer.getMinY(bakedQuad) > 0.4002f ? snow_overlay_sprite : snow_overlay_tiny_sprite;
                                            if (half == Half.TOP) {
                                                if (QuadFixer.getMaxY(bakedQuad) < 0.500001f) {
                                                    continue;
                                                }
                                            } else if (stairsShape == StairsShape.STRAIGHT) {
                                                if (bakedQuadDirection == stairFaceDirection) {
                                                    if (QuadFixer.getMaxY(bakedQuad) < 0.500001f) {
                                                        continue;
                                                    }
                                                } else if (QuadFixer.getMaxY(bakedQuad) < 0.500001f) {
                                                    if (bakedQuadDirection == stairFaceDirection.getClockWise()) {
                                                        spriteUse = getSprite(snow_overlay_half_left);
                                                    } else if (bakedQuadDirection == stairFaceDirection.getCounterClockWise()) {
                                                        spriteUse = getSprite(snow_overlay_half_right);
                                                    }
                                                }
                                            } else if (stairsShape == StairsShape.INNER_LEFT) {
                                                if (bakedQuadDirection == stairFaceDirection
                                                        || bakedQuadDirection == stairFaceDirection.getCounterClockWise()) {
                                                    if (QuadFixer.getMaxY(bakedQuad) < 0.500001f) {
                                                        continue;
                                                    }
                                                } else if (QuadFixer.getMaxY(bakedQuad) < 0.500001f) {
                                                    if (bakedQuadDirection == stairFaceDirection.getClockWise()) {
                                                        spriteUse = getSprite(snow_overlay_half_left);
                                                    } else if (bakedQuadDirection == stairFaceDirection.getOpposite()) {
                                                        spriteUse = getSprite(snow_overlay_half_right);
                                                    }
                                                }
                                            } else if (stairsShape == StairsShape.INNER_RIGHT) {
                                                if (bakedQuadDirection == stairFaceDirection
                                                        || bakedQuadDirection == stairFaceDirection.getClockWise()) {
                                                    if (QuadFixer.getMaxY(bakedQuad) < 0.500001f) {
                                                        continue;
                                                    }
                                                } else if (QuadFixer.getMaxY(bakedQuad) < 0.500001f) {
                                                    if (bakedQuadDirection == stairFaceDirection.getCounterClockWise()) {
                                                        spriteUse = getSprite(snow_overlay_half_right);
                                                    } else if (bakedQuadDirection == stairFaceDirection.getOpposite()) {
                                                        spriteUse = getSprite(snow_overlay_half_left);
                                                    }
                                                }
                                            } else if (stairsShape == StairsShape.OUTER_LEFT) {
                                                if (QuadFixer.getMaxY(bakedQuad) < 0.500001f) {
                                                    if (bakedQuadDirection == stairFaceDirection.getCounterClockWise()) {
                                                        spriteUse = getSprite(snow_overlay_half_right);
                                                    } else if (bakedQuadDirection == stairFaceDirection) {
                                                        spriteUse = getSprite(snow_overlay_half_left);
                                                    }
                                                }
                                            } else if (stairsShape == StairsShape.OUTER_RIGHT) {
                                                if (QuadFixer.getMaxY(bakedQuad) < 0.500001f) {
                                                    if (bakedQuadDirection == stairFaceDirection.getClockWise()) {
                                                        spriteUse = getSprite(snow_overlay_half_left);
                                                    } else if (bakedQuadDirection == stairFaceDirection) {
                                                        spriteUse = getSprite(snow_overlay_half_right);
                                                    }
                                                }
                                            }
                                        } else if (tooTiny)
                                            spriteUse = snow_overlay_tiny_sprite;
                                        else
                                            spriteUse = QuadFixer.getMaxY(bakedQuad) - QuadFixer.getMinY(bakedQuad) > 0.4002f ? snow_overlay_sprite : snow_overlay_tiny_sprite;

                                    }

                                    BakedQuad retexturedBakedQuad;
                                    if (RectangularPrismChecker.isRectangularPrism(bakedQuad)) {
                                        retexturedBakedQuad = new BakedQuadRetexturedAndReUV(bakedQuad, spriteUse, isSlabDown, offset);
                                    } else {
                                        retexturedBakedQuad = new BakedQuadRetextured(bakedQuad, spriteUse);
                                    }


                                    original.add(retexturedBakedQuad);
                                }
                            }

                        }
                    }
                }
            }
        }

        if (!original.isEmpty()
                && direction != null
                && MapChecker.getBlockTypeFlag(blockAndTintGetter, pos, state) == MapChecker.FLAG_LEAVES) {
            // TODO：秋天按几率萧瑟
            int index = Math.abs(((int) (seed + pos.getX())) % 8);
            if ((index) > 0
                    && blockAndTintGetter.getBlockState(pos.relative(direction)).is(state.getBlock())) {
                original = new ArrayList<>();
            }
        }
        return original;
    }

    private static final List<Direction> HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toList();

    public static BlockPos.MutableBlockPos posToMutable(BlockPos pos) {
        return new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    // it meanes the block would have surface layer and below
    public static boolean leaveLike(int flag) {
        return flag == MapChecker.FLAG_LEAVES
                || flag == MapChecker.FLAG_CUSTOM_JSON_WITH_TOP
                || flag == MapChecker.FLAG_CUSTOM_JSON_WITH_TOP_LEAVES;
    }

    public static boolean vineLike(int flag) {
        return flag == MapChecker.FLAG_VINE || flag == MapChecker.FLAG_CUSTOM_JSON_VINE_LIKE;
    }

    // todo other snow passible
    public static boolean solidBlockLike(int flag) {
        return flag == MapChecker.FLAG_BLOCK
                || flag == MapChecker.FLAG_CUSTOM_JSON;
    }

    public static BakedModel findModel(BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, RandomSource random, long seed, @Nullable BlockPos.MutableBlockPos checkPos) {
        // if (!state.is(Blocks.LILY_PAD))
        //     return null;
        Level level = Minecraft.getInstance().level;
        if (level == null) return null;
        int flag = MapChecker.getBlockTypeFlag(blockAndTintGetter, pos, state);
        List<SeasonBlockDefinition> seasonDefCache = null;
        List<SnowDefinition> snowDefClientOverlay = null;
        var onBlock = state.getBlock();

        if (flag == 0) {
            seasonDefCache = ClientRef.seasonDef.get(onBlock);
            snowDefClientOverlay = ClientRef.snowClientDef.get(onBlock);
            if (snowDefClientOverlay != null
                    && snowDefClientOverlay.isEmpty()) snowDefClientOverlay = null;
            if (seasonDefCache == null && snowDefClientOverlay == null)
                return null;
        }

        boolean leaveLike = leaveLike(flag);
        boolean leavesOrVine = leaveLike || vineLike(flag);

        BakedModel replace = null;
        IMapSlice mapSlice = null;
        if (blockAndTintGetter instanceof IMapSlice cmapSlice) {
            mapSlice = cmapSlice;
            if (!leavesOrVine) {
                int cut = mapSlice.getBlockHeight(pos) - pos.getY();

                if (cut > 1 || cut < -3)
                    if (!ClientConfig.Renderer.snowUnderTree.get())
                        return null;
            }
        }


        // int flag = MapChecker.getBlockType(state, blockAndTintGetter, pos);

        int offset = snowDefClientOverlay == null ?
                MapChecker.getSnowOffset(state, flag) : snowDefClientOverlay.get(0).getInfo().getOffset();


        boolean isLight = false;
        if (checkPos == null) checkPos = posToMutable(pos);
        else checkPos.set(pos.getX(), pos.getY(), pos.getZ());

        if (ClientConfig.Renderer.useVanillaCheck.get()) {
            checkPos.setY(pos.getY() + 1);
            isLight = blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >= 15;
        } else {
            // ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, pos);

            int cacheHeight = mapSlice != null ?
                    mapSlice.getBlockHeight(pos)
                    : MapChecker.getHeightOrUpdate(level, pos, false);

            if (ClientConfig.Renderer.snowUnderFence.get()) {
                if (solidBlockLike(flag) && pos.getY() == cacheHeight - 1) {
                    checkPos.setY(pos.getY() + 1);
                    BlockState aboveState = blockAndTintGetter.getBlockState(checkPos);
                    if (
                        // MapChecker.getBlockType(blockAndTintGetter.getBlockState(pos.above()), blockAndTintGetter, pos.above())
                            MapChecker.getBlockTypeFlag(blockAndTintGetter, checkPos, aboveState)
                                    == MapChecker.FLAG_CUSTOM
                                    && !(aboveState.getBlock() instanceof SlabBlock)
                                    && !(aboveState.getBlock() instanceof StairBlock)) {
                        cacheHeight--;
                    } else {
                        for (int i = 0; i < HORIZONTAL_DIRECTIONS.size(); i++) {
                            Direction direction = HORIZONTAL_DIRECTIONS.get(i);
                            checkPos.setX(pos.getX() + direction.getStepX());
                            checkPos.setZ(pos.getZ() + direction.getStepZ());
                            int neighbourHeight = mapSlice != null ?
                                    mapSlice.getBlockHeight(checkPos) :
                                    MapChecker.getHeightOrUpdate(level, checkPos, false);
                            if (neighbourHeight == pos.getY()) {
                                checkPos.setY(pos.getY() + 1);
                                BlockState neighbourState = blockAndTintGetter.getBlockState(checkPos);
                                // 函数调用也是耗时
                                int blockTypeFlag = MapChecker.getBlockTypeFlag(blockAndTintGetter, checkPos, neighbourState);
                                if (blockTypeFlag == MapChecker.FLAG_CUSTOM
                                        && !(neighbourState.getBlock() instanceof SlabBlock)
                                        && !(neighbourState.getBlock() instanceof StairBlock)
                                ) {
                                    cacheHeight = neighbourHeight;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            isLight = cacheHeight <= pos.getY() - offset;
        }

        boolean specialLeaves = false;
        if (!isLight && leavesOrVine && ClientConfig.Renderer.snowyTree.get()) {
            if (blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) >= 9) {
                int y_real = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
                if (y_real <= pos.getY()
                        || blockAndTintGetter.getBlockState(new BlockPos(pos.getX(), y_real, pos.getZ())).getShadeBrightness(blockAndTintGetter, pos) < 0.5f)
                // if (level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) >= pos.getY() + 1)
                {
                    isLight = true;
                    if (leaveLike) {
                        specialLeaves = true;
                    }
                }
            }
        }

        if (!isLight && ClientConfig.Renderer.snowUnderTree.get()) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) >= 9) {
                int y_real = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
                // note 注意这里是section
                try {
                    if (level.getBlockState(new BlockPos(pos.getX(), y_real, pos.getZ()))
                            .getShadeBrightness(blockAndTintGetter, pos) < 0.5f) {
                        isLight = true;
                        if (leaveLike) {
                            if (ClientConfig.Renderer.snowyTree.get())
                                specialLeaves = true;
                            else isLight = false;
                        }
                    }
                } catch (Exception e) {
                    EclipticSeasons.logger(e);
                }
            }
        }

        if (isLight) {
            // TODO 尝试生成预先的概率图，随后卷积概率
            boolean isSnowy = false;
            if (CommonConfig.Season.snowyWinter.get()
                    && onBlock != Blocks.SNOW_BLOCK
                    && ((mapSlice != null && MapChecker.shouldSnowAt(level, pos, mapSlice.getSurfaceFaceBiomeId(pos), state, random, seed))
                    || (mapSlice == null && MapChecker.shouldSnowAt(level, pos, state, random, seed))
                    || (mapSlice != null && mapSlice.getSnowyStatus(pos) == SnowyRemover.SnowyFlag.SNOWY_ALWAYS.ordinal())
            )
            ) {
                isSnowy = true;

                if (CommonConfig.Season.notSnowyNearGlowingBlock.get()) {
                    if (mapSlice != null
                            && mapSlice.getSnowyStatus(pos) == SnowyRemover.SNOWY) {
                        checkPos.set(pos.getX(), pos.getY() + 1 - offset, pos.getZ());
                        if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >=
                                CommonConfig.Season.notSnowyNearGlowingBlockLevel.getAsInt()) {
                            isSnowy = false;
                        }
                    }

                    if (mapSlice == null) {
                        checkPos.set(pos.getX(), pos.getY() + 1 - offset, pos.getZ());
                        if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >=
                                CommonConfig.Season.notSnowyNearGlowingBlockLevel.getAsInt()) {
                            isSnowy = false;
                        }
                    }
                }

                if (isSnowy && ClientConfig.Renderer.snowTransitionBlend.get()) {
                    isSnowy = isSnowy(pos, seed, checkPos, mapSlice, level, blockAndTintGetter);
                }

                if (isSnowy) {


                    // boolean isFlowerAbove = false;
                    // if ((flag == MapChecker.FLAG_BLOCK) && ClientConfig.Renderer.betterSnow.get()) {
                    //     var bl = blockAndTintGetter.getBlockState(pos.above()).getBlock();
                    //     isFlowerAbove = bl instanceof FlowerBlock
                    //             || bl instanceof PinkPetalsBlock
                    //             || bl instanceof DoublePlantBlock
                    //             || bl instanceof SaplingBlock;
                    //
                    //     if (!isFlowerAbove) {
                    //         isFlowerAbove = random.nextInt(12) > 0;
                    //         // isFlowerAbove=true;
                    //     }
                    // }
                    {
                        BlockState snowState = null;
                        if (flag == MapChecker.FLAG_STAIRS) {
                            snowState = BlockRegistry.snowyStairs.get().defaultBlockState()
                                    .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                                    .setValue(StairBlock.HALF, state.getValue(StairBlock.HALF))
                                    .setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
                        } else if (flag == MapChecker.FLAG_VINE) {
                            snowState = BlockRegistry.snowyVine.get().defaultBlockState()
                                    .setValue(VineBlock.EAST, state.getValue(VineBlock.EAST))
                                    .setValue(VineBlock.WEST, state.getValue(VineBlock.WEST))
                                    .setValue(VineBlock.SOUTH, state.getValue(VineBlock.SOUTH))
                                    .setValue(VineBlock.NORTH, state.getValue(VineBlock.NORTH))
                                    .setValue(VineBlock.UP, state.getValue(VineBlock.UP))
                            ;
                        } else if (leaveLike && !specialLeaves) {
                            snowState = BlockRegistry.snowyLeaves.get().defaultBlockState();
                        }
                        BakedModel snowModel = getSnowyModel(state, snowState, flag, offset);

                        if (snowModel != null) {
                            replace = snowModel;
                        }
                    }
                }
            }

            if (!FMLEnvironment.production && !isSnowy && flag == MapChecker.FLAG_BLOCK) {
                int index = -1;
                int ddLength = 0;
                int[][][] directions = DirectionMask.DIRECTIONS;
                int[] indexs = DirectionMask.INDEXS;
                directionChecks:
                for (int i = 0, directionsLength = directions.length; i < directionsLength; i++) {
                    int[][] directionRequireGroup = directions[i];
                    for (int[] direction : directionRequireGroup) {
                        checkPos.set(pos.getX() + direction[0], pos.getY(), pos.getZ() + direction[1]);
                        BlockState neighSate = blockAndTintGetter.getBlockState(checkPos);
                        long neighSateSeed = neighSate.getSeed(checkPos);
                        if (!((mapSlice != null && MapChecker.shouldSnowAt(level, checkPos, mapSlice.getSurfaceFaceBiomeId(checkPos), neighSate, random, neighSateSeed))
                                || (mapSlice == null && MapChecker.shouldSnowAt(level, checkPos, neighSate, random, neighSateSeed))
                                || (mapSlice != null && mapSlice.getSnowyStatus(checkPos) == SnowyRemover.SnowyFlag.SNOWY_ALWAYS.ordinal())
                        )) continue directionChecks;
                    }
                    // todo 注意后续要考虑多条件满足最多项
                    if (directionRequireGroup.length > ddLength) {
                        index = i;
                        ddLength = directionRequireGroup.length;
                    }
                }
                if (index > -1) {
                    index = indexs[index];
                    replace = models.get(snow_edge_overlays.get(index));
                    isSnowy = true;
                }
            }

            // if (!isSnowy)
            if (replace == null || !(replace instanceof IESReplaceModel iesReplaceModel && iesReplaceModel.isReplace())) {


                if (seasonDefCache == null)
                    seasonDefCache = ClientRef.seasonDef.get(onBlock);
                if (seasonDefCache != null)
                    for (SeasonBlockDefinition localSeasonStatus : seasonDefCache) {
                        List<SeasonBlockDefinition.FlatSliceHolder> flatSliceHolders = localSeasonStatus.getFlatSliceEnumMap().get(ClientCon.nowSolarTerm);
                        if (flatSliceHolders != null && !flatSliceHolders.isEmpty()) {
                            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
                            for (SeasonBlockDefinition.FlatSliceHolder flatSliceHolder : flatSliceHolders) {
                                SeasonBlockDefinition.FlatSlice flatSlice = flatSliceHolder.flatSlice();
                                if (!flatSlice.emptyAbove() || blockAndTintGetter.getBlockState(checkPos).isEmpty()) {
                                    if (mapSlice == null
                                            || localSeasonStatus.getBiomes().contains(MapChecker.idToBiome(level, mapSlice.getSurfaceFaceBiomeId(checkPos)))) {
                                        ResourceLocation cinfo = flatSlice.transitionModels() == null ?
                                                flatSlice.mid() :
                                                Mth.abs(((int) (seed + pos.getX()))) % 100 > ClientCon.progress ?
                                                        flatSlice.transitionModels().getFirst() : flatSlice.transitionModels().getSecond();
                                        ModelResolver smr = extraSnowModelBuilds.get(cinfo);
                                        if (smr != null) {
                                            var mmrl = smr.tryFind(state);
                                            if (mmrl != null) {
                                                var to_replace = models.get(mmrl.modelResourceLocation());
                                                if (to_replace != null) {
                                                    if (replace != null) {
                                                        replace = new SnowySeasonBakeModel<>(to_replace, replace, getRenderType(state));
                                                        if (mmrl.replace() && replace instanceof SnowySeasonBakeModel<?> snowyBakedModelWrapper) {
                                                            snowyBakedModelWrapper.setReplace(true);
                                                        }
                                                    } else {
                                                        replace = mmrl.replace() ?
                                                                new TempReplaceModelWrapper<>(to_replace) :
                                                                to_replace;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                // if (ClientConfig.Renderer.flowerOnGrass.get() && state.getBlock() instanceof GrassBlock
                //         && (seed % 14) == 0)
                // // && random.nextInt(15) == 0)
                // {
                //     var solarTerm = ClientCon.nowSolarTerm;
                //
                //     checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
                //     if (solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SPRING, SolarTerm.BEGINNING_OF_SUMMER)) {
                //         int weight = Math.abs(solarTerm.ordinal() - 3) + 1;
                //         if ((seed % (weight * 4)) == 0
                //                 && blockAndTintGetter.getBlockState(checkPos).isAir()
                //                 && (mapSlice == null || ((IBiomeTagHolder) (Object) MapChecker.idToBiome(level, mapSlice.getSurfaceFaceBiomeId(checkPos)).value()).eclipticseasons$getBindTag() == ClimateTypeBiomeTags.SEASONAL)) {
                //             {
                //                 int index = Math.abs(((int) (seed + pos.getX())) % flower_on_grass.size());
                //                 // index=random.nextInt(flower_on_grass.size());
                //                 replace = models.get(flower_on_grass.get(index));
                //             }
                //         }
                //     }
                //     if (replace == null && solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                //         int weight = Math.abs(solarTerm.ordinal() - 7) + 1;
                //         if ((seed % (weight * 3)) == 0
                //                 && blockAndTintGetter.getBlockState(checkPos).isAir()
                //                 && (mapSlice == null || ((IBiomeTagHolder) (Object) MapChecker.idToBiome(level, mapSlice.getSurfaceFaceBiomeId(checkPos)).value()).eclipticseasons$getBindTag() == ClimateTypeBiomeTags.SEASONAL)) {
                //             {
                //                 int index = Math.abs(((int) (seed + pos.getX())) % fourleaf_clovers.size());
                //                 // index=2;
                //                 replace = models.get(fourleaf_clovers.get(index));
                //             }
                //         }
                //     }
                // }
            }
        }
        return replace;
    }

    private static boolean isSnowy(BlockPos pos, long seed, BlockPos.@NotNull MutableBlockPos checkPos, IMapSlice mapSlice, Level level, BlockAndTintGetter blockAndTintGetter) {
        // int randomKey = Math.floorMod(seed + pos.getX() * 31L + pos.getZ() * 17L, 100);
        // if (CommonConfig.Season.notSnowyNearGlowingBlock.get()) {
        //     if (mapSlice == null || mapSlice.getSnowyStatus(pos) == SnowyRemover.SNOWY) {
        //         checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
        //         int brightness = blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos);
        //         int cut = CommonConfig.Season.notSnowyNearGlowingBlockLevel.getAsInt() - brightness;
        //         if (cut > 0 && cut <= 4) {
        //             if (randomKey > cut * 20) {
        //                 return false;
        //             }
        //         }
        //     }
        //
        // }

        int lightLimit = CommonConfig.Season.notSnowyNearGlowingBlockLevel.get();
        boolean lightNot = CommonConfig.Season.notSnowyNearGlowingBlock.get();
        boolean isSnowy;
        Holder<Biome> biomeHolder = mapSlice != null ?
                MapChecker.idToBiome(level, mapSlice.getSurfaceFaceBiomeId(pos)) :
                MapChecker.getSurfaceBiome(level, pos);

        int snowDepth = WeatherManager.getSnowDepthAtBiome(level, biomeHolder.value());

        int count = snowDepth;
        int cc = 1;
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                if (dx == 0 && dz == 0) continue;

                checkPos.set(pos.getX() + dx, pos.getY(), pos.getZ() + dz);
                Holder<Biome> otherBiome = mapSlice != null ?
                        MapChecker.idToBiome(level, mapSlice.getSurfaceFaceBiomeId(checkPos)) :
                        MapChecker.getSurfaceBiome(level, checkPos);
                int i = otherBiome.value() == biomeHolder.value() ? snowDepth :
                        WeatherManager.getSnowDepthAtBiome(level, otherBiome.value());
                count += i;
                cc++;
                if (lightNot) {
                    if (mapSlice == null
                            || mapSlice.getSnowyStatus(pos) == SnowyRemover.SNOWY) {
                        checkPos.set(pos.getX() + dx, pos.getY() + 1, pos.getZ() + dz);
                        int brightness = blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos);
                        if (brightness >= lightLimit - 2) {
                            count -= 200;
                        }
                    }
                }
            }
        }

        int hash = Math.floorMod(seed + pos.getX() * 31L + pos.getZ() * 17L, cc * 100);
        isSnowy = hash <= count;
        return isSnowy;
    }

    public static RenderType getRenderType(BlockState state) {
        // TODO：加一个选择
        // if (!Minecraft.useFancyGraphics()) return RenderType.solid();
        // RenderType chunkRenderType = ItemBlockRenderTypes.getChunkRenderType(state);
        ChunkRenderTypeSet chunkRenderTypeSet = ItemBlockRenderTypes.getRenderLayers(state);
        if (chunkRenderTypeSet.contains(RenderType.translucent())) return RenderType.translucent();
        else if (chunkRenderTypeSet.contains(RenderType.cutout())) return RenderType.cutout();
        return
                // ( CompatModule.isContinuityLoad()||CompatModule.isCTMLoad())
                //         && !CompatModule.isSodiumLoad() ?
                //          RenderType.cutout() :
                RenderType.cutoutMipped();

        // return Minecraft.useFancyGraphics() ?
        //         RenderType.cutoutMipped() : RenderType.solid();
    }

    @Deprecated(forRemoval = true)
    public static boolean isModelReplaceable(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos) {
        return isModelReplaceable(state, blockAndTintGetter, pos, null);
    }

    public static boolean isModelReplaceable(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, BakedModel bakedModel) {
        return (bakedModel instanceof IESReplaceModel model
                && model.isReplace())
                || isModelReplaceable(MapChecker.getBlockTypeFlag(blockAndTintGetter, pos, state));
    }

    private static boolean isModelReplaceable(int flag) {
        return flag == MapChecker.FLAG_GRASS
                || flag == MapChecker.FLAG_GRASS_LARGE;
    }

    public static void clearForRebaked(Map<ModelResourceLocation, BakedModel> modelRegistry) {
        ModelManager.models = modelRegistry;
        loadVersion++;
        initCTMDetected();
        if (ClientCon.getUseLevel() != null) {
            ClientRef.updateClientSide(ClientCon.getUseLevel().registryAccess());
        }
    }


    public static final Map<ResourceLocation, ESModelLoadedJson> extraSnowModels = HashMap.newHashMap(1024);

    public static final Map<ResourceLocation, ModelResolver> extraSnowModelBuilds = HashMap.newHashMap(1024);

    public static void registerExtraSnowyModels(BiConsumer<ModelResourceLocation, UnbakedModel> registerModelAndDependenceMethod) {
        extraSnowModelBuilds.clear();
        // extraSnowModels.clear();
        Map<ResourceLocation, ESModelLoadedJson> snowModelLoadedJsonMap = ClientJsonCacheListener.modelDefCache.build(ESModelLoadedJson.CODEC);
        // extraSnowModels.putAll(snowModelLoadedJsonMap);
        EclipticSeasons.logger("Try to register extra model definitions with size %s.".formatted(snowModelLoadedJsonMap.size()));
        snowModelLoadedJsonMap.forEach(
                (resourceLocation, value) -> {
                    if (value.getMultiPartLike().isValid()) {
                        ModelResourceLocation mrl = ModelManager.snow_mrl(resourceLocation, "0");
                        registerModelAndDependenceMethod.accept(mrl, value.getMultiPartLike());
                        extraSnowModelBuilds.put(
                                resourceLocation, new ModelResolver(List.of(new ModelTester(
                                        mrl, value.isReplace(), List.of()
                                )))
                        );
                    } else {
                        value.getVariants().forEach(
                                (va, multiVariant) -> {
                                    ModelResourceLocation mrl = ModelManager.snow_mrl(resourceLocation, va);
                                    registerModelAndDependenceMethod.accept(
                                            mrl, multiVariant
                                    );
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
                                                            new ModelTester(mrl, value.isReplace(), test)
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
    }
}
