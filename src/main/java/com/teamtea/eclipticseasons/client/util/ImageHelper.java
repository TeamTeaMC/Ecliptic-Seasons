package com.teamtea.eclipticseasons.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.server.packs.resources.ResourceMetadata;

public class ImageHelper {
    public static void fixSnowImageColor(ResourceMetadata section, NativeImage original, ResourceMetadata snowySection, NativeImage snowyImage) {
        FrameSize frameSize = section.getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY).calculateFrameSize(original.getWidth(), original.getHeight());
        int width = frameSize.width();
        int height = frameSize.height();
        int imageSeed = eclipticseasons$getImageSeed(width, height, original);
        FrameSize snowFrameSize = snowySection.getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY).calculateFrameSize(snowyImage.getWidth(), snowyImage.getHeight());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = original.getPixelRGBA(x, y);
                if (ColorHelper.getAlpha(pixel) == 0) continue;
                float rand = eclipticseasons$random01(x, y, imageSeed);
                int snowColor = snowyImage.getPixelRGBA(x % snowFrameSize.width(), y % snowFrameSize.height());
                if (eclipticseasons$isTop(x, y, original)) {
                    // 顶部像素：强积雪（白色）
                    if (rand < 0.7f) {
                        int snowed = ColorHelper.simplyMixColor(pixel, 0.1f, ColorHelper.frostify(snowColor, 10f, 0.1f, 0.07f), 0.9f);
                        original.setPixelRGBA(x, y, snowed);
                    } else {
                        int snowed = ColorHelper.simplyMixColor(pixel, 0.4f, ColorHelper.frostify(snowColor, 10f, 0.1f, 0.07f), 0.6f);
                        original.setPixelRGBA(x, y, snowed);
                    }
                } else {
                    // 非顶部：一定概率生成霜
                    float chance = eclipticseasons$frostProbability(y, height);
                    if (rand < chance) {
                        // int frosted = ColorHelper.simplyMixColor(pixel, 0.6f, snowColor, 0.4f);
                        int frosted = ColorHelper.frostify(pixel, 20f, 0.2f, 0.15f);

                        original.setPixelRGBA(x, y, frosted);
                    } else {
                        // int frosted = ColorHelper.simplyMixColor(pixel, 0.7f, snowColor, 0.3f);
                        int frostSoft = ColorHelper.frostify(pixel, 10f, 0.1f, 0.07f);
                        original.setPixelRGBA(x, y, frostSoft);
                    }
                }
            }
        }
    }

    private static int eclipticseasons$getImageSeed(int width, int height, NativeImage original) {
        int totalBrightness = 0;
        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = original.getPixelRGBA(x, y);
                if (ColorHelper.getAlpha(pixel) == 0) continue;
                int r = ColorHelper.getRed(pixel);
                int g = ColorHelper.getGreen(pixel);
                int b = ColorHelper.getBlue(pixel);
                float brightness = 0.2126f * r + 0.7152f * g + 0.0722f * b;
                totalBrightness += (int) brightness;
                count++;
            }
        }
        return count == 0 ? 0 : totalBrightness / count;
    }

    private static boolean eclipticseasons$isTop(int x, int y, NativeImage original) {
        return y != 0
                && (ColorHelper.getAlpha(original.getPixelRGBA(x, y - 1)) == 0
                || (y > 1 && ColorHelper.getAlpha(original.getPixelRGBA(x, y - 2)) == 0)
                || (y > 2 && ColorHelper.getAlpha(original.getPixelRGBA(x, y - 3)) == 0));
    }

    private static float eclipticseasons$random01(int x, int y, int seed) {
        int h = x * 734287 ^ y * 912443 ^ seed * 104729;
        h ^= (h >>> 13);
        h *= 0x5bd1e995;
        h &= 0xffffff;
        return h / (float) 0xffffff;
    }

    private static float eclipticseasons$frostProbability(int y, int height) {
        return 0.1f + ((height - y) / (float) height) * 0.5f;
    }

}
