package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class ClientWeatherChecker {
    private static boolean isNear(float a, float b, float interval) {
        return Math.abs(a - b) < interval;
    }

    public static float getRate() {
        return ClientConfig.Weather.weatherTransitionSpeed.get().floatValue();
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

    public static float modifyRainAmount(float originalNum, Level level) {
        if (level == null) return originalNum;
        return  (originalNum  * 0.6f);
    }
}
