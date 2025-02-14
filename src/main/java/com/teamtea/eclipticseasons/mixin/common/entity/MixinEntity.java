package com.teamtea.eclipticseasons.mixin.common.entity;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Entity.class})
public abstract class MixinEntity {

    @Shadow
    private Level level;

    @WrapOperation(at = {@At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/level/block/SoundType;")},
            method = {"playStepSound"})
    public SoundType ecliptic$playStepSound(BlockState instance, LevelReader levelReader, BlockPos blockPos, Entity entity, Operation<SoundType> original, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state) {
        if (EclipticSeasonsApi.getInstance().isSnowyBlock(this.level, state, pos))
            return Blocks.SNOW_BLOCK.defaultBlockState().getSoundType(this.level, pos, entity);
        return original.call(instance, levelReader, blockPos, entity);
    }


}
