package com.teamtea.eclipticseasons.mixin.common;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({LevelReader.class})
public interface MixinIWorldReader extends LevelReader {

    /**
     * @author jianzoushihu ( joe vettek)
     * @reason Ecliptic Seasons adjusts the weather system to be localized under Solar Weather conditions,
     * requiring brightness information to be corrected based on block positions.
     */
    @Overwrite
    @Override
    default int getMaxLocalRawBrightness(@NotNull BlockPos pPos) {
        int amount = this.getSkyDarken();
        if (this instanceof Level level && EclipticUtil.hasLocalWeather(level)) {
            amount = WeatherManager.getSkyDarken(level, pPos, amount);
        }
        return this.getMaxLocalRawBrightness(pPos, amount);
    }

}
