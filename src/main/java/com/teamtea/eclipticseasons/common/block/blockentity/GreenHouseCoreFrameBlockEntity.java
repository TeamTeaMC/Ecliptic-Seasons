package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GreenHouseCoreFrameBlockEntity extends SyncBlockEntity {
    public GreenHouseCoreFrameBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.greenhouse_core_container_entity_type.get(), pos, state);
    }
}
