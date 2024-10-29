package com.teamtea.eclipticseasons.client.color.season;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.BirchLeavesColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.LeaveColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.MangroveLeavesColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.SpruceLeavesColor;
import com.teamtea.eclipticseasons.api.util.EclipticTagClientTool;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.color.SolarTermColor;
import com.teamtea.eclipticseasons.api.constant.tag.SeasonTypeBiomeTags;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.core.ColorHelper;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class BiomeColorsHandler {
    // public static int[] newFoliageBuffer = new int[65536];
    // public static int[] newGrassBuffer = new int[65536];
    public static Map<TagKey<Biome>, int[]> newFoliageBufferMap = new HashMap<>();
    public static Map<TagKey<Biome>, int[]> newGrassBufferMap = new HashMap<>();

    public static boolean needRefresh = false;

    public static final ColorResolver GRASS_COLOR = (biome, posX, posZ) ->
    {
        // if(true)return 9680335;
        var clientLevel = Minecraft.getInstance().level;
        if (clientLevel != null) {
            int originColor = biome.getGrassColor(posX, posZ);
            return SolarHolders.getSaveDataLazy(clientLevel).map(data ->
            {
                if (needRefresh) {
                    reloadColors();
                }
                // 由于基本温度被更改
                double temperature = Mth.clamp(biome.getModifiedClimateSettings().temperature()+ SimpleUtil.getNowSolarTerm(clientLevel).getTemperatureChange(), 0.0F, 1.0F);
                double humidity = Mth.clamp(biome.getModifiedClimateSettings().downfall(), 0.0F, 1.0F);
                humidity = humidity * temperature;
                int i = (int) ((1.0D - temperature) * 255.0D);
                int j = (int) ((1.0D - humidity) * 255.0D);
                int k = j << 8 | i;

                int[] newGrassBuffer = newGrassBufferMap.getOrDefault(EclipticTagClientTool.getTag(biome), GrassColor.pixels);
                return k > newGrassBuffer.length ? -65281 : newGrassBuffer[k];
            }).orElse(originColor);
        } else return -1;
    };

    public static final ColorResolver FOLIAGE_COLOR = (biome, posX, posZ) ->
    {
        var clientLevel = Minecraft.getInstance().level;

        if (clientLevel != null) {
            int originColor = biome.getFoliageColor();
            return SolarHolders.getSaveDataLazy(clientLevel).map(data ->
            {
                if (needRefresh) {
                    reloadColors();
                }
                double temperature = Mth.clamp(biome.getModifiedClimateSettings().temperature()+ SimpleUtil.getNowSolarTerm(clientLevel).getTemperatureChange(), 0.0F, 1.0F);
                double humidity = Mth.clamp(biome.getModifiedClimateSettings().downfall(), 0.0F, 1.0F);
                humidity = humidity * temperature;
                int i = (int) ((1.0D - temperature) * 255.0D);
                int j = (int) ((1.0D - humidity) * 255.0D);
                int k = j << 8 | i;
                
                int[] newFoliageBuffer = newFoliageBufferMap.getOrDefault(EclipticTagClientTool.getTag(biome), FoliageColor.pixels);
                return k > newFoliageBuffer.length ? originColor : newFoliageBuffer[k];
            }).orElse(originColor);
        } else return biome.getFoliageColor();
    };

    public static void reloadColors() {
        {
            var clientLevel = Minecraft.getInstance().level;
            if (clientLevel != null) {
                SolarHolders.getSaveDataLazy(clientLevel).ifPresent(data ->
                {
                    for (TagKey<Biome> biomeTagKey : SeasonTypeBiomeTags.BIOMES) {
                        int[] newFoliageBuffer = new int[65536];
                        int[] newGrassBuffer = new int[65536];
                        int[] foliageBuffer = FoliageColor.pixels;
                        int[] grassBuffer = GrassColor.pixels;

                        SolarTerm solar = SolarTerm.get(data.getSolarTermIndex());
                        SolarTermColor colorInfo = solar.getSolarTermColor(biomeTagKey);
                        for (int i = 0; i < foliageBuffer.length; i++) {
                            int originColor = foliageBuffer[i];

                            if (colorInfo.getMix() == 0.0F) {
                                newFoliageBuffer[i] = originColor;
                            } else {
                                newFoliageBuffer[i] = ColorHelper.simplyMixColor(colorInfo.getBirchColor(), colorInfo.getMix(), originColor, 1.0F - colorInfo.getMix());
                            }
                        }

                        for (int i = 0; i < grassBuffer.length; i++) {
                            int originColor = grassBuffer[i];
                            if (colorInfo.getMix() == 0.0F) {
                                newGrassBuffer[i] = originColor;
                            } else {
                                newGrassBuffer[i] = ColorHelper.simplyMixColor(colorInfo.getColor(), colorInfo.getMix(), originColor, 1.0F - colorInfo.getMix());
                            }
                        }
                        newFoliageBufferMap.put(biomeTagKey, newFoliageBuffer);
                        newGrassBufferMap.put(biomeTagKey, newGrassBuffer);
                    }

                    needRefresh = false;
                });
            }
        }
    }

    // 当天气变得寒冷时，云杉可能会显得稍微暗淡一些。
    public static int getSpruceColor(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int tintIndex) {
        return getLeavesColor(FoliageColor.getEvergreenColor(), SpruceLeavesColor.values(), pos);
    }

    // 白桦在秋季通常会变色。它的叶子从绿色变成黄色或金色，有时甚至带有橙色的色调
    public static int getBirchColor(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int tintIndex) {
        return getLeavesColor(FoliageColor.getBirchColor(), BirchLeavesColor.values(), pos);
    }

    // 通常不会经历明显的季节性颜色变化，但是红树很难接受低温，这里因此可以改一下颜色,暗绿色或带棕色调
    public static int getMangroveColor(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int tintIndex) {
        return getLeavesColor(FoliageColor.getMangroveColor(), MangroveLeavesColor.values(), pos);
    }


    public static int getLeavesColor(int base, LeaveColor[] values, BlockPos pos) {
        if (ClientConfig.Renderer.seasonalGrassColorChange.get()) {
            if (pos != null &&
                    Minecraft.getInstance().level instanceof ClientLevel
                    && MapChecker.isValidDimension( Minecraft.getInstance().level)) {
                SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm( Minecraft.getInstance().level);
                LeaveColor leaveColor = values[solarTerm.ordinal()];
                return ColorHelper.simplyMixColor(leaveColor.getColor(), leaveColor.getMix(),
                        base, 1 - leaveColor.getMix());
            }
        }
        return base;
    }
}
