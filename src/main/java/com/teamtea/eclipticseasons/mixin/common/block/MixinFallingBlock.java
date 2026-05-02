package com.teamtea.eclipticseasons.mixin.common.block;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AnvilBlock.class, ConcretePowderBlock.class})
public abstract class MixinFallingBlock {

    @WrapOperation(at = {
            @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getMapColor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/MapColor;")},
            method = {"getDustColor"})
    public MapColor eclipticseasons$getColor(BlockState instance, BlockGetter blockGetter, BlockPos pos, Operation<MapColor> original) {
        var ii = MapColorReplacer.getBlockIfSnowColorAndCareLoad(blockGetter, instance, pos);
        if (ii != null)
            return ii;
        return original.call(instance, blockGetter, pos);
    }


}
