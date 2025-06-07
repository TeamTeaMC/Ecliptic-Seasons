package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.teamtea.eclipticseasons.compat.distanthorizons.DHTool;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.block.TintWithoutLevelOverrider;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({TintWithoutLevelOverrider.class})
public abstract class MixinTintWithoutLevelOverrider {


    @WrapOperation(
            remap = false,
            method = "<init>",
            at = @At(value = "INVOKE", ordinal = 0, target = "LloaderCommon/neoforge/com/seibel/distanthorizons/common/wrappers/block/TintWithoutLevelOverrider;unwrap(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/level/biome/Biome;")
    )
    private Biome eclipticseasons$init_unwrap(Holder<Biome> biome,
                                       Operation<Biome> original,
                                       @Local(argsOnly = true) BiomeWrapper biomeWrapper,
                                       @Local(argsOnly = true) IClientLevelWrapper iClientLevelWrapper) {
        // 也许我们都不喜欢它，但是这必须要修复，否则将会传入DH的缓存Biome，导致我们无法正确读取当前的温度
        // 难道DH不知道它会丢失吗一旦重启关卡，我认为也许他们不在乎
        if (biome.getKey() == null) {
            var biomeObject = DHTool.recoverBiomeObject(biomeWrapper, iClientLevelWrapper);
            if (biomeObject != null)
                return biomeObject;
        }
        return original.call(biome);
    }


}
