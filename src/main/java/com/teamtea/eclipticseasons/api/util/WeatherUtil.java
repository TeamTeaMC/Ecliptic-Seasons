package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class WeatherUtil {
    public static boolean isBlockInRain(Level level, BlockPos blockPos) {
        for (BlockPos pos : List.of(blockPos.above(), blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west())) {
            if (level.isRainingAt( pos))
                return true;
        }
        return false;
    }

    public static boolean isEntityInRain(LivingEntity entity) {
        BlockPos blockpos = entity.blockPosition();
        return entity.level().isRainingAt(blockpos)
                || entity.level().isRainingAt(BlockPos.containing(blockpos.getX(), entity.getBoundingBox().maxY, blockpos.getZ()));
    }

    public static boolean isEntityInRainOrSnow(LivingEntity entity) {
        BlockPos blockPos = entity.blockPosition();
        var pos2 = BlockPos.containing(blockPos.getX(), entity.getBoundingBox().maxY, blockPos.getZ());
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
