package com.teamtea.eclipticseasons.mixin.compat.sodium;

import com.teamtea.eclipticseasons.common.mixin.injector.DirectInject;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBlenderColorProvider;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.model.color.DefaultColorProviders$VanillaAdapter")
public abstract class Mixin_ColorSource implements SodiumBlenderColorProvider {

    @Shadow
    @Final
    private BlockTintSource[] color;

    @DirectInject(
            remap = false,
            require = 0,
            method = "getColors(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos$MutableBlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;[IZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;sodium$getTintIndex()I"
            ),
            mode = DirectInject.Mode.CANCEL_IF_TRUE
    )
    private boolean eclipticseasons$getColorsForESLeaves(
            LevelSlice slice,
            BlockPos pos,
            BlockPos.MutableBlockPos scratchPos,
            BlockState state,
            ModelQuadView quad,
            int[] output,
            boolean smooth
    ) {
        int tintIndex = quad.sodium$getTintIndex();
        if (tintIndex < 0 || tintIndex >= this.color.length) {
            return false;
        }

        if (this.color[tintIndex] instanceof SodiumBlenderColorProvider provider) {
            provider.getColors(slice, pos, scratchPos, state, quad, output, smooth);
            return true;
        }

        return false;
    }
}
