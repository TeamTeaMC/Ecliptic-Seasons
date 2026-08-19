package com.teamtea.eclipticseasons.mixin.common.level;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.environment.SolarTime;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyframeTrackSampler;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.timeline.AttributeTrackSampler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({AttributeTrackSampler.class})
public abstract class MixinAttributeTrackSampler {

    @Shadow
    @Final
    private Holder<WorldClock> clock;
    @Shadow
    @Final
    private ClockManager clockManager;

    @Shadow
    @Final
    private KeyframeTrackSampler<?> argumentSampler;

    @ModifyArg(
            method = "applyTimeBased",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/KeyframeTrackSampler;sample(J)Ljava/lang/Object;"
            ),
            index = 0
    )
    private long eclipticseasons$modifyTotalTicks(long totalTicks) {
        return SolarTime.getTotalTicks(this.clock, totalTicks, this.argumentSampler.periodTicks.orElse(-1));
    }

}
