package com.teamtea.eclipticseasons.mixin.common.entity;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    // TODO:修改走路粒子
    // @ModifyExpressionValue(at = {@At(value = "INVOKE",
    //         target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F")},
    //         method = {"travel"})
    // public float eclipticseasons$gtravel(float original) {
    //     return 0.999999f;
    // }

    @WrapOperation(at = {@At(value = "NEW",
            target = "(Lnet/minecraft/core/particles/ParticleType;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/particles/BlockParticleOption;")},
            method = {"checkFallDamage"})
    public BlockParticleOption eclipticseasons$checkFallDamage_snow(ParticleType<BlockParticleOption> type, BlockState state, Operation<BlockParticleOption> original, @Local(argsOnly = true) BlockPos pos) {
        if (EclipticSeasonsApi.getInstance().isSnowyBlock(level(), state, pos)) {
            state = Blocks.SNOW.defaultBlockState();
        }
        return original.call(type, state);
    }
}
