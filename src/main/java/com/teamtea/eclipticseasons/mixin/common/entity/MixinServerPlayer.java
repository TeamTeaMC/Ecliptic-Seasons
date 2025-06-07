package com.teamtea.eclipticseasons.mixin.common.entity;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Entity{
    public MixinServerPlayer(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(at = {@At(value = "NEW",
            target = "(Lnet/minecraft/core/particles/ParticleType;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/particles/BlockParticleOption;")},
            method = {"doCheckFallDamage"})
    public BlockParticleOption eclipticseasons$doCheckFallDamage_snow(ParticleType<BlockParticleOption> type, BlockState state,Operation<BlockParticleOption> original) {
        if (EclipticSeasonsApi.getInstance().isSnowyBlock(level(), state, getOnPosLegacy()))
        {
            state=Blocks.SNOW.defaultBlockState();
        }
        return original.call(type,state);
    }
}
