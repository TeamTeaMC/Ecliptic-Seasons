package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.core.map.ClientMapFixer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({DynamicGraphMinFixedPoint.class})
public abstract class MixinLight {

    // 目前还不能发现动态树叶的更新
    @Inject(
            at = @At(value = "HEAD"),
            method = "checkEdge(JJIIIZ)V"
    )
    public void eclipticseasons$checkEdge(long pFromPos, long pToPos, int pNewLevel, int pPreviousLevel, int pPropagationLevel, boolean pIsDecreasing, CallbackInfo ci) {
        if ((Object)(this) instanceof BlockLightEngine){
            BlockPos blockPos = new BlockPos(BlockPos.getX(pToPos), BlockPos.getY(pToPos), BlockPos.getZ(pToPos));
            ClientMapFixer.addPlanner( Minecraft.getInstance().level, Blocks.AIR.defaultBlockState(),blockPos, Minecraft.getInstance().level.getGameTime(), ModelManager.getHeightOrUpdate(blockPos,false));
        }
    }
}
