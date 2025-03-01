package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class MapColorReplacer {
    public static MapColor getTopSnowColor(BlockGetter blockGetter, BlockState state, BlockPos pos) {
        return getTopSnowColor(blockGetter, state, pos, false);
    }

    public static MapColor getTopSnowColor(BlockGetter blockGetter, BlockState state, BlockPos pos, boolean ignoreLight) {
        if (!(blockGetter instanceof Level level) || pos == null)
            return null;
        // if without snow we can faster the query
        if (!EclipticUtil.isHereWithSnow(level, pos)) return null;

        boolean isLight = false;

        int flag = MapChecker.getBlockType(state, level, pos);

        int offset = MapChecker.getSnowOffset(state, flag);


        isLight = MapChecker.getHeightOrUpdate(level, pos, false) == pos.getY() - offset;


        // long seed = (long) Mth.abs(pos.hashCode());

        isLight = flag != 0 && isLight
                && state.getBlock() != Blocks.SNOW_BLOCK
                && MapChecker.shouldSnowAt(level, pos.below(offset), state, level.getRandom(), state.getSeed(pos))
                && (ignoreLight || (!CommonConfig.Season.notSnowyNearGlowingBlock.get() ||
                level.getBrightness(LightLayer.BLOCK, pos.below(offset - 1)) <
                        CommonConfig.Season.notSnowyNearGlowingBlockLevel.get()))
        ;

        return isLight ? MapColor.SNOW : null;
    }
}
