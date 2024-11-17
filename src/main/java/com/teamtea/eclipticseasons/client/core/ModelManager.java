package com.teamtea.eclipticseasons.client.core;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.misc.LazyGet;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;

import com.teamtea.eclipticseasons.mixin.EclipticSeasonsMixinPlugin;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.*;

// https://github.com/DoubleNegation/CompactOres/blob/1.18/src/main/java/doublenegation/mods/compactores/CompactOresResourcePack.java#L164

// 未来可以基于RepositorySource实现动态纹理生成（看情况，因为目前不需要，对内存消耗比较大）
public class ModelManager {
    public static Map<ModelResourceLocation, BakedModel> models;

    public static ModelResourceLocation snowOverlayLeaves = new ModelResourceLocation(EclipticSeasons.ModContents.snowyLeaves.getId(), "");
    public static ModelResourceLocation snowySlabBottom = new ModelResourceLocation(EclipticSeasons.ModContents.snowySlab.getId(), "type=bottom,waterlogged=false");
    public static ModelResourceLocation snowOverlayBlock = new ModelResourceLocation(EclipticSeasons.ModContents.snowyBlock.getId(), "");

    // public static
    // LazyGet<BakedModel> snowOverlayLeaves =
    //         LazyGet.of(() -> models.get(new ModelResourceLocation(EclipticSeasons.ModContents.snowyLeaves.getId(), "")));
    // public static
    // LazyGet<BakedModel> snowySlabBottom =
    //         LazyGet.of(() -> models.get(new ModelResourceLocation(EclipticSeasons.ModContents.snowySlab.getId(), "type=bottom,waterlogged=false")));
    // public static
    // LazyGet<BakedModel> snowOverlayBlock =
    //         LazyGet.of(() -> models.get(new ModelResourceLocation(EclipticSeasons.ModContents.snowyBlock.getId(), "")));

    public static LazyGet<BakedModel> snowModel = LazyGet.of(() -> models.get(new ModelResourceLocation(ResourceLocation.parse("minecraft:snow_block"), "")));

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

    public static Map<List<BakedQuad>, List<BakedQuad>> quadMap = new IdentityHashMap<>(1024);
    public static Map<List<BakedQuad>, List<BakedQuad>> quadMap_1 = new IdentityHashMap<>(1024);
    public static Map<List<BakedQuad>, List<BakedQuad>> quadMap_GRASS = new IdentityHashMap<>(128);

    public static Map<BakedModel, Integer> snowyModelsCache = new IdentityHashMap<>();
    public static Map<BlockState, BakedModel> stateModelsCache = new IdentityHashMap<>();

    public static BakedModel getSnowyModel(BlockState state, BlockState snowState, int flag, int offset) {
        Block onBlock = state.getBlock();
        BakedModel snowModel = stateModelsCache.getOrDefault(state, null);
        if (snowModel == null) {
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
                snowModel = models.get(snowOverlayBlock);
            }
            if (snowModel != null) {
                stateModelsCache.putIfAbsent(snowState, snowModel);
            }
            if (snowModel != null) {
                snowyModelsCache.putIfAbsent(snowModel, flag);
            }
        }

        return snowModel;
    }

    private final static List<BakedQuad> EMPTY = List.of();

    public static List<BakedQuad> cancelTop(BakedModel bakedModel, BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> original) {

        // if (true)
        //     return original;
        if (!original.isEmpty() && (direction == Direction.UP || direction == null) && snowyModelsCache.getOrDefault(bakedModel, -1) == -1) {
            random.setSeed(seed);
            BakedModel snowModel = ModelManager.findModel(blockAndTintGetter, pos, state, random);
            if (snowModel != null && snowyModelsCache.getOrDefault(snowModel, -1) > MapChecker.FLAG_NONE && bakedModel != null && bakedModel != snowModel) {
                int blockType = MapChecker.getBlockType(state, blockAndTintGetter, pos);
                if (blockType == MapChecker.FLAG_CUSTOM)
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


        // if (CompatModule.isCTMLoad())
        if (snowyModelsCache.getOrDefault(bakedModel, -1) != -1) {
            // if (true) {
            //     if (original.size() == 1) {
            //         BakedQuad first = original.getFirst();
            //         first=new BakedQuadRetexturedAndOffset(first,13/16f,4/16f);
            //         return List.of(first);
            //     }
            // }
            int blockType = MapChecker.getBlockType(state, blockAndTintGetter, pos);
            if (blockType == MapChecker.FLAG_CUSTOM) {
                original = new ArrayList<>();
            }

            boolean yuushyaBlock = CompatModule.isCTMLoad() && BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().startsWith("yuushya");
            if ((blockType == MapChecker.FLAG_CUSTOM || yuushyaBlock)
                // && state.toString().contains("stairs_a_cherry_blindwall")
                // &&(state.hasProperty(StairBlock.SHAPE)&& state.getValue(StairBlock.SHAPE) == StairsShape.OUTER_RIGHT)
            ) {
                // 也许我们不需要这个，但是这样比较合适
                if (yuushyaBlock && blockType == MapChecker.FLAG_STAIRS_TOP && state.getBlock() instanceof StairBlock)
                    return original;
                // if(blockType==MapChecker.FLAG_STAIRS)
                {
                    if (blockType == MapChecker.FLAG_CUSTOM || blockType == MapChecker.FLAG_STAIRS || (direction != null && direction.ordinal() > 1)) {
                        BakedModel bakedModelCTM = models.get(BlockModelShaper.stateToModelLocation(state));
                        if (bakedModelCTM != null) {
                            ModelData modelDataCTM = bakedModelCTM.getModelData(blockAndTintGetter, pos, state, ModelData.EMPTY);
                            random.setSeed(seed);
                            ChunkRenderTypeSet renderTypes = bakedModelCTM.getRenderTypes(state, random, modelDataCTM);
                            ArrayList<BakedQuad> quadsCTM = new ArrayList<>();
                            for (RenderType renderType : renderTypes.asList()) {
                                random.setSeed(seed);
                                quadsCTM.addAll(bakedModelCTM.getQuads(state, direction, random, modelDataCTM, renderType));
                            }

                            boolean tooTiny = false;
                            tooTiny |= state.getBlock() instanceof FenceBlock;
                            tooTiny |= state.getBlock() instanceof FenceGateBlock;
                            tooTiny |= state.getBlock() instanceof IronBarsBlock;
                            if (!tooTiny)
                                quadsCTM = fixQuadCTM(quadsCTM);


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
                                    // float[] ys = new float[4];
                                    // float[] zs = new float[4];
                                    // float[] xs = new float[4];
                                    // float[] posx = new float[12];
                                    // boolean isTop = false;
                                    // for (int i = 0; i < 4; i++) {
                                    //     int j = BakedQuadRetexturedAndReUV.verticeSpace * i;
                                    //     xs[i] = Float.intBitsToFloat(bakedQuad.getVertices()[j]);
                                    //     ys[i] = Float.intBitsToFloat(bakedQuad.getVertices()[j + 1]);
                                    //     zs[i] = Float.intBitsToFloat(bakedQuad.getVertices()[j + 2]);
                                    //     posx[3 * i] = xs[i];
                                    //     posx[3 * i + 1] = ys[i];
                                    //     posx[3 * i + 2] = zs[i];
                                    //     if (ys[i] > 0.5f) {
                                    //         isTop = true;
                                    //         break;
                                    //     }
                                    // }
                                    //
                                    // if (!isTop) {
                                    //     // isSlabDown = true;
                                    // }
                                    // if (blockType == MapChecker.FLAG_STAIRS) {
                                    //     {
                                    //         if (bakedQuadDirection != Direction.UP) {
                                    //             float maxY = 0;
                                    //             for (float y : ys) {
                                    //                 maxY = Math.max(maxY, y);
                                    //             }
                                    //             float maxZ = 0;
                                    //             for (float z : zs) {
                                    //                 maxZ = Math.max(maxZ, z);
                                    //             }
                                    //             float maxX = 0;
                                    //             for (float x : xs) {
                                    //                 maxX = Math.max(maxX, x);
                                    //             }
                                    //             Direction face = state.getValue(StairBlock.FACING);
                                    //
                                    //             if (state.getValue(StairBlock.SHAPE) == StairsShape.STRAIGHT) {
                                    //                 if (bakedQuadDirection.getOpposite() == face) {
                                    //                     if (maxY < 0.50001f) {
                                    //                         isSlabDown = true;
                                    //                     } else {
                                    //                         offset = 0;
                                    //                     }
                                    //                 } else if (bakedQuadDirection == face) {
                                    //                     if (maxY < 0.50001f) {
                                    //                         continue;
                                    //                     } else {
                                    //                         offset = 0;
                                    //                     }
                                    //                 } else if (bakedQuadDirection.getClockWise() == face || bakedQuadDirection.getCounterClockWise() == face) {
                                    //                     if (maxY < 0.50001f) {
                                    //                         switch (face) {
                                    //                             case WEST -> {
                                    //                                 if (maxX > 0.50001f) isSlabDown = true;
                                    //                                 else continue;
                                    //                             }
                                    //                             case EAST -> {
                                    //                                 if (maxX < 0.50001f) isSlabDown = true;
                                    //                                 else continue;
                                    //                             }
                                    //                             case NORTH -> {
                                    //                                 if (maxZ > 0.50001f) isSlabDown = true;
                                    //                                 else continue;
                                    //                             }
                                    //                             case SOUTH -> {
                                    //                                 if (maxZ < 0.50001f) isSlabDown = true;
                                    //                                 else continue;
                                    //                             }
                                    //                         }
                                    //                     } else {
                                    //                         offset = 0;
                                    //                     }
                                    //                 } else {
                                    //                     continue;
                                    //                 }
                                    //             } else {
                                    //                 if (maxY < 0.50001f) {
                                    //                     isSlabDown = true;
                                    //                 } else {
                                    //                     offset = 0;
                                    //                     spriteUse = snow_sprite;
                                    //                 }
                                    //             }
                                    //
                                    //         }
                                    //     }
                                    // }
                                    // if (blockType == MapChecker.FLAG_CUSTOM)
                                    {
                                        if (bakedQuadDirection != Direction.UP) {
                                            isSlabDown = true;
                                            float maxY = getMaxY(bakedQuad);
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
                                            spriteUse = getMaxY(bakedQuad) - getMinY(bakedQuad) > 0.4002f ? snow_overlay_sprite : snow_overlay_tiny_sprite;
                                    }
                                    BakedQuadRetexturedAndReUV retexturedAndReUV = new BakedQuadRetexturedAndReUV(bakedQuad, spriteUse, isSlabDown, offset);
                                    original.add(retexturedAndReUV);
                                }
                            }

                        }
                    }
                }
            }
        }
        return original;
    }

    public static int posIndex = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.POSITION) / 4;
    public static int vertice_Space = DefaultVertexFormat.BLOCK.getVertexSize() / 4;

    public static float getMinValue(int[] vertices, int index) {
        float minV = 1;
        for (int i = 0; i < 4; i++) {
            int j = vertice_Space * i;
            float v = Float.intBitsToFloat(vertices[j + posIndex + index]);
            if (v < minV) minV = v;
        }
        float epsilon = 1e-7f;
        return Math.abs(minV) < epsilon ? 0.0f : minV;
    }

    public static float getMaxValue(int[] vertices, int index) {
        float maxV = -1;
        for (int i = 0; i < 4; i++) {
            int j = vertice_Space * i;
            float v = Float.intBitsToFloat(vertices[j + posIndex + index]);
            if (v > maxV) maxV = v;
        }
        return maxV;
    }

    public static float getMaxX(BakedQuad bakedQuad) {
        return getMaxValue(bakedQuad.getVertices(), 0);
    }

    public static float getMaxY(BakedQuad bakedQuad) {
        return getMaxValue(bakedQuad.getVertices(), 1);
    }

    public static float getMaxZ(BakedQuad bakedQuad) {
        return getMaxValue(bakedQuad.getVertices(), 2);
    }

    public static float getMinX(BakedQuad bakedQuad) {
        return getMinValue(bakedQuad.getVertices(), 0);
    }

    public static float getMinY(BakedQuad bakedQuad) {
        return getMinValue(bakedQuad.getVertices(), 1);
    }

    public static float getMinZ(BakedQuad bakedQuad) {
        return getMinValue(bakedQuad.getVertices(), 2);
    }

    public static boolean cover(BakedQuad bakedQuad, BakedQuad testQuad) {


        float x0 = getMinX(bakedQuad);
        float x1 = getMaxX(bakedQuad);
        float x2 = getMinX(testQuad);
        float x3 = getMaxX(testQuad);

        float y0 = getMinY(bakedQuad);
        float y1 = getMaxY(bakedQuad);
        float y2 = getMinY(testQuad);
        float y3 = getMaxY(testQuad);

        float z0 = getMinZ(bakedQuad);
        float z1 = getMaxZ(bakedQuad);
        float z2 = getMinZ(testQuad);
        float z3 = getMaxZ(testQuad);

        // TODO: CTM would bring some invalid quad
        boolean result = (x0 == x1 ? 1 : 0) + (y0 == y1 ? 1 : 0) + (z0 == z1 ? 1 : 0) >= 2;
        if (result) return false;

        if (bakedQuad.getDirection() == Direction.UP) {
            if (y0 > y3)
                if ((getMaxX(bakedQuad) >= getMaxX(testQuad) && getMinX(bakedQuad) <= getMinX(testQuad))
                        && getMaxZ(bakedQuad) >= getMaxZ(testQuad) && getMinZ(bakedQuad) <= getMinZ(testQuad))
                    return true;
        } else if (bakedQuad.getDirection() == testQuad.getDirection()) {
            // 平行x轴

            if (x0 == x1 && x2 == x3 && x0 == x2) {
                if (z0 <= z2 && z1 >= z3)
                    return y1 > y3;

            } else if (z0 == z1 && z2 == z3 && z0 == z2) {
                if (x0 <= x2 && x1 >= x3)
                    return y1 > y3;
            }


        }


        return false;
    }

    public static ArrayList<BakedQuad> fixQuadCTM(ArrayList<BakedQuad> quadsCTM) {
        quadsCTM.removeIf(bakedQuad -> bakedQuad.getDirection() == Direction.DOWN);
        quadsCTM.removeIf(bakedQuad -> bakedQuad.getSprite().contents().name().getPath().contains("grape_small_leaves"));
        quadsCTM.removeIf(bakedQuad -> bakedQuad.getSprite().contents().name().getPath().contains("grape_stage"));

        quadsCTM.sort(Comparator.comparingDouble(b -> getMaxY(((BakedQuad) b))).reversed());

        ArrayList<BakedQuad> visibleFaces = new ArrayList<>();
        for (int i = 0; i < quadsCTM.size(); i++) {
            BakedQuad faceA = quadsCTM.get(i);
            boolean isCovered = false;

            for (int j = 0; j < i; j++) {
                BakedQuad faceB = quadsCTM.get(j);
                if (cover(faceB, faceA)) {
                    isCovered = true;
                    break;
                }
            }
            if (!isCovered) {
                visibleFaces.add(faceA);
            }
        }
        return visibleFaces;
    }


    public static List<BakedQuad> appendOverlay(BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> list) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return list;

        if (direction != Direction.DOWN && !list.isEmpty()) {

            var onBlock = state.getBlock();
            int flag = MapChecker.getBlockType(state, level, pos);
            if (flag == 0) return list;
            int offset = MapChecker.getSnowOffset(state, flag);

            boolean isLight = false;

            isLight = ClientConfig.Renderer.useVanillaCheck.get() && Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos.above()) >= 15 : MapChecker.getHeightOrUpdate(level, pos, false) == pos.getY() - offset;


            // SimpleUtil.testTime(()->{getHeightOrUpdate(pos, false);});

            if (isLight) {
                if (ClientConfig.Renderer.snowyWinter.get() && onBlock != Blocks.SNOW_BLOCK && MapChecker.shouldSnowAt(level, pos.below(offset), state, random, seed)) {
                    // DynamicLeavesBlock
                    boolean isFlowerAbove = false;
                    if ((flag == MapChecker.FLAG_BLOCK) && ClientConfig.Renderer.betterSnow.get()) {
                        var bl = blockAndTintGetter.getBlockState(pos.above()).getBlock();
                        isFlowerAbove = bl instanceof FlowerBlock || bl instanceof PinkPetalsBlock || bl instanceof DoublePlantBlock || bl instanceof SaplingBlock;

                        if (!isFlowerAbove) {
                            isFlowerAbove = random.nextInt(12) > 0;
                            // isFlowerAbove=true;
                        }
                    }
                    // isFlowerAbove=false;
                    var useMap = isFlowerAbove ? quadMap_1 : quadMap;
                    List<BakedQuad> cc = EclipticSeasonsMixinPlugin.isOptLoad() || list.isEmpty() ? null : useMap.getOrDefault(list, null);
                    // if ((list.isEmpty()))
                    //     cc = null;
                    if (cc != null) {
                        return cc;
                    } else {
                        BlockState snowState = null;
                        if (models != null && flag == MapChecker.FLAG_STAIRS) {
                            snowState = EclipticSeasons.ModContents.snowyStairs.get().defaultBlockState().setValue(StairBlock.FACING, state.getValue(StairBlock.FACING)).setValue(StairBlock.HALF, state.getValue(StairBlock.HALF)).setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
                        }
                        BakedModel snowModel = getSnowyModel(state, snowState, flag, offset);

                        if (snowModel != null) {
                            int size = list.size();
                            var snowList = snowModel.getQuads(snowState, direction, null);
                            ArrayList<BakedQuad> newList;
                            if (flag == MapChecker.FLAG_GRASS) {
                                newList = new ArrayList<>(snowList);
                            } else if (direction == Direction.UP) {
                                if (isFlowerAbove) {
                                    newList = new ArrayList<>();
                                    var layerState = Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1);
                                    var layerBlock = models.get(BlockModelShaper.stateToModelLocation(layerState));
                                    layerBlock = models.get(snow_height2);

                                    for (Direction direction1 : List.of(Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.UP)) {
                                        newList.addAll(layerBlock.getQuads(layerState, direction1, random));
                                    }
                                } else {
                                    newList = new ArrayList<BakedQuad>(size + snowList.size());
                                    // newList.addAll(list);
                                    newList.addAll(snowList);
                                }
                                // else newList = new ArrayList<>(snowList);
                            } else {
                                newList = new ArrayList<BakedQuad>(size + snowList.size());
                                newList.addAll(list);
                                newList.addAll(snowList);
                            }


                            if (flag == MapChecker.FLAG_FARMLAND) {

                                for (Direction direction1 : List.of(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.UP)) {
                                    newList.addAll(snowModel.getQuads(null, direction1, random));
                                }
                            }

                            if (!EclipticSeasonsMixinPlugin.isOptLoad()) useMap.putIfAbsent(list, newList);

                            list = newList;


                        }
                    }
                } else if (ClientConfig.Renderer.flowerOnGrass.get() && direction == Direction.UP && state.getBlock() instanceof GrassBlock && random.nextInt(15) == 0) {
                    var solarTerm = SolarTerm.NONE;
                    int weight = 100;
                    solarTerm = EclipticUtil.getNowSolarTerm(level);
                    weight = Math.abs(solarTerm.ordinal() - 3) + 1;
                    if (solarTerm.getSeason() == Season.SPRING && random.nextInt(weight * 4) == 0 && blockAndTintGetter.getBlockState(pos.above()).isAir()) {
                        var cc = quadMap_GRASS.getOrDefault(list, null);
                        if (cc != null) {
                            return cc;
                        } else {
                            // BakedModel snowModel = models.get(grass_flower);
                            BakedModel snowModel = models.get(flower_on_grass.get(random.nextInt(flower_on_grass.size())));
                            if (snowModel != null) {
                                int size = list.size();
                                var snowList = snowModel.getQuads(null, direction, null);
                                ArrayList<BakedQuad> newList;
                                newList = new ArrayList<>(size + snowList.size());
                                newList.addAll(list);
                                newList.addAll(snowList);
                                quadMap_GRASS.putIfAbsent(list, newList);
                                list = newList;
                            }
                        }
                    }
                }
            }


        }
        return list;
    }

    public static BakedModel findModel(BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, RandomSource random) {
        Level level = Minecraft.getInstance().level;
        BakedModel replace = null;
        // 这里不需要担心，是因为我们给不符合要求的level默认返回一个0或者最低值-1
        if (level == null) return replace;


        var onBlock = state.getBlock();
        int flag = MapChecker.getBlockType(state, level, pos);
        if (flag == 0) return replace;
        int offset = MapChecker.getSnowOffset(state, flag);

        boolean isLight = false;

        if (ClientConfig.Renderer.useVanillaCheck.get()) {
            isLight = level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos.above()) >= 15;
        } else {
            // ChunkInfoMap chunkMap = MapChecker.getChunkMap(level, pos);
            int cacheHeight = MapChecker.getHeightOrUpdate(level, pos, false);
            if (ClientConfig.Renderer.betterSnow.get()) {
                if (flag == MapChecker.FLAG_BLOCK && pos.getY() == cacheHeight - 1) {
                    if (MapChecker.getBlockType(blockAndTintGetter.getBlockState(pos.above()), blockAndTintGetter, pos.above()) == MapChecker.FLAG_CUSTOM) {
                        cacheHeight--;
                    } else {
                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            int neighbourHeight = MapChecker.getHeight(level, pos.relative(direction));
                            if (neighbourHeight == pos.getY() && MapChecker.getBlockType(blockAndTintGetter.getBlockState(pos.above()), blockAndTintGetter, pos.above()) != MapChecker.FLAG_BLOCK) {
                                cacheHeight = neighbourHeight;
                                break;
                            }
                        }
                    }
                }
            }
            isLight = cacheHeight == pos.getY() - offset;
        }

        if (ClientConfig.Renderer.notSnowyNearGlowingBlock.get()) {
            if (isLight) {
                BlockPos above = pos.offset(0, 1 - offset, 0);
                if (level.getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(above) >=
                        ClientConfig.Renderer.notSnowyNearGlowingBlockLevel.getAsInt())
                    isLight = false;
            }
        }

        if (isLight) {
            if (ClientConfig.Renderer.snowyWinter.get()
                    && onBlock != Blocks.SNOW_BLOCK
                    && MapChecker.shouldSnowAt(level, pos.below(offset), state, random, state.getSeed(pos))) {
                // DynamicLeavesBlock
                boolean isFlowerAbove = false;
                if ((flag == MapChecker.FLAG_BLOCK) && ClientConfig.Renderer.betterSnow.get()) {
                    var bl = level.getBlockState(pos.above()).getBlock();
                    isFlowerAbove = bl instanceof FlowerBlock
                            || bl instanceof PinkPetalsBlock
                            || bl instanceof DoublePlantBlock
                            || bl instanceof SaplingBlock;

                    if (!isFlowerAbove) {
                        isFlowerAbove = random.nextInt(12) > 0;
                        // isFlowerAbove=true;
                    }
                }
                {
                    BlockState snowState = null;
                    if (models != null && flag == MapChecker.FLAG_STAIRS) {
                        snowState = EclipticSeasons.ModContents.snowyStairs.get().defaultBlockState().setValue(StairBlock.FACING, state.getValue(StairBlock.FACING)).setValue(StairBlock.HALF, state.getValue(StairBlock.HALF)).setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
                    }
                    BakedModel snowModel = getSnowyModel(state, snowState, flag, offset);

                    if (snowModel != null) {
                        replace = snowModel;
                    }
                }
            } else if (ClientConfig.Renderer.flowerOnGrass.get() && state.getBlock() instanceof GrassBlock && random.nextInt(15) == 0) {
                var solarTerm = SolarTerm.NONE;
                int weight = 100;
                solarTerm = EclipticUtil.getNowSolarTerm(level);
                weight = Math.abs(solarTerm.ordinal() - 3) + 1;
                if (solarTerm.getSeason() == Season.SPRING && random.nextInt(weight * 4) == 0 && level.getBlockState(pos.above()).isAir()) {
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
        RenderType chunkRenderType = ItemBlockRenderTypes.getChunkRenderType(state);
        if (chunkRenderType == RenderType.translucent()) return RenderType.translucent();
        else if (chunkRenderType == RenderType.cutout()) return RenderType.cutout();
        return RenderType.cutoutMipped();
        // return Minecraft.useFancyGraphics() ?
        //         RenderType.cutoutMipped() : RenderType.solid();
    }

    // TODO: Note some block may be not motion for some state
    public static boolean isModelReplaced(BlockState state) {
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

    public static void clearForRebaked(Map<ModelResourceLocation, BakedModel> modelRegistry) {
        ModelManager.models = modelRegistry;
        quadMap.clear();
        quadMap_1.clear();
        snowyModelsCache.clear();
        stateModelsCache.clear();
    }
}
