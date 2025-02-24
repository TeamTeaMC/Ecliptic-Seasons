package com.teamtea.eclipticseasons.mixin.client;


import net.minecraft.client.multiplayer.ClientLevel;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.teamtea.eclipticseasons.client.core.ModelManager;

@Mixin({LevelChunk.class})
public abstract class MixinClientLevelChunk {
    @Shadow @Final private Level level;

    // 目前还不能发现动态树叶的更新
    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1),
            method = "setBlockState", locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void eclipticseasons$setBlockState(BlockPos p_62865_, BlockState p_62866_, boolean p_62867_, CallbackInfoReturnable<BlockState> cir) {
        if (level instanceof ClientLevel clientLevel ){
            ModelManager.getHeightOrUpdate(p_62865_,true);
        }
    }
}
