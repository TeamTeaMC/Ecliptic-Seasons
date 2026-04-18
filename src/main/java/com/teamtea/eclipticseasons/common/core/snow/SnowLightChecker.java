package com.teamtea.eclipticseasons.common.core.snow;

import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class SnowLightChecker {
    public static boolean isTooLight(BlockAndLightGetter level, BlockPos pos, BlockState state, int blockType) {
        return isTooLight(level, pos, null, state, blockType);
    }

    public static boolean isTooLight(BlockAndLightGetter level, BlockPos pos, BlockPos.@Nullable MutableBlockPos mutableBlockPos, BlockState state, int blockType) {
        if (CommonConfig.Snow.notSnowyNearGlowingBlock.get()) {
            int aboveOffset = 1 - MapChecker.getSnowOffset(state, blockType);
            if (mutableBlockPos != null) {
                mutableBlockPos.setY(pos.getY() + aboveOffset);
                pos = mutableBlockPos;
            } else pos = pos.above(aboveOffset);
            return level.getBrightness(LightLayer.BLOCK, pos) >=
                    CommonConfig.Snow.notSnowyNearGlowingBlockLevel.get();
        }
        return false;
    }
}
