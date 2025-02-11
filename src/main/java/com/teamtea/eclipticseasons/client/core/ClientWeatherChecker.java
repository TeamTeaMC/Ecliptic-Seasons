package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

// TODO:全局雨量控制表
public class ClientWeatherChecker {

    public static float lastBiomeRainLevel = -1;
    public static float lastBiomeRThunderLevel = -1;
    public static float nowBiomeRainLevel = 0;
    public static int changeTime = 0;
    public static int changeTime_thunder = 0;
    public static int MAX_CHANGE_TIME = 200;

    public static boolean isNear(float a, float b, float interval) {
        return Math.abs(a - b) < interval;
    }


    public static Boolean isRain(ClientLevel clientLevel) {
        return (double) getRainLevel(clientLevel, 1.0F) > 0.2D;
    }


    public static Float getStandardRainLevel(float p46723, ClientLevel clientLevel, Holder<Biome> biomeHolder) {
        // if (biomeHolder != null && biomeHolder.is(Tags.Biomes.IS_DESERT)) {
        //     return 0.0f;
        // }
        // return Mth.lerp(p46723, clientLevel.oRainLevel, clientLevel.rainLevel);
        var lists = WeatherManager.getBiomeList(clientLevel);
        if (lists != null)
            for (WeatherManager.BiomeWeather biomeWeather : lists) {
                if (biomeWeather.biomeHolder == biomeHolder) {
                    //  0.2<f<3(2.3)
                    return biomeWeather.rainTime > 0 ? 1f : 0.0f;
                }
            }
        return 0.0f;
    }

    //   TODO：net.minecraft.client.renderer.LevelRenderer.renderSnowAndRain 可以参考平滑方式
    public static Float getRainLevel(ClientLevel clientLevel, float p46723) {
        // if (Minecraft.getInstance().cameraEntity instanceof Player player &&clientLevel.getBiome(Minecraft.getInstance().cameraEntity.getOnPos()).is(Biomes.PLAINS) )return 0.01f;
        float rainLevel = getStandardRainLevel(p46723, clientLevel, null);
        if (Minecraft.getInstance().cameraEntity instanceof Player player) {
            // Ecliptic.logger(clientLevel.getNoiseBiome((int) player.getX(), (int) player.getY(), (int) player.getZ()));
            // TODO：根据群系过渡计算雨量（也许需要维护一个群系位置）,目前设置为时间平滑
            var pos = player.getOnPos();
            for (BlockPos blockPos : List.of(pos.east(4), pos.north(4), pos.south(4), pos.west(4))) {
                var standBiome = clientLevel.getBiome(blockPos);
                float orainLevel = getStandardRainLevel(p46723, clientLevel, standBiome);
                if (orainLevel > rainLevel) {
                    rainLevel = orainLevel;
                }
            }

            if (changeTime > 0) {
                changeTime--;
                if (lastBiomeRainLevel >= 0 && !isNear(rainLevel, lastBiomeRainLevel, 0.01f)) {
                    rainLevel = rainLevel + (lastBiomeRainLevel - rainLevel) * 0.99f;
                }
                // else
                {
                    lastBiomeRainLevel = rainLevel;
                    // Ecliptic.logger(lastBiomeRainLevel,rainLevel);
                }


            } else {
                if (rainLevel != lastBiomeRainLevel) {
                    // 设置了一个极限时间，可能需要看情况
                    changeTime = MAX_CHANGE_TIME;
                    rainLevel = lastBiomeRainLevel;
                }
            }
        }
        return rainLevel;
    }

    public static Float getStandardThunderLevel(float p46723, ClientLevel clientLevel, Holder<Biome> biomeHolder) {
        var lists = WeatherManager.getBiomeList(clientLevel);
        if (lists != null)
            for (WeatherManager.BiomeWeather biomeWeather : lists) {
                if (biomeWeather.biomeHolder == biomeHolder) {
                    return biomeWeather.thunderTime > 0 ? 1.0f : 0.0f;
                }
            }
        return 0.0f;
    }

    public static Boolean isThundering(ClientLevel clientLevel) {
        return (double) getThunderLevel(clientLevel, 1.0F) > 0.2D;
    }


    //   TODO：net.minecraft.client.renderer.LevelRenderer.renderSnowAndRain 可以参考平滑方式
    public static Float getThunderLevel(ClientLevel clientLevel, float p46723) {
        float thunderLevel = getStandardThunderLevel(p46723, clientLevel, null);
        if (Minecraft.getInstance().cameraEntity instanceof Player player) {
            // TODO：根据群系过渡计算雨量（也许需要维护一个群系位置）,目前设置为时间平滑
            var pos = player.getOnPos();
            for (BlockPos blockPos : List.of(pos.east(4), pos.north(4), pos.south(4), pos.west(4))) {
                var standBiome = clientLevel.getBiome(blockPos);
                float orainLevel = getStandardThunderLevel(p46723, clientLevel, standBiome);
                if (orainLevel > thunderLevel) {
                    thunderLevel = orainLevel;
                }
            }

            if (changeTime_thunder > 0) {
                changeTime_thunder--;
                if (lastBiomeRThunderLevel >= 0 && !isNear(thunderLevel, lastBiomeRThunderLevel, 0.01f)) {
                    thunderLevel = thunderLevel + (lastBiomeRThunderLevel - thunderLevel) * 0.99f;
                }
                // else
                {
                    lastBiomeRThunderLevel = thunderLevel;
                }


            } else {
                if (thunderLevel != lastBiomeRThunderLevel) {
                    // 设置了一个极限时间，可能需要看情况
                    changeTime_thunder = MAX_CHANGE_TIME;
                    thunderLevel = lastBiomeRThunderLevel;
                }
            }
        }
        return thunderLevel;
    }


    public static Boolean isRainingAt(ClientLevel clientLevel, BlockPos blockPos) {
        if (!clientLevel.canSeeSky(blockPos)) {
            return false;
        } else if (clientLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
            return false;
        }
        return clientLevel.getBiome(blockPos).value().getPrecipitation() == Biome.Precipitation.RAIN;
    }

    public static boolean isThunderAt(ClientLevel clientLevel, BlockPos blockPos) {
        if (!clientLevel.canSeeSky(blockPos)) {
            return false;
        } else if (clientLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
            return false;
        }
        return getStandardThunderLevel(1.0f, clientLevel, clientLevel.getBiome(blockPos)) > 0.9f;
    }

    // 0-》15
    public static int ModifySnowAmount(int constant) {
        return 15;
    }
}
