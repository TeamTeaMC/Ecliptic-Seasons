package com.teamtea.eclipticseasons.mixin.common.block;


import com.teamtea.eclipticseasons.common.block.BlockInCopperGrateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WaterloggedTransparentBlock.class)

public abstract class MixinCopperGrateBlock extends TransparentBlock {


    public MixinCopperGrateBlock(Properties p_309186_) {
        super(p_309186_);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult result = BlockInCopperGrateBlock.getItemInteractionResult(stack, level, pos, state);
        if (result == ItemInteractionResult.sidedSuccess(level.isClientSide())) {
            if (!player.isCreative()) stack.shrink(1);
        }
        if (result != null) return result;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }


}
