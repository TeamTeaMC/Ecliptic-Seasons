package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.ClientConfig;

import com.teamtea.eclipticseasons.mixin.EclipticSeasonsMixinPlugin;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// https://github.com/DoubleNegation/CompactOres/blob/1.18/src/main/java/doublenegation/mods/compactores/CompactOresResourcePack.java#L164
// 未来可以基于RepositorySource实现动态纹理生成（看情况，因为目前不需要，对内存消耗比较大）
public class ModelManager {
    public static Map<ResourceLocation, IBakedModel> models;
    public static ModelResourceLocation snowOverlayLeaves = new ModelResourceLocation(BlockRegistry.snowyLeaves.getId(), "");
    public static ModelResourceLocation snowySlabBottom = new ModelResourceLocation(BlockRegistry.snowySlab.getId(), "type=bottom,waterlogged=false");
    public static ModelResourceLocation snowOverlayBlock = new ModelResourceLocation(BlockRegistry.snowyBlock.getId(), "");

    public static ResourceLocation snowy_fern = EclipticSeasons.rl("block/snowy_fern");
    public static ResourceLocation snowy_grass = EclipticSeasons.rl("block/snowy_grass");
    public static ResourceLocation snowy_large_fern_bottom = EclipticSeasons.rl("block/snowy_large_fern_bottom");
    public static ResourceLocation snowy_large_fern_top = EclipticSeasons.rl("block/snowy_large_fern_top");
    public static ResourceLocation snowy_tall_grass_bottom = EclipticSeasons.rl("block/snowy_tall_grass_bottom");
    public static ResourceLocation snowy_tall_grass_top = EclipticSeasons.rl("block/snowy_tall_grass_top");
    public static ResourceLocation overlay_2 = EclipticSeasons.rl("block/overlay_2");
    public static ResourceLocation snow_height2 = EclipticSeasons.rl("block/snow_height2");
    public static ResourceLocation snow_height2_top = EclipticSeasons.rl("block/snow_height2_top");
    public static ResourceLocation grass_flower = EclipticSeasons.rl("block/grass_flower");
    public static ResourceLocation butterfly1 = EclipticSeasons.rl("block/butterfly_blue");
    public static ResourceLocation butterfly2 = EclipticSeasons.rl("block/butterfly_magenta");
    public static ResourceLocation butterfly3 = EclipticSeasons.rl("block/butterfly_red");

    public static ResourceLocation mrl(String s) {
        return mrl(s, "");
    }

    public static ResourceLocation mrl(String s, String s2) {
        return new ModelResourceLocation(EclipticSeasons.rl(s), s2);
    }

    public static ResourceLocation vrl(String s, String s2) {
        return new ModelResourceLocation(new ResourceLocation(s), s2);
    }

    public static final int ChunkSize = 16 * 32;
    public static final int ChunkSizeLoc = ChunkSize - 1;
    public static final int ChunkSizeAxis = 4 + 5;

    public static boolean snowOverlayCanSurviveOn(BlockState state) {
        return !state.is(EclipticBlockTags.SNOW_OVERLAY_CANNOT_SURVIVE_ON);
    }

    public static boolean shouldCutoutMipped(BlockState state) {
        if (Minecraft.getInstance().level != null) {
            Block onBlock = state.getBlock();
            if (!(onBlock instanceof FenceBlock)) {
                if (onBlock instanceof SlabBlock
                        || onBlock instanceof FarmlandBlock
                        || onBlock instanceof GrassPathBlock
                        || onBlock instanceof StairsBlock
                        || state.isSolidRender(EmptyBlockReader.INSTANCE, BlockPos.ZERO)) {
                    return snowOverlayCanSurviveOn(state);
                }
            }
        }
        return false;
    }

    public static void clearHeightMap() {
        updateLock = true;
        synchronized (ModelManager.RegionList) {
            ModelManager.RegionList.clear();
        }
        updateLock = false;
    }

    public static class ChunkHeightMap {
        private final int[][] matrix = new int[ChunkSize][ChunkSize];
        private final Object[][] lockArray = new Object[ChunkSize][ChunkSize];
        private final int x;
        private final int z;

        public ChunkHeightMap(int x, int z) {
            this.x = x;
            this.z = z;
            EclipticSeasons.logger(String.format("Create new Height Map with [%s, %s]", x, z));
            for (int i = 0; i < ChunkSize; i++) {
                for (int j = 0; j < ChunkSize; j++) {
                    matrix[i][j] = Integer.MIN_VALUE;
                    lockArray[i][j] = new Object();
                }
            }
            EclipticSeasons.logger(String.format("End create [%s, %s]", x, z));
        }

        public int getHeight(int x, int z) {
            x = getChunkValue(x);
            z = getChunkValue(z);
            return matrix[x][z];
        }

        public int getHeight(BlockPos pos) {
            return getHeight(pos.getX(), pos.getZ());
        }

        public int updateHeight(int x, int z, int y) {
            x = getChunkValue(x);
            z = getChunkValue(z);
            int old;
            synchronized (lockArray[x][z]) {
                old = matrix[x][z];
                matrix[x][z] = y;
            }
            return old;
        }

        public int updateHeight(BlockPos pos, int y) {
            return updateHeight(pos.getX(), pos.getZ(), y);
        }
    }

    // 获取chunk内部位置
    public static int getChunkValue(int i) {
        return i & (ChunkSizeLoc);
    }

    // 获取chunk位置
    public static int blockToSectionCoord(int i) {
        return i >> ChunkSizeAxis;
    }

    public static final ArrayList<ChunkHeightMap> RegionList = new ArrayList<>(4);
    // IdentityHashMap似乎不适合Opt
    public static Map<List<BakedQuad>, List<BakedQuad>> quadMap = new HashMap<>(1024);
    public static Map<List<BakedQuad>, List<BakedQuad>> quadMap_1 = new HashMap<>(1024);
    public static Map<List<BakedQuad>, List<BakedQuad>> quadMap_GRASS = new HashMap<>(128);

    private static boolean updateLock;

    public static int getHeightOrUpdate(BlockPos pos, boolean shouldUpdate) {
        if (ClientConfig.Renderer.useVanillaCheck.get())
            return Integer.MIN_VALUE;
        int x = blockToSectionCoord(pos.getX());
        int z = blockToSectionCoord(pos.getZ());
        ChunkPos chunkPos = new ChunkPos(x, z);
        // var map = ChunkMap.getOrDefault(chunkPos, null);

        ChunkHeightMap map = null;
        // try{
        while (updateLock) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        for (int i = 0; i < RegionList.size(); i++) {
            ChunkHeightMap chunkHeightMap = RegionList.get(i);
            if (chunkHeightMap.x == x && chunkHeightMap.z == z) {
                map = chunkHeightMap;
                break;
            }
        }


        int h = 0;
        if (map != null) {
            h = map.getHeight(pos);
            if (h == Integer.MIN_VALUE || shouldUpdate) {
                int rh = Minecraft.getInstance().level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, pos).getY() - 1;
                if (ClientConfig.Renderer.underSnow.get()) {
                    BlockPos.Mutable rmPos = new BlockPos.Mutable(pos.getX(), rh, pos.getZ());
                    BlockState s = Minecraft.getInstance().level.getBlockState(rmPos);
                    while ((!(s.getBlock() instanceof LeavesBlock) && !s.isFaceSturdy(Minecraft.getInstance().level, rmPos, Direction.DOWN))) {
                        rh--;
                        s = Minecraft.getInstance().level.getBlockState(new BlockPos(pos.getX(), rh, pos.getZ()));
                        rmPos.move(Direction.DOWN, 1);
                    }
                }
                map.updateHeight(pos, rh);
                h = rh;
            }
            // return h;
        } else {

            updateLock = true;
            // ChunkMap.put(chunkPos, map);
            synchronized (RegionList) {
                boolean hasBuild = false;
                for (ChunkHeightMap chunkHeightMap : RegionList) {
                    if (chunkHeightMap.x == x && chunkHeightMap.z == z) {
                        hasBuild = true;
                        map = chunkHeightMap;
                        break;
                    }
                }
                if (!hasBuild) {
                    map = new ChunkHeightMap(x, z);
                    RegionList.add(map);
                }
            }
            updateLock = false;

            h = Minecraft.getInstance().level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, pos).getY() - 1;
            map.updateHeight(pos, h);
            // h = getHeightOrUpdate(pos, false);
            // return h;

        }
        return h;
    }


    public static final int FLAG_BLOCK = 1;
    public static final int FLAG_SLAB = 2;
    public static final int FLAG_STAIRS = 3;
    public static final int FLAG_LEAVES = 4;
    public static final int FLAG_GRASS = 5;
    public static final int FLAG_GRASS_LARGE = 501;
    public static final int FLAG_FARMLAND = 6;
    public static final List<Block> LowerPlant = Stream.of(Blocks.GRASS, Blocks.FERN).collect(Collectors.toList());
    public static final List<Block> LARGE_GRASS = Stream.of(Blocks.TALL_GRASS, Blocks.LARGE_FERN).collect(Collectors.toList());

    public static List<BakedQuad> appendOverlay(IBlockDisplayReader blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, Random random, long seed, List<BakedQuad> original) {
        if (!MapChecker.isValidDimension(Minecraft.getInstance().level)) {
            return original;
        }

        if (ClientConfig.Renderer.snowyWinter.get()
                && direction != Direction.DOWN
                && !original.isEmpty()) {

            Block onBlock = state.getBlock();
            int flag = 0;
            if (onBlock instanceof LeavesBlock) {
                flag = FLAG_LEAVES;
            } else if ((state.isSolidRender(blockAndTintGetter, pos)
                    // state.isSolid()
                    || onBlock instanceof LeavesBlock
                    || (onBlock instanceof SlabBlock && state.getValue(SlabBlock.TYPE) == SlabType.TOP)
                    || (onBlock instanceof StairsBlock && state.getValue(StairsBlock.HALF) == Half.TOP))) {
                flag = FLAG_BLOCK;
            } else if (onBlock instanceof SlabBlock) {
                flag = FLAG_SLAB;
            } else if (onBlock instanceof StairsBlock) {
                flag = FLAG_STAIRS;
            } else if (LowerPlant.contains(onBlock)) {
                flag = FLAG_GRASS;
            } else if (LARGE_GRASS.contains(onBlock)) {
                flag = FLAG_GRASS_LARGE;
            } else if ((onBlock instanceof FarmlandBlock || onBlock instanceof GrassPathBlock) && direction == null) {
                flag = FLAG_FARMLAND;
            } else return original;

            int offset = 0;
            if (flag == FLAG_GRASS || flag == FLAG_GRASS_LARGE) {
                if (flag == FLAG_GRASS) {
                    offset = 1;
                } else if (flag == FLAG_GRASS_LARGE) {
                    if (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                        offset = 1;
                    } else {
                        offset = 2;
                    }
                }
            }


            boolean isLight = false;

            isLight = ClientConfig.Renderer.useVanillaCheck.get() && Minecraft.getInstance().level != null ?
                    Minecraft.getInstance().level.getLightEngine().getLayerListener(LightType.SKY).getLightValue(pos.above()) >= 15
                    : getHeightOrUpdate(pos, false) == pos.getY() - offset;


            // SimpleUtil.testTime(()->{getHeightOrUpdate(pos, false);});

            if (isLight) {
                if (onBlock != Blocks.SNOW_BLOCK
                        && shouldSnowAt(blockAndTintGetter, pos.below(offset), state, random, seed)) {
                    // DynamicLeavesBlock
                    boolean isFlowerAbove = false;
                    if ((flag == FLAG_BLOCK) && ClientConfig.Renderer.deeperSnow.get()) {
                        Block bl = blockAndTintGetter.getBlockState(pos.above()).getBlock();
                        isFlowerAbove = bl instanceof FlowerBlock
                                // || bl instanceof PinkPetalsBlock
                                || bl instanceof DoublePlantBlock
                                || bl instanceof SaplingBlock;

                        if (!isFlowerAbove) {
                            isFlowerAbove = random.nextInt(12) > 0;
                            // isFlowerAbove=true;
                        }
                    }
                    // isFlowerAbove=false;
                    Map<List<BakedQuad>, List<BakedQuad>> useMap = isFlowerAbove ? quadMap_1 : quadMap;
                    List<BakedQuad> cc =
                            EclipticSeasonsMixinPlugin.isOptLoad() ? null : useMap.getOrDefault(original, null);
                    if (cc != null) {
                        return cc;
                    } else {
                        IBakedModel snowModel = null;
                        BlockState snowState = null;
                        if (flag == FLAG_BLOCK) {
                            // snowModel = !isFlowerAbove ? snowOverlayBlock.resolve().get() : models.get(overlay_2);
                            snowModel = models.get(snowOverlayBlock);
                        } else if (flag == FLAG_LEAVES) {
                            snowModel = models.get(snowOverlayLeaves);
                        } else if (flag == FLAG_SLAB) {
                            snowModel = models.get(snowySlabBottom);
                        } else if (models != null && flag == FLAG_STAIRS) {
                            snowState = BlockRegistry.snowyStairs.get().defaultBlockState()
                                    .setValue(StairsBlock.FACING, state.getValue(StairsBlock.FACING))
                                    .setValue(StairsBlock.HALF, state.getValue(StairsBlock.HALF))
                                    .setValue(StairsBlock.SHAPE, state.getValue(StairsBlock.SHAPE));
                            // 楼梯的方向是无
                            snowModel = models.get(BlockModelShapes.stateToModelLocation(snowState));
                        } else if (flag == FLAG_GRASS) {
                            if (onBlock == Blocks.GRASS) {
                                snowModel = models.get(snowy_grass);
                            } else if (onBlock == Blocks.FERN) {
                                snowModel = models.get(snowy_fern);
                            } else snowModel = models.get(snowy_grass);
                        } else if (flag == FLAG_GRASS_LARGE) {
                            if (onBlock == Blocks.TALL_GRASS) {
                                snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                            } else if (onBlock == Blocks.LARGE_FERN) {
                                snowModel = models.get(offset == 1 ? snowy_large_fern_bottom : snowy_large_fern_top);
                            } else snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                        } else if (flag == FLAG_FARMLAND) {
                            snowModel = models.get(snow_height2_top);
                            // snowModel = snowOverlayBlock.resolve().get();
                        }

                        if (snowModel != null) {
                            int size = original.size();
                            List<BakedQuad> snowList = snowModel.getQuads(snowState, direction, null);
                            ArrayList<BakedQuad> newList;
                            if (flag == FLAG_GRASS) {
                                newList = new ArrayList<>(snowList);
                            } else if (direction == Direction.UP) {
                                if (isFlowerAbove) {
                                    newList = new ArrayList<>();
                                    BlockState layerState = Blocks.SNOW.defaultBlockState().setValue(SnowBlock.LAYERS, 1);
                                    // var layerBlock = models.get(BlockModelShapes.stateToModelLocation(layerState));
                                    IBakedModel layerBlock = models.get(snow_height2);

                                    for (Direction direction1 : Stream.of(Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.UP).collect(Collectors.toList())) {
                                        newList.addAll(layerBlock.getQuads(layerState, direction1, random));
                                    }
                                } else newList = new ArrayList<>(snowList);
                            } else {
                                newList = new ArrayList<BakedQuad>(size + snowList.size());
                                newList.addAll(original);
                                newList.addAll(snowList);
                            }


                            if (flag == FLAG_FARMLAND) {

                                for (Direction direction1 : Stream.of(Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.UP).collect(Collectors.toList())) {
                                    newList.addAll(snowModel.getQuads(null, direction1, random));
                                }
                            }

                            if (!EclipticSeasonsMixinPlugin.isOptLoad())
                                useMap.putIfAbsent(original, newList);

                            original = newList;


                        }
                    }
                } else if (direction == Direction.UP
                        && state.getBlock() instanceof GrassBlock
                        && random.nextInt(15) == 0
                ) {
                    ClientWorld level = Minecraft.getInstance().level;
                    SolarTerm solarTerm = SolarTerm.NONE;
                    int weight = 100;
                    if (level != null) {
                        solarTerm = EclipticUtil.getNowSolarTerm(level);
                        weight = Math.abs(solarTerm.ordinal() - 3) + 1;
                    }
                    if (solarTerm.getSeason() == Season.SPRING
                            && random.nextInt(weight * 4) == 0
                            && blockAndTintGetter.getBlockState(pos.above()).isAir()) {
                        List<BakedQuad> cc =
                                EclipticSeasonsMixinPlugin.isOptLoad() ? null : quadMap_GRASS.getOrDefault(original, null);
                        if (cc != null) {
                            return cc;
                        } else {
                            IBakedModel snowModel = models.get(grass_flower);
                            if (snowModel != null) {
                                int size = original.size();
                                List<BakedQuad> snowList = snowModel.getQuads(null, direction, null);
                                ArrayList<BakedQuad> newList;
                                newList = new ArrayList<BakedQuad>(size + snowList.size());
                                newList.addAll(original);
                                newList.addAll(snowList);
                                if (!EclipticSeasonsMixinPlugin.isOptLoad())
                                    quadMap_GRASS.putIfAbsent(original, newList);
                                original = newList;
                            }
                        }
                    }
                }
            }


        }
        return original;
    }

    public static boolean shouldSnowAt(IBlockDisplayReader blockAndTintGetter, BlockPos pos, BlockState state, Random random, long seed) {
        int snowLine = ClientConfig.Renderer.snowLine.get();
        Biome biome = Minecraft.getInstance().level.getBiome(pos);
        RegistryKey<Biome> biomeRegistryKey = RegistryKey.create(Registry.BIOME_REGISTRY, biome.getRegistryName());

        for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biomeRegistryKey)) {
            Integer integer = ClientConfig.SNOW_LINE_BIOME.getOrDefault(type, null);
            if (integer != null) {
                snowLine = integer;
                break;
            }
        }

        if (pos.getY() > snowLine
                || WeatherManager.getSnowDepthAtBiome(Minecraft.getInstance().level, biome) > Math.abs(seed % 100)) {
            return snowOverlayCanSurviveOn(state);
        }
        return false;
    }

}
