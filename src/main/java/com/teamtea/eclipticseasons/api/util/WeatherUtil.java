package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeatherUtil {
    public static boolean isBlockInRain(World level, BlockPos blockPos) {
        for (BlockPos pos : Stream.of(blockPos.above(), blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()).collect(Collectors.toList())) {
            if (WeatherManager.isRainingAt((ServerWorld) level, pos))
                return true;
        }
        return false;
    }

    public static boolean isEntityInRain(LivingEntity entity) {
        // return WeatherManager.isRainingAt((ServerLevel) entity.level(), entity.blockPosition());
        BlockPos blockpos = entity.blockPosition();
        return entity.level.isRainingAt(blockpos)
                || entity.level.isRainingAt(new BlockPos(blockpos.getX(), entity.getBoundingBox().maxY, blockpos.getZ()));
        // return entity.isInWaterOrRain();
    }

    @Deprecated
    public static float getTempAt(World level, BlockPos pos) {
        Biome biome = level.getBiome(pos);
        float bt = biome.getBaseTemperature();
        bt += EclipticUtil.getNowSolarTerm(level).getTemperatureChange();
        return bt;
    }

    @Deprecated
    public static float getBiomeDownFall(World level, BlockPos pos) {
        Biome biome = level.getBiome(pos);
        float bt = biome.getDownfall();
        return bt;
    }

    @Deprecated
    public static float getHumidityAt(World level, BlockPos pos) {
        Biome biome = level.getBiome(pos);
        float bt = biome.getDownfall();
        float bt2 = biome.getBaseTemperature();
        bt2 += EclipticUtil.getNowSolarTerm(level).getTemperatureChange();
        return MathHelper.clamp(bt, 0.0F, 1.0F) * MathHelper.clamp(bt2, 0.0F, 1.0F);
    }
}
