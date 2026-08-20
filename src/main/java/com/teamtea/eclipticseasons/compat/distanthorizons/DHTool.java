package com.teamtea.eclipticseasons.compat.distanthorizons;

import com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper_forge;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_forge;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;

public class DHTool {
    private static final ThreadLocal<BlockPos.MutableBlockPos> MUTABLE_BLOCK_POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

    public static int applySnowColor(
            MapColor color
            // ,
            // IClientLevelWrapper instance,
            // DhBlockPos dhBlockPos,
            // IBiomeWrapper iBiomeWrapper,
            // FullDataSourceV2 fullDataSourceV2,
            // IBlockStateWrapper iBlockStateWrapper
    ) {
        return color == MapColor.SNOW
                ? -1
                : color.col;
    }

    public static MapColor computeBaseColor(IClientLevelWrapper instance, DhBlockPos dhBlockPos, IBiomeWrapper iBiomeWrapper, IBlockStateWrapper iBlockStateWrapper,  FullDataPointIdMap fullDataMapping, LongArrayList fullColumnData,  IWrapperFactory WRAPPER_FACTORY,int skylight) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()
                || !CommonConfig.isSnowyWinter()
                || dhBlockPos.equals(DhBlockPos.ZERO)
                || !(iBlockStateWrapper instanceof BlockStateWrapper_forge forgeBlockState)
                || forgeBlockState.isAir()) {
            return null;
        }

        int targetDataIndex = findDataPointIndex(instance, dhBlockPos, fullColumnData);

        if (targetDataIndex < 0 || FullDataPointUtil.getSkyLight(fullColumnData.getLong(targetDataIndex)) <= 0) {
            return null;
        }

        Biome biome = unwrapBiome(iBiomeWrapper);
        if (!(instance instanceof ClientLevelWrapper_forge) || biome == null) {
            return null;
        }

        BlockPos.MutableBlockPos mcPos = mutableBlockPos(dhBlockPos);
        Level level = ClientCon.getUseLevel();
        BlockState blockState = forgeBlockState.blockState;

        if (!MapChecker.shouldSnowAtBiome(
                level,
                biome,
                blockState,
                level.getRandom(),
                blockState.getSeed(mcPos),
                mcPos)) {
            return null;
        }

        ObjectOpenHashSet<IBlockStateWrapper> blockStatesToIgnore = WRAPPER_FACTORY.getRendererIgnoredBlocks(instance);

        for (int i = 0; i <= targetDataIndex; i++) {
            long fullData = fullColumnData.getLong(i);
            int id = FullDataPointUtil.getId(fullData);

            IBlockStateWrapper queriedWrapper;
            try {
                queriedWrapper = fullDataMapping.getBlockStateWrapper(id);
            } catch (IndexOutOfBoundsException ignored) {
                continue;
            }

            if (CommonConfig.Debug.notLightAbove.get()
                    && queriedWrapper
                    instanceof BlockStateWrapper_forge queriedForgeState
                    && queriedForgeState.blockState != null
                    && queriedForgeState.blockState.getBlock()
                    instanceof LightBlock
                    && queriedForgeState.blockState.hasProperty(
                    LightBlock.LEVEL
            )
                    && queriedForgeState.blockState.getValue(
                    LightBlock.LEVEL
            ) == 0) {
                break;
            }

            if (!(queriedWrapper instanceof BlockStateWrapper_forge queriedForgeState)
                    || queriedWrapper.isAir()
                    || blockStatesToIgnore.contains(queriedWrapper)) {
                continue;
            }

            if (i == targetDataIndex
                    && (MapChecker.getDefaultBlockTypeFlag(
                    queriedForgeState.blockState
            ) != 0
                    || (!queriedWrapper.isSolid()
                    && !queriedWrapper.isLiquid()))) {
                return MapColor.SNOW;
            }

            if (!queriedWrapper.isLiquid() && !queriedForgeState.blockState.blocksMotion()) {
                if (i + 1 == targetDataIndex) {
                    return MapColor.SNOW;
                }

                break;
            }
        }

        return null;
    }

    private static int findDataPointIndex(
            IClientLevelWrapper instance,
            DhBlockPos dhBlockPos,
            LongArrayList fullColumnData) {
        int targetBottomY =
                dhBlockPos.getY() - instance.getMinHeight();

        int low = 0;
        int high = fullColumnData.size() - 1;

        while (low <= high) {
            int middle = (low + high) >>> 1;

            int middleBottomY = FullDataPointUtil.getBottomY(
                    fullColumnData.getLong(middle)
            );

            if (middleBottomY == targetBottomY) {
                return middle;
            }

            if (middleBottomY > targetBottomY) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return -1;
    }

    public static IBlockStateWrapper shouldFrozen(ClientLevelWrapper_forge instance, IBiomeWrapper biomeWrapper, DhBlockPosMutable dhBlockPosMutable, BlockState blockState, FullDataPointIdMap fullDataMapping, LongArrayList fullColumnData, int index) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) {
            return null;
        }

        Biome biome = unwrapBiome(biomeWrapper);

        if (ClientConfig.Debug.frozenWater.get()
                && biome != null
                && blockState.is(Blocks.WATER)
                && blockState.getFluidState()
                .isSourceOfType(Fluids.WATER)) {
            if (index > 0 && index < fullColumnData.size() - 1) {
                try {
                    int id = FullDataPointUtil.getId(
                            fullColumnData.getLong(index - 1)
                    );

                    if (!fullDataMapping
                            .getBlockStateWrapper(id)
                            .isAir()) {
                        return null;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }

            BlockPos.MutableBlockPos mcPos = mutableBlockPos(dhBlockPosMutable);
            Level level = instance.getLevel();

            if (MapChecker.shouldSnowAtBiome(
                    level,
                    biome,
                    blockState,
                    level.getRandom(),
                    blockState.getSeed(mcPos),
                    mcPos)) {
                return BlockStateWrapper_forge.fromBlockState(
                        Blocks.ICE.defaultBlockState(),
                        instance
                );
            }
        }
        return null;
    }

    private static Biome unwrapBiome(IBiomeWrapper biomeWrapper) {
        Object wrappedBiome = biomeWrapper.getWrappedMcObject();
        if (wrappedBiome instanceof Holder<?> holder && holder.value() instanceof Biome biome) {
            return biome;
        }
        return wrappedBiome instanceof Biome biome ? biome : null;
    }

    /**
     * From {@link com.seibel.distanthorizons.common.wrappers.McObjectConverter#convert(DhBlockPos)}.
     * As it changed its signature.
     *
     */
    private static BlockPos.MutableBlockPos mutableBlockPos(DhBlockPos wrappedPos) {
        return MUTABLE_BLOCK_POS.get().set(wrappedPos.getX(), wrappedPos.getY(), wrappedPos.getZ());
    }
}
