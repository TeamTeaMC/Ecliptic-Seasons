package com.teamtea.eclipticseasons.common.core.crop;


import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.common.handler.SolarUtil;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;


public final class CropGrowthHandler {
    public static void beforeCropGrowUp(CropGrowEvent.Pre event) {
        var block = event.getState();
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, block);
    }


    public static void beforeCropGrowUp(BlockGrowFeatureEvent event) {
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, world.getBlockState(pos));
    }

    public static void beforeCropGrowUp(net.neoforged.bus.api.Event event, LevelAccessor world, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();
        CropSeasonInfo seasonInfo = CropInfoManager.getSeasonInfo(block);
        if (seasonInfo != null && ServerConfig.Season.enableCrop.get()) {
            if (seasonInfo.isSuitable(SolarUtil.getSeason((Level) world))) {
                Humidity env = Humidity.getHumid(world.getBiome(pos).value().getModifiedClimateSettings().downfall(), world.getBiome(pos).value().getTemperature(pos));
                CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
                checkHumidity(event, world, humidityInfo, env);
            } else if (ServerConfig.Season.cropGrowChanceInWrongSeason.get()>0
                    &&world.getRandom().nextInt(100) < ServerConfig.Season.cropGrowChanceInWrongSeason.get()*100){
                Humidity env = Humidity.getHumid(world.getBiome(pos).value().getModifiedClimateSettings().downfall(), world.getBiome(pos).value().getTemperature(pos));
                CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
                checkHumidity(event, world, humidityInfo, env);
            } else {
                setResult(event, CANCEL);
            }
        } else {
            Humidity env = Humidity.getHumid(world.getBiome(pos).value().getModifiedClimateSettings().downfall(), world.getBiome(pos).value().getTemperature(pos));
            CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
            checkHumidity(event, world, humidityInfo, env);
        }
    }


    public static void checkHumidity(net.neoforged.bus.api.Event event, LevelAccessor world, CropHumidityInfo humidityInfo, Humidity env) {
        if (humidityInfo != null && ServerConfig.Season.enableCropHumidityControl.get()) {
            float f = humidityInfo.getGrowChance(env);
            if (f == 0) {
                setResult(event, CANCEL);
            } else if (f > 1.0F) {
                setResult(event, GROW);
            } else {
                if (world.getRandom().nextInt(1000) < 1000 * f) {
                    setResult(event, PASS);
                } else {
                    setResult(event, CANCEL);
                }
            }
        }
    }

    public static final int CANCEL = 1;
    public static final int PASS = 2;
    public static final int GROW = 3;


    public static void setResult(net.neoforged.bus.api.Event event, int flag) {
        if (flag == CANCEL) {
            if (event instanceof CropGrowEvent.Pre cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
            } else if (event instanceof BlockGrowFeatureEvent blockGrowFeatureEvent) {
                blockGrowFeatureEvent.setCanceled(true);
            }
        } else if (flag == PASS) {
            if (event instanceof CropGrowEvent.Pre cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.DEFAULT);
            }
        } else if (flag == GROW) {
            if (event instanceof CropGrowEvent.Pre cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.GROW);
            }
        }

    }
}
