package com.teamtea.eclipticseasons.mixin.common.block;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireBlock.class)
public class MixinFireBlock {

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z", ordinal = 0)
    )
    private boolean mixin$Tick_isRaining(ServerLevel level, Operation<Boolean> original, @Local(ordinal = 0) BlockPos blockPos) {
        if (EclipticUtil.hasLocalWeather(level))
            return level.isRainingAt(blockPos);
        else return original.call(level);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z", ordinal = 1)
    )
    private boolean mixin$Tick_isRaining2(ServerLevel level, Operation<Boolean> original, @Local(ordinal = 0) BlockPos.MutableBlockPos blockPos) {
        if (EclipticUtil.hasLocalWeather(level))
            return level.isRainingAt(blockPos);
        else return original.call(level);
    }

}
