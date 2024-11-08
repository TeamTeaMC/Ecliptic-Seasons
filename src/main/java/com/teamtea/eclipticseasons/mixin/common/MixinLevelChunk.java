package com.teamtea.eclipticseasons.mixin.common;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.ServerMapFixer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LevelChunk.class})
public abstract class MixinLevelChunk {
    @Shadow
    @Final
    Level level;

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1),
            method = "setBlockState"
    )
    public void ecliptic$Client_setBlockState(BlockPos pos, BlockState state, boolean p_62867_, CallbackInfoReturnable<BlockState> cir,
                                              @Local(ordinal = 1) BlockState oldState) {
        if (level != null && !level.isClientSide()) {
            // int j = pos.getX() & 15;
            // int l = pos.getZ() & 15;
            // MapChecker.updatePosForce(level, pos, level.getHeight(Heightmap.Types.MOTION_BLOCKING, j, l));
            ServerMapFixer.addPlanner(level, state, oldState, pos, level.getGameTime(), MapChecker.getHeight(level, pos), false);
        }
    }
}
