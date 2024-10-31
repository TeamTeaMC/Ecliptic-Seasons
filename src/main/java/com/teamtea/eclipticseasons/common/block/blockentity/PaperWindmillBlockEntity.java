package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PaperWindmillBlockEntity extends SyncBlockEntity {
    public PaperWindmillBlockEntity( BlockPos pos, BlockState state) {
        super(EclipticSeasons.ModContents.paper_wind_mill_entity_type.get(), pos, state);
    }
}
