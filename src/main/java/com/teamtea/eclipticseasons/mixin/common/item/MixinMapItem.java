package com.teamtea.eclipticseasons.mixin.common.item;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MapItem.class)
public abstract class MixinMapItem {


    @WrapOperation(at = {
            @At(value = "INVOKE",
                    ordinal = 2,
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getMapColor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/MapColor;"),
            @At(value = "INVOKE",
                    ordinal = 3,
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getMapColor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/MapColor;")},
            method = {"update"})
    public MapColor eclipticseasons$getColor(BlockState instance, BlockGetter blockGetter, BlockPos pos, Operation<MapColor> original) {
        if (CommonConfig.Map.changeMapColorMapItem.get()) {
            var ii = MapColorReplacer.getBlockIfSnowColorAndCareLoad(blockGetter, instance, pos);
            if (ii != null)
                return ii;
        }
        return original.call(instance, blockGetter, pos);
    }
}
