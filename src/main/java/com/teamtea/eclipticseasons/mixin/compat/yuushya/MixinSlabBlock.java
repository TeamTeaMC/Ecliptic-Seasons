package com.teamtea.eclipticseasons.mixin.compat.yuushya;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.block.HalfSlabBlock;
import com.teamtea.eclipticseasons.compat.yuushya.YuushyaChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin({SlabBlock.class})
public abstract class MixinSlabBlock {

    // @Unique
    // private boolean eclipticSeasons$hasCheckHalfSlab = false;
    //
    // @Unique
    // private boolean eclipticSeasons$isHalfSlab = false;
    //
    //
    // @Inject(at = {@At(value = "INVOKE",
    //         target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;")},
    //         method = {"getShape"},
    //         cancellable = true, remap = false)
    // private void ecliptic$getShape(BlockState state,
    //                                BlockGetter level,
    //                                BlockPos pos,
    //                                CollisionContext context,
    //                                CallbackInfoReturnable<VoxelShape> cir) {
    //     if (!eclipticSeasons$hasCheckHalfSlab) {
    //         Optional<ResourceKey<Block>> resourceKey = BuiltInRegistries.BLOCK.getResourceKey(state.getBlock());
    //         if(resourceKey.isPresent()){
    //             this.eclipticSeasons$isHalfSlab =
    //                     YuushyaChecker.isyuushyaRBlock(state)
    //                             && resourceKey.get().location().getPath().startsWith("half_slab");
    //         }
    //         eclipticSeasons$hasCheckHalfSlab = true;
    //     }
    //     if (eclipticSeasons$isHalfSlab) {
    //         SlabType slabtype = state.getValue(SlabBlock.TYPE);
    //         if (slabtype == SlabType.BOTTOM) {
    //             {
    //                 cir.setReturnValue(HalfSlabBlock.BOTTOM_AABB);
    //             }
    //         }else if (slabtype == SlabType.TOP) {
    //             {
    //                 cir.setReturnValue(HalfSlabBlock.TOP_AABB);
    //             }
    //         }
    //     }
    // }

}
