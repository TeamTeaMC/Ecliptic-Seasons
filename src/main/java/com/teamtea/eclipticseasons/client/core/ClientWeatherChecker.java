package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.api.util.WeatherUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.fml.loading.FMLEnvironment;

public class ClientWeatherChecker {
    private static boolean isNear(float a, float b, float interval) {
        return Math.abs(a - b) < interval;
    }

    public static float getRate() {
        // return ClientConfig.Weather.weatherTransitionSpeed.get().floatValue();
        return 0;
    }

    // 0-》15
    public static int ModifySnowAmount(int constant, float pPartialTick, Level level) {
        if (level == null) return constant;
        return (int) (constant * Mth.clamp(level.getRainLevel(pPartialTick) * 0.6f, 0.6f, 1f) * getAmount());
    }

    public static float modifyVolume(SoundEvent soundEvent, float pVolume, Level level) {
        if (level == null) return pVolume;
        return pVolume * getAmount();
    }

    public static float modifyPitch(SoundEvent soundEvent, float pPitch, Level level) {
        if (level == null) return pPitch;
        return pPitch;
        // return pPitch;
    }

    public static float modifyRainAmount(float originalNum, Level level) {
        if (level == null) return originalNum;
        return (originalNum * getAmount());
    }

    public static float getAmount() {
        return weatherEffectByEntity == null
                || !ClientConfig.Weather.tweakPrecipitationParticleTexture.get()
                || !weatherEffectByEntity.shouldChangeAmount(true) ?
                1 : weatherEffectByEntity.getModifiedAmount(1, true);
    }


    public static Identifier modifyRainAmount3(Identifier identifier, boolean rain) {
        if (weatherEffectByEntity == null
                || !ClientConfig.Weather.tweakPrecipitationParticleTexture.get()
                || !weatherEffectByEntity.shouldChangeTexture(rain)) return identifier;
        return weatherEffectByEntity.onTextureBinding(identifier, rain);
    }

    private static WeatherEffect weatherEffectByEntity;

    public static void tickLevel(Level level) {
        weatherEffectByEntity = WeatherUtil.getWeatherEffectByEntity(ClientCon.getAgent().getCameraEntity());
    }

    public static void unload(Level level) {
        weatherEffectByEntity = null;
    }
}
