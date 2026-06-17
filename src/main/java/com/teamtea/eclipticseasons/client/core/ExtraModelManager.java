package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonBlockDefinition;
import com.teamtea.eclipticseasons.api.data.client.model.ModelResolver;
import com.teamtea.eclipticseasons.api.data.client.model.ModelTester;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.misc.client.*;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.model.*;
import com.teamtea.eclipticseasons.client.model.bakequad.*;
import com.teamtea.eclipticseasons.client.model.unbake.SolarBlockModel;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.Platform;
import com.teamtea.eclipticseasons.compat.ctm.CtmLoader;
import com.teamtea.eclipticseasons.compat.ctm.CtmProperties;
import com.teamtea.eclipticseasons.config.ClientConfig;

import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class ExtraModelManager {

    public static HashMap<ResourceLocation, SpriteContents> blocksCache = new HashMap<>();

    public static List<BakedQuad> EMPTY_BAKED_QUAD_LIST = List.of();
    public static int loadVersion = 0;

    public static Map<ModelResourceLocation, BakedModel> models;

    public static ModelResourceLocation snowOverlayLeaves = new ModelResourceLocation(BlockRegistry.snowyLeaves.getId(), "");
    public static ModelResourceLocation snowySlabBottom = new ModelResourceLocation(BlockRegistry.snowySlab.getId(), "type=bottom,waterlogged=false");
    public static ModelResourceLocation snowOverlayBlock = new ModelResourceLocation(BlockRegistry.snowyBlock.getId(), "");

    public static ModelResourceLocation ice = mrl("block/ice");

    public static ModelResourceLocation snowy_custom = mrl("block/snowy_custom");
    public static ModelResourceLocation snowy_custom_ao = mrl("block/snowy_custom_ao");

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

    public static ModelResourceLocation extra_mrl(ResourceLocation resourceLocation, String v) {
        return ModelResourceLocation.standalone(resourceLocation.withPrefix("extra/" + (v.isEmpty() ? "" : v + "/")));
    }

    public static ResourceLocation textureRL(String s) {
        return EclipticSeasons.rl("block/" + s);
    }

    public static TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(resourceLocation);
    }


    private static boolean shouldCutoutMipped(BlockState state) {
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
                (MapChecker.leaveLike(flag))
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
                            flag = snowDefinition.getInfo().getFlag();
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
                    snowModel = !CommonConfig.Snow.snowyTree.get() ?
                            models.get(snowOverlayLeaves) :
                            notSpecialLeaves ? models.get(snowy_leaves_top) : models.get(snowy_leaves_attach);
                } else if (flag == MapChecker.FLAG_SLAB) {
                    snowModel = models.get(snowySlabBottom);
                } else if (flag == MapChecker.FLAG_STAIRS_TOP) {
                    snowModel = models.get(stairs_top);
                } else if (flag == MapChecker.FLAG_STAIRS) {
                    if (snowState != null)
                        snowModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(snowState);
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
                    if (snowState != null)
                        snowModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(snowState);
                } else if (flag == MapChecker.FLAG_FARMLAND) {
                    snowModel = models.get(snow_height2_top);
                    // snowModel = snowOverlayBlock.resolve().get();
                } else if (flag == MapChecker.FLAG_CUSTOM) {
                    snowModel = models.get(snowy_custom);
                } else if (flag == MapChecker.FLAG_CUSTOM_AO) {
                    snowModel = models.get(snowy_custom_ao);
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

    public static List<BakedQuad> cancelTop(@Nullable BakedModel bakedModel, @Nonnull BlockAndTintGetter blockAndTintGetter, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nullable Direction direction, @Nonnull RandomSource random, long seed, @Nonnull List<BakedQuad> original, @Nullable List<BakedQuad> cache) {
        return cancelTop(IExtraRendererContextOwner.of(blockAndTintGetter), bakedModel, blockAndTintGetter, state, pos, direction, random, seed, original, cache);
    }

    // TODO：关于覆盖cutout面的问题，似乎可以给纹理加一个半透明像素，然后用cutout渲染就能正常覆盖了
    public static List<BakedQuad> cancelTop(@Nonnull ExtraRendererContext rendererHolder, @Nullable BakedModel bakedModel, @Nonnull BlockAndTintGetter blockAndTintGetter, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nullable Direction direction, @Nonnull RandomSource random, long seed, @Nonnull List<BakedQuad> original, @Nullable List<BakedQuad> cache) {
        if (rendererHolder.getExtraModel() == null) return original;
        if (bakedModel != null
                && ClientConfig.isTopFaceCulling()
                && !original.isEmpty()
                && (direction == Direction.UP || direction == null)
                && !(IESReplaceModel.isInvalid(bakedModel))
                && blockAndTintGetter instanceof IMapSliceProvider
        ) {
            random.setSeed(seed);
            // blockAndTintGetter 现在优化以后可以用来处理了
            var snowModel = rendererHolder.getExtraModel();
            if (snowModel instanceof SnowyBakedModelWrapper) {
                int blockType = MapChecker.getDefaultBlockTypeFlag(state);
                if (MapChecker.customBuiltin(blockType))
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


        if (bakedModel instanceof SnowyBakedModelWrapper<?> snowyBakedModelWrapper) {


            int blockType = snowyBakedModelWrapper.getBindBlockType() > MapChecker.FLAG_IGNORE ?
                    snowyBakedModelWrapper.getBindBlockType() :
                    MapChecker.getDefaultBlockTypeFlag(state);

            if (MapChecker.customBuiltin(blockType)) {
                original = new ArrayList<>();
            }


            boolean yuushyaBlock = false;
            // TODO:完善CTM支持
            yuushyaBlock |= cache != null && !cache.isEmpty();
            yuushyaBlock &= blockType != MapChecker.FLAG_LEAVES && blockType != MapChecker.FLAG_VINE;
            boolean sodiumStairs = ((blockType == MapChecker.FLAG_STAIRS || blockType == MapChecker.FLAG_STAIRS_TOP)
                    && CompatModule.isSodium() && state.getBlock() instanceof StairBlock);
            sodiumStairs = false;
            if (MapChecker.customBuiltin(blockType)
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
                    if (MapChecker.customBuiltin(blockType)
                            || sodiumStairs
                            || (yuushyaBlock && (
                            blockType == MapChecker.FLAG_STAIRS
                                    ||
                                    blockType == MapChecker.FLAG_STAIRS_TOP && direction != Direction.UP
                                    || blockType == MapChecker.FLAG_SLAB
                                    || blockType == MapChecker.FLAG_BLOCK && direction != Direction.UP))
                            || direction != null && direction.ordinal() > 1) {
                        ArrayList<BakedQuad> quadsCTM = null;

                        if (cache == null || cache.isEmpty()) {

                            BakedModel bakedModelCTM = rendererHolder.getOriginalModel();
                            if (bakedModelCTM != null) {
                                ModelData modelDataCTM = rendererHolder.getModelData();
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
                                    // if (MapChecker.customBuiltin(blockType))
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
                                                        spriteUse = snow_overlay_sprite;
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

                                    // if (bakedQuadDirection != Direction.WEST) continue;
                                    BakedQuad retexturedBakedQuad;
                                    // if (RectangularPrismChecker.isRectangularPrism(bakedQuad))
                                    {
                                        retexturedBakedQuad = new BakedQuadRetexturedAndReUV(bakedQuad, spriteUse, isSlabDown, offset);
                                    }
                                    // else {
                                    //     retexturedBakedQuad = new BakedQuadRetextured(bakedQuad, spriteUse);
                                    // }


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
                && MapChecker.getDefaultBlockTypeFlag(state) == MapChecker.FLAG_LEAVES) {
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

    public static boolean canSnowy(BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, long seed, @Nullable BlockPos.MutableBlockPos checkPos) {
        // if (!state.is(Blocks.LILY_PAD))
        //     return null;
        Level level = Minecraft.getInstance().level;
        if (level == null) return false;
        int flag = MapChecker.getDefaultBlockTypeFlag(state);
        var onBlock = state.getBlock();
        List<SnowDefinition> snowDefClientOverlay = ClientRef.snowClientDef.get(onBlock);

        if (flag == MapChecker.FLAG_NONE
                && (snowDefClientOverlay == null
                || snowDefClientOverlay.isEmpty()
                || snowDefClientOverlay.get(0).getInfo().getFlag() == MapChecker.FLAG_NONE)) {
            return false;
        }

        boolean leaveLike = MapChecker.leaveLike(flag);
        boolean leavesOrVine = leaveLike || MapChecker.vineLike(flag);

        IMapSlice mapSlice = null;
        if (blockAndTintGetter instanceof IMapSlice cmapSlice) {
            mapSlice = cmapSlice;
            if (!leavesOrVine) {
                int cut = mapSlice.getBlockHeight(pos) - pos.getY();
                if (cut > 1
                    // || cut < -3
                )
                    if (!ClientConfig.Renderer.snowUnderFence.get())
                        return false;
            }
        }

        int offset = snowDefClientOverlay == null ?
                MapChecker.getSnowOffset(state, flag) : snowDefClientOverlay.get(0).getInfo().getOffset();


        boolean isLight = false;
        if (checkPos == null) checkPos = posToMutable(pos);
        else checkPos.set(pos.getX(), pos.getY(), pos.getZ());

        if (ClientConfig.Renderer.useVanillaCheck.get()) {
            checkPos.setY(pos.getY() + 1);
            isLight = blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >= 15;
        } else {
            int cacheHeight = mapSlice != null ? mapSlice.getBlockHeight(checkPos) :
                    MapChecker.getHeightOrUpdate(level, checkPos, false);
            isLight = cacheHeight <= pos.getY() - offset;
        }

        boolean specialLeaves = false;

        if (!isLight && ClientConfig.Renderer.snowUnderFence.get()) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) >= 9) {
                int y_real = blockAndTintGetter instanceof IMapSliceProvider ip ?
                        ip.getSolidBlockHeight(checkPos) :
                        level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
                checkPos.setY(y_real);
                BlockState getterBlockState = blockAndTintGetter.getBlockState(checkPos);
                if (getterBlockState.isEmpty()) {
                    try {
                        getterBlockState = level.getBlockState(checkPos);
                    } catch (Exception e) {
                        EclipticSeasons.logger(e);
                    }
                }
                if (getterBlockState.getShadeBrightness(blockAndTintGetter, checkPos) < 0.5f) {
                    isLight = true;
                    if (leaveLike) {
                        if (CommonConfig.Snow.snowyTree.get())
                            specialLeaves = true;
                        else isLight = false;
                    }
                }
            }
        }

        if (isLight) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (MapChecker.leaveLike(flag)) {
                if (!specialLeaves) {
                    BlockState aboveState = blockAndTintGetter.getBlockState(checkPos);
                    if (isLight) {
                        // specialLeaves = true;
                        specialLeaves = blockAndTintGetter instanceof IMapSliceProvider ip ?
                                ip.getSolidBlockHeight(checkPos) > pos.getY() :
                                aboveState.is(state.getBlock());
                    }
                }
            } else {
                if (MapChecker.extraSnowPassable(state)) {
                    isLight = !MapChecker.extraSnowPassable(blockAndTintGetter.getBlockState(checkPos));
                } else if (!ClientConfig.Renderer.snowUnderFence.get()) {
                    isLight = !MapChecker.solidTest(blockAndTintGetter.getBlockState(checkPos));
                }
            }
        }

        if (isLight && specialLeaves) {
            isLight = CommonConfig.Snow.snowyTree.get();
            if (!isLight) specialLeaves = false;
        }
        boolean isSnowy = false;
        if (isLight) {
            checkPos.setY(pos.getY());
            if (CommonConfig.isSnowyWinter()
                    && onBlock != Blocks.SNOW_BLOCK
                    && maySnowyAt(level, mapSlice, state, checkPos, null, seed)
            ) {
                isSnowy = true;
                checkPos.set(pos.getX(), pos.getY() + 1 - offset, pos.getZ());
                isSnowy = notTooBright(blockAndTintGetter, mapSlice, checkPos);
            }

        }
        return isSnowy;
    }

    public static boolean maySnowyAt(Level level, IMapSlice mapSlice, BlockState state, BlockPos checkPos, RandomSource random, long seed) {
        if (mapSlice != null) {
            int snowyStatus = mapSlice.getSnowyStatus(checkPos);
            if (snowyStatus == SnowyRemover.SnowyFlag.SNOWY_ALWAYS.ordinal()) {
                return true;
            }
            if (snowyStatus == SnowyRemover.SnowyFlag.NONE_SNOWY.ordinal()) {
                return false;
            }
            if (EclipticUtil.canSnowyBlockInteract() && MapChecker.notWater(state))
                return mapSlice.isSnowyBlock(checkPos);
            return MapChecker.shouldSnowAt(level, checkPos, mapSlice.getSurfaceFaceBiomeId(checkPos), state, random, seed);
        } else {
            return MapChecker.shouldSnowAt(level, checkPos, state, random, seed);
        }
    }


    public static boolean notTooBright(BlockAndTintGetter blockAndTintGetter, IMapSlice mapSlice, BlockPos checkPos) {
        boolean isSnowy = true;
        if (CommonConfig.Snow.notSnowyNearGlowingBlock.get()
                && !EclipticUtil.canSnowyBlockInteract()) {
            if (mapSlice != null
                    && mapSlice.getSnowyStatus(checkPos) == SnowyRemover.SNOWY) {
                if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >=
                        CommonConfig.Snow.notSnowyNearGlowingBlockLevel.getAsInt()) {
                    isSnowy = false;
                }
            }

            if (mapSlice == null) {
                if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >=
                        CommonConfig.Snow.notSnowyNearGlowingBlockLevel.getAsInt()) {
                    isSnowy = false;
                }
            }
        }
        return isSnowy;
    }


    public static BakedModel findModel(BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, RandomSource random, long seed, @Nullable BlockPos.MutableBlockPos checkPos) {
        // if (!state.is(Blocks.LILY_PAD))
        //     return null;
        Level level = Minecraft.getInstance().level;
        if (level == null) return null;
        int flag = MapChecker.getDefaultBlockTypeFlag(state);
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

        boolean extendCheck = false;
        if (checkPos == null) checkPos = posToMutable(pos);
        else checkPos.set(pos.getX(), pos.getY(), pos.getZ());
        checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
        if (ClientConfig.Debug.smoothSnowyEdges.get() && blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) > 0) {
            extendCheck = true;
        }

        boolean leaveLike = MapChecker.leaveLike(flag);
        boolean leavesOrVine = leaveLike || MapChecker.vineLike(flag);

        BakedModel replace = null;
        IMapSlice mapSlice = null;
        if (blockAndTintGetter instanceof IMapSlice cmapSlice) {
            mapSlice = cmapSlice;
            if (!leavesOrVine) {
                int cut = mapSlice.getBlockHeight(pos) - pos.getY();

                if (cut > 1
                    // || cut < -3
                )
                    if (!ClientConfig.Renderer.snowUnderFence.get()
                            && !extendCheck)
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
                    mapSlice.getBlockHeight(checkPos)
                    : MapChecker.getHeightOrUpdate(level, checkPos, false);

            // if (ClientConfig.Renderer.snowUnderFence.get()) {
            //     if (solidBlockLike(flag) && pos.getY() == cacheHeight - 1) {
            //         checkPos.setY(pos.getY() + 1);
            //         BlockState aboveState = blockAndTintGetter.getBlockState(checkPos);
            //         if (
            //             // MapChecker.getBlockType(blockAndTintGetter.getBlockState(pos.above()), blockAndTintGetter, pos.above())
            //                 MapChecker.getBlockTypeFlag(blockAndTintGetter, checkPos, aboveState)
            //                         == MapChecker.FLAG_CUSTOM
            //                         && !(aboveState.getBlock() instanceof SlabBlock)
            //                         && !(aboveState.getBlock() instanceof StairBlock)) {
            //             cacheHeight--;
            //         } else {
            //             for (int i = 0; i < HORIZONTAL_DIRECTIONS.size(); i++) {
            //                 Direction direction = HORIZONTAL_DIRECTIONS.get(i);
            //                 checkPos.setX(pos.getX() + direction.getStepX());
            //                 checkPos.setZ(pos.getZ() + direction.getStepZ());
            //                 int neighbourHeight = mapSlice != null ?
            //                         mapSlice.getBlockHeight(checkPos) :
            //                         MapChecker.getHeightOrUpdate(level, checkPos, false);
            //                 if (neighbourHeight == pos.getY()) {
            //                     checkPos.setY(pos.getY() + 1);
            //                     BlockState neighbourState = blockAndTintGetter.getBlockState(checkPos);
            //                     // 函数调用也是耗时
            //                     int blockTypeFlag = MapChecker.getBlockTypeFlag(blockAndTintGetter, checkPos, neighbourState);
            //                     if (blockTypeFlag == MapChecker.FLAG_CUSTOM
            //                             && !(neighbourState.getBlock() instanceof SlabBlock)
            //                             && !(neighbourState.getBlock() instanceof StairBlock)
            //                     ) {
            //                         cacheHeight = neighbourHeight;
            //                         break;
            //                     }
            //                 }
            //             }
            //         }
            //     }
            // }

            isLight = cacheHeight <= pos.getY() - offset;
            // isLight = cacheHeight <= pos.getY() - 0;
        }

        // if (isLight) {
        //     if (!(MapChecker.extraSnowPassable(state))
        //             &&Heightmap.Types.MOTION_BLOCKING.isOpaque().test(state)) {
        //         int y_real = mapSlice!=null ?
        //                 mapSlice.getBlockHeight(checkPos) :
        //                 level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
        //         if(y_real!=pos.getY())
        //         isLight=false;
        //     }
        // }

        boolean specialLeaves = false;
        // if (!isLight && leavesOrVine && ClientConfig.Renderer.snowyTree.get()) {
        //     if (blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) >= 9) {
        //         int y_real = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
        //         if (y_real <= pos.getY()
        //                 || blockAndTintGetter.getBlockState(new BlockPos(pos.getX(), y_real, pos.getZ())).getShadeBrightness(blockAndTintGetter, pos) < 0.5f)
        //         // if (level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) >= pos.getY() + 1)
        //         {
        //             isLight = true;
        //             if (leaveLike) {
        //                 specialLeaves = true;
        //             }
        //         }
        //     }
        // }

        if (!isLight && ClientConfig.Renderer.snowUnderFence.get()) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) >= 9) {
                int y_real = blockAndTintGetter instanceof IMapSliceProvider ip ?
                        ip.getSolidBlockHeight(checkPos) :
                        level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
                checkPos.setY(y_real);
                BlockState getterBlockState = blockAndTintGetter.getBlockState(checkPos);
                if (getterBlockState.isEmpty()) {
                    try {
                        getterBlockState = level.getBlockState(checkPos);
                    } catch (Exception e) {
                        EclipticSeasons.logger(e);
                    }
                }
                if (getterBlockState.getShadeBrightness(blockAndTintGetter, checkPos) < 0.5f) {
                    isLight = true;
                    if (leaveLike) {
                        if (CommonConfig.Snow.snowyTree.get())
                            specialLeaves = true;
                        else isLight = false;
                    }
                }
            }
        }

        if (isLight) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (MapChecker.leaveLike(flag)) {
                if (!specialLeaves) {
                    BlockState aboveState = blockAndTintGetter.getBlockState(checkPos);
                    // boolean checkExtra = aboveState.is(state.getBlock())
                    //         && (Heightmap.Types.MOTION_BLOCKING_NO_LEAVES.isOpaque().test(aboveState) ||
                    //         MapChecker.extraSnowPassable(aboveState));
                    // if (checkExtra) {
                    //     isLight = CommonConfig.Season.snowyTree.get();
                    //     if (isLight) {
                    //         // specialLeaves = aboveState.is(state.getBlock());
                    //         specialLeaves = blockAndTintGetter instanceof IMapSliceProvider ip ?
                    //                 ip.getSolidBlockHeight(checkPos) > pos.getY() :
                    //                 aboveState.is(state.getBlock());
                    //     }
                    // }
                    if (isLight) {
                        // specialLeaves = true;
                        specialLeaves = blockAndTintGetter instanceof IMapSliceProvider ip ?
                                ip.getSolidBlockHeight(checkPos) > pos.getY() :
                                aboveState.is(state.getBlock());
                    }
                }
            } else {
                if (MapChecker.extraSnowPassable(state)) {
                    isLight = !MapChecker.extraSnowPassable(blockAndTintGetter.getBlockState(checkPos));
                } else if (!ClientConfig.Renderer.snowUnderFence.get()) {
                    isLight = !MapChecker.solidTest(blockAndTintGetter.getBlockState(checkPos));
                }
            }
        }

        if (isLight && specialLeaves) {
            isLight = CommonConfig.Snow.snowyTree.get();
            if (!isLight) specialLeaves = false;
        }

        if (isLight || extendCheck) {
            boolean isSnowy = false;
            checkPos.setY(pos.getY());
            if (CommonConfig.isSnowyWinter()
                    && isLight
                    && onBlock != Blocks.SNOW_BLOCK
                    && (maySnowyAt(level, mapSlice, state, checkPos, random, seed))
            ) {
                isSnowy = true;

                checkPos.set(pos.getX(), pos.getY() + 1 - offset, pos.getZ());
                isSnowy = notTooBright(blockAndTintGetter, mapSlice, checkPos);


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

            if (
                // !FMLEnvironment.production
                    !isSnowy
                            && (flag == MapChecker.FLAG_BLOCK || onBlock == Blocks.GRASS_BLOCK)
                            && ClientConfig.Debug.smoothSnowyEdges.get()) {
                int index = -1;
                int ddLength = 0;
                int[][][] directions = DirectionMask.DIRECTIONS;
                int[] indexs = DirectionMask.INDICES;
                BlockPos.MutableBlockPos originalCache = null;
                directionChecks:
                for (int i = 0, directionsLength = directions.length; i < directionsLength; i++) {
                    int[][] directionRequireGroup = directions[i];
                    for (int[] direction : directionRequireGroup) {
                        checkPos.set(pos.getX() + direction[0], pos.getY() + 1, pos.getZ() + direction[1]);
                        if ((mapSlice != null ? mapSlice.getBlockHeight(checkPos)
                                : MapChecker.getHeightOrUpdate(level, checkPos, false)) != pos.getY()) {
                            continue directionChecks;
                        }
                        checkPos.set(pos.getX() + direction[0], pos.getY(), pos.getZ() + direction[1]);

                        BlockState neighSate = blockAndTintGetter.getBlockState(checkPos);
                        long neighSateSeed = neighSate.getSeed(checkPos);

                        if (!(neighSate.is(Blocks.GRASS_BLOCK) || MapChecker.getDefaultBlockTypeFlag(neighSate) == MapChecker.FLAG_BLOCK)) {
                            continue directionChecks;
                        }
                        if (originalCache == null)
                            originalCache = new BlockPos.MutableBlockPos(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                        else originalCache.set(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                        if (!canSnowy(blockAndTintGetter, originalCache, neighSate, neighSateSeed, checkPos)) {
                            continue directionChecks;
                        }
                        // if (!((mapSlice != null && MapChecker.shouldSnowAt(level, checkPos, mapSlice.getSurfaceFaceBiomeId(checkPos), neighSate, random, neighSateSeed))
                        //         || (mapSlice == null && MapChecker.shouldSnowAt(level, checkPos, neighSate, random, neighSateSeed))
                        //         || (mapSlice != null && mapSlice.getSnowyStatus(checkPos) == SnowyRemover.SnowyFlag.SNOWY_ALWAYS.ordinal())
                        // ))
                    }
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
        }

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
                                if ((mapSlice == null && localSeasonStatus.getBiomes().contains(MapChecker.getSurfaceBiome(level, checkPos))
                                        || (mapSlice != null && localSeasonStatus.getBiomes().contains(MapChecker.idToBiome(level, mapSlice.getSurfaceFaceBiomeId(checkPos)))))) {
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

        return replace;
    }

    private static final Direction[] SNOW_LAYER_DIRECTIONS_TO_CHECK = {
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final BlockPos[] SNOW_LAYER_DIRECTIONS_TO_CHECK_4 = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    };

    private static final BlockPos[] SNOW_LAYER_DIRECTIONS_TO_CHECK_8 = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, -1)
    };

    public static BakedModel shouldRenderedWithSnowInside(
            BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state,
            @Nullable BlockPos.MutableBlockPos checkPos) {
        int layers = getRenderedLevelWithSnowInside(blockAndTintGetter, pos, state, checkPos);
        return layers > 0 ? getSnowLayerModel(layers) : null;
    }

    public static int getRenderedLevelWithSnowInside(
            BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state,
            @Nullable BlockPos.MutableBlockPos checkPos) {

        if (!ClientConfig.Renderer.snowInFence.get()) return 0;
        Level useLevel = ClientCon.getUseLevel();
        if (!(blockAndTintGetter instanceof IMapSlice mapSlice)
                || useLevel == null) return 0;

        mapSlice.setLevelForFakeSnow(pos, 0);

        if (blockAndTintGetter.getBrightness(LightLayer.SKY, pos) == 0) {
            return 0;
        }

        if (checkPos == null) checkPos = posToMutable(pos);
        else checkPos.set(pos.getX(), pos.getY(), pos.getZ());

        // checkPos.move(Direction.UP);

        if (state.isAir() || !state.getFluidState().isEmpty() || state.is(EclipticBlockTags.SNOW_LAYER_CANNOT_SURVIVE_IN))
            return 0;
        if (ClientConfig.Renderer.snowInFenceOnlySnowy.get()
                && !maySnowyAt(useLevel, mapSlice, state, pos, useLevel.getRandom(), state.getSeed(pos))) {
            return 0;
        }

        BlockState belowState = blockAndTintGetter.getBlockState(checkPos.setY(pos.getY() - 1));
        if (belowState.is(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
            return 0;
        } else {
            if (!belowState.is(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)
                    && !(Block.isFaceFull(belowState.getCollisionShape(blockAndTintGetter, checkPos), Direction.UP)
                    || belowState.is(Blocks.SNOW) && belowState.getValue(SnowLayerBlock.LAYERS) == 8))
                return 0;
        }


        int snowNearbyCount = 0;
        int airNeighborCount = 0;
        int minLayers = 8;

        var directions = ClientConfig.Renderer.snowInFenceDirection.get() ?
                SNOW_LAYER_DIRECTIONS_TO_CHECK_8 : SNOW_LAYER_DIRECTIONS_TO_CHECK_4;

        for (var dir : directions) {
            checkPos.set(pos.getX() + dir.getX(), pos.getY() + dir.getY(), pos.getZ() + dir.getZ());
            BlockState neighborState = blockAndTintGetter.getBlockState(checkPos);

            if (neighborState.isAir()) {
                checkPos.move(Direction.DOWN);
                if (!blockAndTintGetter.getBlockState(checkPos).blocksMotion()) {
                    airNeighborCount++;
                }
                continue;
            }

            int currentNeighborLayers = 0;
            if (neighborState.getBlock() == Blocks.SNOW) {
                currentNeighborLayers = neighborState.getValue(SnowLayerBlock.LAYERS);
            } else if (neighborState.getBlock() == Blocks.SNOW_BLOCK) {
                currentNeighborLayers = 8;
            }

            if (currentNeighborLayers > 0) {
                snowNearbyCount++;
                if (currentNeighborLayers < minLayers) {
                    minLayers = currentNeighborLayers;
                }
            }
        }

        int baseRequired = ClientConfig.Renderer.snowInFenceCount.get();
        int dynamicRequired = Math.max(1, baseRequired - airNeighborCount);

        if (snowNearbyCount >= dynamicRequired) {
            if (state.isCollisionShapeFullBlock(blockAndTintGetter, pos)
                    || state.isFaceSturdy(blockAndTintGetter, pos, Direction.DOWN)) return 0;

            mapSlice.setLevelForFakeSnow(pos, minLayers);
            return minLayers;
        }

        return 0;
    }


    public static BakedModel getSnowLayerModel(int layers) {
        int clampedLayers = Mth.clamp(layers, 1, 8);
        BlockState snowState = Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, clampedLayers);
        return Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(snowState);
    }


    public static BlockState shouldBlockAsSnowyState(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos.MutableBlockPos mutableBlockPos) {
        if (!ClientConfig.Renderer.snowInFence.get()) return state;
        // if (!state.blocksMotion() || !state.getFluidState().isEmpty())
        //    return state;
        if (!(state.getBlock() instanceof SnowyDirtBlock)) return state;
        int y = mutableBlockPos.getY();
        mutableBlockPos.setY(y + 1);
        BlockState blockState = blockAndTintGetter.getBlockState(mutableBlockPos);
        BakedModel bm = ExtraModelManager.shouldRenderedWithSnowInside(blockAndTintGetter, mutableBlockPos, blockState, null);
        if (bm != null) {
            if (state.hasProperty(BlockStateProperties.SNOWY)) {
                state = state.setValue(BlockStateProperties.SNOWY, true);
            }
        }
        mutableBlockPos.setY(y);
        return state;
    }

    public static int getLayer(BlockAndTintGetter blockAndTintGetter, BlockPos.MutableBlockPos pos, BlockState state, BakedModel snowModel, long seed) {
        if (!(blockAndTintGetter instanceof IMapSlice mapSlice) || !ClientConfig.Renderer.extraSnowLayer.get())
            return 0;
        if (mapSlice.getBlockHeight(pos) != pos.getY()
                && mapSlice.getSolidBlockHeight(pos) != pos.getY()) return 0;
        // if (mapSlice.getBlockHeight(pos) > pos.getY()) return 0;

        int realY = pos.getY() + 1;

        // Avoid cache lookup here;
        // Coordinate traversal makes snow priority undefined and may break face culling.

        mapSlice.setLevelForFakeSnow(pos.getX(), realY, pos.getZ(), 0);

        if (MapChecker.getDefaultBlockTypeFlag(state) <= MapChecker.FLAG_NONE) return 0;
        if (mapSlice.getSnowyStatus(pos) == SnowyRemover.NONE_SNOWY) return 0;
        Level useLevel = ClientCon.getUseLevel();
        if (useLevel == null) return 0;

        if (!(state.isSolidRender(blockAndTintGetter, pos) || state.getBlock() instanceof LeavesBlock)) return 0;
        if (!state.getFluidState().isEmpty()) return 0;

        BlockPos abovePos = pos.setY(pos.getY() + 1);
        BlockState aboveState = blockAndTintGetter.getBlockState(abovePos);
        if (!aboveState.getFluidState().isEmpty()) return 0;
        if (aboveState.getBlock() instanceof LeavesBlock || aboveState.isFaceSturdy(blockAndTintGetter, abovePos, Direction.DOWN) || aboveState.is(EclipticBlockTags.SNOW_LAYER_CANNOT_SURVIVE_IN))
            return 0;
        if (!((snowModel != null && !ISnowyReplaceModel.isInvalid(snowModel)) || maySnowyAt(useLevel, mapSlice, state, pos, null, seed)))
            return 0;

        if (!notTooBright(blockAndTintGetter, mapSlice, pos)) return 0;

        var biome = MapChecker.idToBiome(useLevel, mapSlice.getSurfaceFaceBiomeId(pos));
        if (biome == null) return 0;

        int snowDepth = Mth.clamp(WeatherManager.getSnowDepthAtBiome(useLevel, biome.value()), 0, 100);
        if (snowDepth <= 0) return 0;

        final long posLong = pos.asLong();
        long h = (posLong ^ seed) * 0x5DEECE66DL + 0xBL;
        h = (h ^ (h >>> 16)) * 0x27D4EB2DL;
        h = h ^ (h >>> 15);
        int noiseInt = (int) (h & 0x7FFFFFFF) % 100;

        int maxLayers = (state.getBlock() instanceof LeavesBlock) ?
                ClientConfig.Renderer.extraSnowLayerMaxLayersOnLeaves.get() :
                ClientConfig.Renderer.extraSnowLayerMaxLayers.get();

        float targetLayers;

        if (snowDepth <= 50) {
            targetLayers = (snowDepth / 50.0f) * 0.05f;
        } else {
            float progress = (snowDepth - 50) / 50.0f;
            targetLayers = 0.05f + (progress * progress) * (maxLayers - 0.05f);
        }

        int base = (int) Math.floor(targetLayers);
        int chance = (int) ((targetLayers - base) * 100);

        if (noiseInt < chance) base++;

        int minLayers = Mth.clamp(base, 0, maxLayers);
        mapSlice.setLevelForFakeSnow(pos.getX(), realY, pos.getZ(), minLayers);
        return minLayers;
    }

    public static BlockState getFakeBlockState(BlockState original, BlockState selfState, BlockGetter view, BlockPos.MutableBlockPos otherPos, BlockPos selfPos, Direction facing) {
        if (facing == Direction.DOWN
                || !(view instanceof BlockAndTintGetter getter)
                || !(view instanceof IMapSlice mapSlice)
                || !(ClientConfig.Renderer.extraSnowLayerCulling.get())
                || mapSlice.getBlockHeight(selfPos) > selfPos.getY()) return original;

        boolean snowInFence = ClientConfig.Renderer.snowInFence.get();
        boolean extraSnowLayer = ClientConfig.Renderer.extraSnowLayer.get();
        if (!snowInFence && !extraSnowLayer) return original;


        boolean notUp = facing != Direction.UP;
        boolean snowSelf = !selfState.is(Blocks.SNOW);
        if (snowSelf || notUp) {

            // Snowscape may insert snow blocks into fences/vegetation.
            // Returning the original shape avoids incorrect face culling of the snow model.
            if (snowInFence && original.isSolid()) return original;

            int cacheLevel = mapSlice.getLevelForFakeSnow(otherPos);
            if (cacheLevel > IFakeSnowHolder.NONE_CHECK_FAKE_SNOW_LEVEL)
                return cacheLevel == 0 ? original :
                        Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, cacheLevel);

            int y = otherPos.getY();
            if (snowInFence) {
                int x = otherPos.getX();
                int z = otherPos.getZ();
                int snowInsideLevel = ExtraModelManager.getRenderedLevelWithSnowInside(getter, otherPos, original, null);
                otherPos.set(x, y, z);
                if (snowInsideLevel > 0) {
                    return Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, snowInsideLevel);
                }
                otherPos.set(x, y, z);
            }

            if (extraSnowLayer) {
                otherPos.setY(y - 1);
                BlockState belowState = !notUp ? selfState : view.getBlockState(otherPos);
                int layer = ExtraModelManager.getLayer(getter, otherPos, belowState, null, belowState.getSeed(otherPos));
                otherPos.setY(y);
                if (layer > 0)
                    return Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, layer);
            }
        }
        return original;
    }

    public static RenderType getRenderType(BlockState state) {
        // TODO：加一个选择
        // if (!Minecraft.useFancyGraphics()) return RenderType.solid();
        // RenderType chunkRenderType = ItemBlockRenderTypes.getChunkRenderType(state);
        ChunkRenderTypeSet chunkRenderTypeSet = ItemBlockRenderTypes.getRenderLayers(state);
        if (chunkRenderTypeSet.contains(RenderType.translucent())) return RenderType.translucent();
        else if (chunkRenderTypeSet.contains(RenderType.cutout())) return RenderType.cutout();
        // return
        //         // ( CompatModule.isContinuityLoad()||CompatModule.isCTMLoad())
        //         //         && !CompatModule.isSodiumLoad() ?
        //         //          RenderType.cutout() :
        //         Minecraft.useFancyGraphics()&& state.getBlock() instanceof LeavesBlock?
        //                 RenderType.cutoutMipped(): RenderType.cutout();
        return RenderType.cutoutMipped();
    }

    @Deprecated(forRemoval = true)
    public static boolean isModelReplaceable(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos) {
        return isModelReplaceable(state, blockAndTintGetter, pos, null);
    }

    public static boolean isModelReplaceable(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, BakedModel bakedModel) {
        return (bakedModel instanceof IESReplaceModel model
                && model.isReplace())
                || isModelReplaceable(MapChecker.getDefaultBlockTypeFlag(state));
    }

    public static boolean isModelReplaceable(int flag) {
        return flag == MapChecker.FLAG_GRASS
                || flag == MapChecker.FLAG_GRASS_LARGE;
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

    public static void clearForRebaked(Map<ModelResourceLocation, BakedModel> modelRegistry) {
        ExtraModelManager.models = modelRegistry;
        loadVersion++;
        initCTMDetected();
        if (ClientCon.getUseLevel() != null) {
            ClientRef.updateClientSide(ClientCon.getUseLevel().registryAccess());
        }

        snowOverlayLeaves = BlockModelShaper.stateToModelLocation(BlockRegistry.snowyLeaves.get().defaultBlockState());
        snowySlabBottom = BlockModelShaper.stateToModelLocation(BlockRegistry.snowySlab.get().defaultBlockState()
                .setValue(SlabBlock.TYPE, SlabType.BOTTOM)
                .setValue(SlabBlock.WATERLOGGED, false)
        );
        snowOverlayBlock = BlockModelShaper.stateToModelLocation(BlockRegistry.snowyBlock.get().defaultBlockState());
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
                (resourceLocation, loadedJson) -> {
                    if (!loadedJson.getRequire().isEmpty()) {
                        for (String modid : loadedJson.getRequire()) {
                            if (!Platform.isModLoaded(modid)) {
                                return;
                            }
                        }
                    }
                    if (loadedJson.getMultiPartLike().isValid()) {
                        ModelResourceLocation mrl = ExtraModelManager.extra_mrl(resourceLocation, "0");
                        registerModelAndDependenceMethod.accept(mrl, loadedJson.getMultiPartLike());
                        extraSnowModelBuilds.put(
                                resourceLocation, new ModelResolver(List.of(new ModelTester(
                                        mrl, loadedJson.isReplace(), List.of()
                                )))
                        );
                    } else {
                        loadedJson.getVariants().forEach(
                                (va, multiVariant) -> {
                                    ModelResourceLocation mrl = ExtraModelManager.extra_mrl(resourceLocation, va.replaceAll("=", "_").replace(",", "_"));
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
                                                            new ModelTester(mrl, loadedJson.isReplace(), test)
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

    public static final Map<ResourceLocation, List<SeasonalTexture>> SEASONAL_TEXTURE_HASH_MAP = new HashMap<>();

    public static BlockModel remappingSeasonTextures(ResourceLocation resourceLocation, BlockModel returnValue) {
        if (SEASONAL_TEXTURE_HASH_MAP.containsKey(resourceLocation)) {
            List<SeasonalTexture> seasonalTexture = SEASONAL_TEXTURE_HASH_MAP.get(resourceLocation);
            return SolarBlockModel.of(returnValue).setSeasonalTexture(seasonalTexture);
        }
        return null;
    }
}
