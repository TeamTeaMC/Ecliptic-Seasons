package com.teamtea.eclipticseasons.client.color.season;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.NoneSolarTermColors;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.SolarTermColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.BirchLeavesColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.LeaveColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.MangroveLeavesColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.SpruceLeavesColor;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.client.render.ColorHelper;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;
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

        // if (SimpleUtil.getModsUse(0).getLast().contains("map")){
        //     return MapColor.SNOW.col;
        // }
        int originColor = biome.getGrassColor(posX, posZ);
        if (ClientConfig.Renderer.seasonalGrassColorChange.get()) {
            if (needRefresh) {
                reloadColors();
            }
            // 由于基本温度被更改
            // double temperature = Mth.clamp(biome.getModifiedClimateSettings().temperature() + EclipticUtil.getNowSolarTerm(clientLevel).getTemperatureChange(), 0.0F, 1.0F);

            double temperature = Mth.clamp(biome.getModifiedClimateSettings().temperature(), 0.0F, 1.0F);
            double humidity = Mth.clamp(biome.getModifiedClimateSettings().downfall(), 0.0F, 1.0F);
            humidity = humidity * temperature;
            int i = (int) ((1.0D - temperature) * 255.0D);
            int j = (int) ((1.0D - humidity) * 255.0D);
            int k = j << 8 | i;

            int[] newGrassBuffer = newGrassBufferMap.getOrDefault(BiomeClimateManager.getTag(biome), GrassColor.pixels);
            // 注意大概率会DH
            return k > newGrassBuffer.length ? originColor : newGrassBuffer[k];
        }
        return originColor;
    };

    public static final ColorResolver FOLIAGE_COLOR = (biome, posX, posZ) ->
    {
        int originColor = biome.getFoliageColor();
        if (ClientConfig.Renderer.seasonalGrassColorChange.get()) {
            if (needRefresh) {
                reloadColors();
            }
            double temperature = Mth.clamp(biome.getModifiedClimateSettings().temperature(), 0.0F, 1.0F);
            double humidity = Mth.clamp(biome.getModifiedClimateSettings().downfall(), 0.0F, 1.0F);
            humidity = humidity * temperature;
            int i = (int) ((1.0D - temperature) * 255.0D);
            int j = (int) ((1.0D - humidity) * 255.0D);
            int k = j << 8 | i;

            int[] newFoliageBuffer = newFoliageBufferMap.getOrDefault(BiomeClimateManager.getTag(biome), FoliageColor.pixels);
            return k > newFoliageBuffer.length ? originColor : newFoliageBuffer[k];
        }
        return originColor;
    };

    public static void reloadColors() {
        {

            {
                for (TagKey<Biome> biomeTagKey : ClimateTypeBiomeTags.BIOME_TYPES) {
                    int[] newFoliageBuffer = new int[65536];
                    int[] newGrassBuffer = new int[65536];
                    int[] foliageBuffer = FoliageColor.pixels;
                    int[] grassBuffer = GrassColor.pixels;

                    SolarTerm solar = EclipticSeasonsApi.getInstance().getSolarTerm(Minecraft.getInstance().level);
                    SolarTermColor colorInfo =
                            solar == SolarTerm.NONE ?
                                    NoneSolarTermColors.BEGINNING_OF_SPRING :
                                    solar.getSolarTermColor(biomeTagKey);
                    for (int i = 0; i < foliageBuffer.length; i++) {
                        int originColor = foliageBuffer[i];

                        if (colorInfo.getMix() == 0.0F) {
                            newFoliageBuffer[i] = originColor;
                        } else {
                            newFoliageBuffer[i] = ColorHelper.simplyMixColor(colorInfo.getLeaveColor(), colorInfo.getMix(), originColor, 1.0F - colorInfo.getMix());
                        }
                    }

                    for (int i = 0; i < grassBuffer.length; i++) {
                        int originColor = grassBuffer[i];
                        if (colorInfo.getMix() == 0.0F) {
                            newGrassBuffer[i] = originColor;
                        } else {
                            newGrassBuffer[i] = ColorHelper.simplyMixColor(colorInfo.getGrassColor(), colorInfo.getMix(), originColor, 1.0F - colorInfo.getMix());
                        }
                    }
                    newFoliageBufferMap.put(biomeTagKey, newFoliageBuffer);
                    newGrassBufferMap.put(biomeTagKey, newGrassBuffer);
                }

                needRefresh = false;
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
                    && MapChecker.isValidDimension(Minecraft.getInstance().level)) {

                SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(Minecraft.getInstance().level);
                LeaveColor leaveColor = values[solarTerm.ordinal()];

                int color = leaveColor.getColor();

                if (values instanceof BirchLeavesColor[]
                        && solarTerm.isInTerms(SolarTerm.END_OF_HEAT, SolarTerm.LIGHT_SNOW)) {
                    float saturation = 1.0f;
                    float brightness = 1.0f;
                    float xChange = (float) ((Math.sin((float) pos.getX() / 16) + 1) / 2);
                    float yChange = (float) ((Math.sin((float) pos.getY() / 128) + 1) / 2);
                    float zChange = (float) ((Math.sin((float) pos.getZ() / 32) + 1) / 2);

                    float change = (xChange + yChange + zChange) / 3;
                    float hue = 0.025f + change * 0.14f;
                    Color foliage = Color.getHSBColor(hue, saturation, brightness);
                    color = foliage.getRGB();
                }

                return ColorHelper.simplyMixColor(color, leaveColor.getMix(),
                        base, 1 - leaveColor.getMix());
            }
        }
        return base;
    }
}
