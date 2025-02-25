package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.core.map.ClientMapFixer;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.teamtea.eclipticseasons.client.core.ModelManager;

@Mixin({Chunk.class})
public abstract class MixinClientLevelChunk {
    @Shadow
    @Final
    private World level;

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/Heightmap;update(IIILnet/minecraft/block/BlockState;)Z", ordinal = 1),
            method = "setBlockState"
    )
    public void eclipticseasons$setBlockState(BlockPos pos, BlockState state, boolean p_177436_3_, CallbackInfoReturnable<BlockState> cir) {
        if (level instanceof ClientWorld) {
            // ModelManager.getHeightOrUpdate(pos, true);
            ClientMapFixer.addPlanner(((ClientWorld) level), state, pos, level.getGameTime(), ModelManager.getHeightOrUpdate(pos, false));

        }
    }
}
