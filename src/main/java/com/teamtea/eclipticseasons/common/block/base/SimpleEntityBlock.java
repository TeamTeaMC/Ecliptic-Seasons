package com.teamtea.eclipticseasons.common.block.base;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleEntityBlock extends BaseEntityBlock {


	public SimpleEntityBlock(Properties properties) {
		super(properties);
	}


	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}


	@Nullable
	protected static <E extends BlockEntity, A extends
			BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> entityType, BlockEntityType<E> entityType1, BlockEntityTicker<? super E> ticker) {

		return entityType1 == entityType ? (BlockEntityTicker<A>) ticker : null;
	}


	public boolean hasBlockEntity(BlockState state) {
		return true;
	}
}
