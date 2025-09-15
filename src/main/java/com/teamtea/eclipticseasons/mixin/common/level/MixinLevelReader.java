package com.teamtea.eclipticseasons.mixin.common.level;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LevelReader.class})
public interface MixinLevelReader {

    @ModifyExpressionValue(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getSkyDarken()I")},
            method = {"getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;)I"})
    default int eclipticseasons$getMaxLocalRawBrightness(int amount, @Local(argsOnly = true) BlockPos pPos) {
        if (this instanceof Level level
                && EclipticUtil.hasLocalWeather(level)
                // && MapChecker.isLoadNearByOnlyServer(level, pPos)
        ) {
            amount = WeatherManager.getSkyDarken(level, pPos, amount);
        }
        return amount;
    }

}
