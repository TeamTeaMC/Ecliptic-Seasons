package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.client.core.model.ModelBakerySubsystem;
import me.cortex.voxy.common.world.other.Mapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ModelBakerySubsystem.class})
public abstract class MixinModelBakerySubsystem {


    @Shadow(remap = false)
    @Final
    private Mapper mapper;

    @Definition(id = "mapper", field = "Lme/cortex/voxy/client/core/model/ModelBakerySubsystem;mapper:Lme/cortex/voxy/common/world/other/Mapper;")
    @Definition(id = "getBlockStateCount", method = "Lme/cortex/voxy/common/world/other/Mapper;getBlockStateCount()I")
    @Expression("this.mapper.getBlockStateCount()<?")
    @WrapOperation(
            remap = false,
            method = "requestBlockBake",
            at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private boolean eclipticseasons$requestBlockBake(int left, int right,
                                                     Operation<Boolean> original, @Local(argsOnly = true) int blockId) {
        return original.call(left,VoxyTool.fixId(mapper,right));
    }


}
