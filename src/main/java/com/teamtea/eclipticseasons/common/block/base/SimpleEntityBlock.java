package com.teamtea.eclipticseasons.common.block.base;


import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;

public abstract class SimpleEntityBlock  extends Block implements ITileEntityProvider {

	public SimpleEntityBlock(Properties properties) {
		super(properties);
	}

}
