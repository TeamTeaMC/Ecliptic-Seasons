package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.client.core.model.ModelBakerySubsystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ModelBakerySubsystem.class})
public abstract class MixinModelBakerySubsystem {

    @ModifyExpressionValue(
            remap = false,
            method = "requestBlockBake",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/world/other/Mapper;getBlockStateCount()I")
    )
    private int eclipticseasons$requestBlockBake(int original, @Local(argsOnly = true) int blockId) {
        if (original < blockId && VoxyTool.isVoxyTest()) {
            original = blockId + 1;
        }
        return original;
    }


}
