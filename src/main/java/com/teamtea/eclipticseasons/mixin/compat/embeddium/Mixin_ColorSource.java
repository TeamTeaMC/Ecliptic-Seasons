package com.teamtea.eclipticseasons.mixin.compat.embeddium;

import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.common.mixin.condition.ModCondition;
import com.teamtea.eclipticseasons.common.mixin.injector.DirectInject;
import com.teamtea.eclipticseasons.compat.embeddium.EmbeddiumBlenderColorProvider;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@ConditionalMixin(noneOf = {@ModCondition(value = "embeddium", name = "Celeritas")})
@Mixin(targets = "me.jellysquid.mods.sodium.client.model.color.DefaultColorProviders$VanillaAdapter")
public abstract class Mixin_ColorSource implements EmbeddiumBlenderColorProvider {


    @Shadow(remap = false)
    @Final
    private BlockColor provider;

    @DirectInject(
            remap = false,
            method = "getColors(Lme/jellysquid/mods/sodium/client/world/WorldSlice;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lme/jellysquid/mods/sodium/client/model/quad/ModelQuadView;[I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/model/quad/ModelQuadView;getColorIndex()I"
            ),
            mode = DirectInject.Mode.CANCEL_IF_TRUE
    )
    private boolean eclipticseasons$getColorsForESLeaves(
            WorldSlice view, BlockPos pos, BlockState state, ModelQuadView quad, int[] output
    ) {
        if (this.provider instanceof EmbeddiumBlenderColorProvider provider2) {
            provider2.getColors(view, pos, state, quad, output);
            return true;
        }

        return false;
    }
}
