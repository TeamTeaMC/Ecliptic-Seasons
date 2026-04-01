package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.client.model.ModelResolver;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonBlockDefinition;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.client.IFakeSnowHolder;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.IMapSliceProvider;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.model.block.IESReplaceModel;
import com.teamtea.eclipticseasons.client.model.block.ISnowyReplaceModel;
import com.teamtea.eclipticseasons.client.model.block.ReplacingBlockStateModel;
import com.teamtea.eclipticseasons.client.model.block.part.SimpleBlockModelPart;
import com.teamtea.eclipticseasons.client.model.block.quad.DirectionMask;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static com.teamtea.eclipticseasons.client.core.ExtraModelManager.models;


public class ExtraRenderDispatcher {

    public static void appendModels(IMapSlice mapSlice, BlockState blockState, RandomSource random, BlockPos pos, List<BlockStateModelPart> parts) {
        if (canSnowy(mapSlice, pos, blockState, blockState.getSeed(pos), mapSlice.getModelCheckPos())) {
            BlockState snowState = null;
            int flag = MapChecker.getDefaultBlockTypeFlag(blockState);
            int offset = MapChecker.getSnowOffset(blockState, flag);
            if (flag == MapChecker.FLAG_STAIRS) {
                snowState = BlockRegistry.snowyStairs.get().defaultBlockState()
                        .setValue(StairBlock.FACING, blockState.getValue(StairBlock.FACING))
                        .setValue(StairBlock.HALF, blockState.getValue(StairBlock.HALF))
                        .setValue(StairBlock.SHAPE, blockState.getValue(StairBlock.SHAPE));
            } else if (flag == MapChecker.FLAG_VINE) {
                snowState = BlockRegistry.snowyVine.get().defaultBlockState()
                        .setValue(VineBlock.EAST, blockState.getValue(VineBlock.EAST))
                        .setValue(VineBlock.WEST, blockState.getValue(VineBlock.WEST))
                        .setValue(VineBlock.SOUTH, blockState.getValue(VineBlock.SOUTH))
                        .setValue(VineBlock.NORTH, blockState.getValue(VineBlock.NORTH))
                        .setValue(VineBlock.UP, blockState.getValue(VineBlock.UP))
                ;
            } else if (mapSlice.getSolidBlockHeight(pos) == pos.getY()) {
                snowState = BlockRegistry.snowyLeaves.get().defaultBlockState();
            }

            BlockStateModel blockStateModel = ExtraModelManager.getSnowyModel(blockState, snowState, flag, offset);
            if (blockStateModel != null) {

                if (ExtraModelManager.isModelReplaceable(flag)) {
                    parts.clear();
                }
                // net.neoforged.neoforge.client.model.quad.MutableQuad

                // booleanRef.set(false);
                // new BakedQuad().spriteInfo()

                blockStateModel.collectParts(mapSlice, pos, blockState, random, parts);
            }
        }
    }


    public static void changeSprite(IMapSlice mapSlice, BlockState blockState, RandomSource random, BlockPos pos, List<BlockStateModelPart> parts, BlockStateModel blockStateModel) {
        ArrayList<BlockStateModelPart> objects = new ArrayList<>();
        blockStateModel.collectParts(mapSlice, pos, blockState, random, objects);
        Map<Direction, List<BakedQuad>> map = new IdentityHashMap<>();
        MutableQuad mutableQuad = new MutableQuad();
        for (BlockStateModelPart object : objects) {
            for (Direction value : Direction.values()) {
                List<BakedQuad> quads = object.getQuads(value);
                for (BakedQuad quad : quads) {
                    mutableQuad.setFrom(quad);
                    mutableQuad.setSprite(quad.materialInfo().sprite(), ChunkSectionLayer.CUTOUT, RenderTypes.cutoutMovingBlock());
                    List<BakedQuad> bakedQuads = map.computeIfAbsent(value, (p) -> new ArrayList<>());
                    bakedQuads.add(mutableQuad.toBakedQuad());
                }
            }
            List<BakedQuad> quads = object.getQuads(null);
            for (BakedQuad quad : quads) {
                mutableQuad.setFrom(quad);
                // should check render type
                mutableQuad.setSprite(quad.materialInfo().sprite(), ChunkSectionLayer.CUTOUT, RenderTypes.cutoutMovingBlock());
                List<BakedQuad> bakedQuads = map.computeIfAbsent(null, (p) -> new ArrayList<>());
                bakedQuads.add(mutableQuad.toBakedQuad());
            }
        }
        parts.add(new SimpleBlockModelPart(map));
    }

    public static BlockPos.MutableBlockPos posToMutable(BlockPos pos) {
        return pos.mutable();
        // return new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockStateModel findModel(@NotNull IMapSlice mapSlice, BlockPos pos, BlockState state, RandomSource random, long seed, @Nullable BlockPos.MutableBlockPos checkPos, List<BlockStateModelPart> parts) {
        // if (!state.is(Blocks.LILY_PAD))
        //     return null;
        Level level = Minecraft.getInstance().level;
        if (level == null) return null;
        BlockAndTintGetter blockAndTintGetter = mapSlice;
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
        if (ClientConfig.Debug.smoothSnowyEdges.get()
                && blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) > 0) {
            extendCheck = true;
        }

        boolean leaveLike = MapChecker.leaveLike(flag);
        boolean leavesOrVine = leaveLike || MapChecker.vineLike(flag);

        BlockStateModel first = null;
        BlockStateModel second = null;

        boolean shouldReplace = false;

        if (!leavesOrVine) {
            int cut = mapSlice.getBlockHeight(pos) - pos.getY();
            if (cut > 1 && !ClientConfig.Renderer.snowUnderFence.get() && !extendCheck)
                return null;
        }

        int offset = snowDefClientOverlay == null ?
                MapChecker.getSnowOffset(state, flag) :
                snowDefClientOverlay.get(0).getInfo().getOffset();


        boolean isLight = false;
        if (checkPos == null) checkPos = posToMutable(pos);
        else checkPos.set(pos.getX(), pos.getY(), pos.getZ());

        if (ClientConfig.Renderer.useVanillaCheck.get()) {
            checkPos.setY(pos.getY() + 1);
            isLight = blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >= 15;
        } else {
            int cacheHeight = mapSlice.getBlockHeight(checkPos);
            isLight = cacheHeight <= pos.getY() - offset;
        }

        boolean specialLeaves = false;

        if (!isLight && ClientConfig.Renderer.snowUnderFence.get()) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) >= 9) {
                IMapSliceProvider ip = (IMapSliceProvider) blockAndTintGetter;
                int y_real = ip.getSolidBlockHeight(checkPos);
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
                    // specialLeaves = true;
                    IMapSliceProvider ip = (IMapSliceProvider) blockAndTintGetter;
                    specialLeaves = ip.getSolidBlockHeight(checkPos) > pos.getY();
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
                    BlockStateModel snowModel = ExtraModelManager.getSnowyModel(state, snowState, flag, offset);

                    if (snowModel != null) {
                        first = snowModel;
                        shouldReplace = ReplacingBlockStateModel.replace(snowModel);
                    }
                }

            }

            if (!isSnowy
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
                        if (mapSlice.getBlockHeight(checkPos) != pos.getY()) {
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
                    }
                    if (directionRequireGroup.length > ddLength) {
                        index = i;
                        ddLength = directionRequireGroup.length;
                    }
                }
                if (index > -1) {
                    index = indexs[index];
                    first = getModel(ExtraModelManager.snow_edge_overlays.get(index));
                    isSnowy = true;
                }
            }

            // if (!isSnowy)
        }

        if (first == null || !(first instanceof IESReplaceModel iesReplaceModel && iesReplaceModel.isReplace())) {
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
                                    Identifier cinfo = flatSlice.transitionModels() == null ?
                                            flatSlice.mid() :
                                            Mth.abs(((int) (seed + pos.getX()))) % 100 > ClientCon.progress ?
                                                    flatSlice.transitionModels().getFirst() : flatSlice.transitionModels().getSecond();
                                    ModelResolver smr = ExtraModelManager.extraSnowModelBuilds.get(cinfo);
                                    if (smr != null) {
                                        var mmrl = smr.tryFind(state);
                                        if (mmrl != null) {
                                            var to_replace = getModel(mmrl.modelIdentifier());
                                            if (to_replace != null) {
                                                if (first != null) {
                                                    second = to_replace;
                                                    if (mmrl.replace())
                                                        shouldReplace = true;
                                                } else {
                                                    first = to_replace;
                                                    if (mmrl.replace())
                                                        shouldReplace = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            if (ClientConfig.Renderer.flowerOnGrass.get() && state.getBlock() instanceof GrassBlock
                    && (seed % 14) == 0)
            // && random.nextInt(15) == 0)
            {
                var solarTerm = ClientCon.nowSolarTerm;

                checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
                if (solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SPRING, SolarTerm.BEGINNING_OF_SUMMER)) {
                    int weight = Math.abs(solarTerm.ordinal() - 3) + 1;
                    if ((seed % (weight * 4)) == 0
                            && blockAndTintGetter.getBlockState(checkPos).isAir()
                            && (mapSlice == null || ((IBiomeTagHolder) (Object) MapChecker.idToBiome(level, mapSlice.getSurfaceFaceBiomeId(checkPos)).value()).eclipticseasons$getBindTag() == ClimateTypeBiomeTags.SEASONAL)) {
                        {
                            int index = Math.abs(((int) (seed + pos.getX())) % ExtraModelManager.flower_on_grass.size());
                            // index=random.nextInt(flower_on_grass.size());
                            if (first == null)
                                first = getModel(ExtraModelManager.flower_on_grass.get(index));
                            else {
                                second = getModel(ExtraModelManager.flower_on_grass.get(index));
                            }
                        }
                    }
                }
                // if (first == null && solarTerm.isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                //     int weight = Math.abs(solarTerm.ordinal() - 7) + 1;
                //     if ((seed % (weight * 3)) == 0
                //             && blockAndTintGetter.getBlockState(checkPos).isAir()
                //             && (mapSlice == null || ((IBiomeTagHolder) (Object) MapChecker.idToBiome(level, mapSlice.getSurfaceFaceBiomeId(checkPos)).value()).eclipticseasons$getBindTag() == ClimateTypeBiomeTags.SEASONAL)) {
                //         {
                //             int index = Math.abs(((int) (seed + pos.getX())) % ExtraModelManager.fourleaf_clovers.size());
                //             // index=2;
                //             // first = getModel(ExtraModelManager.fourleaf_clovers.get(index));
                //             if (first == null)
                //                 first = getModel(ExtraModelManager.fourleaf_clovers.get(index));
                //             else {
                //                 second = getModel(ExtraModelManager.fourleaf_clovers.get(index));
                //             }
                //         }
                //     }
                // }
            }
        }

        if (parts != null) {
            if (shouldReplace) {
                parts.clear();
            }
            if (first != null)
                first.collectParts(mapSlice, pos, state, random, parts);
            if (second != null)
                second.collectParts(mapSlice, pos, state, random, parts);
        }
        return first;
    }

    private static @Nullable BlockStateModel getModel(StandaloneModelKey<BlockStateModel> index) {
        return models.standaloneModels().get(index);
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

    public static BlockStateModel shouldRenderedWithSnowInside(
            BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state,
            @Nullable BlockPos.MutableBlockPos checkPos) {
        int layers = getRenderedLevelWithSnowInside(blockAndTintGetter, pos, state, checkPos);
        return layers > 0 ? ExtraModelManager.getSnowLayerModel(layers) : null;
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
        if (belowState.is(BlockTags.CANNOT_SUPPORT_SNOW_LAYER)) {
            return 0;
        } else {
            if (!belowState.is(BlockTags.CANNOT_SUPPORT_SNOW_LAYER)
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


    public static BlockState shouldBlockAsSnowyState(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos.MutableBlockPos mutableBlockPos) {
        if (!ClientConfig.Renderer.snowInFence.get()) return state;
        // if (!state.blocksMotion() || !state.getFluidState().isEmpty())
        //    return state;
        if (!(state.getBlock() instanceof SnowyBlock)) return state;
        int y = mutableBlockPos.getY();
        mutableBlockPos.setY(y + 1);
        BlockState blockState = blockAndTintGetter.getBlockState(mutableBlockPos);
        BlockStateModel bm = shouldRenderedWithSnowInside(blockAndTintGetter, mutableBlockPos, blockState, null);
        if (bm != null) {
            if (state.hasProperty(BlockStateProperties.SNOWY)) {
                state = state.setValue(BlockStateProperties.SNOWY, true);
            }
        }
        mutableBlockPos.setY(y);
        return state;
    }

    public static int getLayer(BlockAndTintGetter blockAndTintGetter, BlockPos.MutableBlockPos pos, BlockState state, BlockStateModel snowModel, long seed) {
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
        Level useLevel = ClientCon.getUseLevel();
        if (useLevel == null) return 0;

        if (!(state.isSolidRender() || state.getBlock() instanceof LeavesBlock)) return 0;
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

            int cacheLevel = mapSlice.getLevelForFakeSnow(otherPos);
            if (cacheLevel > IFakeSnowHolder.NONE_CHECK_FAKE_SNOW_LEVEL)
                return cacheLevel == 0 ? original :
                        Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, cacheLevel);

            int y = otherPos.getY();
            if (snowInFence) {
                int x = otherPos.getX();
                int z = otherPos.getZ();
                int snowInsideLevel = getRenderedLevelWithSnowInside(getter, otherPos, original, null);
                otherPos.set(x, y, z);
                if (snowInsideLevel > 0) {
                    return Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, snowInsideLevel);
                }
                otherPos.set(x, y, z);
            }

            if (extraSnowLayer) {
                otherPos.setY(y - 1);
                BlockState belowState = !notUp ? selfState : view.getBlockState(otherPos);
                int layer = getLayer(getter, otherPos, belowState, null, belowState.getSeed(otherPos));
                otherPos.setY(y);
                if (layer > 0)
                    return Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, layer);
            }
        }
        return original;
    }

    public static List<BakedQuad> cancelTop(BlockStateModelPart part, BlockAndTintGetter level, BlockState state, BlockPos pos, Direction side, RandomSource rand, List<BakedQuad> original, List<BakedQuad> cacheBakeQuad) {
        return original;
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
        if (checkPos == null) checkPos = ExtraModelManager.posToMutable(pos);
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

    public static boolean notTooBright(BlockAndLightGetter blockAndTintGetter, IMapSlice mapSlice, BlockPos checkPos) {
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
}
