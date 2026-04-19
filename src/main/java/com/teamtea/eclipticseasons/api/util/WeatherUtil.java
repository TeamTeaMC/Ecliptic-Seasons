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

    public static @Nullable WeatherEffect getWeatherEffectByEntity(Entity entity) {
        if (entity == null) return null;
        Level level = entity.level();
        BlockPos containing = BlockPos.containing(entity.getEyePosition());
        WeatherManager.BiomeWeather biomeWeather =
                WeatherManager.getBiomeWeather(level, MapChecker.getSurfaceBiome(level, containing));
        if (biomeWeather != null && biomeWeather.effect != null) {
            return biomeWeather.effect.value();
        }
        return null;
    }
}
