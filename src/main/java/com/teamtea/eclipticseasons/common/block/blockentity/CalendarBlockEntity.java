package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;


public class CalendarBlockEntity extends SyncBlockEntity {
    public CalendarBlockEntity() {
        super(BlockEntityRegistry.calendar_entity_type.get());
    }
}
