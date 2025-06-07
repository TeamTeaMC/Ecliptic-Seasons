package com.teamtea.eclipticseasons.mixin.common.block;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BubbleColumnBlock.class)
public class MixinBubbleColumnBlock {

    @Inject(
            method = "updateColumn(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
    )
    private static void eclipticseasons$updateColumn(LevelAccessor level, BlockPos pos, BlockState fluid, BlockState state, CallbackInfo ci, @Local(ordinal = 2) BlockState blockState) {

        // if (blockState.is(Blocks.BUBBLE_COLUMN) && level.getBlockState(pos.above()).isAir()) {
        //     SolarDataManager saveData = SolarHolders.getSaveData((Level) level);
        //     saveData.addMap(pos, state);
        // }
    }


}
