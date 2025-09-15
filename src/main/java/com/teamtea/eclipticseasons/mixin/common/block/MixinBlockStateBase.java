package com.teamtea.eclipticseasons.mixin.common.block;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.misc.CustomRandomTick;
import com.teamtea.eclipticseasons.common.core.crop.ExtraTickType;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.NaturalPlantHandler;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.hook.ESEventHook;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase implements CustomRandomTick {

    @Shadow
    public abstract boolean is(TagKey<Block> tag);

    @Shadow
    private boolean isRandomlyTicking;

    @Shadow
    public abstract boolean isRandomlyTicking();

    @Shadow
    protected abstract BlockState asState();

    @Inject(
            method = "initCache",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$initCache(CallbackInfo ci) {
        if (this instanceof IBlockStateFlagger iBlockStateFlagger) {
            iBlockStateFlagger.setBlockTypeFlag(-1);
            iBlockStateFlagger.setForceTickControl(is(EclipticBlockTags.NATURAL_PLANTS));
            if (!isRandomlyTicking() && is(EclipticBlockTags.VOLATILE)) {
                isRandomlyTicking = true;
            }
        }
        if (this instanceof CustomRandomTick customRandomTick) {
            customRandomTick.eclipticseasons$reset();
        }

    }

    @Inject(
            method = "randomTick",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void eclipticseasons$randomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        eclipticseasons$tick(asState(), level, pos);

        if (this instanceof IBlockStateFlagger iBlockStateFlagger
                && (iBlockStateFlagger.forceTickControl() || CommonConfig.isForceCropCompatMode())) {
            boolean canCropGrow = ESEventHook.canExtraCropGrow(level, pos, iBlockStateFlagger.es$asState(), true);
            if (!canCropGrow) ci.cancel();
        }
    }


    @Unique
    public int eclipticseasons$tickType = ExtraTickType.UNCHECK;

    @Unique
    public List<WetterStructure> eclipticseasons$wetterStructures = null;

    @Override
    public void eclipticseasons$tick(BlockState state, ServerLevel worldIn, BlockPos pos) {
        switch (eclipticseasons$tickType) {
            case ExtraTickType.NONE -> {
                return;
            }
            case ExtraTickType.WETTER -> {
                CropGrowthHandler.handleRandomTick(worldIn, pos, state, eclipticseasons$wetterStructures);
            }
            case ExtraTickType.NATURAL -> {
                NaturalPlantHandler.tickBlock(worldIn, pos, state);
            }
            case ExtraTickType.WETTER_AND_NATURAL -> {
                CropGrowthHandler.handleRandomTick(worldIn, pos, state, eclipticseasons$wetterStructures);
                NaturalPlantHandler.tickBlock(worldIn, pos, state);
            }
            default -> {
                List<WetterStructure> wetterStructures = CropGrowthHandler.validTick(state);
                eclipticseasons$tickType = wetterStructures.isEmpty() ? ExtraTickType.NONE : ExtraTickType.WETTER;
                eclipticseasons$wetterStructures = wetterStructures;
                if (NaturalPlantHandler.shouldTick(state))
                    eclipticseasons$tickType = eclipticseasons$tickType == ExtraTickType.NONE ? ExtraTickType.NATURAL : ExtraTickType.WETTER_AND_NATURAL;
                eclipticseasons$tick(state, worldIn, pos);
            }
        }
    }

    @Override
    public void eclipticseasons$reset() {
        eclipticseasons$tickType = ExtraTickType.UNCHECK;
        eclipticseasons$wetterStructures = null;
    }

    @Override
    public int eclipticseasons$getType() {
        return eclipticseasons$tickType;
    }


    // @Inject(
    //         method = "onRemove",
    //         at = @At(value = "HEAD")
    // )
    // private void eclipticseasons$onRemove(Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
    //     EclipticSeasons.logger(asState(),newState,eclipticseasons$tickType);
    // }

    // @Inject(at = {@At(value = "HEAD")},
    //         method = {"entityInside"})
    // public void eclipticseasons$entityInside(Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
    //     if (entity instanceof LivingEntity livingEntity
    //             && level instanceof ServerLevel serverLevel) {
    //         int flag = MapChecker.getBlockTypeFlag(level, pos, asState());
    //         if (flag != MapChecker.FLAG_NONE
    //                 && flag != MapChecker.FLAG_BLOCK
    //                 && level.getRandom().nextInt(48) == 0
    //                 && SnowyMapChecker.shouldCheckSnowyStatus(serverLevel, pos)) {
    //             SnowyMapChecker.removeSnowyStatus(serverLevel, pos);
    //         }
    //     }
    // }
}
