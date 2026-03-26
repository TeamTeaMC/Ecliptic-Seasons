package com.teamtea.eclipticseasons.api.misc;

import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;


/**
 * Not use the class, use the {@link MapChecker#getBlockTypeFlag(BlockAndTintGetter, BlockPos, BlockState)}
 * **/
@ApiStatus.Internal
public interface IBlockStateFlagger {

    int getBlockTypeFlag();

    void setBlockTypeFlag(int flag);

    BlockState es$asState();

    boolean forceTickControl();

    void setForceTickControl(boolean force);
}
