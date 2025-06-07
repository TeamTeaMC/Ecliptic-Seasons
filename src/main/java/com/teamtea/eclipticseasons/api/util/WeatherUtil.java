package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

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
                ||WeatherManager.isRainingOrSnowAt(entity.level(), pos2);
    }


    // @Deprecated(forRemoval = true)
    // public static float getTempAt(Level level, BlockPos pos) {
    //     var biome = level.getBiome(pos);
    //     float bt = biome.value().getModifiedClimateSettings().temperature();
    //     bt += EclipticUtil.getNowSolarTerm(level).getTemperatureChange();
    //     return bt;
    // }
    //
    // @Deprecated(forRemoval = true)
    // public static float getBiomeDownFall(Level level, BlockPos pos) {
    //     var biome = level.getBiome(pos);
    //     float bt = biome.value().getModifiedClimateSettings().downfall();
    //     return bt;
    // }
    //
    // @Deprecated(forRemoval = true)
    // public static float getHumidityAt(Level level, BlockPos pos) {
    //     var biome = level.getBiome(pos);
    //     float bt = biome.value().getModifiedClimateSettings().downfall();
    //     float bt2 = biome.value().getModifiedClimateSettings().temperature();
    //     bt2 += EclipticUtil.getNowSolarTerm(level).getTemperatureChange();
    //     return Mth.clamp(bt, 0.0F, 1.0F) * Mth.clamp(bt2, 0.0F, 1.0F);
    // }
}
