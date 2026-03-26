package com.teamtea.eclipticseasons.mixin.common.block;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.WeatherUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeehiveBlockEntity.class)
public class MixinBeehiveBlockEntity {

    // @WrapOperation(
    //         method = "releaseOccupant",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")
    // )
    // private static boolean mixin$releaseOccupantCheckRain(Level level, Operation<Boolean> original, @Local(ordinal = 0) BlockPos blockPos) {
    //     if (EclipticUtil.hasLocalWeather(level))
    //         return WeatherUtil.isBlockInRainOrSnow(level, blockPos);
    //     else return original.call(level);
    // }


}
