package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;


/**
 * Not use the class, use the {@link com.teamtea.eclipticseasons.client.core.ModelManager#getBlockTypeFlag(BlockAndTintGetter, BlockPos, BlockState)}
 * **/
@ApiStatus.Internal
public interface IBlockStateFlagger {

    int getBlockTypeFlag();

    void setBlockTypeFlag(int flag);

    BlockState es$asState();
}
