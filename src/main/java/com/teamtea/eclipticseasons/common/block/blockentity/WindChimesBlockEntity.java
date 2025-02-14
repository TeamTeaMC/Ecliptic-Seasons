package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WindChimesBlockEntity extends SyncBlockEntity {
    public WindChimesBlockEntity( BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.wind_chimes_entity_type.get(), pos, state);
    }
}
