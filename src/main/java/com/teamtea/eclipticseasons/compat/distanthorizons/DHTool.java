package com.teamtea.eclipticseasons.compat.distanthorizons;

import com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper;
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
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.client.lod.color.SeasonalBlockColorCache;
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

public class DHTool {
    private static final ThreadLocal<BlockPos.MutableBlockPos> MUTABLE_BLOCK_POS_THREAD_LOCAL = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    private static final RandomSource RANDOM_SOURCE_THREAD_LOCAL = RandomSource.createThreadLocalInstance(42L);
    @Setter
    private static int snowColor = 0xFFF9FEFE;

    public static int applySnowColor(
            Integer color
            // ,
            // IClientLevelWrapper instance,
            // DhBlockPos dhBlockPos,
            // IBiomeWrapper iBiomeWrapper,
            // FullDataSourceV2 fullDataSourceV2,
            // IBlockStateWrapper iBlockStateWrapper
    ) {
        return color;
    }


    public static Integer computeBaseColor(IClientLevelWrapper instance, DhBlockPos dhBlockPos, IBiomeWrapper iBiomeWrapper, IBlockStateWrapper iBlockStateWrapper, FullDataPointIdMap fullDataMapping, LongArrayList fullColumnData, IWrapperFactory WRAPPER_FACTORY, int skylight, FullDataSourceV2 fullDataSourceV2, int targetDataIndex) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()
                || !CommonConfig.isSnowyWinter()
                || dhBlockPos.equals(DhBlockPos.ZERO)
                || !(iBlockStateWrapper instanceof BlockStateWrapper blockStateWrapper)
                || blockStateWrapper.isAir()) {
            return null;
        }

        boolean hasSeasonalColor = SeasonalBlockColorCache.hasSeasonalModelColor(blockStateWrapper.blockState);

        boolean noUnderSky = skylight <= 0;
        if (noUnderSky && !hasSeasonalColor) {
            return null;
        }

        Biome biome = unwrapBiome(iBiomeWrapper);
        if (!(instance instanceof ClientLevelWrapper) || biome == null) {
            return null;
        }

        Level level = ClientCon.getUseLevel();
        BlockState blockState = blockStateWrapper.blockState;
        if (level == null || blockState == null) {
            return null;
        }

        BlockPos.MutableBlockPos mcPos = convert(dhBlockPos);


        long seed = blockState.getSeed(mcPos);
        if (noUnderSky || !MapChecker.shouldSnowAtBiome(level, biome, blockState, RANDOM_SOURCE_THREAD_LOCAL, seed, mcPos)) {
            return hasSeasonalColor ?
                    SeasonalBlockColorCache.getColor(biome, mcPos, seed, blockState)
                    : null;
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

            if (!(queriedWrapper instanceof BlockStateWrapper queriedState)
                    || queriedWrapper.isAir()
                    || blockStatesToIgnore.contains(queriedWrapper)) {
                continue;
            }

            if (queriedWrapper.isLiquid()) break;

            if (CommonConfig.Debug.notLightAbove.get()
                    && queriedState.blockState != null
                    && queriedState.blockState.getBlock() instanceof LightBlock
                    && queriedState.blockState.hasProperty(LightBlock.LEVEL)
                    && queriedState.blockState.getValue(LightBlock.LEVEL) == 0) {
                break;
            }

            if (i == targetDataIndex
                    && (MapChecker.getDefaultBlockTypeFlag(queriedState.blockState) != 0
                    || (!queriedWrapper.isSolid() && !queriedWrapper.isLiquid()))) {
                return snowColor;
                // ColorUtil.setAlpha(instance.getBlockColor(dhBlockPos, iBiomeWrapper, fullDataSourceV2, BlockStateWrapper.fromBlockState(Blocks.SNOW.defaultBlockState(), instance)), 255)
            }
            if (!queriedWrapper.isLiquid() && !queriedState.blockState.blocksMotion()) {
                if (i + 1 == targetDataIndex) {
                    return snowColor;
                }

                break;
            }
        }

        return hasSeasonalColor ?
                SeasonalBlockColorCache.getColor(biome, mcPos, seed, blockState)
                : null;
    }

    public static IBlockStateWrapper shouldFrozen(ClientLevelWrapper instance, IBiomeWrapper biomeWrapper, DhBlockPosMutable dhBlockPosMutable, BlockState blockState, FullDataPointIdMap fullDataMapping, LongArrayList fullColumnData, int index) {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) {
            return null;
        }

        Biome biome = unwrapBiome(biomeWrapper);

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
            Level level = instance.getLevel();

            if (MapChecker.shouldSnowAtBiome(level, biome, blockState, RANDOM_SOURCE_THREAD_LOCAL, blockState.getSeed(mcPos), mcPos)) {
                return BlockStateWrapper.fromBlockState(Blocks.ICE.defaultBlockState(), instance);
            }
        }
        return null;
    }

    public static Biome unwrapBiome(IBiomeWrapper biomeWrapper) {
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
    private static BlockPos.MutableBlockPos convert(DhBlockPos wrappedPos) {
        return MUTABLE_BLOCK_POS_THREAD_LOCAL.get().set(wrappedPos.getX(), wrappedPos.getY(), wrappedPos.getZ());
    }
}
