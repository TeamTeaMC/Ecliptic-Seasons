package com.teamtea.eclipticseasons.mixin.common.entity;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({Entity.class})
public abstract class MixinEntity {

    @Shadow
    private Level level;

    @Shadow
    public abstract Level level();

    @WrapOperation(at = {@At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;playStepSound(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;FF)V")},
            method = {"playStepSound"})
    public void eclipticseasons$playStepSound(BlockState instance, Level level, BlockPos blockPos, Entity entity, float volumeMultiplier, float pitchMultiplier, Operation<Void> original) {
        if (EclipticSeasonsApi.getInstance().isSnowyBlock(this.level, instance, blockPos))
            instance = Blocks.SNOW.defaultBlockState();
        original.call(instance, level, blockPos, entity, volumeMultiplier, pitchMultiplier);
    }

    @WrapOperation(at = {@At(value = "NEW",
            target = "(Lnet/minecraft/core/particles/ParticleType;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/particles/BlockParticleOption;")},
            method = {"spawnSprintParticle"})
    public BlockParticleOption eclipticseasons$spawnSprintParticle_snow(ParticleType<?> type, BlockState state, BlockPos pos, Operation<BlockParticleOption> original) {
        if (EclipticSeasonsApi.getInstance().isSnowyBlock(level, state, pos)) {
            state = Blocks.SNOW.defaultBlockState();
        }
        return original.call(type, state, pos);
    }

    @Inject(at = {@At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;stepOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/Entity;)V")},
            method = "applyEffectsFromBlocks(Ljava/util/List;)V")
    public void eclipticseasons$move_stepOn(List movements,
                                            CallbackInfo ci,
                                            @Local BlockPos pos,
                                            @Local BlockState blockstate) {
        SnowyMapChecker.onEntityStepOn((Entity) (Object) this, level, pos, blockstate);
    }
}
