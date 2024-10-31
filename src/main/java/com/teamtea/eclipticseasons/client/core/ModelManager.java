package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.misc.LazyGet;
import com.teamtea.eclipticseasons.config.ClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

// https://github.com/DoubleNegation/CompactOres/blob/1.18/src/main/java/doublenegation/mods/compactores/CompactOresResourcePack.java#L164
// 未来可以基于RepositorySource实现动态纹理生成（看情况，因为目前不需要，对内存消耗比较大）
public class ModelManager {
    public static Map<ModelResourceLocation, BakedModel> models;
    public static
    LazyGet<BakedModel> snowOverlayLeaves =
            LazyGet.of(() -> models.get(new ModelResourceLocation(EclipticSeasons.ModContents.snowyLeaves.getId(), "")));
    public static
    LazyGet<BakedModel> snowySlabBottom =
            LazyGet.of(() -> models.get(new ModelResourceLocation(EclipticSeasons.ModContents.snowySlab.getId(), "type=bottom,waterlogged=false")));
    public static
    LazyGet<BakedModel> snowOverlayBlock =
            LazyGet.of(() -> models.get(new ModelResourceLocation(EclipticSeasons.ModContents.snowyBlock.getId(), "")));
    public static
    LazyGet<BakedModel> snowModel =
            LazyGet.of(() -> models.get(new ModelResourceLocation(ResourceLocation.parse("minecraft:snow_block"), "")));

    public static ModelResourceLocation snowy_fern = mrl("block/snowy_fern");
    public static ModelResourceLocation snowy_grass = mrl("block/snowy_grass");
    public static ModelResourceLocation snowy_large_fern_bottom = mrl("block/snowy_large_fern_bottom");
    public static ModelResourceLocation snowy_large_fern_top = mrl("block/snowy_large_fern_top");
    public static ModelResourceLocation snowy_tall_grass_bottom = mrl("block/snowy_tall_grass_bottom");
    public static ModelResourceLocation snowy_tall_grass_top = mrl("block/snowy_tall_grass_top");
    public static ModelResourceLocation snowy_dandelion = mrl("block/snowy_dandelion");
    public static ModelResourceLocation dandelion_top = mrl("block/dandelion_top");
    public static ModelResourceLocation overlay_2 = mrl("block/overlay_2");
    public static ModelResourceLocation snow_height2 = mrl("block/snow_height2");
    public static ModelResourceLocation snow_height2_top = mrl("block/snow_height2_top");
    public static ModelResourceLocation grass_flower = mrl("block/grass_flower");
    public static List<ModelResourceLocation> flower_on_grass = List.of(1, 2, 3, 4, 5, 6).stream().map(
            i -> mrl("block/flower_%s".formatted(i))
    ).toList();

    public static ModelResourceLocation mrl(String s) {
        return ModelResourceLocation.standalone(EclipticSeasons.rl(s));
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

    public static BakedModel getSnowyModel(BlockState state ,BlockState snowState,int flag,int offset){
        Block onBlock=state.getBlock();
        BakedModel snowModel = null;
        if ( flag ==  MapChecker.FLAG_BLOCK) {
            snowModel = snowOverlayBlock.get();
        } else if ( flag ==  MapChecker.FLAG_LEAVES) {
            snowModel = snowOverlayLeaves.get();
        } else if ( flag ==  MapChecker.FLAG_SLAB) {
            snowModel = snowySlabBottom.get();
        } else if ( flag ==  MapChecker.FLAG_STAIRS) {
            if (snowState != null)
                snowModel = models.get(BlockModelShaper.stateToModelLocation(snowState));
        } else if (flag ==  MapChecker.FLAG_GRASS) {
            if (onBlock == Blocks.SHORT_GRASS) {
                snowModel = models.get(snowy_grass);
            } else if (onBlock == Blocks.FERN) {
                snowModel = models.get(snowy_fern);
            } else if (onBlock == Blocks.DANDELION) {
                snowModel = models.get(snowy_dandelion);
            } else snowModel = models.get(snowy_grass);
        } else if (flag == MapChecker. FLAG_GRASS_LARGE) {
            if (onBlock == Blocks.TALL_GRASS) {
                snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
            } else if (onBlock == Blocks.LARGE_FERN) {
                snowModel = models.get(offset == 1 ? snowy_large_fern_bottom : snowy_large_fern_top);
            } else snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
        } else if (flag ==  MapChecker.FLAG_FARMLAND) {
            snowModel = models.get(snow_height2_top);
            // snowModel = snowOverlayBlock.get();
        }
        return snowModel;
    }
    // 实际上这里之所以太慢还有个问题就是会一个方块访问七次
    public static List<BakedQuad> appendOverlay(BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> list) {
        // Minecraft.getInstance().level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,pos);
        // 不处理空列表，这代表着不处理这个方向
        // if (state.is(BlockTags.LEAVES) && !list.isEmpty()) {
        //     return List.of(new BakedQuadRetextured(list.get(0),
        //             ClientSetup.snowOverlayBlock.get().getQuads(null, Direction.UP, null).get(0).getSprite()));
        // }
        // if (true)return list;

        var level = Minecraft.getInstance().level;
        if (level == null) {
            EclipticSeasons.logger("NULLLLLLLL");
        }
        // if (level != null)return list;
        if (level != null
                && direction != Direction.DOWN
                && !list.isEmpty()
                && MapChecker.isValidDimension(level)
        ) {

            var onBlock = state.getBlock();

            int flag = MapChecker.getBlockType(state, level, pos);
            if (flag == 0)
                return list;

            int offset = MapChecker.getSnowOffset(state, flag);


            boolean isLight = false;

            isLight = ClientConfig.Renderer.useVanillaCheck.get() && level != null ?
                    level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos.above()) >= 15
                    : MapChecker.getHeightOrUpdate(level, pos, false) == pos.getY() - offset;


            // SimpleUtil.testTime(()->{getHeightOrUpdate(pos, false);});

            if (isLight) {
                if (ClientConfig.Renderer.snowyWinter.get() &&
                        onBlock != Blocks.SNOW_BLOCK
                        && MapChecker.shouldSnowAt(level, pos.below(offset), state, random, seed)) {
                    // DynamicLeavesBlock

                    boolean isFlowerAbove = false;
                    if ((flag == MapChecker.FLAG_BLOCK) && ClientConfig.Renderer.deeperSnow.get()) {
                        var bl = blockAndTintGetter.getBlockState(pos.above()).getBlock();
                        isFlowerAbove = bl instanceof FlowerBlock
                                || bl instanceof PinkPetalsBlock
                                || bl instanceof DoublePlantBlock
                                || bl instanceof SaplingBlock;

                        if (!isFlowerAbove) {
                            isFlowerAbove = random.nextInt(12) > 0;
                            // isFlowerAbove=true;
                        }
                    }
                    // isFlowerAbove=false;
                    var useMap = isFlowerAbove ? quadMap_1 : quadMap;
                    var cc = useMap.getOrDefault(list, null);
                    if (cc != null) {
                        return cc;
                    } else {
                        BlockState snowState = null;
                        if ( flag == MapChecker.FLAG_STAIRS) {
                            snowState = EclipticSeasons.ModContents.snowyStairs.get().defaultBlockState()
                                    .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                                    .setValue(StairBlock.HALF, state.getValue(StairBlock.HALF))
                                    .setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
                        }
                        BakedModel snowModel = getSnowyModel(state, snowState, flag, offset);

                        if (snowModel != null) {
                            int size = list.size();
                            var snowList = snowModel.getQuads(snowState, direction, random);
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
                                } else newList = new ArrayList<>(snowList);
                            } else {
                                newList = new ArrayList<BakedQuad>(size + snowList.size());
                                newList.addAll(list);
                                newList.addAll(snowList);
                            }

                            if (onBlock == Blocks.DANDELION) {
                                newList.addAll(models.get(dandelion_top).getQuads(null, null, null));
                            }

                            if (flag == MapChecker.FLAG_FARMLAND) {

                                for (Direction direction1 : List.of(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.UP)) {
                                    newList.addAll(snowModel.getQuads(null, direction1, random));
                                }
                            }

                            useMap.putIfAbsent(list, newList);
                            list = newList;


                        }
                    }
                } else if (
                        ClientConfig.Renderer.flowerOnGrass.get()
                        &&direction == Direction.UP
                        && state.getBlock() instanceof GrassBlock
                        && random.nextInt(15) == 0
                ) {
                    // var level = Minecraft.getInstance().level;
                    var solarTerm = SolarTerm.NONE;
                    int weight = 100;
                    if (level != null) {
                        solarTerm = EclipticUtil.getNowSolarTerm(level);
                        weight = Math.abs(solarTerm.ordinal() - 3) + 1;
                    }
                    if (solarTerm.getSeason() == Season.SPRING
                            && random.nextInt(weight * 4) == 0
                            && blockAndTintGetter.getBlockState(pos.above()).isAir()) {
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

}
