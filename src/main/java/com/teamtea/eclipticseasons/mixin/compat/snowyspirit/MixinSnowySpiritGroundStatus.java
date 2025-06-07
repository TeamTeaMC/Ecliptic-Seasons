package com.teamtea.eclipticseasons.mixin.compat.snowyspirit;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.mehvahdjukaar.snowyspirit.common.entity.GroundStatus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({GroundStatus.class})
public abstract class MixinSnowySpiritGroundStatus {

    @ModifyExpressionValue(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")},
            method = {"computeFriction"},
            remap = false)
    private static BlockState eclipticseasons$computeFriction(BlockState original,
                                                       @Local(argsOnly = true) Entity sled) {
        if (EclipticSeasonsApi.getInstance().isSnowyBlock(sled.level(), original, sled.getOnPos()))
            original = Blocks.SNOW.defaultBlockState();
        return original;
    }

}
