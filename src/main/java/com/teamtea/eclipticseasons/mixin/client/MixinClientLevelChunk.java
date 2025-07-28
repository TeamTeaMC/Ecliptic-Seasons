package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.map.ClientMapFixer;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.ServerMapFixer;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LevelChunk.class})
public abstract class MixinClientLevelChunk {
    @Shadow
    @Final
    Level level;

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 2),
            method = "setBlockState"
    )
    public void eclipticseasons$Client_setBlockState(BlockPos pos, BlockState state, boolean p_62867_, CallbackInfoReturnable<BlockState> cir) {
        if (level != null && level.isClientSide()) {
            // MapChecker.getHeightOrUpdate(clientLevel, pos, true);
            // ClientMapFixer.addPlanner(clientLevel, state, pos, clientLevel.getGameTime(), MapChecker.getHeight(clientLevel, pos));
            if (CommonConfig.Map.delayedUpdates.get()) {
                if (!EclipticUtil.isHereWithSnow(level, pos)) {
                    MapChecker.getHeightOrUpdate(level, pos, true);
                }
            } else {
                ClientMapFixer.addPlanner(level, state, pos, level.getGameTime(), MapChecker.getHeight(level, pos));
            }
        }
    }
}
