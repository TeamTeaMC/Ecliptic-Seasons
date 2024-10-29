package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

// TODO:全局雨量控制表
public class ClientWeatherChecker {

    public static float lastBiomeRainLevel = -1;
    public static float lastBiomeRThunderLevel = -1;
    public static float nowBiomeRainLevel = 0;
    public static int changeTime = 0;
    public static long lastTime = 0;
    public static int changeTime_thunder = 0;
    public static int MAX_CHANGE_TIME = 200;

    public static boolean updateForPlayerLogin = false;

    public static boolean isNear(float a, float b, float interval) {
        return Math.abs(a - b) < interval;
    }


    public static Boolean isRain(ClientLevel clientLevel) {
        return (double) getRainLevel(clientLevel, 1.0F) > 0.2D;
    }


    public static float getStandardRainLevel(float p46723, ClientLevel clientLevel, Holder<Biome> biomeHolder) {
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
    public static float getRainLevel(ClientLevel clientLevel, float p46723) {
        // 初始小于0会导致出现暗角
        if (updateForPlayerLogin) {
            if (Minecraft.getInstance().cameraEntity instanceof Player) {
                updateForPlayerLogin = false;
                lastBiomeRainLevel = -1;
            }
        }

        if (lastBiomeRainLevel < 0) {
            lastBiomeRainLevel =
                    Minecraft.getInstance().cameraEntity instanceof Player player ?
                            getStandardRainLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, player.getOnPos().above()))
                            :
                            getStandardRainLevel(1f, clientLevel, null);
        }
        return lastBiomeRainLevel;
    }

    // 后续优化方向为优先计算玩家面朝的方向，这个方向加一个权限。
    public static float updateRainLevel(ClientLevel clientLevel) {
        // if (Minecraft.getInstance().cameraEntity instanceof Player player &&clientLevel.getBiome(Minecraft.getInstance().cameraEntity.getOnPos()).is(Biomes.PLAINS) )return 0.01f;
        float rainLevel = getStandardRainLevel(1f, clientLevel, null);
        if (Minecraft.getInstance().cameraEntity instanceof Player player) {
            // Ecliptic.logger(clientLevel.getNoiseBiome((int) player.getX(), (int) player.getY(), (int) player.getZ()));
            // TODO：根据群系过渡计算雨量（也许需要维护一个群系位置）,目前设置为时间平滑
            var pos = player.getOnPos();
            int offset = ClientConfig.Renderer.weatherBufferDistance.get();

            rainLevel = getStandardRainLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, pos));

            var lookAt = Minecraft.getInstance().hitResult.getLocation();
            var crs = lookAt.subtract(Minecraft.getInstance().getCameraEntity().position());
            lookAt = lookAt.add(crs).add(crs).add(crs);
            var lookPos = BlockPos.containing(lookAt);
            rainLevel += getStandardRainLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, lookPos)) * 2;


            for (BlockPos blockPos : List.of(pos.east(offset), pos.north(offset), pos.south(offset), pos.west(offset))) {
                // var standBiome = clientLevel.getBiome(blockPos);
                var standBiome = MapChecker.getSurfaceBiome(clientLevel, blockPos);

                float orainLevel = getStandardRainLevel(1f, clientLevel, standBiome);
                // if (orainLevel > rainLevel) {
                //     rainLevel = orainLevel;
                // }
                rainLevel += orainLevel;
            }
            rainLevel = rainLevel / 7f;


            if (changeTime > 0) {
                changeTime--;

                if (lastBiomeRainLevel >= 0 && !isNear(rainLevel, lastBiomeRainLevel, 0.01f)) {
                    // rainLevel = rainLevel + (lastBiomeRainLevel - rainLevel) * 0.99f;
                    // rainLevel = rainLevel + (lastBiomeRainLevel - rainLevel) * 0.99f;
                    float add = 0.008f * ((rainLevel - lastBiomeRainLevel) > 0 ? 1 : -1);
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

    public static float getStandardThunderLevel(float p46723, ClientLevel clientLevel, Holder<Biome> biomeHolder) {
        var lists = WeatherManager.getBiomeList(clientLevel);
        if (lists != null)
            for (WeatherManager.BiomeWeather biomeWeather : lists) {
                if (biomeWeather.biomeHolder == biomeHolder) {
                    return biomeWeather.rainTime > 0 ? 1.0f : 0.0f;
                }
            }
        return 0.0f;
    }

    public static Boolean isThundering(ClientLevel clientLevel) {
        return (double) getThunderLevel(clientLevel, 1.0F) > 0.2D;
    }


    //   TODO：net.minecraft.client.renderer.LevelRenderer.renderSnowAndRain 可以参考平滑方式
    public static float getThunderLevel(ClientLevel clientLevel, float p46723) {
        if (updateForPlayerLogin) {
            if (Minecraft.getInstance().cameraEntity instanceof Player) {
                lastBiomeRainLevel = -1;
            }
        }
        if (lastBiomeRThunderLevel < 0) {
            lastBiomeRThunderLevel =
                    Minecraft.getInstance().cameraEntity instanceof Player player ?
                            getStandardThunderLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, player.getOnPos().above()))
                            :
                            getStandardThunderLevel(1f, clientLevel, null);
        }
        return lastBiomeRThunderLevel;
    }


    public static float updateThunderLevel(ClientLevel clientLevel) {
        float thunderLevel = getStandardThunderLevel(1f, clientLevel, null);
        if (Minecraft.getInstance().cameraEntity instanceof Player player) {
            var pos = player.getOnPos();
            int offset = ClientConfig.Renderer.weatherBufferDistance.get();
            thunderLevel = getStandardThunderLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, pos));
            var lookAt = Minecraft.getInstance().hitResult.getLocation();
            var crs = lookAt.subtract(Minecraft.getInstance().getCameraEntity().position());
            lookAt = lookAt.add(crs).add(crs).add(crs);
            var lookPos = BlockPos.containing(lookAt);
            thunderLevel += getStandardThunderLevel(1f, clientLevel, MapChecker.getSurfaceBiome(clientLevel, lookPos)) * 2;

            for (BlockPos blockPos : List.of(pos.east(offset), pos.north(offset), pos.south(offset), pos.west(offset))) {
                var standBiome = MapChecker.getSurfaceBiome(clientLevel, blockPos);
                float othunderLevel = getStandardThunderLevel(1f, clientLevel, standBiome);
                thunderLevel += othunderLevel;
            }
            thunderLevel = thunderLevel / 7f;

            if (changeTime_thunder > 0) {
                changeTime_thunder--;
                if (lastBiomeRThunderLevel >= 0 && !isNear(thunderLevel, lastBiomeRThunderLevel, 0.01f)) {
                    float add = 0.008f * ((thunderLevel - lastBiomeRThunderLevel) > 0 ? 1 : -1);
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


    public static Boolean isRainingAt(ClientLevel clientLevel, BlockPos blockPos) {
        if (!clientLevel.canSeeSky(blockPos)) {
            return false;
        } else if (clientLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
            return false;
        }
        return WeatherManager.getPrecipitationAt(clientLevel, MapChecker.getSurfaceBiome(clientLevel, blockPos).value(), blockPos)
                == Biome.Precipitation.RAIN
                && WeatherManager.isRainingOrSnowAtBiome(clientLevel, MapChecker.getSurfaceBiome(clientLevel, blockPos).value());
    }

    public static boolean isThunderAt(ClientLevel clientLevel, BlockPos blockPos) {
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

    public static void unloadLevel(ClientLevel clientLevel) {
        lastBiomeRThunderLevel = -1;
        lastBiomeRainLevel = -1;
        updateForPlayerLogin = true;
    }

    // public static Boolean hasPrecipitation(Biome biome) {
    //     return !EclipticTagClientTool.getTag(biome).equals(SeasonTypeBiomeTags.RAINLESS);
    // }
}
