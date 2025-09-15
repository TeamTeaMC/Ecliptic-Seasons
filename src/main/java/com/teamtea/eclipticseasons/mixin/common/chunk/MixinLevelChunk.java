package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 2),
            method = "setBlockState"
    )
    public void eclipticseasons$server_setBlockState(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir,
                                                     @Local(ordinal = 1) BlockState oldState,
                                                     @Local Block block) {
        if (level != null) {
            MapChecker.getHeightOrUpdate(level, pos, true);

            SnowyMapChecker.updatePos(level,(LevelChunk) (Object) this,pos, state, oldState, block);
        }
    }


}
