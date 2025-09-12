package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

public class MapColorReplacer {
    public static MapColor getTopSnowColor(BlockGetter blockGetter, BlockState state, BlockPos pos) {
        return getTopSnowColor(blockGetter, state, pos, false, false);
    }

    public static MapColor getTopSnowColor(BlockGetter blockGetter, BlockState state, BlockPos pos, boolean ignoreLight) {
        return getTopSnowColor(blockGetter, state, pos, ignoreLight, false);
    }

    public static MapColor getBlockIfSnowColorAndCareLoad(BlockGetter blockGetter, BlockState state, BlockPos pos) {
        return getTopSnowColor(blockGetter, state, pos, false, true);
    }

    public static MapColor getTopSnowColor(BlockGetter blockGetter, BlockState state, BlockPos pos, boolean ignoreLight, boolean forceCheckLoad) {
        if (!(blockGetter instanceof Level level) || pos == null)
            return null;
        if (!CommonConfig.isSnowyWinter()) return null;

        if (SnowyMapChecker.shouldCheckSnowyStatus(level, pos)) {
            return SnowyMapChecker.isSnowyBlock(level, pos) ? MapColor.SNOW : null;
        }

        // if without snow we can faster the query
        // note 也许会更慢？和 x小地图有关
        // if (!EclipticUtil.isHereWithSnow(level, pos)) return null;

        boolean isLight = false;

        int flag = MapChecker.getBlockTypeFlag(level, pos, state);

        int offset = MapChecker.getSnowOffset(state, flag);

        // ignoreLight |= EclipticUtil.canSnowyBlockInteract();

        // long seed = (long) Mth.abs(pos.hashCode());

        isLight = flag != 0 && (!forceCheckLoad || MapChecker.isLoadedOnlyServer(level, pos))
                && MapChecker.getHeightOrUpdate(level, pos, false) <= pos.getY() - offset
                && state.getBlock() != Blocks.SNOW_BLOCK
                && (!forceCheckLoad || MapChecker.isLoadNearByOnlyServer(level, pos))
                && MapChecker.shouldSnowAt(level, pos.below(offset), state, level.getRandom(), state.getSeed(pos))
                && (ignoreLight || !SnowyMapChecker.isTooLight(level, pos, state, flag))
        ;

        if (isLight) {
            if (MapChecker.leaveLike(flag)) {
                BlockState aboveState = level.getBlockState(pos.above());
                boolean specialLeaves = aboveState.is(state.getBlock())
                        && (Heightmap.Types.MOTION_BLOCKING_NO_LEAVES.isOpaque().test(aboveState) ||
                        MapChecker.extraSnowPassable(aboveState));
                if (specialLeaves) {
                    isLight = CommonConfig.Snow.snowyTree.get();
                }
            } else {
                if (MapChecker.extraSnowPassable(state)) {
                    isLight = !MapChecker.extraSnowPassable(level.getBlockState(pos.above()));
                }
            }
        }

        return isLight ? MapColor.SNOW : null;
    }
}
