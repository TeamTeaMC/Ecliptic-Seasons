package com.teamtea.eclipticseasons.mixin.common.block;


import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.misc.CustomRandomTick;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.hook.ESEventHook;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
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

    @Unique
    public boolean eclipticseasons$forceTickControl = false;

    @Shadow
    public abstract boolean is(TagKey<Block> tag);

    @Shadow
    private boolean isRandomlyTicking;

    @Shadow
    protected abstract BlockState asState();

    @Inject(
            method = "initCache",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$initCache(CallbackInfo ci) {
        if ((Object) this instanceof BlockState) {
            eclipticseasons$forceTickControl = (is(EclipticBlockTags.NATURAL_PLANTS));
            if (!isRandomlyTicking && is(EclipticBlockTags.VOLATILE)) {
                isRandomlyTicking = true;
            }
        }
        if (this instanceof CustomRandomTick) {
            eclipticseasons$reset();
        }
    }

    // @Inject(
    //         method = "onPlace",
    //         at = @At(value = "HEAD")
    // )
    // private void eclipticseasons$onPlace(Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston, CallbackInfo ci) {
    //     if (pLevel instanceof ServerLevel serverLevel)
    //         eclipticseasons$tick(asState(), serverLevel, pPos);
    // }

    @Inject(
            method = "randomTick",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void eclipticseasons$randomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        eclipticseasons$tick(asState(), level, pos);

        if ((Object) this instanceof BlockState blockState
                && (eclipticseasons$forceTickControl || CommonConfig.isForceCropCompatMode())) {
            boolean canCropGrow = ESEventHook.canExtraCropGrow(level, pos, blockState, true);
            if (!canCropGrow) ci.cancel();
        }
    }

    @Unique
    public int eclipticseasons$tickType = -1;

    @Unique
    public List<WetterStructure> eclipticseasons$wetterStructures = null;

    @Override
    public void eclipticseasons$tick(BlockState state, ServerLevel worldIn, BlockPos pos) {
        switch (eclipticseasons$tickType) {
            case 0 -> {
                return;
            }
            case 1 -> {
                CropGrowthHandler.handleRandomTick(worldIn, pos, state, eclipticseasons$wetterStructures);
            }
            default -> {
                List<WetterStructure> wetterStructures = CropGrowthHandler.validTick(state);
                eclipticseasons$tickType = wetterStructures.isEmpty() ? 0 : 1;
                eclipticseasons$wetterStructures = wetterStructures;
                eclipticseasons$tick(state, worldIn, pos);
            }
        }
    }

    @Override
    public void eclipticseasons$reset() {
        eclipticseasons$tickType = -1;
        eclipticseasons$wetterStructures = null;
    }
}
