package com.teamtea.eclipticseasons.common.block;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.block.base.SimpleHorizontalEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PaperWindmillBlock extends SimpleHorizontalEntityBlock {
    public PaperWindmillBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(PaperWindmillBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return EclipticSeasons.ModContents.paper_wind_mill_entity_type.get().create(pPos, pState);
    }
}
