package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyBlockState;
import com.teamtea.eclipticseasons.client.model.*;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.yuushya.YuushyaChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nullable;
import java.util.*;

// https://github.com/DoubleNegation/CompactOres/blob/1.18/src/main/java/doublenegation/mods/compactores/CompactOresResourcePack.java#L164

// 未来可以基于RepositorySource实现动态纹理生成（看情况，因为目前不需要，对内存消耗比较大）
public class ModelManager {

    public static List<BakedQuad> EMPTY_BAKED_QUAD_LIST = List.of();
    public static int loadVersion = 0;

    public static Map<ModelResourceLocation, BakedModel> models;

    public static ModelResourceLocation snowOverlayLeaves = new ModelResourceLocation(EclipticSeasons.ModContents.snowyLeaves.getId(), "");
    public static ModelResourceLocation snowySlabBottom = new ModelResourceLocation(EclipticSeasons.ModContents.snowySlab.getId(), "type=bottom,waterlogged=false");
    public static ModelResourceLocation snowOverlayBlock = new ModelResourceLocation(EclipticSeasons.ModContents.snowyBlock.getId(), "");

    public static BlockState LIGHT_0;

    public static ModelResourceLocation snowy_custom = mrl("block/snowy_custom");
    public static ModelResourceLocation stairs_top = mrl("block/stairs_top");
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
    public static List<ModelResourceLocation> flower_on_grass = List.of(1, 2, 3, 4, 5, 6).stream().map(i -> mrl("block/flower_%s".formatted(i))).toList();

    public static ResourceLocation snow = ResourceLocation.withDefaultNamespace("block/snow");
    public static ResourceLocation snow_overlay_half_left = textureRL("snow_overlay_half_left");
    public static ResourceLocation snow_overlay_half_right = textureRL("snow_overlay_half_right");
    public static ResourceLocation snow_overlay = textureRL("snow_overlay");
    public static ResourceLocation snow_overlay_leaves = textureRL("snow_overlay_leaves");
    public static ResourceLocation snow_overlay_tiny = textureRL("snow_overlay_tiny");

    public static ModelResourceLocation mrl(String s) {
        return ModelResourceLocation.standalone(EclipticSeasons.rl(s));
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

    public static BakedModel getSnowyModel(BlockState state, BlockState snowState, int flag, int offset) {
        ISnowyBlockState snowyBlockState = (ISnowyBlockState) state;
        // BakedModel snowModel = stateModelsCache.getOrDefault(state, null);
        BakedModel snowModel = snowyBlockState.getSnowyModel(loadVersion);
        if (snowModel == null) {
            Block onBlock = state.getBlock();
            if (flag == MapChecker.FLAG_BLOCK) {
                snowModel = models.get(snowOverlayBlock);
            } else if (flag == MapChecker.FLAG_LEAVES) {
                snowModel = models.get(snowOverlayLeaves);
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
            } else if (flag == MapChecker.FLAG_FARMLAND) {
                snowModel = models.get(snow_height2_top);
                // snowModel = snowOverlayBlock.resolve().get();
            } else if (flag == MapChecker.FLAG_CUSTOM) {
                snowModel = models.get(snowy_custom);
            }
            if (YuushyaChecker.isyuushyaContinuityBlock(state)) {
                snowModel = models.get(snowy_custom);
            }
            if (snowModel != null) {
                // stateModelsCache.putIfAbsent(snowState, snowModel);
                SnowyBakedModelWrapper<?> bakedModel =
                        snowModel instanceof SnowyBakedModelWrapper<?> ?
                                (SnowyBakedModelWrapper<?>) snowModel :
                                new SnowyBakedModelWrapper<>(snowModel);
                if (SnowyBakedModelWrapper.isInvalid(bakedModel))
                    bakedModel.updateBlockType(flag);
                snowyBlockState.setSnowyModel(
                        bakedModel, loadVersion);
            }

            // if (snowModel != null) {
            //     snowyModelsCache.putIfAbsent(snowModel, flag);
            // }
        }

        return snowModel;
    }

    private final static List<BakedQuad> EMPTY = List.of();

    public static List<BakedQuad> cancelTop(BakedModel bakedModel, BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> original) {
        return cancelTop(bakedModel, blockAndTintGetter, state, pos, direction, random, seed, original, EMPTY_BAKED_QUAD_LIST,null);
    }

    public static List<BakedQuad> cancelTop(BakedModel bakedModel, BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> original, List<BakedQuad> cache, @Nullable BakedModel snowModel) {

        if (bakedModel != null
                && !original.isEmpty()
                && (direction == Direction.UP || direction == null)
                && !(bakedModel instanceof SnowyBakedModelWrapper)
        ) {
            random.setSeed(seed);
            // blockAndTintGetter 现在优化以后可以用来处理了
            if (snowModel == null)
                snowModel = ModelManager.findModel(blockAndTintGetter, pos, state, random, seed);

            if (snowModel instanceof SnowyBakedModelWrapper) {
                int blockType = ((IBlockStateFlagger) state).getBlockTypeFlag(blockAndTintGetter, pos);
                if (blockType == MapChecker.FLAG_CUSTOM)
                    return original;
                if (direction == Direction.UP) {
                    if (blockType == MapChecker.FLAG_BLOCK) return EMPTY;
                }


                // fabric 连接纹理用到了后处理，此处如果不返回给它就会停止渲染
                if (YuushyaChecker.isyuushyaContinuityBlock(state)) {
                    if (blockType == MapChecker.FLAG_STAIRS
                            || blockType == MapChecker.FLAG_SLAB)
                        return original;
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

            int blockType = ((IBlockStateFlagger) state).getBlockTypeFlag(blockAndTintGetter, pos);
            if (blockType == MapChecker.FLAG_CUSTOM) {
                original = new ArrayList<>();
            }

            boolean yuushyaBlock = YuushyaChecker.isyuushyaContinuityBlock(state);
            if ((blockType == MapChecker.FLAG_CUSTOM || yuushyaBlock)
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
                        if (!cache.isEmpty()) {
                            cache = new ArrayList<>(cache);
                            cache.removeIf(bakedQuad -> bakedQuad.getDirection() != Direction.UP);
                        }
                    }
                }
                // if(blockType==MapChecker.FLAG_STAIRS)
                {
                    if (blockType == MapChecker.FLAG_CUSTOM
                            || (yuushyaBlock && (blockType == MapChecker.FLAG_STAIRS
                            || blockType == MapChecker.FLAG_STAIRS_TOP && direction != Direction.UP
                            || blockType == MapChecker.FLAG_SLAB
                            || blockType == MapChecker.FLAG_BLOCK && direction != Direction.UP))
                            || direction != null && direction.ordinal() > 1) {
                        ArrayList<BakedQuad> quadsCTM = null;

                        if (cache.isEmpty() || cache == null) {
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
        return original;
    }

    // TODO: 这里需要给Map做切片，生物群系要缓冲，看怎么切
    public static BakedModel findModel(BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, RandomSource random, long seed) {
        Level level = Minecraft.getInstance().level;
        BakedModel replace = null;
        // 这里不需要担心，是因为我们给不符合要求的level默认返回一个0或者最低值-1
        IMapSlice mapSlice = null;
        if (blockAndTintGetter instanceof IMapSlice cmapSlice) {
            mapSlice = cmapSlice;
            int cut = mapSlice.getBlockHeight(pos) - pos.getY();
            if (cut > 1 || cut < -3)
                return replace;
        }

        if (level == null) return replace;


        var onBlock = state.getBlock();
        // int flag = MapChecker.getBlockType(state, blockAndTintGetter, pos);
        int flag = ((IBlockStateFlagger) state).getBlockTypeFlag(blockAndTintGetter, pos);
        if (flag == 0) return replace;
        int offset = MapChecker.getSnowOffset(state, flag);

        boolean isLight = false;

        if (ClientConfig.Renderer.useVanillaCheck.get()) {
            isLight = blockAndTintGetter.getBrightness(LightLayer.BLOCK, pos.above()) >= 15;
        } else {
            // ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, pos);

            int cacheHeight = mapSlice != null ?
                    mapSlice.getBlockHeight(pos)
                    : MapChecker.getHeightOrUpdate(level, pos, false);

            if (ClientConfig.Renderer.betterSnow.get()) {
                if (flag == MapChecker.FLAG_BLOCK && pos.getY() == cacheHeight - 1) {
                    if (
                        // MapChecker.getBlockType(blockAndTintGetter.getBlockState(pos.above()), blockAndTintGetter, pos.above())
                            ((IBlockStateFlagger) blockAndTintGetter.getBlockState(pos.above())).getBlockTypeFlag(blockAndTintGetter, pos.above())
                                    == MapChecker.FLAG_CUSTOM) {
                        cacheHeight--;
                    } else {
                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            int neighbourHeight = mapSlice != null ?
                                    mapSlice.getBlockHeight(pos.relative(direction)) : MapChecker.getHeightOrUpdate(level, pos.relative(direction), false);
                            if (neighbourHeight == pos.getY()) {
                                BlockPos above = pos.above();
                                BlockState neighbourState = blockAndTintGetter.getBlockState(above);
                                // 函数调用也是耗时
                                int blockTypeFlag = ((IBlockStateFlagger) neighbourState).getBlockTypeFlag(blockAndTintGetter, above);
                                if (blockTypeFlag != MapChecker.FLAG_BLOCK
                                        && !(neighbourState.getBlock() instanceof SlabBlock)
                                        && !(neighbourState.getBlock() instanceof StairBlock)) {
                                    cacheHeight = neighbourHeight;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            isLight = cacheHeight == pos.getY() - offset;
        }


        if (isLight) {
            if (ClientConfig.Renderer.snowyWinter.get()
                    && onBlock != Blocks.SNOW_BLOCK
                    && (MapChecker.shouldSnowAt(level, pos.below(offset), state, random, seed)
                    || (mapSlice != null
                    && mapSlice.getSnowyStatus(pos) == SnowyRemover.SnowyFlag.SNOWY_ALWAYS.ordinal()))
                // && (mapSlice!=null?
                // MapChecker.shouldSnowAt(level, pos.below(offset),mapSlice.getSurfaceFaceBiomeId(pos), state, random, seed):
                //  MapChecker.shouldSnowAt(level, pos.below(offset), state, random, seed))
            ) {
                boolean isSnowy = true;

                if (ClientConfig.Renderer.notSnowyNearGlowingBlock.get()) {
                    if (mapSlice != null
                            && mapSlice.getSnowyStatus(pos) == SnowyRemover.SNOWY) {
                        BlockPos above = pos.offset(0, 1 - offset, 0);
                        if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, above) >=
                                ClientConfig.Renderer.notSnowyNearGlowingBlockLevel.getAsInt()) {
                            isSnowy = false;
                            if (!ClientConfig.Debug.disableLight0AboveCancelLightCheck.getAsBoolean()) {
                                BlockState aboveState = blockAndTintGetter.getBlockState(pos.above());
                                if (aboveState == LIGHT_0) {
                                    isSnowy = true;
                                }
                            }
                        }
                    }
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
                        if (models != null && flag == MapChecker.FLAG_STAIRS) {
                            snowState = EclipticSeasons.ModContents.snowyStairs.get().defaultBlockState()
                                    .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                                    .setValue(StairBlock.HALF, state.getValue(StairBlock.HALF))
                                    .setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
                        }
                        BakedModel snowModel = getSnowyModel(state, snowState, flag, offset);

                        if (snowModel != null) {
                            replace = snowModel;
                        }
                    }
                }
            } else if (ClientConfig.Renderer.flowerOnGrass.get() && state.getBlock() instanceof GrassBlock
                    && (seed % 14) == 0)
            // && random.nextInt(15) == 0)
            {
                var solarTerm = ClientCon.nowSolarTerm;
                int weight = Math.abs(solarTerm.ordinal() - 3) + 1;
                if (solarTerm.getSeason() == Season.SPRING
                        && (seed % (weight * 4)) == 0
                        && blockAndTintGetter.getBlockState(pos.above()).isAir()) {
                    {
                        int index = Math.abs(((int) (seed + pos.getX())) % flower_on_grass.size());
                        // index=random.nextInt(flower_on_grass.size());
                        replace = models.get(flower_on_grass.get(index));
                    }
                }
            }
        }
        return replace;
    }

    public static RenderType getRenderType(BlockState state) {
        // if (!Minecraft.useFancyGraphics()) return RenderType.solid();
        //
        // if (YuushyaChecker.isyuushyaContinuityBlock(state)) {
        //     EclipticSeasons.logger(ItemBlockRenderTypes.getChunkRenderType(state));
        // }
        // RenderType chunkRenderType = ItemBlockRenderTypes.getChunkRenderType(state);
        ChunkRenderTypeSet chunkRenderTypeSet = ItemBlockRenderTypes.getRenderLayers(state);
        if (chunkRenderTypeSet.contains(RenderType.translucent())) return RenderType.translucent();
        else if (chunkRenderTypeSet.contains(RenderType.cutout())) return RenderType.cutout();
        return
                // ( CompatModule.isContinuityLoad()||CompatModule.isCTMLoad())
                //         && !CompatModule.isSodiumLoad() ?
                //          RenderType.cutout() :
                // TODO:我也是服了这个渲染顺序
                RenderType.cutoutMipped();

        // return Minecraft.useFancyGraphics() ?
        //         RenderType.cutoutMipped() : RenderType.solid();
    }

    // TODO: Note some block may be not motion for some state
    public static boolean isModelReplaceable(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.SHORT_GRASS ||
                block == Blocks.FERN ||
                block == Blocks.TALL_GRASS ||
                block == Blocks.LARGE_FERN;
        // if (MapChecker.LARGE_GRASS.contains(block) || MapChecker.LowerPlant.contains(block))
        //     return true;
// return !state.blocksMotion()
        //         && MapChecker.getBlockType(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO) != MapChecker.FLAG_NONE;
    }

    public static boolean isModelReplaceable(int flag) {
        return flag == MapChecker.FLAG_GRASS
                || flag == MapChecker.FLAG_GRASS_LARGE;
    }

    public static void clearForRebaked(Map<ModelResourceLocation, BakedModel> modelRegistry) {
        ModelManager.models = modelRegistry;
        loadVersion++;
        // snowyModelsCache.clear();
        // stateModelsCache.clear();
        LIGHT_0 = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 0);
    }
}
