package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

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

    public static float getTempAt(Level level, BlockPos pos) {
        var biome = level.getBiome(pos);
        float bt = biome.value().getModifiedClimateSettings().temperature();
        bt += EclipticUtil.getNowSolarTerm(level).getTemperatureChange();
        return bt;
    }

    public static float getBiomeDownFall(Level level, BlockPos pos) {
        var biome = level.getBiome(pos);
        float bt = biome.value().getModifiedClimateSettings().downfall();
        return bt;
    }

    public static float getHumidityAt(Level level, BlockPos pos) {
        var biome = level.getBiome(pos);
        float bt = biome.value().getModifiedClimateSettings().downfall();
        float bt2 = biome.value().getModifiedClimateSettings().temperature();
        bt2 += EclipticUtil.getNowSolarTerm(level).getTemperatureChange();
        return Mth.clamp(bt, 0.0F, 1.0F) * Mth.clamp(bt2, 0.0F, 1.0F);
    }
}
