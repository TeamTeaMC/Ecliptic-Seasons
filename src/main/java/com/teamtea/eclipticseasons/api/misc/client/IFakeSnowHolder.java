package com.teamtea.eclipticseasons.api.misc.client;

import net.minecraft.core.BlockPos;

public interface IFakeSnowHolder {

    int NONE_CHECK_FAKE_SNOW_LEVEL = -1;

    default void setLevelForFakeSnow(long pos, int level) {
    }


    default int getLevelForFakeSnow(long pos) {
        return NONE_CHECK_FAKE_SNOW_LEVEL;
    }

    default int getLevelForFakeSnow(int x, int y, int z) {
        return getLevelForFakeSnow(BlockPos.asLong(x, y, z));
    }

    default int getLevelForFakeSnow(BlockPos pos) {
        return getLevelForFakeSnow(pos.asLong());
    }

    default void setLevelForFakeSnow(BlockPos pos, int level) {
        setLevelForFakeSnow(pos.asLong(), level);
    }

    default void setLevelForFakeSnow(int x, int y, int z, int level) {
        setLevelForFakeSnow(BlockPos.asLong(x, y, z), level);
    }
}
