package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import org.jspecify.annotations.Nullable;
import java.util.List;

public class WeatherUtil {
    public static boolean isBlockInRainOrSnow(Level level, BlockPos blockPos) {
        for (BlockPos pos : List.of(blockPos.above(), blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west())) {
            // if (WeatherManager.isRainingAt((ServerLevel) level, pos))
            // if (WeatherManager.getPrecipitationAt(level, MapChecker.getSurfaceBiome(level, pos).value(), pos) != Biome.Precipitation.NONE)
            //     return true;
            if (WeatherManager.isRainingOrSnowAtBiome(level, MapChecker.getSurfaceBiome(level, pos)))
                return true;
        }
        return false;
    }

    public static boolean isEntityInRainOrSnow(LivingEntity entity) {
        // return WeatherManager.isRainingAt((ServerLevel) entity.level(), entity.blockPosition());
        BlockPos blockPos = entity.blockPosition();
        var pos2 = BlockPos.containing(blockPos.getX(), entity.getBoundingBox().maxY, blockPos.getZ());
        // var pre = WeatherManager.getPrecipitationAt(entity.level(), MapChecker.getSurfaceBiome(entity.level(), blockPos).value(), blockPos);
        // var after = WeatherManager.getPrecipitationAt(entity.level(), MapChecker.getSurfaceBiome(entity.level(), pos2).value(), pos2);
        //
        //
        // return pre != Biome.Precipitation.NONE ||
        //         after != Biome.Precipitation.NONE;
        // return entity.isInWaterOrRain();
        return WeatherManager.isRainingOrSnowAt(entity.level(), blockPos)
                || WeatherManager.isRainingOrSnowAt(entity.level(), pos2);
    }


    public static @Nullable WeatherEffect getWeatherEffectByEntity(Entity entity) {
        if (entity == null) return null;
        Level level = entity.level();
        if (EclipticUtil.hasLocalWeather(level)) {
            BlockPos containing = BlockPos.containing(entity.getEyePosition());
            WeatherManager.BiomeWeather biomeWeather =
                    WeatherManager.getBiomeWeather(level, MapChecker.getSurfaceBiome(level, containing));
            if (biomeWeather != null && biomeWeather.effect != null) {
                return biomeWeather.effect.value();
            }
        }
        return null;
    }
}
