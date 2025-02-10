package com.teamtea.eclipticseasons.common.core.crop;


import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.common.handler.SolarUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

public final class CropGrowthHandler {
    public static void beforeCropGrowUp(BlockEvent.CropGrowEvent.Pre event) {
        Block block = event.getState().getBlock();
        var world = event.getWorld();
        BlockPos pos = event.getPos();
        CropSeasonInfo seasonInfo = CropInfoManager.getSeasonInfo(block);
        if (seasonInfo != null && CommonConfig.Season.enableCrop.get()) {
            if (seasonInfo.isSuitable(SolarUtil.getSeason((Level) world))) {
                Humidity env = Humidity.getHumid(world.getBiome(pos).value().getDownfall(), world.getBiome(pos).value().getTemperature(pos));
                CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
                checkHumidity(event, world, humidityInfo, env);
            } else if (world.getRandom().nextInt(100) < 25) {
                Humidity env = Humidity.getHumid(world.getBiome(pos).value().getDownfall(), world.getBiome(pos).value().getTemperature(pos));
                CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
                checkHumidity(event, world, humidityInfo, env);
            } else {
                event.setResult(Event.Result.DENY);
            }
        } else {
            Humidity env = Humidity.getHumid(world.getBiome(pos).value().getDownfall(), world.getBiome(pos).value().getTemperature(pos));
            CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
            checkHumidity(event, world, humidityInfo, env);
        }
    }

    public static void checkHumidity(BlockEvent.CropGrowEvent.Pre event, LevelAccessor world, CropHumidityInfo humidityInfo, Humidity env) {
        if (humidityInfo != null && CommonConfig.Season.enableCropHumidityControl.get()) {
            float f = humidityInfo.getGrowChance(env);
            if (f == 0) {
                event.setResult(Event.Result.DENY);
            } else if (f > 1.0F) {
                event.setResult(Event.Result.ALLOW);
            } else {
                if (world.getRandom().nextInt(1000) < 1000 * f) {
                    event.setResult(Event.Result.DEFAULT);
                } else {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }
}
