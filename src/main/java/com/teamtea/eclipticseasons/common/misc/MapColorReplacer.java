package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class MapColorReplacer {
    public static MapColor getTopSnowColor(BlockGetter blockGetter, BlockState state, BlockPos pos) {
        if (!(blockGetter instanceof Level level) || pos == null)
            return null;

        boolean isLight = false;

        int flag = MapChecker.getBlockType(state, level, pos);
        int offset = MapChecker.getSnowOffset(state, flag);

        // isLight = ClientConfig.Renderer.useVanillaCheck.get() ?
        //         level.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos.above()) >= 15
        //         : ModelManager.getHeightOrUpdate(pos, false) == pos.getY() - offset;
        isLight = MapChecker.getHeightOrUpdate(level, pos, false) == pos.getY() - offset;

        // SimpleUtil.testTime(()->{getHeightOrUpdate(pos, false);});

        // long seed = (long) Mth.abs(pos.hashCode());

        isLight = flag != 0 && isLight
                && state.getBlock() != Blocks.SNOW_BLOCK
                && MapChecker.shouldSnowAt(level, pos.below(offset), state, level.getRandom(), state.getSeed(pos))
                && (!ClientConfig.Renderer.notSnowyNearGlowingBlock.get() ||
                        level.getBrightness(LightLayer.BLOCK, pos.below(offset - 1)) <
                                ClientConfig.Renderer.notSnowyNearGlowingBlockLevel.get());

        return isLight ? MapColor.SNOW : null;
    }
}
