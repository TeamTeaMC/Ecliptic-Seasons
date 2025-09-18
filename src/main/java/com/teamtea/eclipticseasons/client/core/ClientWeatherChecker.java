package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;

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

    public static float getRate() {
        return ClientConfig.Weather.weatherTransitionSpeed.get().floatValue();
    }

    public static float rate = 0.008f;


    private static boolean isNear(float a, float b, float interval) {
        return Math.abs(a - b) < interval;
    }


    public static boolean isRain(Level clientLevel) {
        return (double) getRainLevel(clientLevel, 1.0F) > 0.2D;
    }


    public static float getStandardRainLevel(float p46723, Level clientLevel, Holder<Biome> biomeHolder) {
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
    public static float getRainLevel(Level clientLevel, float p46723) {
        // 初始小于0会导致出现暗角
        if (updateForPlayerLogin) {
            if (ClientCon.agent.getCameraEntity() instanceof Player) {
                updateForPlayerLogin = false;
                lastBiomeRainLevel = -1;
            }
        }

        if (lastBiomeRainLevel < 0) {
            lastBiomeRainLevel =
                    ClientCon.agent.getCameraEntity() instanceof Player player ?
                            getStandardRainLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, player.getOnPos().above()))
                            :
                            getStandardRainLevel(1f, clientLevel, null);
        }
        return lastBiomeRainLevel;
    }

    // 后续优化方向为优先计算玩家面朝的方向，这个方向加一个权限。
    public static float updateRainLevel(Level clientLevel) {
        float rainLevel = getStandardRainLevel(1f, clientLevel, null);
        if (ClientCon.agent.getCameraEntity() instanceof Player player) {
            var pos = player.getOnPos();
            int offset = ClientConfig.Weather.weatherBufferDistance.get();
            boolean frontUse = ClientConfig.Weather.weatherFrontBias.get();
            float count = 1;

            rainLevel = getStandardRainLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, pos));

            if (frontUse && ClientCon.agent.getHitResult() != null) {
                var lookAt = ClientCon.agent.getHitResult().getLocation();
                var crs = lookAt.subtract(ClientCon.agent.getCameraEntity().position());
                lookAt = lookAt.add(crs.normalize().scale(offset));
                var lookPos = BlockPos.containing(lookAt);
                rainLevel += getStandardRainLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, lookPos)) * 2;
                count += 2;
            }

            for (BlockPos blockPos : new BlockPos[]{pos.east(offset), pos.north(offset), pos.south(offset), pos.west(offset)}) {
                // var standBiome = clientLevel.getBiome(blockPos);
                var standBiome = MapChecker.getSurfaceBiome(clientLevel, blockPos);

                float orainLevel = getStandardRainLevel(1f, clientLevel, standBiome);
                // if (orainLevel > rainLevel) {
                //     rainLevel = orainLevel;
                // }
                rainLevel += orainLevel;
                count++;
            }
            rainLevel = rainLevel / count;

            if (changeTime > 0) {
                changeTime--;

                if (lastBiomeRainLevel >= 0 && !isNear(rainLevel, lastBiomeRainLevel, 0.01f)) {
                    // rainLevel = rainLevel + (lastBiomeRainLevel - rainLevel) * 0.99f;
                    // rainLevel = rainLevel + (lastBiomeRainLevel - rainLevel) * 0.99f;
                    float add = getRate() * ((rainLevel - lastBiomeRainLevel) > 0 ? 1 : -1);
                    lastBiomeRainLevel += add;
                    rainLevel = lastBiomeRainLevel;
                }
                // else
                {
                    lastBiomeRainLevel = rainLevel;
                    // EclipticSeasonsMod.logger(lastBiomeRainLevel,rainLevel);
                }

                lastBiomeRainLevel = Mth.clamp(rainLevel, 0.0F, 1.0F);

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

    public static float getStandardThunderLevel(float p46723, Level clientLevel, Holder<Biome> biomeHolder) {
        var lists = WeatherManager.getBiomeList(clientLevel);
        if (lists != null)
            for (WeatherManager.BiomeWeather biomeWeather : lists) {
                if (biomeWeather.biomeHolder == biomeHolder) {
                    return biomeWeather.thunderTime > 0 ? 1.0f : 0.0f;
                }
            }
        return 0.0f;
    }

    public static boolean isThundering(Level clientLevel) {
        return (double) getThunderLevel(clientLevel, 1.0F) > 0.2D;
    }


    //   TODO：net.minecraft.client.renderer.LevelRenderer.renderSnowAndRain 可以参考平滑方式
    public static float getThunderLevel(Level clientLevel, float p46723) {
        if (updateForPlayerLogin) {
            if (ClientCon.agent.getCameraEntity() instanceof Player) {
                lastBiomeRainLevel = -1;
            }
        }
        if (lastBiomeRThunderLevel < 0) {
            lastBiomeRThunderLevel =
                    ClientCon.agent.getCameraEntity() instanceof Player player ?
                            getStandardThunderLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, player.getOnPos().above()))
                            :
                            getStandardThunderLevel(1f, clientLevel, null);
        }
        return lastBiomeRThunderLevel;
    }


    public static float updateThunderLevel(Level clientLevel) {
        float thunderLevel = getStandardThunderLevel(1f, clientLevel, null);
        if (ClientCon.agent.getCameraEntity() instanceof Player player) {
            var pos = player.getOnPos();
            int offset = ClientConfig.Weather.weatherBufferDistance.get();
            boolean frontUse = ClientConfig.Weather.weatherFrontBias.get();

            thunderLevel = getStandardThunderLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, pos));
            float count = 1;

            if (frontUse && ClientCon.agent.getHitResult() != null) {
                var lookAt = ClientCon.agent.getHitResult().getLocation();
                var crs = lookAt.subtract(ClientCon.agent.getCameraEntity().position());
                lookAt = lookAt.add(crs.normalize().scale(offset));
                var lookPos = BlockPos.containing(lookAt);
                thunderLevel += getStandardThunderLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, lookPos)) * 2;
                count += 2;
            }

            for (BlockPos blockPos : new BlockPos[]{pos.east(offset), pos.north(offset), pos.south(offset), pos.west(offset)}) {
                var standBiome = MapChecker.getSurfaceBiome(clientLevel, blockPos);
                float othunderLevel = getStandardThunderLevel(1f, clientLevel, standBiome);
                thunderLevel += othunderLevel;
                count++;
            }
            thunderLevel = thunderLevel / count;

            if (changeTime_thunder > 0) {
                changeTime_thunder--;
                if (lastBiomeRThunderLevel >= 0 && !isNear(thunderLevel, lastBiomeRThunderLevel, 0.01f)) {
                    float add = getRate() * ((thunderLevel - lastBiomeRThunderLevel) > 0 ? 1 : -1);
                    lastBiomeRThunderLevel += add;
                    thunderLevel = lastBiomeRThunderLevel;
                }
                lastBiomeRThunderLevel = Mth.clamp(thunderLevel, 0.0F, 1.0F);
            } else {
                if (thunderLevel != lastBiomeRThunderLevel) {
                    changeTime_thunder = MAX_CHANGE_TIME;
                    thunderLevel = lastBiomeRThunderLevel;
                }
            }
        }
        return thunderLevel;
    }


    public static boolean isRainingAt(Level clientLevel, BlockPos blockPos) {
        if (!clientLevel.canSeeSky(blockPos)) {
            return false;
        } else if (clientLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
            return false;
        }
        return EclipticUtil.getRainOrSnow(clientLevel, MapChecker.getSurfaceBiome(clientLevel, blockPos).value(), blockPos)
                == Biome.Precipitation.RAIN;
    }

    public static boolean isThunderAt(Level clientLevel, BlockPos blockPos) {
        if (!clientLevel.canSeeSky(blockPos)) {
            return false;
        } else if (clientLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
            return false;
        }
        return getStandardThunderLevel(1.0f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, blockPos)) > 0.9f;
    }

    // 0-》15
    public static int ModifySnowAmount(int constant, float pPartialTick, Level level) {
        if (level == null) return constant;
        return (int) (constant * Mth.clamp(level.getRainLevel(pPartialTick) * 0.6f, 0.6f, 1f));
    }

    public static float modifyVolume(SoundEvent soundEvent, float pVolume, Level level) {
        if (level == null) return pVolume;
        return pVolume * level.getRainLevel(1.0f) * 0.55f;
    }

    public static float modifyPitch(SoundEvent soundEvent, float pPitch, Level level) {
        if (level == null) return pPitch;
        return pPitch * level.getRainLevel(1.0f);
        // return pPitch;
    }

    public static int modifyRainAmount(int originalNum, Level level) {
        if (level == null) return originalNum;
        return (int) (originalNum * level.getRainLevel(1.0f) * 0.6f);
    }

    public static void unloadLevel(Level clientLevel) {
        lastBiomeRThunderLevel = -1;
        lastBiomeRainLevel = -1;
        updateForPlayerLogin = true;
        lastRainyBiome.clear();
    }

    public static void tickAllCheck(Level clientLevel) {
        updateRainLevel(clientLevel);
        updateThunderLevel(clientLevel);
        tickLastRainyBiome(clientLevel);
    }

    public static void addLastRainyBiome(Biome biome, long gameTime) {
        lastRainyBiome.removeIf(biomeLongSimplePair -> biomeLongSimplePair.getKey() == biome);
        lastRainyBiome.add(SimplePair.of(biome, gameTime));
    }

    public static boolean isBiomeRainyLast(Biome biome) {
        for (int i = 0; i < lastRainyBiome.size(); i++) {
            SimplePair<Biome, Long> biomeLongSimplePair = lastRainyBiome.get(i);
            if (biomeLongSimplePair.getKey() == biome) return true;
        }
        return false;
        // return lastRainyBiome.stream().anyMatch(biomeLongEntry -> biomeLongEntry.getKey() == biome);
    }

    public static void tickLastRainyBiome(Level clientLevel) {
        for (int i = 0; i < lastRainyBiome.size(); i++) {
            SimplePair<Biome, Long> biomeLongSimplePair = lastRainyBiome.get(i);
            biomeLongSimplePair.setValue(biomeLongSimplePair.getValue() - 1);
            if (biomeLongSimplePair.getValue() <= 0) {
                lastRainyBiome.remove(i);
                i--;
            }
        }
    }


    // public static boolean hasPrecipitation(Biome biome) {
    //     return !EclipticTagClientTool.getTag(biome).equals(SeasonTypeBiomeTags.RAINLESS);
    // }
}
