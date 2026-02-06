package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.common.voxelization.ILightingSupplier;
import me.cortex.voxy.common.voxelization.VoxelizedSection;
import me.cortex.voxy.common.voxelization.WorldConversionFactory;
import me.cortex.voxy.common.world.other.Mapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({WorldConversionFactory.class})
public abstract class MixinWorldConversionFactory {

    @WrapOperation(
            remap = false,
            method = "convert",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/world/other/Mapper;composeMappingId(BII)J")
    )
    private static long eclipticseasons$convert(
            byte light, int blockId, int biomeId, Operation<Long> original,
            @Local(argsOnly = true) Mapper stateMapper,
            @Local(argsOnly = true) ILightingSupplier lightSupplier,
            @Local(name = "i") int i,
            @Local(argsOnly = true) VoxelizedSection section) {
        blockId = VoxyTool.changeBlockId(blockId, stateMapper, i, section);
        return original.call(light, blockId, biomeId);
    }


}
