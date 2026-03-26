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

@Mixin({AttributeTrackSampler.class})
public abstract class MixinAttributeTrackSampler {

    @Shadow
    @Final
    private Holder<WorldClock> clock;
    @Shadow
    @Final
    private ClockManager clockManager;

    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/KeyframeTrackSampler;sample(J)Ljava/lang/Object;")}, method = {"applyTimeBased"})
    public <T> T eclipticseasons$getTotalTicks(KeyframeTrackSampler<T> instance, long totalTicks, Operation<T> original) {
        totalTicks = SolarTime.getTotalTicks(clock, totalTicks, instance.periodTicks.orElse(-1));
        return original.call(instance, totalTicks);
    }

}
