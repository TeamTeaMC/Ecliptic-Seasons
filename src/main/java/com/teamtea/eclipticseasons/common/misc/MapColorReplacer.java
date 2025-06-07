package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.MapColor;

public class MapColorReplacer {
    public static MapColor getTopSnowColor(BlockGetter blockGetter, BlockState state, BlockPos pos) {
        return getTopSnowColor(blockGetter, state, pos, false);
    }

    public static MapColor getTopSnowColor(BlockGetter blockGetter, BlockState state, BlockPos pos, boolean ignoreLight) {
        if (!(blockGetter instanceof Level level) || pos == null)
            return null;
        if (!CommonConfig.Season.snowyWinter.get()) return null;
        // if(state.getBlock()!=Blocks.VINE)return null;
        // if without snow we can faster the query
        // note 也许会更慢？和 x小地图有关
        // if (!EclipticUtil.isHereWithSnow(level, pos)) return null;

        boolean isLight = false;

        int flag = MapChecker.getBlockTypeFlag(level, pos, state);

        int offset = MapChecker.getSnowOffset(state, flag);


        // long seed = (long) Mth.abs(pos.hashCode());

        // todo 这里应该也要调整高度计算规则
        isLight = flag != MapChecker.FLAG_NONE && MapChecker.getHeightOrUpdate(level, pos, false) <= pos.getY() - offset
                && state.getBlock() != Blocks.SNOW_BLOCK
                && MapChecker.shouldSnowAt(level, pos.below(offset), state, level.getRandom(), state.getSeed(pos))
                && (ignoreLight || (!CommonConfig.Season.notSnowyNearGlowingBlock.get() ||
                level.getBrightness(LightLayer.BLOCK, pos.below(offset - 1)) <
                        CommonConfig.Season.notSnowyNearGlowingBlockLevel.get()))
        ;

        return isLight ? MapColor.SNOW : null;
    }
}
