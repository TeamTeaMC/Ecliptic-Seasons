package com.teamtea.eclipticseasons.common.core.crop;


import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.common.handler.SolarUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;


public final class CropGrowthHandler {
    public static void beforeCropGrowUp(BlockEvent.CropGrowEvent.Pre event) {
        var block = event.getState();
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, block);
    }


    public static void beforeCropGrowUp(SaplingGrowTreeEvent event) {
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, world.getBlockState(pos));
    }

    public static void beforeCropGrowUp(net.minecraftforge.eventbus.api.Event event, LevelAccessor world, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();
        Holder<Biome> biome = world.getBiome(pos);
        CropSeasonInfo seasonInfo = CropInfoManager.getSeasonInfo(block);
        boolean notCancel = false;
        if (seasonInfo != null && CommonConfig.Crop.enableCrop.get()) {
            notCancel |= seasonInfo.isSuitable(SolarUtil.getSeason((Level) world));
            notCancel |= CommonConfig.Crop.cropGrowChanceInWrongSeason.get() > 0
                    && world.getRandom().nextInt(100) < CommonConfig.Crop.cropGrowChanceInWrongSeason.get() * 100;
            // notCancel |= isInRoom(world, pos, blockState);
        } else {
            notCancel = true;
        }
        if (!notCancel) {
            setResult(event, CANCEL);
        } else {
            Humidity env = Humidity.getHumid(biome.value().getModifiedClimateSettings().downfall(), biome.value().getTemperature(pos));
            CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
            checkHumidity(event, world, humidityInfo, env);
        }
    }


    public static void checkHumidity(net.minecraftforge.eventbus.api.Event event, LevelAccessor world, CropHumidityInfo humidityInfo, Humidity env) {
        if (humidityInfo != null && CommonConfig.Crop.enableCropHumidityControl.get()) {
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


    public static void setResult(net.minecraftforge.eventbus.api.Event event, int flag) {
        if (event.hasResult()) {
            if (flag == CANCEL) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
                    cropGrowEvent.setResult(BlockEvent.CropGrowEvent.Pre.Result.DENY);
                } else if (event instanceof SaplingGrowTreeEvent blockGrowFeatureEvent) {
                    blockGrowFeatureEvent.setResult(Event.Result.DENY);
                }
            } else if (flag == PASS) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
                    cropGrowEvent.setResult(BlockEvent.CropGrowEvent.Pre.Result.DEFAULT);
                }
            } else if (flag == GROW) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
                    cropGrowEvent.setResult(BlockEvent.CropGrowEvent.Pre.Result.ALLOW);
                }
            }
        }

    }
}
