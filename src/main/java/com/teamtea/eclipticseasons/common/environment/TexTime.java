package com.teamtea.eclipticseasons.common.environment;

import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;

// we can datapack those things, just use float
public class TexTime {

    public static void attachSolarLayer(Level level, EnvironmentAttributeSystem.Builder environmentAttributes) {
        environmentAttributes.addTimeBasedLayer(
                EnvironmentAttributes.SUN_ANGLE,
                (base, cacheTickId) -> {
                    return !CommonConfig.Season.daylightChange.get() || !MapChecker.isValidDimension(level)
                            ? base
                            : samplePhase(level).sunAngleDeg();
                }
        );

        environmentAttributes.addTimeBasedLayer(
                EnvironmentAttributes.MOON_ANGLE,
                (base, cacheTickId) -> {
                    return !CommonConfig.Season.daylightChange.get() || !MapChecker.isValidDimension(level)
                            ? base
                            : wrapDeg(samplePhase(level).sunAngleDeg() - 180f);
                }
        );

        environmentAttributes.addTimeBasedLayer(
                EnvironmentAttributes.STAR_ANGLE,
                (base, cacheTickId) -> {
                    return !CommonConfig.Season.daylightChange.get() || !MapChecker.isValidDimension(level)
                            ? base
                            : wrapDeg(samplePhase(level).sunAngleDeg() - 180f);
                }
        );

        environmentAttributes.addTimeBasedLayer(
                EnvironmentAttributes.SKY_LIGHT_FACTOR,
                (base, cacheTickId) -> {
                    return !CommonConfig.Season.daylightChange.get() || !MapChecker.isValidDimension(level)
                            ? base
                            : getSkyLightFactor(level);
                }
        );

        environmentAttributes.addTimeBasedLayer(
                EnvironmentAttributes.SKY_LIGHT_LEVEL,
                (base, cacheTickId) -> {
                    return !CommonConfig.Season.daylightChange.get() || !MapChecker.isValidDimension(level)
                            ? base
                            : getSkyLightLevel(level);
                }
        );

        environmentAttributes.addTimeBasedLayer(
                EnvironmentAttributes.SUNRISE_SUNSET_COLOR,
                (base, cacheTickId) -> {
                    return !CommonConfig.Season.daylightChange.get() || !MapChecker.isValidDimension(level)
                            ? base
                            : getSunriseSunsetColor(level);
                }
        );
    }

    public static float samplePhase(Holder<WorldClock> clockHolder, long total) {
        float t = SolarAngelHelper.getSeasonCelestialAngle(clockHolder, total);
        return normalize(t);
    }

    public static float getSeasonalDayTick(Holder<WorldClock> clockHolder, long total) {
        return (samplePhase(clockHolder, total) + 0.25f) % 1f;
    }

    private static SolarPhase samplePhase(Level level) {
        float t = SolarAngelHelper.getSeasonCelestialAngle(level.dimensionType().defaultClock().orElse(null), level.getDefaultClockTime());
        t = normalize(t);
        float sunAngle = t * 360.0f;
        return new SolarPhase(t, sunAngle);
    }

    private record SolarPhase(float time, float sunAngleDeg) {
    }

    public static int getSeasonalDayTick(Level level) {
        float t = (samplePhase(level).time() + 0.25f) % 1f;
        return ((int) (t * 24000.0f)) % 24000;
    }

    private static float getSkyLightFactor(Level level) {
        if (ClientCon.getAgent().getSkyFlashTime(level) > 0) {
            return 1.0F;
        }

        int tick = getSeasonalDayTick(level);

        int[] ticks = {
                730, 11270, 13140, 22860, 24000
        };

        float[] values = {
                1.0f, 1.0f, 0.24f, 0.24f, 1.0f
        };

        return lerpByTicks(tick, ticks, values);
    }

    private static float getSkyLightLevel(Level level) {
        int tick = getSeasonalDayTick(level);

        int[] ticks = {
                133, 11867, 13670, 22330, 24000
        };

        float[] values = {
                1.0f, 1.0f, 0.26666668f, 0.26666668f, 1.0f
        };

        return lerpByTicks(tick, ticks, values);
    }

    private static int getSunriseSunsetColor(Level level) {
        int tick = getSeasonalDayTick(level);

        int[] ticks = {
                71, 310, 565, 730,
                11270, 11397, 11522, 11690, 11929,
                12243, 12358, 12512, 12613, 12732, 12841, 13035, 13252,
                13775, 13888, 14039, 14192,
                21807, 21961, 22112, 22225,
                22748, 22965, 23159, 23272, 23488, 23642, 23757,
                24000
        };

        int[] values = {
                1609540403, 703969843, 117167155, 16770355,
                16770355, 83679283, 268028723, 703969843, 1609540403,
                -1310226637, -857440717, -371166669, -153261261, -19242189, -19440589, -321760973, -1043577037,
                918435635, 532362547, 163001139, 11744051,
                11678515, 163001139, 532362547, 918435635,
                -1043577037, -321760973, -19440589, -19242189, -371166669, -857440717, -1310226637,
                1609540403
        };

        return lerpColorByTicks(tick, ticks, values);
    }

    private static float lerpByTicks(int tick, int[] ticks, float[] values) {
        if (ticks.length == 0 || ticks.length != values.length) {
            return 0.0f;
        }

        if (tick <= ticks[0]) {
            return values[0];
        }

        for (int i = 1; i < ticks.length; i++) {
            if (tick <= ticks[i]) {
                int t0 = ticks[i - 1];
                int t1 = ticks[i];
                float v0 = values[i - 1];
                float v1 = values[i];
                float x = (float) (tick - t0) / (float) (t1 - t0);
                return v0 + (v1 - v0) * x;
            }
        }

        return values[values.length - 1];
    }

    private static int lerpColorByTicks(int tick, int[] ticks, int[] values) {
        if (ticks.length == 0 || ticks.length != values.length) {
            return 0;
        }

        if (tick <= ticks[0]) {
            return values[0];
        }

        for (int i = 1; i < ticks.length; i++) {
            if (tick <= ticks[i]) {
                int t0 = ticks[i - 1];
                int t1 = ticks[i];
                int c0 = values[i - 1];
                int c1 = values[i];
                float x = (float) (tick - t0) / (float) (t1 - t0);
                return lerpColor(x, c0, c1);
            }
        }

        return values[values.length - 1];
    }

    private static int lerpColor(float t, int a, int b) {
        t = clamp(t);

        int aA = (a >> 24) & 0xFF;
        int aR = (a >> 16) & 0xFF;
        int aG = (a >> 8) & 0xFF;
        int aB = a & 0xFF;

        int bA = (b >> 24) & 0xFF;
        int bR = (b >> 16) & 0xFF;
        int bG = (b >> 8) & 0xFF;
        int bB = b & 0xFF;

        int A = (int) (aA + (bA - aA) * t);
        int R = (int) (aR + (bR - aR) * t);
        int G = (int) (aG + (bG - aG) * t);
        int B = (int) (aB + (bB - aB) * t);

        return (A << 24) | (R << 16) | (G << 8) | B;
    }

    private static float normalize(float t) {
        t %= 1.0f;
        return t < 0.0f ? t + 1.0f : t;
    }

    private static float clamp(float x) {
        return Math.max(0.0f, Math.min(1.0f, x));
    }

    private static float wrapDeg(float deg) {
        deg %= 360.0f;
        return deg < 0.0f ? deg + 360.0f : deg;
    }
}