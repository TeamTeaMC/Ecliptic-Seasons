package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.core.map.ClientMapFixer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.lighting.BlockLightEngine;
import net.minecraft.world.lighting.LevelBasedGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelBasedGraph.class})
public abstract class MixinBlockLightEngine {

    @Inject(
            at = @At(value = "RETURN"),
            method = "checkEdge(JJIIIZ)V"
    )
    public void eclipticseasons$checkEdge(long pFromPos, long pToPos, int pNewLevel, int pPreviousLevel, int pPropagationLevel, boolean pIsDecreasing, CallbackInfo ci) {
        if ((Object) (this) instanceof BlockLightEngine) {
            if (pNewLevel > pPreviousLevel) {
                BlockPos blockPos = new BlockPos(BlockPos.getX(pToPos), BlockPos.getY(pToPos), BlockPos.getZ(pToPos));
                ClientMapFixer.addLightPlanner(Minecraft.getInstance().level,  blockPos, Minecraft.getInstance().level.getGameTime());
            }
        }
    }
}
