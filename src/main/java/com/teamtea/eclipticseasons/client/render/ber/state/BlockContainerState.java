package com.teamtea.eclipticseasons.client.render.ber.state;

import lombok.Getter;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

@Getter
public class BlockContainerState extends BlockEntityRenderState {
    public Block innerBlock = Blocks.AIR;
}
