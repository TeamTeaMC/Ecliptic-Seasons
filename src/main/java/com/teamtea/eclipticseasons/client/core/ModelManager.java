package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.model.*;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import com.teamtea.eclipticseasons.EclipticSeasons;

import java.util.*;

// https://github.com/DoubleNegation/CompactOres/blob/1.18/src/main/java/doublenegation/mods/compactores/CompactOresResourcePack.java#L164
// 未来可以基于RepositorySource实现动态纹理生成（看情况，因为目前不需要，对内存消耗比较大）
public class ModelManager {
    public static final RenderType CUTOUT_MIPPED = null;

    public static Map<ResourceLocation, BakedModel> models;
    public static ModelResourceLocation snowOverlayLeaves = new ModelResourceLocation(BlockRegistry.snowyLeaves.getId(), "");
    public static ModelResourceLocation snowySlabBottom = new ModelResourceLocation(BlockRegistry.snowySlab.getId(), "type=bottom,waterlogged=false");
    public static ModelResourceLocation snowOverlayBlock = new ModelResourceLocation(BlockRegistry.snowyBlock.getId(), "");


    public static ResourceLocation snowy_custom = EclipticSeasons.rl("block/snowy_custom");
    public static ResourceLocation stairs_top = EclipticSeasons.rl("block/stairs_top");
    public static ResourceLocation snowy_fern = EclipticSeasons.rl("block/snowy_fern");
    public static ResourceLocation snowy_grass = EclipticSeasons.rl("block/snowy_grass");
    public static ResourceLocation snowy_large_fern_bottom = EclipticSeasons.rl("block/snowy_large_fern_bottom");
    public static ResourceLocation snowy_large_fern_top = EclipticSeasons.rl("block/snowy_large_fern_top");
    public static ResourceLocation snowy_tall_grass_bottom = EclipticSeasons.rl("block/snowy_tall_grass_bottom");
    public static ResourceLocation snowy_tall_grass_top = EclipticSeasons.rl("block/snowy_tall_grass_top");
    public static ResourceLocation snowy_dandelion = EclipticSeasons.rl("block/snowy_dandelion");
    public static ResourceLocation dandelion_top = EclipticSeasons.rl("block/dandelion_top");
    public static ResourceLocation overlay_2 = EclipticSeasons.rl("block/overlay_2");
    public static ResourceLocation snow_height2 = EclipticSeasons.rl("block/snow_height2");
    public static ResourceLocation snow_height2_top = EclipticSeasons.rl("block/snow_height2_top");
    public static ResourceLocation grass_flower = EclipticSeasons.rl("block/grass_flower");

    public static ResourceLocation snow = new ResourceLocation("block/snow");
    public static ResourceLocation snow_overlay_half_left = textureRL("snow_overlay_half_left");
    public static ResourceLocation snow_overlay_half_right = textureRL("snow_overlay_half_right");
    public static ResourceLocation snow_overlay = textureRL("snow_overlay");
    public static ResourceLocation snow_overlay_leaves = textureRL("snow_overlay_leaves");
    public static ResourceLocation snow_overlay_tiny = textureRL("snow_overlay_tiny");


    public static ResourceLocation textureRL(String s) {
        return EclipticSeasons.rl("block/" + s);
    }

    public static List<ResourceLocation> flower_on_grass = List.of(1, 2, 3, 4, 5, 6).stream().map(
            i -> EclipticSeasons.rl("block/flower_%s".formatted(i))
    ).toList();


    public static ResourceLocation mrl(String s, String s2) {
        return new ModelResourceLocation(EclipticSeasons.rl(s), s2);
    }


    public static HashMap<ResourceLocation, SpriteContents> blocksCache = new HashMap<>();

    public static boolean shouldCutoutMipped(BlockState state) {
        if (ClientConfig.Renderer.snowyWinter.get()) {
            if (Minecraft.getInstance().level != null) {
                var onBlock = state.getBlock();
                if (!(onBlock instanceof FenceBlock)) {
                    if (onBlock instanceof SlabBlock ||
                            onBlock instanceof FarmBlock ||
                            onBlock instanceof DirtPathBlock ||
                            onBlock instanceof StairBlock
                            || state.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static Map<BakedModel, Integer> snowyModelsCache = new IdentityHashMap<>();


    public static BakedModel getSnowyModel(BlockState state, BlockState snowState, int flag, int offset) {
        Block onBlock = state.getBlock();
        BakedModel snowModel = null;
        if (flag == MapChecker.FLAG_BLOCK) {
            snowModel = models.get(snowOverlayBlock);
        } else if (flag == MapChecker.FLAG_LEAVES) {
            snowModel = models.get(snowOverlayLeaves);
        } else if (flag == MapChecker.FLAG_SLAB) {
            snowModel = models.get(snowySlabBottom);
        } else if (flag == MapChecker.FLAG_STAIRS_TOP) {
            snowModel = models.get(stairs_top);
        } else if (models != null && flag == MapChecker.FLAG_STAIRS) {
            if (snowState != null)
                snowModel = models.get(BlockModelShaper.stateToModelLocation(snowState));
        } else if (flag == MapChecker.FLAG_GRASS) {
            if (onBlock == Blocks.GRASS) {
                snowModel = models.get(snowy_grass);
            } else if (onBlock == Blocks.FERN) {
                snowModel = models.get(snowy_fern);
            } else if (onBlock == Blocks.DANDELION) {
                snowModel = models.get(snowy_dandelion);
            } else snowModel = models.get(snowy_grass);
        } else if (flag == MapChecker.FLAG_GRASS_LARGE) {
            if (onBlock == Blocks.TALL_GRASS) {
                snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
            } else if (onBlock == Blocks.LARGE_FERN) {
                snowModel = models.get(offset == 1 ? snowy_large_fern_bottom : snowy_large_fern_top);
            } else snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
        } else if (flag == MapChecker.FLAG_FARMLAND) {
            snowModel = models.get(snow_height2_top);
        } else if (flag == MapChecker.FLAG_CUSTOM) {
            snowModel = models.get(snowy_custom);
        }

        if (snowModel != null) {
            snowModel =
                    snowModel instanceof SnowyBakedModelWrapper<?> ?
                            (SnowyBakedModelWrapper<?>) snowModel :
                            new SnowyBakedModelWrapper<>(snowModel);
            if (snowModel instanceof SnowyBakedModelWrapper<?> snowyBakedModelWrapper
                    && SnowyBakedModelWrapper.isInvalid(snowyBakedModelWrapper))
                snowyBakedModelWrapper.updateBlockType(flag);
            snowyModelsCache.putIfAbsent(snowModel, flag);
        }
        return snowModel;
    }


    private final static List<BakedQuad> EMPTY = List.of();


    public static TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(resourceLocation);
    }

    public static List<BakedQuad> cancelTop(BakedModel bakedModel, BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> original) {

        if (bakedModel != null
                && !original.isEmpty()
                && (direction == Direction.UP || direction == null)
                && !(bakedModel instanceof SnowyBakedModelWrapper)
        ) {
            random.setSeed(seed);
            // blockAndTintGetter 现在优化以后可以用来处理了
            var snowModel = ModelManager.findModel(blockAndTintGetter, pos, state, random);

            if (snowModel instanceof SnowyBakedModelWrapper) {
                int blockType = MapChecker.getBlockType(state, blockAndTintGetter, pos);
                if (blockType == MapChecker.FLAG_CUSTOM)
                    return original;
                if (direction == Direction.UP) {
                    if (blockType == MapChecker.FLAG_BLOCK) return EMPTY;
                }


                if (original.size() == 1) {
                    if (original.get(0).getDirection() == Direction.UP) return EMPTY;
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


            int blockType = MapChecker.getBlockType(state, blockAndTintGetter, pos);
            if (blockType == MapChecker.FLAG_CUSTOM) {
                original = new ArrayList<>();
            }

            if ((blockType == MapChecker.FLAG_CUSTOM)
                // && state.toString().contains("stairs_a_cherry_blindwall")
                // &&(state.hasProperty(StairBlock.SHAPE)&& state.getValue(StairBlock.SHAPE) == StairsShape.OUTER_RIGHT)
            ) {
                // if(blockType==MapChecker.FLAG_STAIRS)
                {
                    if (blockType == MapChecker.FLAG_CUSTOM
                            || direction != null && direction.ordinal() > 1) {
                        ArrayList<BakedQuad> quadsCTM = null;

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

                        if (quadsCTM != null) {

                            boolean tooTiny = false;
                            tooTiny |= state.getBlock() instanceof FenceBlock;
                            tooTiny |= state.getBlock() instanceof FenceGateBlock;
                            tooTiny |= state.getBlock() instanceof IronBarsBlock;
                            tooTiny |= state.getBlock() instanceof StairBlock;
                            if (!tooTiny)
                                quadsCTM = QuadFixer.fixQuadCTM(quadsCTM);


                            TextureAtlasSprite snow_overlay_sprite = getSprite(snow_overlay);
                            TextureAtlasSprite snow_overlay_tiny_sprite = getSprite(snow_overlay_tiny);
                            TextureAtlasSprite snow_sprite = getSprite(snow);
                            float offset = 0.5f;
                            boolean isSlabDown = false;
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
                                        if (tooTiny)
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

    // 实际上这里之所以太慢还有个问题就是会一个方块访问七次
    public static List<BakedQuad> appendOverlay(BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> list) {
        random.setSeed(seed);
        BakedModel snowModel = ModelManager.findModel(blockAndTintGetter, pos, state, random);
        Level level = Minecraft.getInstance().level;
        if (level == null) return list;
        if (direction != Direction.DOWN
                && !list.isEmpty()
        ) {
            int flag = MapChecker.getBlockType(state, level, pos);
            if (snowyModelsCache.getOrDefault(snowModel, -1) > -1) {
                BlockState snowState = null;
                if (models != null && flag == MapChecker.FLAG_STAIRS) {
                    snowState = BlockRegistry.snowyStairs.get().defaultBlockState()
                            .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                            .setValue(StairBlock.HALF, state.getValue(StairBlock.HALF))
                            .setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
                }
                if (snowModel != null) {
                    int size = list.size();
                    var snowList = snowModel.getQuads(snowState, direction, null);
                    ArrayList<BakedQuad> newList;
                    if (flag == MapChecker.FLAG_GRASS) {
                        newList = new ArrayList<>(snowList);
                    } else if (direction == Direction.UP) {
                        newList = new ArrayList<>(size + snowList.size());
                        newList.addAll(snowList);
                    } else {
                        newList = new ArrayList<>(size + snowList.size());
                        newList.addAll(list);
                        newList.addAll(snowList);
                    }

                    if (flag == MapChecker.FLAG_FARMLAND) {
                        for (Direction direction1 : List.of(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.UP)) {
                            newList.addAll(snowModel.getQuads(null, direction1, random));
                        }
                    }
                    list = newList;
                }
            } else if (
                    state.getBlock() instanceof GrassBlock
            ) {
                if (snowModel != null) {
                    int size = list.size();
                    var snowList = snowModel.getQuads(null, direction, null);
                    ArrayList<BakedQuad> newList = new ArrayList<>(size + snowList.size());
                    newList.addAll(list);
                    newList.addAll(snowList);
                    list = newList;
                }
            }
        }
        return list;
    }

    public static BakedModel findModel(BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, RandomSource random) {
        Level level = Minecraft.getInstance().level;
        BakedModel replace = null;
        if (level == null) return replace;

        var onBlock = state.getBlock();
        int flag = MapChecker.getBlockType(state, blockAndTintGetter, pos);
        if (flag == 0)
            return replace;
        int offset = MapChecker.getSnowOffset(state, flag);

        boolean isLight = false;

        if (ClientConfig.Renderer.useVanillaCheck.get()) {
            isLight = blockAndTintGetter.getBrightness(LightLayer.BLOCK, pos.above()) >= 15;
        } else {
            // ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, pos);

            int cacheHeight = MapChecker.getHeightOrUpdate(level, pos, false);

            if (ClientConfig.Renderer.betterSnow.get()) {
                if (flag == MapChecker.FLAG_BLOCK && pos.getY() == cacheHeight - 1) {
                    BlockPos above = pos.above();
                    BlockState aboveState = blockAndTintGetter.getBlockState(above);
                    if ( MapChecker.getBlockType(aboveState, blockAndTintGetter, above) == MapChecker.FLAG_CUSTOM
                                    && !(aboveState.getBlock() instanceof SlabBlock)
                                    && !(aboveState.getBlock() instanceof StairBlock)) {
                        cacheHeight--;
                    } else {
                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            above = pos.relative(direction);
                            int neighbourHeight = MapChecker.getHeightOrUpdate(level, above, false);
                            if (neighbourHeight == pos.getY()) {
                                BlockState neighbourState = blockAndTintGetter.getBlockState(above);
                                int blockTypeFlag = MapChecker.getBlockType(neighbourState, blockAndTintGetter, above);
                                if (blockTypeFlag == MapChecker.FLAG_CUSTOM
                                        && !(aboveState.getBlock() instanceof SlabBlock)
                                        && !(aboveState.getBlock() instanceof StairBlock)) {
                                    cacheHeight = neighbourHeight;
                                    break;
                                }
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
                    && MapChecker.shouldSnowAt(level, pos.below(offset), state, random, state.getSeed(pos))) {
                // DynamicLeavesBlock

                boolean isSnowy = true;

                if (ClientConfig.Renderer.notSnowyNearGlowingBlock.get()) {
                    BlockPos above = pos.offset(0, 1 - offset, 0);
                    if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, above) >=
                            ClientConfig.Renderer.notSnowyNearGlowingBlockLevel.get()) {
                        isSnowy = false;
                    }
                }

                if (isSnowy) {
                    BlockState snowState = null;
                    if (models != null && flag == MapChecker.FLAG_STAIRS) {
                        snowState = BlockRegistry.snowyStairs.get().defaultBlockState()
                                .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                                .setValue(StairBlock.HALF, state.getValue(StairBlock.HALF))
                                .setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
                    }
                    BakedModel snowModel = getSnowyModel(state, snowState, flag, offset);

                    if (snowModel != null) {
                        replace = snowModel;
                    }
                }

            } else if (
                    ClientConfig.Renderer.flowerOnGrass.get()
                            && state.getBlock() instanceof GrassBlock
                            && random.nextInt(15) == 0
            ) {
                var solarTerm = SolarTerm.NONE;
                int weight = 100;
                solarTerm = EclipticUtil.getNowSolarTerm(level);
                weight = Math.abs(solarTerm.ordinal() - 3) + 1;
                if (solarTerm.getSeason() == Season.SPRING
                        && random.nextInt(weight * 4) == 0
                        && blockAndTintGetter.getBlockState(pos.above()).isAir()) {
                    {
                        BakedModel snowModel = models.get(flower_on_grass.get(random.nextInt(flower_on_grass.size())));
                        replace = snowModel;
                    }
                }
            }
        }
        return replace;
    }

    public static RenderType getRenderType(BlockState state) {
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

    public static boolean isModelReplaced(BlockState state) {
        return !state.blocksMotion()
                && MapChecker.getBlockType(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO) != MapChecker.FLAG_NONE;
    }

    public static void clearForRebaked(Map<ResourceLocation, BakedModel> modelRegistry) {
        ModelManager.models = modelRegistry;
        snowyModelsCache.clear();
    }
}
