package com.teamtea.eclipticseasons.client.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ColorHelper
{
    public static int getRed(int color)
    {
        return color >> 16 & 255;
    }

    public static int getGreen(int color)
    {
        return color >> 8 & 255;
    }

    public static int getBlue(int color)
    {
        return color & 255;
    }

    public static int getAlpha(int color)
    {
        return color >> 24 & 255;
    }

    public static float getRedF(int color)
    {
        return getRed(color) / 255.0F;
    }

    public static float getGreenF(int color)
    {
        return getGreen(color) / 255.0F;
    }

    public static float getBlueF(int color)
    {
        return getBlue(color) / 255.0F;
    }

    public static float getAlphaF(int color)
    {
        return getAlpha(color) / 255.0F;
    }

    public static int simplyMixColor(int color1, float alpha1, int color2, float alpha2)
    {
        int red = (int) (getRed(color1) * alpha1 + getRed(color2) * alpha2);
        int green = (int) (getGreen(color1) * alpha1 + getGreen(color2) * alpha2);
        int blue = (int) (getBlue(color1) * alpha1 + getBlue(color2) * alpha2);
        int alpha = (int) (getAlpha(color1) * alpha1 + getAlpha(color2) * alpha2);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static float getAvg(ArrayList<Integer> list) {
        int sum = 0;
        float interval = 51f;
        Map<Integer, Integer> colorMap = new HashMap<>();
        colorMap.put(10, 0);
        colorMap.put(51, 0);
        colorMap.put(102, 0);
        colorMap.put(153, 0);
        colorMap.put(204, 0);
        colorMap.put(240, 0);
        for (int i = 0; i < list.size(); i++) {
            float tmp = ((float) list.get(i)) / interval;
            int stage = Math.round(tmp);
            switch (stage) {
                case 0:
                    colorMap.replace(10, colorMap.get(10) + 1);
                    break;
                case 1:
                    colorMap.replace(51, colorMap.get(51) + 1);
                    break;
                case 2:
                    colorMap.replace(102, colorMap.get(102) + 1);
                    break;
                case 3:
                    colorMap.replace(153, colorMap.get(153) + 1);
                    break;
                case 4:
                    colorMap.replace(204, colorMap.get(204) + 1);
                    break;
                case 5:
                    colorMap.replace(240, colorMap.get(240) + 1);
                    break;
            }
        }
        final int[] maxColor = new int[]{10};
        colorMap.forEach(((key, value) -> {
            if (colorMap.get(maxColor[0]) < value) maxColor[0] = key;
        }));
        float maxAmount=colorMap.get(maxColor[0]);
        colorMap.remove(maxColor[0]);
        final int[] secondColor = maxColor[0]!=10?new int[]{10}:new int[]{51};
        colorMap.forEach(((key, value) -> {
            if (colorMap.get(secondColor[0]) < value)
                secondColor[0] = key;
        }));
        float secondAmount=colorMap.get(secondColor[0]);
        float r =secondAmount/maxAmount;
        if(r>0.7)return maxColor[0]*0.6f+secondColor[0]*0.4f;
        return maxColor[0];
    }
}
