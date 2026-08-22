package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.client.core.rendering.building.RenderDataFactory;
import me.cortex.voxy.common.world.WorldEngine;
import me.cortex.voxy.common.world.WorldSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Applies seasonal state to a temporary copy immediately before mesh generation.
 * This class keeps its old name because it is already registered in the mixin json.
 */
@Mixin(RenderDataFactory.class)
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public abstract class MixinWorldUpdater {

    @Shadow
    @Final
    private WorldEngine world;

    @ModifyExpressionValue(
            remap = false,
            method = "generateMesh",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/cortex/voxy/common/world/WorldSection;_unsafeGetRawDataArray()[J"
            )
    )
    private long[] eclipticseasons$applySeasonalRenderData(long[] original, @Local(argsOnly = true) WorldSection section) {
        return VoxyTool.createSeasonalRenderData(world, section, original);
    }
}
