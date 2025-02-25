package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO:全局雨量控制表
public class ClientWeatherChecker {

    public static List<SimplePair<Biome, Long>> lastRainyBiome = new ArrayList<>();

    public static float lastBiomeRainLevel = -1;
    public static float lastBiomeRThunderLevel = -1;
    public static float nowBiomeRainLevel = 0;
    public static int changeTime = 0;
    public static long lastTime = 0;
    public static int changeTime_thunder = 0;
    public static int MAX_CHANGE_TIME = 200;

    public static boolean updateForPlayerLogin = true;

    public static float rate = 0.008f;

    public static boolean isNear(float a, float b, float interval) {
        return Math.abs(a - b) < interval;
    }

    public static Boolean isRain(ClientWorld clientLevel) {
        return (double) getRainLevel(clientLevel, 1.0F) > 0.2D;
    }

    public static float getRainLevel(ClientWorld clientLevel, float p46723) {

        // 初始小于0会导致出现暗角
        if (updateForPlayerLogin) {
            if (Minecraft.getInstance().player != null) {
                updateForPlayerLogin = false;
                lastBiomeRainLevel = -1;
            }
        }

        if (lastBiomeRainLevel < 0) {
            lastBiomeRainLevel =
                    Minecraft.getInstance().player != null ?
                            getStandardRainLevel(1f, clientLevel, clientLevel.getBiome(Minecraft.getInstance().player.blockPosition()))
                            :
                            getStandardRainLevel(1f, clientLevel, null);
        }
        return lastBiomeRainLevel;
    }

    public static float getStandardRainLevel(float p46723, ClientWorld clientLevel, Biome biomeHolder) {
        ArrayList<WeatherManager.BiomeWeather> lists = WeatherManager.getBiomeList(clientLevel);
        if (lists != null) {
            for (WeatherManager.BiomeWeather biomeWeather : lists) {
                if (biomeWeather.biomeHolder == biomeHolder) {
                    return biomeWeather.rainTime > 0 ? 1f : 0.0f;
                }
            }
            // ResourceLocation key = clientLevel.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biomeHolder);
            // if(key!=null){
            //     for (WeatherManager.BiomeWeather biomeWeather : lists) {
            //         if (key .equals(biomeWeather.location) ) {
            //             return biomeWeather.rainTime > 0 ? 1f : 0.0f;
            //         }
            //     }
            // }
        }
        return 0.0f;
    }

    public static float updateRainLevel(ClientWorld clientLevel, float p46723) {
        float rainLevel = getStandardRainLevel(p46723, clientLevel, null);
        if (Minecraft.getInstance().player != null) {
            BlockPos pos = Minecraft.getInstance().player.blockPosition();
            for (BlockPos blockPos : Stream.of(pos.east(4), pos.north(4), pos.south(4), pos.west(4)).collect(Collectors.toList())) {
                Biome standBiome = clientLevel.getBiome(blockPos);
                float orainLevel = getStandardRainLevel(p46723, clientLevel, standBiome);
                if (orainLevel > rainLevel) {
                    rainLevel = orainLevel;
                }
            }

            if (changeTime > 0) {
                changeTime--;

                if (lastBiomeRainLevel >= 0 && !isNear(rainLevel, lastBiomeRainLevel, 0.01f)) {
                    float add = rate * ((rainLevel - lastBiomeRainLevel) > 0 ? 1 : -1);
                    lastBiomeRainLevel += add;
                    rainLevel = lastBiomeRainLevel;
                }
                {
                    lastBiomeRainLevel = rainLevel;
                }

                lastBiomeRainLevel = MathHelper.clamp(rainLevel, 0.0F, 1.0F);

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

    public static float getStandardThunderLevel(float p46723, ClientWorld clientLevel, Biome biomeHolder) {
        ArrayList<WeatherManager.BiomeWeather> lists = WeatherManager.getBiomeList(clientLevel);
        if (lists != null)
            for (WeatherManager.BiomeWeather biomeWeather : lists) {
                if (biomeWeather.biomeHolder == biomeHolder) {
                    return biomeWeather.thunderTime > 0 ? 1.0f : 0.0f;
                }
            }
        return 0.0f;
    }

    public static Boolean isThundering(ClientWorld clientLevel) {
        return (double) getThunderLevel(clientLevel, 1.0F) > 0.2D;
    }

    public static float getThunderLevel(ClientWorld clientLevel, float p46723) {

        if (updateForPlayerLogin) {
            if (Minecraft.getInstance().player != null) {
                lastBiomeRainLevel = -1;
            }
        }
        if (lastBiomeRThunderLevel < 0) {
            lastBiomeRThunderLevel =
                    Minecraft.getInstance().player != null ?
                            getStandardThunderLevel(1f, clientLevel, clientLevel.getBiome(Minecraft.getInstance().player.blockPosition()))
                            :
                            getStandardThunderLevel(1f, clientLevel, null);
        }
        return lastBiomeRThunderLevel;
    }

    public static float updateThunderLevel(ClientWorld clientLevel, float p46723) {
        float thunderLevel = getStandardThunderLevel(p46723, clientLevel, null);
        if (Minecraft.getInstance().player != null) {
            BlockPos pos = Minecraft.getInstance().player.blockPosition();
            for (BlockPos blockPos : Stream.of(pos.east(4), pos.north(4), pos.south(4), pos.west(4)).collect(Collectors.toList())) {
                Biome standBiome = clientLevel.getBiome(blockPos);
                float orainLevel = getStandardThunderLevel(p46723, clientLevel, standBiome);
                if (orainLevel > thunderLevel) {
                    thunderLevel = orainLevel;
                }
            }

            if (changeTime_thunder > 0) {
                changeTime_thunder--;
                if (lastBiomeRThunderLevel >= 0 && !isNear(thunderLevel, lastBiomeRThunderLevel, 0.01f)) {
                    float add = rate * ((thunderLevel - lastBiomeRThunderLevel) > 0 ? 1 : -1);
                    lastBiomeRThunderLevel += add;
                    thunderLevel = lastBiomeRThunderLevel;
                }
                lastBiomeRThunderLevel = MathHelper.clamp(thunderLevel, 0.0F, 1.0F);
            } else {
                if (thunderLevel != lastBiomeRThunderLevel) {
                    changeTime_thunder = MAX_CHANGE_TIME;
                    thunderLevel = lastBiomeRThunderLevel;
                }
            }
        }
        return thunderLevel;
    }


    public static Boolean isRainingAt(ClientWorld clientLevel, BlockPos blockPos) {
        if (!clientLevel.canSeeSky(blockPos)) {
            return false;
        } else if (clientLevel.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
            return false;
        }
        return clientLevel.getBiome(blockPos).getPrecipitation() == Biome.RainType.RAIN;
    }

    public static boolean isThunderAt(ClientWorld clientLevel, BlockPos blockPos) {
        if (!clientLevel.canSeeSky(blockPos)) {
            return false;
        } else if (clientLevel.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
            return false;
        }
        return getStandardThunderLevel(1.0f, clientLevel, clientLevel.getBiome(blockPos)) > 0.9f;
    }

    public static int ModifySnowAmount(int constant, float pPartialTick, ClientWorld level) {
        if (level == null) return constant;
        return (int) (constant * MathHelper.clamp(level.getRainLevel(pPartialTick) * 0.6f, 0.6f, 1f));
    }

    public static float modifyVolume(SoundEvent soundEvent, float pVolume, ClientWorld level) {
        if (level == null) return pVolume;
        return pVolume * level.getRainLevel(1.0f) * 0.55f;
    }

    public static float modifyPitch(SoundEvent soundEvent, float pPitch, ClientWorld level) {
        if (level == null) return pPitch;
        return pPitch * level.getRainLevel(1.0f);
        // return pPitch;
    }

    public static int modifyRainAmount(int originalNum, ClientWorld level) {
        if (level == null) return originalNum;
        return (int) (originalNum * level.getRainLevel(1.0f) * 0.6f);
    }

    public static void unloadLevel(ClientWorld clientLevel) {
        lastBiomeRThunderLevel = -1;
        lastBiomeRainLevel = -1;
        updateForPlayerLogin = true;
        lastRainyBiome.clear();
    }

    public static void tickAllCheck(ClientWorld clientLevel) {
        // if (!EclipticUtil.isSolarWeatherClosed())
        {
            updateRainLevel(clientLevel, 0);
            updateThunderLevel(clientLevel, 0);
            tickLastRainyBiome(clientLevel);
        }
    }

    public static void addLastRainyBiome(Biome biome, long gameTime) {
        lastRainyBiome.removeIf(biomeLongSimplePair -> biomeLongSimplePair.getKey() == biome);
        lastRainyBiome.add(SimplePair.of(biome, gameTime));
    }

    public static boolean isBiomeRainyLast(Biome biome) {
        return lastRainyBiome.stream().anyMatch(biomeLongEntry -> biomeLongEntry.getKey() == biome);
    }

    public static void tickLastRainyBiome(ClientWorld clientLevel) {
        for (int i = 0; i < lastRainyBiome.size(); i++) {
            SimplePair<Biome, Long> biomeLongSimplePair = lastRainyBiome.get(i);
            biomeLongSimplePair.setValue(biomeLongSimplePair.getValue() - 1);
            if (biomeLongSimplePair.getValue() <= 0) {
                lastRainyBiome.remove(i);
                i--;
            }
        }
    }
}
