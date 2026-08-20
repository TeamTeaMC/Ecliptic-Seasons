package com.teamtea.eclipticseasons.mixin.common.block;


import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.misc.CustomRandomTick;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.ExtraTickType;
import com.teamtea.eclipticseasons.common.core.crop.NaturalPlantHandler;
import com.teamtea.eclipticseasons.common.hook.ESEventHook;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
        // if (this instanceof CustomRandomTick customRandomTick)
        {
            eclipticseasons$reset();
        }

    }

    @WrapWithCondition(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"
            )
    )
    private boolean eclipticseasons$allowRandomTick(Block instance, BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource randomSource) {
        if (this instanceof IBlockStateFlagger flagger
                && (flagger.forceTickControl()
                || CommonConfig.isForceCropCompatMode())) {
            return ESEventHook.canExtraCropGrow(
                    serverLevel,
                    pos,
                    flagger.es$asState(),
                    true
            );
        }
        return true;
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

}
