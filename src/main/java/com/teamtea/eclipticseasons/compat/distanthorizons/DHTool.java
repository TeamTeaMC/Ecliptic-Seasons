package com.teamtea.eclipticseasons.compat.distanthorizons;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBlockColorOverrideEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper;
import com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import net.minecraft.world.level.block.LeavesBlock;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBlockColorOverrideEvent.EventParam;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class DHTool {
    private static final ThreadLocal<BlockPos.MutableBlockPos> MUTABLE_BLOCK_POS_THREAD_LOCAL = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    private static final RandomSource RANDOM_SOURCE_THREAD_LOCAL = RandomSource.createThreadLocalInstance(42L);
    @Setter
    private static int snowColor = 0xFFF9FEFE;

    private static final Map<IBiomeWrapper, Biome> BIOME_CACHE = new ConcurrentHashMap<>();

    public static void clearCaches() {
        BIOME_CACHE.clear();
    }

    public static int getSeasonalColor(IClientLevelWrapper instance, IBiomeWrapper iBiomeWrapper,
                                       IBlockStateWrapper iBlockStateWrapper, int x, int y, int z) {
        if (!ClientConfig.Renderer.seasonalGrassColorChange.get()) return -1;

        BlockState blockState = null;
        if (iBlockStateWrapper instanceof BlockStateWrapper bsw) {
            blockState = bsw.blockState;
        }
        if (blockState == null) return -1;

        if (!shouldChangeColor(blockState)) return -1;

        Biome biome = BIOME_CACHE.get(iBiomeWrapper);
        if (biome == null) {
            biome = extractBiome(iBiomeWrapper);
            if (biome != null) {
                BIOME_CACHE.put(iBiomeWrapper, biome);
            } else {
                return -1;
            }
        }

        int color = -1;
        if (blockState.is(Blocks.GRASS_BLOCK) ||
                blockState.is(Blocks.SHORT_GRASS) || blockState.is(Blocks.TALL_GRASS) ||
                blockState.is(Blocks.FERN) || blockState.is(Blocks.LARGE_FERN)) {
            color = BiomeColorsHandler.GRASS_COLOR.getColor(biome, x, z);
        } else if (blockState.getBlock() instanceof LeavesBlock) {
            if (blockState.is(Blocks.BIRCH_LEAVES)) {
                color = BiomeColorsHandler.getBirchColor(blockState, null, new BlockPos(x, y, z));
            } else if (blockState.is(Blocks.SPRUCE_LEAVES)) {
                color = BiomeColorsHandler.getSpruceColor(blockState, null, new BlockPos(x, y, z));
            } else if (blockState.is(Blocks.MANGROVE_LEAVES)) {
                color = BiomeColorsHandler.getMangroveColor(blockState, null, new BlockPos(x, y, z));
            } else {
                color = BiomeColorsHandler.FOLIAGE_COLOR.getColor(biome, x, z);
            }
        }

        if (color != -1) {
            float brightness = 0.50f;
            int r = (int) (((color >> 16) & 0xFF) * brightness);
            int g = (int) (((color >> 8) & 0xFF) * brightness);
            int b = (int) ((color & 0xFF) * brightness);
            return 0xFF000000 | (Math.min(255, r) << 16) | (Math.min(255, g) << 8) | Math.min(255, b);
        }
        return -1;
    }

    private static boolean shouldChangeColor(BlockState state) {
        if (state.is(Blocks.DANDELION) || state.is(Blocks.POPPY) ||
                state.is(Blocks.BLUE_ORCHID) || state.is(Blocks.ALLIUM) ||
                state.is(Blocks.AZURE_BLUET) || state.is(Blocks.RED_TULIP) ||
                state.is(Blocks.ORANGE_TULIP) || state.is(Blocks.WHITE_TULIP) ||
                state.is(Blocks.PINK_TULIP) || state.is(Blocks.OXEYE_DAISY) ||
                state.is(Blocks.CORNFLOWER) || state.is(Blocks.LILY_OF_THE_VALLEY) ||
                state.is(Blocks.WITHER_ROSE) || state.is(Blocks.FLOWER_POT) ||
                state.is(Blocks.POTTED_POPPY) || state.is(Blocks.DEAD_BUSH) ||
                state.is(Blocks.VINE) || state.is(Blocks.WATER) ||
                state.is(Blocks.LILY_PAD) || state.is(Blocks.SEAGRASS) ||
                state.is(Blocks.TALL_SEAGRASS)) {
            return false;
        }
        return true;
    }

    private static Biome extractBiome(IBiomeWrapper iBiomeWrapper) {
        Object obj = iBiomeWrapper.getWrappedMcObject();
        if (obj instanceof Holder<?> holder) {
            Object value = holder.value();
            if (value instanceof Biome biome) {
                return biome;
            }
        } else if (obj instanceof Biome biome) {
            return biome;
        }
        return null;
    }

    public static void registerColorOverride() {
        try {
            Object eventInjector = DhApi.events;
            Method registerMethod = eventInjector.getClass().getMethod("register", DhApiBlockColorOverrideEvent.class);
            DhApiBlockColorOverrideEvent event = new DhApiBlockColorOverrideEvent() {
                @Override
                public void onBlockColorOverridden(DhApiEventParam<EventParam> eventParam) {
                    handleColorOverride(eventParam);
                }
            };
            registerMethod.invoke(eventInjector, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleColorOverride(DhApiEventParam<EventParam> eventParam) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return;
        if (!ClientConfig.Renderer.seasonalGrassColorChange.get()) return;

        EventParam param = extractEventParam(eventParam);
        if (param == null) return;

        IClientLevelWrapper levelWrapper = (IClientLevelWrapper) param.getLevelWrapper();
        IBiomeWrapper biomeWrapper = (IBiomeWrapper) param.getBiomeWrapper();
        IBlockStateWrapper blockStateWrapper = (IBlockStateWrapper) param.getBlockStateWrapper();

        int x = param.getBlockPosX();
        int y = param.getBlockPosY();
        int z = param.getBlockPosZ();

        int color = getSeasonalColor(levelWrapper, biomeWrapper, blockStateWrapper, x, y, z);
        if (color != -1) {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            if (a == 0) a = 255;
            param.setColor(a, r, g, b);
        }
    }

    private static EventParam extractEventParam(DhApiEventParam<EventParam> eventParam) {
        try {
            Field field = eventParam.getClass().getDeclaredField("param");
            field.setAccessible(true);
            return (EventParam) field.get(eventParam);
        } catch (Exception e) {
            return null;
        }
    }

    public static MapColor computeBaseColor(IClientLevelWrapper instance,
                                            DhBlockPos dhBlockPos,
                                            IBiomeWrapper iBiomeWrapper,
                                            IBlockStateWrapper iBlockStateWrapper,
                                            FullDataPointIdMap fullDataMapping,
                                            LongArrayList fullColumnData,
                                            IWrapperFactory WRAPPER_FACTORY,
                                            int skyLight) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return null;
        if (!CommonConfig.Snow.snowyWinter.get()) return null;
        if (ClientCon.nowSeason != Season.WINTER) return null;

        if (instance == null || dhBlockPos == null || iBiomeWrapper == null || iBlockStateWrapper == null) return null;
        Level level;
        try {
            level = (Level) instance.getWrappedMcObject();
        } catch (Exception e) {
            return null;
        }
        if (level == null) return null;
        if (dhBlockPos.equals(DhBlockPos.ZERO)) return null;
        if (!(iBlockStateWrapper instanceof BlockStateWrapper blockStateWrapper)) return null;
        if (blockStateWrapper.isAir()) return null;
        if (skyLight <= 0) return null;

        BlockState blockState = blockStateWrapper.blockState;
        BlockPos.MutableBlockPos mcPos = convert(dhBlockPos);

        Biome biome = null;
        if (iBiomeWrapper instanceof BiomeWrapper bw) {
            try {
                Field biomeField = BiomeWrapper.class.getDeclaredField("biome");
                biomeField.setAccessible(true);
                Holder<Biome> holder = (Holder<Biome>) biomeField.get(bw);
                if (holder != null) {
                    biome = holder.value();
                }
            } catch (Exception e) {
                Object obj = iBiomeWrapper.getWrappedMcObject();
                if (obj instanceof Holder<?> holder && holder.value() instanceof Biome b) {
                    biome = b;
                }
            }
        } else {
            Object obj = iBiomeWrapper.getWrappedMcObject();
            if (obj instanceof Holder<?> holder && holder.value() instanceof Biome b) {
                biome = b;
            }
        }

        if (biome == null) {
            return null;
        }

        boolean shouldSnow = MapChecker.shouldSnowAtBiome(level, biome, blockState, level.getRandom(), blockState.getSeed(mcPos), mcPos);
        if (!shouldSnow) {
            return null;
        }

        ObjectOpenHashSet<IBlockStateWrapper> blockStatesToIgnore = WRAPPER_FACTORY.getRendererIgnoredBlocks(instance);
        for (int i = 0; i < fullColumnData.size(); i++) {
            long fullData = fullColumnData.getLong(i);
            int id = FullDataPointUtil.getId(fullData);
            IBlockStateWrapper iBlockStateWrapper_NowQuery;
            try {
                iBlockStateWrapper_NowQuery = fullDataMapping.getBlockStateWrapper(id);
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
            int bottomY = FullDataPointUtil.getBottomY(fullData);
            int blockHeight = FullDataPointUtil.getHeight(fullData);
            if (CommonConfig.Debug.notLightAbove.get()
                    && iBlockStateWrapper_NowQuery instanceof BlockStateWrapper blockStateWrapper_NowQuery) {
                if (blockStateWrapper_NowQuery.blockState != null &&
                        blockStateWrapper_NowQuery.blockState.getBlock() instanceof LightBlock) {
                    if (blockStateWrapper_NowQuery.blockState.hasProperty(LightBlock.LEVEL)
                            && blockStateWrapper_NowQuery.blockState.getValue(LightBlock.LEVEL) == 0)
                        break;
                }
            }

            if (iBlockStateWrapper_NowQuery instanceof BlockStateWrapper blockStateWrapper_NowQuery
                    && !iBlockStateWrapper_NowQuery.isAir()
                    && !blockStatesToIgnore.contains(iBlockStateWrapper_NowQuery)
            ) {
                if (bottomY + instance.getMinHeight() == dhBlockPos.getY() &&
                        (MapChecker.getDefaultBlockTypeFlag(blockStateWrapper_NowQuery.blockState) != 0
                                || (!blockStateWrapper_NowQuery.isSolid() && !blockStateWrapper_NowQuery.isLiquid())
                        )) {
                    return MapColor.SNOW;
                } else {
                    if (!blockStateWrapper_NowQuery.isLiquid()
                            && !blockStateWrapper_NowQuery.blockState.blocksMotion()) {
                        if (i + 1 < fullColumnData.size()) {
                            int belowBottomY = FullDataPointUtil.getBottomY(fullColumnData.getLong(i + 1));
                            if (belowBottomY + instance.getMinHeight() == dhBlockPos.getY()) {
                                return MapColor.SNOW;
                            }
                        }
                        break;
                    }
                }
            }
        }

        return null;
    }

    public static IBlockStateWrapper shouldFrozen(ClientLevelWrapper instance,
                                                  IBiomeWrapper biomeWrapper,
                                                  DhBlockPosMutable dhBlockPosMutable,
                                                  BlockState blockState,
                                                  FullDataPointIdMap fullDataMapping,
                                                  LongArrayList fullColumnData,
                                                  int index) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return null;
        if (instance == null || biomeWrapper == null || blockState == null) return null;

        Level level = instance.getLevel();
        if (level == null) return null;

        Biome biome = null;
        if (biomeWrapper instanceof BiomeWrapper bw) {
            try {
                Field biomeField = BiomeWrapper.class.getDeclaredField("biome");
                biomeField.setAccessible(true);
                Holder<Biome> holder = (Holder<Biome>) biomeField.get(bw);
                if (holder != null) {
                    biome = holder.value();
                }
            } catch (Exception e) {
                Object obj = biomeWrapper.getWrappedMcObject();
                if (obj instanceof Holder<?> holder && holder.value() instanceof Biome b) {
                    biome = b;
                }
            }
        } else {
            Object obj = biomeWrapper.getWrappedMcObject();
            if (obj instanceof Holder<?> holder && holder.value() instanceof Biome b) {
                biome = b;
            }
        }

        if (ClientConfig.Debug.frozenWater.get()
                && biome != null
                && blockState.is(Blocks.WATER)
                && blockState.getFluidState().isSourceOfType(Fluids.WATER)) {
            if (index > 0 && index < fullColumnData.size() - 1) {
                try {
                    int id = FullDataPointUtil.getId(fullColumnData.getLong(index - 1));

                    if (!fullDataMapping.getBlockStateWrapper(id).isAir()) {
                        return null;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
            BlockPos.MutableBlockPos mcPos = convert(dhBlockPosMutable);
            if (MapChecker.shouldSnowAtBiome(level, biome, blockState, level.getRandom(), blockState.getSeed(mcPos), mcPos)) {
                return BlockStateWrapper.fromBlockState(Blocks.ICE.defaultBlockState(), instance);
            }
        }
        return null;
    }

    private static BlockPos.MutableBlockPos convert(DhBlockPos wrappedPos) {
        return MUTABLE_BLOCK_POS_THREAD_LOCAL.get().set(wrappedPos.getX(), wrappedPos.getY(), wrappedPos.getZ());
    }

    private static BlockPos.MutableBlockPos convert(DhBlockPosMutable wrappedPos) {
        return MUTABLE_BLOCK_POS_THREAD_LOCAL.get().set(wrappedPos.getX(), wrappedPos.getY(), wrappedPos.getZ());
    }
}