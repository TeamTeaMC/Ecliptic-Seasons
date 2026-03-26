package com.teamtea.eclipticseasons.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class MeterBlockItem extends BlockItem {


    public MeterBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        MeterItem.sendInfo(this, level, player);
        return InteractionResult.CONSUME;
    }

}
