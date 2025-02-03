package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.common.registry.ModContents;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CalendarBlockEntity extends SyncBlockEntity {
    public CalendarBlockEntity( BlockPos pos, BlockState state) {
        super(ModContents.calendar_entity_type.get(), pos, state);
    }
}
