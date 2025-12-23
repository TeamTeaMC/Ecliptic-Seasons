package com.teamtea.eclipticseasons.client.color.season;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.NoneSolarTermColors;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.SolarTermColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.BirchLeavesColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.LeaveColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.MangroveLeavesColor;
import com.teamtea.eclipticseasons.api.constant.solar.color.leaves.SpruceLeavesColor;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.api.data.client.ColorMode;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.client.IBiomeColorHolder;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ColorHelper;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.awt.*;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class BiomeColorsHandler {
    // public static int[] newFoliageBuffer = new int[65536];
    // public static int[] newGrassBuffer = new int[65536];
    public static Map<TagKey<Biome>, int[]> newFoliageBufferMap = new IdentityHashMap<>();
    public static Map<TagKey<Biome>, int[]> newGrassBufferMap = new IdentityHashMap<>();

    public static boolean needRefresh = false;

    public static final ColorResolver GRASS_COLOR = (biome, posX, posZ) ->
    {
        int originColor = biome.getGrassColor(posX, posZ);
        if (ClientConfig.Renderer.seasonalGrassColorChange.get()) {
            BiomeColor.Instance biomeColor = getBiomeColor(biome, originColor);
            if (biomeColor != null) {
                ColorMode.Instance instance = biomeColor.grassColor().get(ClientCon.nowSolarTerm);
                if (instance != null) {
                    int color = ColorHelper.simplyMixColor(instance.value(), instance.mix(), originColor, Math.abs(1 - instance.mix()));
                    if (ClientConfig.Renderer.smootherSeasonalGrassColorChange.get()) {
                        ColorMode.Instance instance2 = biomeColor.grassColor().get(ClientCon.nowSolarTerm.getLastSolarTerm());
                        if (instance2 != null && !instance.equals(instance2)) {
                            int color2 = ColorHelper.simplyMixColor(instance2.value(), instance2.mix(), originColor, Math.abs(1 - instance2.mix()));
                            float progressFloat = ClientCon.progress / 100f;
                            return ColorHelper.simplyMixColor(color, progressFloat, color2, 1 - progressFloat);
                        }
                    }
                    return color;
                }
            }
            TagKey<Biome> biomeTagKey = ((IBiomeTagHolder) (Object) biome).eclipticseasons$getBindColorTag();
            if (ClientConfig.Renderer.smootherSeasonalGrassColorChange.get()) {
                SolarTermColor colorInfo = ClientCon.nowSolarTerm.getSolarTermColor(biomeTagKey);
                SolarTermColor colorInfo2 = ClientCon.nowSolarTerm.getLastSolarTerm().getSolarTermColor(biomeTagKey);
                int color = ColorHelper.simplyMixColor(colorInfo.getGrassColor(), colorInfo.getMix(), originColor, 1 - colorInfo.getMix());
                int color2 = ColorHelper.simplyMixColor(colorInfo2.getGrassColor(), colorInfo2.getMix(), originColor, 1 - colorInfo2.getMix());
                float progressFloat = ClientCon.progress / 100f;
                return ColorHelper.simplyMixColor(color, progressFloat, color2, 1 - progressFloat);
            }

            // if (needRefresh) {
            //     reloadColors();
            // }
            // 由于基本温度被更改
            // double temperature = Mth.clamp(biome.getModifiedClimateSettings().temperature() + EclipticUtil.getNowSolarTerm(clientLevel).getTemperatureChange(), 0.0F, 1.0F);

            double temperature = Mth.clamp(biome.getModifiedClimateSettings().temperature(), 0.0F, 1.0F);
            double humidity = Mth.clamp(biome.getModifiedClimateSettings().downfall(), 0.0F, 1.0F);
            humidity = humidity * temperature;
            int i = (int) ((1.0D - temperature) * 255.0D);
            int j = (int) ((1.0D - humidity) * 255.0D);
            int k = j << 8 | i;
            int[] newGrassBuffer = newGrassBufferMap.getOrDefault(biomeTagKey, GrassColor.pixels);

            int color = k > newGrassBuffer.length ? originColor : newGrassBuffer[k];
            // if (biomeTagKey != ClimateTypeBiomeTags.SEASONAL
            //         && biomeTagKey != ClimateTypeBiomeTags.MONSOONAL) {
            //     color = ColorHelper.simplyMixColor(color, 0.1f, originColor, 0.9f);
            // }
            // 注意大概率会DH
            return color;
        }
        return originColor;
    };

    private static BiomeColor.Instance getBiomeColor(Object biome, int originColor) {
        if (biome instanceof IBiomeColorHolder iBiomeColorHolder) {
            return iBiomeColorHolder.getBiomeColor();
        }
        return null;
    }

    public static final ColorResolver FOLIAGE_COLOR = (biome, posX, posZ) ->
    {
        int originColor = biome.getFoliageColor();
        if (ClientConfig.Renderer.seasonalGrassColorChange.get()) {
            BiomeColor.Instance biomeColor = getBiomeColor(biome, originColor);
            if (biomeColor != null) {
                ColorMode.Instance instance = biomeColor.foliageColor().get(ClientCon.nowSolarTerm);
                if (instance != null) {
                    int color = ColorHelper.simplyMixColor(instance.value(), instance.mix(), originColor, Math.abs(1 - instance.mix()));
                    if (ClientConfig.Renderer.smootherSeasonalGrassColorChange.get()) {
                        ColorMode.Instance instance2 = biomeColor.foliageColor().get(ClientCon.nowSolarTerm.getLastSolarTerm());
                        if (instance2 != null && !instance.equals(instance2)) {
                            int color2 = ColorHelper.simplyMixColor(instance2.value(), instance2.mix(), originColor, Math.abs(1 - instance2.mix()));
                            float progressFloat = ClientCon.progress / 100f;
                            return ColorHelper.simplyMixColor(color, progressFloat, color2, 1 - progressFloat);
                        }
                    }
                    return color;
                }
            }
            TagKey<Biome> biomeTagKey = ((IBiomeTagHolder) (Object) biome).eclipticseasons$getBindColorTag();
            if (ClientConfig.Renderer.smootherSeasonalGrassColorChange.get()) {
                SolarTermColor colorInfo = ClientCon.nowSolarTerm.getSolarTermColor(biomeTagKey);
                SolarTermColor colorInfo2 = ClientCon.nowSolarTerm.getLastSolarTerm().getSolarTermColor(biomeTagKey);
                int color = ColorHelper.simplyMixColor(colorInfo.getLeaveColor(), colorInfo.getMix(), originColor, 1 - colorInfo.getMix());
                int color2 = ColorHelper.simplyMixColor(colorInfo2.getLeaveColor(), colorInfo2.getMix(), originColor, 1 - colorInfo2.getMix());
                float progressFloat = ClientCon.progress / 100f;
                return ColorHelper.simplyMixColor(color, progressFloat, color2, 1 - progressFloat);
            }
            // if (needRefresh) {
            //     reloadColors();
            // }
            double temperature = Mth.clamp(biome.getModifiedClimateSettings().temperature(), 0.0F, 1.0F);
            double humidity = Mth.clamp(biome.getModifiedClimateSettings().downfall(), 0.0F, 1.0F);
            humidity = humidity * temperature;
            int i = (int) ((1.0D - temperature) * 255.0D);
            int j = (int) ((1.0D - humidity) * 255.0D);
            int k = j << 8 | i;
            // TagKey<Biome> biomeTagKey = ((IBiomeTagHolder) (Object) biome).eclipticseasons$getBindColorTag();
            int[] newFoliageBuffer = newFoliageBufferMap.getOrDefault(biomeTagKey, FoliageColor.pixels);
            int color = k > newFoliageBuffer.length ? originColor : newFoliageBuffer[k];
            // if (biomeTagKey != ClimateTypeBiomeTags.SEASONAL
            //         && biomeTagKey != ClimateTypeBiomeTags.MONSOONAL) {
            //     color = ColorHelper.simplyMixColor(color, 0.1f, originColor, 0.9f);
            // }
            return color;
        }
        return originColor;
    };

    public static void reloadColors() {
        {
            for (TagKey<Biome> biomeTagKey : ClimateTypeBiomeTags.BIOME_COLOR_TYPES) {
                int[] newFoliageBuffer = new int[65536];
                int[] newGrassBuffer = new int[65536];
                int[] foliageBuffer = FoliageColor.pixels;
                int[] grassBuffer = GrassColor.pixels;

                SolarTerm solar = ClientCon.nowSolarTerm;
                SolarTermColor colorInfo = solar == SolarTerm.NONE ?
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

    // 当天气变得寒冷时，云杉可能会显得稍微暗淡一些。
    public static int getSpruceColor(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int tintIndex) {
        return getLeavesColor(FoliageColor.getEvergreenColor(), SpruceLeavesColor.collectValues(), pos);
    }

    // 白桦在秋季通常会变色。它的叶子从绿色变成黄色或金色，有时甚至带有橙色的色调
    public static int getBirchColor(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int tintIndex) {
        return getLeavesColor(FoliageColor.getBirchColor(), BirchLeavesColor.collectValues(), pos);
    }

    // 通常不会经历明显的季节性颜色变化，但是红树很难接受低温，这里因此可以改一下颜色,暗绿色或带棕色调
    public static int getMangroveColor(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos, int tintIndex) {
        return getLeavesColor(FoliageColor.getMangroveColor(), MangroveLeavesColor.collectValues(), pos);
    }


    public static int getLeavesColor(int base, LeaveColor[] values, BlockPos pos) {
        if (ClientConfig.Renderer.seasonalGrassColorChange.get()
                && ClientConfig.Renderer.seasonalColorChangeExtend.get()) {
            if (pos != null && MapChecker.isValidDimension(ClientCon.getUseLevel())) {

                SolarTerm solarTerm = ClientCon.nowSolarTerm;
                LeaveColor leaveColor = values[solarTerm.ordinal()];

                int color = ColorHelper.simplyMixColor(fixColor(values, pos, solarTerm, leaveColor.getColor()), leaveColor.getMix(),
                        base, 1 - leaveColor.getMix());
                if (ClientConfig.Renderer.smootherSeasonalGrassColorChange.get()) {
                    SolarTerm solarTerm2 = solarTerm.getLastSolarTerm();
                    LeaveColor leaveColor2 = values[solarTerm2.ordinal()];
                    int color2 = ColorHelper.simplyMixColor(fixColor(values, pos, solarTerm2, leaveColor2.getColor()), leaveColor2.getMix(),
                            base, 1 - leaveColor2.getMix());
                    float progressFloat = ClientCon.progress / 100f;
                    return ColorHelper.simplyMixColor(color, progressFloat, color2, 1 - progressFloat);
                }
                return color;
            }
        }
        return base;
    }

    private static int fixColor(LeaveColor[] values, BlockPos pos, SolarTerm solarTerm, int color) {
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
        return color;
    }

    public static int getSkyColor(Biome biome, int originColor) {
        return getBiomeColorInternal(biome, originColor, BiomeColorsHandler::getSkyColorMap);
    }
    public static int getWaterColor(Biome biome, int originColor) {
        return getBiomeColorInternal(biome, originColor, BiomeColorsHandler::getWaterColorMap);
    }
    public static int getWaterFogColor(Biome biome, int originColor) {
        return getBiomeColorInternal(biome, originColor, BiomeColorsHandler::getWaterFogColorMap);
    }
    public static int getFogColor(Biome biome, int originColor) {
        return getBiomeColorInternal(biome, originColor, BiomeColorsHandler::getFogColorMap);
    }

    public static Enum2ObjectMap<SolarTerm, ColorMode.Instance> getSkyColorMap(BiomeColor.Instance instance) {
        return instance.skyColor();
    }
    public static Enum2ObjectMap<SolarTerm, ColorMode.Instance> getWaterColorMap(BiomeColor.Instance instance) {
        return instance.waterColor();
    }
    public static Enum2ObjectMap<SolarTerm, ColorMode.Instance> getWaterFogColorMap(BiomeColor.Instance instance) {
        return instance.waterFogColor();
    }
    public static Enum2ObjectMap<SolarTerm, ColorMode.Instance> getFogColorMap(BiomeColor.Instance instance) {
        return instance.fogColor();
    }

    public static int getBiomeColorInternal(Biome biome, int originColor, Function<BiomeColor.Instance, Enum2ObjectMap<SolarTerm, ColorMode.Instance>> function) {
        if (ClientConfig.Renderer.seasonalGrassColorChange.get()) {
            BiomeColor.Instance biomeColor = getBiomeColor(biome, originColor);
            if (biomeColor != null) {
                ColorMode.Instance instance = function.apply(biomeColor).get(ClientCon.nowSolarTerm);
                if (instance != null) {
                    int color = ColorHelper.simplyMixColor(instance.value(), instance.mix(), originColor, Math.abs(1 - instance.mix()));
                    if (ClientConfig.Renderer.smootherSeasonalGrassColorChange.get()) {
                        ColorMode.Instance instance2 = function.apply(biomeColor).get(ClientCon.nowSolarTerm.getLastSolarTerm());
                        if (instance2 != null && !instance.equals(instance2)) {
                            int color2 = ColorHelper.simplyMixColor(instance2.value(), instance2.mix(), originColor, Math.abs(1 - instance2.mix()));
                            float progressFloat = ClientCon.progress / 100f;
                            return ColorHelper.simplyMixColor(color, progressFloat, color2, 1 - progressFloat);
                        }
                    }
                    return color;
                }
            }
        }
        return originColor;
    }

    public static boolean shouldSetFallenLeaves(BlockAndTintGetter pLevel, BlockPos pBlockPos) {
        if (!ClientConfig.Renderer.foliageUnderTree.get()) return false;
        if (pLevel instanceof IMapSlice mapSlice) {
            BlockPos.MutableBlockPos mutable = pBlockPos.mutable();
            mutable.setY(mutable.getY() + 1);
            int brightness = pLevel.getBrightness(LightLayer.SKY, mutable);
            if (brightness > 0 && brightness < 15) {
                int solidBlockHeight = mapSlice.getSolidBlockHeight(pBlockPos);
                int blockHeight = mapSlice.getBlockHeight(pBlockPos);
                if (solidBlockHeight == blockHeight) return false;
                if (blockHeight > pBlockPos.getY()) return false;
                try {
                    if (EclipticSeasonsApi.getInstance().getAgroSeason(ClientCon.getUseLevel(), mutable) != Season.AUTUMN)
                        return false;
                    BlockPos heightmapPos = ClientCon.getUseLevel().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pBlockPos).below();
                    BlockState blockState = ClientCon.getUseLevel().getBlockState(heightmapPos);
                    if (blockState.is(BlockTags.LEAVES) || blockState.is(BlockTags.LOGS)) {
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
                //
                //while (solidBlockHeight >= mutable.getY()) {
                //    mutable.setY(mutable.getY() + 1);
                //    try {
                //        BlockState blockState = ClientCon.getUseLevel().getBlockState(mutable);
                //        if (blockState.is(BlockTags.LEAVES) || blockState.is(BlockTags.LOGS)) {
                //            return true;
                //        }
                //    } catch (CancellationException e) {
                //        return false;
                //    }
                //}
            }
        }
        return false;
    }
}
