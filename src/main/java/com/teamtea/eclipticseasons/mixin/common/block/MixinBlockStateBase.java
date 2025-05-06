package com.teamtea.eclipticseasons.mixin.common.block;


import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
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

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockStateBase {

    @Unique
    public boolean eclipticseasons$forceTickControl = false;

    @Shadow
    public abstract boolean is(TagKey<Block> tag);

    @Shadow
    private boolean isRandomlyTicking;

    @Inject(
            method = "initCache",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$initCache(CallbackInfo ci) {
        if ((Object) this instanceof BlockState) {
            eclipticseasons$forceTickControl = (is(EclipticBlockTags.NATURAL_PLANTS));
            if (!isRandomlyTicking && is(EclipticBlockTags.VOLATILE_PLANTS)) {
                isRandomlyTicking = true;
            }
        }
    }

    @Inject(
            method = "randomTick",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void eclipticseasons$randomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if ((Object) this instanceof BlockState blockState
                && (eclipticseasons$forceTickControl || CommonConfig.isForceCropCompatMode())) {
            boolean canCropGrow = ESEventHook.canExtraCropGrow(level, pos, blockState, true);
            if (!canCropGrow) ci.cancel();
        }
    }
}
