package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.misc.LazyGet;
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
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

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

    public static
    LazyGet<BakedModel> snowModel =
            LazyGet.of(() -> models.get(new ModelResourceLocation(ResourceLocation.parse("minecraft:snow_block"), "")));

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
        }
        if (snowModel != null) {
            snowyModelsCache.putIfAbsent(snowModel, flag);
        }
        return snowModel;
    }

    public static List<BakedQuad> appendOverlay(BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> list, BakedModel bakedModel, RenderType renderType) {
        if (renderType == RenderType.cutoutMipped()) {
            if (bakedModel.getClass() == SimpleBakedModel.class) {
                if (!ItemBlockRenderTypes.getRenderLayers(state).asList().contains(RenderType.cutoutMipped())) {
                    list = new ArrayList<>();
                }
            }
            return ModelManager.appendOverlay(blockAndTintGetter, state, pos, direction, random, seed, list);

        }
        return list;

    }

    private final static List<BakedQuad> EMPTY = List.of();

    public static List<BakedQuad> cancelTop(BakedModel bakedModel, BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> original) {

        // if (true)
        //     return original;
        if (!original.isEmpty()
                && (direction == Direction.UP || direction == null)
                && snowyModelsCache.getOrDefault(bakedModel, -1) == -1
        ) {
            BakedModel snowModel = ModelManager.findModel(blockAndTintGetter, pos, state, random);
            if (snowModel != null
                    && snowyModelsCache.getOrDefault(snowModel, -1) > MapChecker.FLAG_NONE
                    && bakedModel != null
                    && bakedModel != snowModel) {
                int blockType = MapChecker.getBlockType(state, blockAndTintGetter, pos);
                if (direction == Direction.UP) {
                    if (blockType == MapChecker.FLAG_BLOCK)
                        return EMPTY;
                }

                if (original.size() == 1) {
                    if (original.getFirst().getDirection() == Direction.UP)
                        return EMPTY;
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

        if (
                snowyModelsCache.getOrDefault(bakedModel, -1) != -1
                        && BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().startsWith("yuushya")
                        // && state.toString().contains("slab_a_green_blindwall")
                        // && state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.TOP
                         ) {
            int blockType = MapChecker.getBlockType(state, blockAndTintGetter, pos);

            // if(blockType==MapChecker.FLAG_STAIRS)
            {
                if (blockType == MapChecker.FLAG_STAIRS || (direction != null && direction.ordinal() > 1)) {

                    BakedModel bakedModelCTM = models.get(BlockModelShaper.stateToModelLocation(state));
                    if(bakedModelCTM!=null) {
                        ModelData modelDataCTM = bakedModelCTM.getModelData(blockAndTintGetter, pos, state, ModelData.EMPTY);
                        ChunkRenderTypeSet renderTypes = bakedModelCTM.getRenderTypes(state, random, modelDataCTM);
                        List<BakedQuad> quadsCTM = new ArrayList<>();
                        for (RenderType renderType : renderTypes.asList()) {
                            quadsCTM.addAll(bakedModelCTM.getQuads(state, direction, random, modelDataCTM, renderType));
                        }
                        if (quadsCTM.isEmpty())
                            return original;
                        TextureAtlasSprite sprite = original.getFirst().getSprite();
                        TextureAtlasSprite se_sprite = null;
                        if (blockType == MapChecker.FLAG_STAIRS) {
                            se_sprite = models.get(snowOverlayBlock).getParticleIcon();
                        }

                        boolean isSlabDown = blockType == MapChecker.FLAG_SLAB || blockType == MapChecker.FLAG_STAIRS;
                        original = new ArrayList<>(quadsCTM.size());
                        for (BakedQuad bakedQuad : quadsCTM) {
                            if (bakedQuad.getDirection() != Direction.DOWN) {
                                BakedQuadRetexturedAndReUV retexturedAndReUV = new BakedQuadRetexturedAndReUV(bakedQuad,
                                        se_sprite != null && blockType == MapChecker.FLAG_STAIRS && bakedQuad.getDirection() != Direction.UP ?
                                                se_sprite :
                                                sprite, isSlabDown);
                                if (retexturedAndReUV.getDirection() != Direction.DOWN) {
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


    public static List<BakedQuad> appendOverlay(BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> list) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return list;

        if (direction != Direction.DOWN
                && !list.isEmpty()
        ) {

            var onBlock = state.getBlock();
            int flag = MapChecker.getBlockType(state, level, pos);
            if (flag == 0)
                return list;
            int offset = MapChecker.getSnowOffset(state, flag);

            boolean isLight = false;

            isLight = ClientConfig.Renderer.useVanillaCheck.get() && Minecraft.getInstance().level != null ?
                    Minecraft.getInstance().level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos.above()) >= 15
                    : MapChecker.getHeightOrUpdate(level, pos, false) == pos.getY() - offset;


            // SimpleUtil.testTime(()->{getHeightOrUpdate(pos, false);});

            if (isLight) {
                if (ClientConfig.Renderer.snowyWinter.get()
                        && onBlock != Blocks.SNOW_BLOCK
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
                    List<BakedQuad> cc =
                            EclipticSeasonsMixinPlugin.isOptLoad()
                                    || list.isEmpty() ? null : useMap.getOrDefault(list, null);
                    // if ((list.isEmpty()))
                    //     cc = null;
                    if (cc != null) {
                        return cc;
                    } else {
                        BlockState snowState = null;
                        if (models != null && flag == MapChecker.FLAG_STAIRS) {
                            snowState = EclipticSeasons.ModContents.snowyStairs.get().defaultBlockState()
                                    .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                                    .setValue(StairBlock.HALF, state.getValue(StairBlock.HALF))
                                    .setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
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

                            if (!EclipticSeasonsMixinPlugin.isOptLoad())
                                useMap.putIfAbsent(list, newList);

                            list = newList;


                        }
                    }
                } else if (
                        ClientConfig.Renderer.flowerOnGrass.get()
                                && direction == Direction.UP
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
        if (flag == 0)
            return replace;
        int offset = MapChecker.getSnowOffset(state, flag);

        boolean isLight = false;

        isLight = ClientConfig.Renderer.useVanillaCheck.get() && Minecraft.getInstance().level != null ?
                Minecraft.getInstance().level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos.above()) >= 15
                : MapChecker.getHeightOrUpdate(level, pos, false) == pos.getY() - offset;


        if (isLight) {
            if (ClientConfig.Renderer.snowyWinter.get()
                    && onBlock != Blocks.SNOW_BLOCK
                    && MapChecker.shouldSnowAt(level, pos.below(offset), state, random, state.getSeed(pos))) {
                // DynamicLeavesBlock
                boolean isFlowerAbove = false;
                if ((flag == MapChecker.FLAG_BLOCK) && ClientConfig.Renderer.deeperSnow.get()) {
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
                        && level.getBlockState(pos.above()).isAir()) {
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
        if (!Minecraft.useFancyGraphics())
            return RenderType.solid();
        RenderType chunkRenderType = ItemBlockRenderTypes.getChunkRenderType(state);
        if (chunkRenderType == RenderType.translucent())
            return RenderType.translucent();
        else if (chunkRenderType == RenderType.cutout())
            return RenderType.cutout();
        return RenderType.cutoutMipped();
        // return Minecraft.useFancyGraphics() ?
        //         RenderType.cutoutMipped() : RenderType.solid();
    }

    public static boolean isModelReplaced(BlockState state) {
        return !state.blocksMotion()
                && MapChecker.getBlockType(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO) != MapChecker.FLAG_NONE;
    }

    public static void clearForRebaked(Map<ModelResourceLocation, BakedModel> modelRegistry) {
        ModelManager.models = modelRegistry;
        quadMap.clear();
        quadMap_1.clear();
        snowyModelsCache.clear();
    }
}
