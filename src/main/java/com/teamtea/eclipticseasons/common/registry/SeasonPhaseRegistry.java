package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import net.minecraft.ChatFormatting;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class SeasonPhaseRegistry {
    public static final ResourceKey<SeasonPhase> DRY_START = createKey("dry_start");
    public static final ResourceKey<SeasonPhase> DRY_MIDDLE = createKey("dry_middle");
    public static final ResourceKey<SeasonPhase> DRY_END = createKey("dry_end");
    public static final ResourceKey<SeasonPhase> RAIN_START = createKey("rain_start");
    public static final ResourceKey<SeasonPhase> RAIN_MIDDLE = createKey("rain_middle");
    public static final ResourceKey<SeasonPhase> RAIN_END = createKey("rain_end");
    public static final ResourceKey<SeasonPhase> WET_START = createKey("wet_start");
    public static final ResourceKey<SeasonPhase> WET_MIDDLE = createKey("wet_middle");
    public static final ResourceKey<SeasonPhase> WET_END = createKey("wet_end");

    public static final ResourceKey<SeasonPhase> DRY = createKey("dry");
    public static final ResourceKey<SeasonPhase> WET = createKey("wet");
    public static final ResourceKey<SeasonPhase> RAIN = createKey("rain");

    public static final ResourceKey<SeasonPhase> COLD_BEGINNING_OF_SPRING = createKey("cold_beginning_of_spring");  // 立春
    public static final ResourceKey<SeasonPhase> COLD_RAIN_WATER = createKey("cold_rain_water");  // 雨水
    public static final ResourceKey<SeasonPhase> COLD_INSECTS_AWAKENING = createKey("cold_insects_awakening");  // 惊蛰
    public static final ResourceKey<SeasonPhase> COLD_SPRING_EQUINOX = createKey("cold_spring_equinox");  // 春分
    public static final ResourceKey<SeasonPhase> COLD_FRESH_GREEN = createKey("cold_fresh_green");  // 清明
    public static final ResourceKey<SeasonPhase> COLD_GRAIN_RAIN = createKey("cold_grain_rain");  // 谷雨

    public static final ResourceKey<SeasonPhase> COLD_BEGINNING_OF_SUMMER = createKey("cold_beginning_of_summer");  // 立夏
    public static final ResourceKey<SeasonPhase> COLD_LESSER_FULLNESS = createKey("cold_lesser_fullness");  // 小满
    public static final ResourceKey<SeasonPhase> COLD_GRAIN_IN_EAR = createKey("cold_grain_in_ear");  // 芒种
    public static final ResourceKey<SeasonPhase> COLD_SUMMER_SOLSTICE = createKey("cold_summer_solstice");  // 夏至
    public static final ResourceKey<SeasonPhase> COLD_LESSER_HEAT = createKey("cold_lesser_heat");  // 小暑
    public static final ResourceKey<SeasonPhase> COLD_GREATER_HEAT = createKey("cold_greater_heat");  // 大暑

    public static final ResourceKey<SeasonPhase> COLD_BEGINNING_OF_AUTUMN = createKey("cold_beginning_of_autumn");  // 立秋
    public static final ResourceKey<SeasonPhase> COLD_END_OF_HEAT = createKey("cold_end_of_heat");  // 处暑
    public static final ResourceKey<SeasonPhase> COLD_WHITE_DEW = createKey("cold_white_dew");  // 白露
    public static final ResourceKey<SeasonPhase> COLD_AUTUMNAL_EQUINOX = createKey("cold_autumnal_equinox");  // 秋分
    public static final ResourceKey<SeasonPhase> COLD_COLD_DEW = createKey("cold_cold_dew");  // 寒露
    public static final ResourceKey<SeasonPhase> COLD_FIRST_FROST = createKey("cold_first_frost");  // 霜降

    public static final ResourceKey<SeasonPhase> COLD_BEGINNING_OF_WINTER = createKey("cold_beginning_of_winter");  // 立冬
    public static final ResourceKey<SeasonPhase> COLD_LIGHT_SNOW = createKey("cold_light_snow");  // 小雪
    public static final ResourceKey<SeasonPhase> COLD_HEAVY_SNOW = createKey("cold_heavy_snow");  // 大雪
    public static final ResourceKey<SeasonPhase> COLD_WINTER_SOLSTICE = createKey("cold_winter_solstice");  // 冬至
    public static final ResourceKey<SeasonPhase> COLD_LESSER_COLD = createKey("cold_lesser_cold");  // 小寒
    public static final ResourceKey<SeasonPhase> COLD_GREATER_COLD = createKey("cold_greater_cold");  // 大寒

    private static ResourceKey<SeasonPhase> createKey(String name) {
        return ResourceKey.create(ESRegistries.SEASON_PHASE, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstapContext<SeasonPhase> context) {

        ResourceLocation monsoonIcons = EclipticSeasons.rl("monsoon_icons");

        context.register(DRY_START, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("dry_start"),
                ChatFormatting.RED,
                Optional.empty(),
                new SeasonPhase.FontIcon(monsoonIcons, "a")
        ));

        context.register(DRY_MIDDLE, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("dry_middle"),
                ChatFormatting.RED,
                Optional.empty(),
                new SeasonPhase.FontIcon(monsoonIcons, "b")
        ));

        context.register(DRY_END, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("dry_end"),
                ChatFormatting.RED,
                Optional.empty(),
                new SeasonPhase.FontIcon(monsoonIcons, "c")
        ));

        context.register(RAIN_START, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("rain_start"),
                ChatFormatting.DARK_BLUE,
                Optional.empty(),
                new SeasonPhase.FontIcon(monsoonIcons, "d")
        ));

        context.register(RAIN_MIDDLE, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("rain_middle"),
                ChatFormatting.DARK_BLUE,
                Optional.empty(),
                new SeasonPhase.FontIcon(monsoonIcons, "e")
        ));

        context.register(RAIN_END, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("rain_end"),
                ChatFormatting.DARK_BLUE,
                Optional.empty(),
                new SeasonPhase.FontIcon(monsoonIcons, "f")
        ));


        context.register(WET_START, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("wet_start"),
                ChatFormatting.DARK_GREEN,
                Optional.empty(),
                new SeasonPhase.FontIcon(monsoonIcons, "g")
        ));

        context.register(WET_MIDDLE, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("wet_middle"),
                ChatFormatting.DARK_GREEN,
                Optional.empty(),
                new SeasonPhase.FontIcon(monsoonIcons, "h")
        ));

        context.register(WET_END, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("wet_end"),
                ChatFormatting.DARK_GREEN,
                Optional.empty(),
                new SeasonPhase.FontIcon(monsoonIcons, "i")
        ));


        context.register(DRY, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("dry"),
                ChatFormatting.GOLD,
                Optional.of(new SeasonPhase.Icon(EclipticSeasons.rl("dry_middle").withPrefix(ESRegistries.SEASON_PHASE.location().getPath() + "/"))),
                new SeasonPhase.FontIcon(monsoonIcons, "b")
        ));

        context.register(RAIN, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("rain"),
                ChatFormatting.BLUE,
                Optional.of(new SeasonPhase.Icon(EclipticSeasons.rl("rain_middle").withPrefix(ESRegistries.SEASON_PHASE.location().getPath() + "/"))),
                new SeasonPhase.FontIcon(monsoonIcons, "e")
        ));

        context.register(WET, new SeasonPhase(Season.SUMMER,
                EclipticSeasons.rl("wet"),
                ChatFormatting.GREEN,
                Optional.of(new SeasonPhase.Icon(EclipticSeasons.rl("wet_middle").withPrefix(ESRegistries.SEASON_PHASE.location().getPath() + "/"))),
                new SeasonPhase.FontIcon(monsoonIcons, "h")
        ));

        ResourceLocation solarIcons = EclipticSeasons.rl("solar_icons");
        ResourceLocation coldSeasonsIcons = EclipticSeasons.rl("seasons_icons").withPrefix("font/");
        context.register(COLD_BEGINNING_OF_SPRING, new SeasonPhase(Season.SPRING,
                COLD_BEGINNING_OF_SPRING.location(),
                Season.SPRING.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.BEGINNING_OF_SPRING.getIconWidth(),
                        SolarTerm.BEGINNING_OF_SPRING.getIconHeight(),
                        SolarTerm.BEGINNING_OF_SPRING.getIconAtlasSize(),
                        SolarTerm.BEGINNING_OF_SPRING.getIconPosition().getKey(),
                        SolarTerm.BEGINNING_OF_SPRING.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.BEGINNING_OF_SPRING.getFontLabel())
        ));

        context.register(COLD_RAIN_WATER, new SeasonPhase(Season.SPRING,
                COLD_RAIN_WATER.location(),
                Season.SPRING.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.RAIN_WATER.getIconWidth(),
                        SolarTerm.RAIN_WATER.getIconHeight(),
                        SolarTerm.RAIN_WATER.getIconAtlasSize(),
                        SolarTerm.RAIN_WATER.getIconPosition().getKey(),
                        SolarTerm.RAIN_WATER.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.RAIN_WATER.getFontLabel())
        ));

        context.register(COLD_INSECTS_AWAKENING, new SeasonPhase(Season.SPRING,
                COLD_INSECTS_AWAKENING.location(),
                Season.SPRING.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.INSECTS_AWAKENING.getIconWidth(),
                        SolarTerm.INSECTS_AWAKENING.getIconHeight(),
                        SolarTerm.INSECTS_AWAKENING.getIconAtlasSize(),
                        SolarTerm.INSECTS_AWAKENING.getIconPosition().getKey(),
                        SolarTerm.INSECTS_AWAKENING.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.INSECTS_AWAKENING.getFontLabel())
        ));

        context.register(COLD_SPRING_EQUINOX, new SeasonPhase(Season.SPRING,
                COLD_SPRING_EQUINOX.location(),
                Season.SPRING.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.SPRING_EQUINOX.getIconWidth(),
                        SolarTerm.SPRING_EQUINOX.getIconHeight(),
                        SolarTerm.SPRING_EQUINOX.getIconAtlasSize(),
                        SolarTerm.SPRING_EQUINOX.getIconPosition().getKey(),
                        SolarTerm.SPRING_EQUINOX.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.SPRING_EQUINOX.getFontLabel())
        ));

        context.register(COLD_FRESH_GREEN, new SeasonPhase(Season.SPRING,
                COLD_FRESH_GREEN.location(),
                Season.SPRING.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.FRESH_GREEN.getIconWidth(),
                        SolarTerm.FRESH_GREEN.getIconHeight(),
                        SolarTerm.FRESH_GREEN.getIconAtlasSize(),
                        SolarTerm.FRESH_GREEN.getIconPosition().getKey(),
                        SolarTerm.FRESH_GREEN.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.FRESH_GREEN.getFontLabel())
        ));

        context.register(COLD_GRAIN_RAIN, new SeasonPhase(Season.SPRING,
                COLD_GRAIN_RAIN.location(),
                Season.SPRING.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.GRAIN_RAIN.getIconWidth(),
                        SolarTerm.GRAIN_RAIN.getIconHeight(),
                        SolarTerm.GRAIN_RAIN.getIconAtlasSize(),
                        SolarTerm.GRAIN_RAIN.getIconPosition().getKey(),
                        SolarTerm.GRAIN_RAIN.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.GRAIN_RAIN.getFontLabel())
        ));

        context.register(COLD_BEGINNING_OF_SUMMER, new SeasonPhase(Season.SUMMER,
                COLD_BEGINNING_OF_SUMMER.location(),
                Season.SUMMER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.BEGINNING_OF_SUMMER.getIconWidth(),
                        SolarTerm.BEGINNING_OF_SUMMER.getIconHeight(),
                        SolarTerm.BEGINNING_OF_SUMMER.getIconAtlasSize(),
                        SolarTerm.BEGINNING_OF_SUMMER.getIconPosition().getKey(),
                        SolarTerm.BEGINNING_OF_SUMMER.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.BEGINNING_OF_SUMMER.getFontLabel())
        ));

        context.register(COLD_LESSER_FULLNESS, new SeasonPhase(Season.SUMMER,
                COLD_LESSER_FULLNESS.location(),
                Season.SUMMER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.LESSER_FULLNESS.getIconWidth(),
                        SolarTerm.LESSER_FULLNESS.getIconHeight(),
                        SolarTerm.LESSER_FULLNESS.getIconAtlasSize(),
                        SolarTerm.LESSER_FULLNESS.getIconPosition().getKey(),
                        SolarTerm.LESSER_FULLNESS.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.LESSER_FULLNESS.getFontLabel())
        ));

        context.register(COLD_GRAIN_IN_EAR, new SeasonPhase(Season.SUMMER,
                COLD_GRAIN_IN_EAR.location(),
                Season.SUMMER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.GRAIN_IN_EAR.getIconWidth(),
                        SolarTerm.GRAIN_IN_EAR.getIconHeight(),
                        SolarTerm.GRAIN_IN_EAR.getIconAtlasSize(),
                        SolarTerm.GRAIN_IN_EAR.getIconPosition().getKey(),
                        SolarTerm.GRAIN_IN_EAR.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.GRAIN_IN_EAR.getFontLabel())
        ));

        context.register(COLD_SUMMER_SOLSTICE, new SeasonPhase(Season.SUMMER,
                COLD_SUMMER_SOLSTICE.location(),
                Season.SUMMER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.SUMMER_SOLSTICE.getIconWidth(),
                        SolarTerm.SUMMER_SOLSTICE.getIconHeight(),
                        SolarTerm.SUMMER_SOLSTICE.getIconAtlasSize(),
                        SolarTerm.SUMMER_SOLSTICE.getIconPosition().getKey(),
                        SolarTerm.SUMMER_SOLSTICE.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.SUMMER_SOLSTICE.getFontLabel())
        ));

        context.register(COLD_LESSER_HEAT, new SeasonPhase(Season.SUMMER,
                COLD_LESSER_HEAT.location(),
                Season.SUMMER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.LESSER_HEAT.getIconWidth(),
                        SolarTerm.LESSER_HEAT.getIconHeight(),
                        SolarTerm.LESSER_HEAT.getIconAtlasSize(),
                        SolarTerm.LESSER_HEAT.getIconPosition().getKey(),
                        SolarTerm.LESSER_HEAT.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.LESSER_HEAT.getFontLabel())
        ));

        context.register(COLD_GREATER_HEAT, new SeasonPhase(Season.SUMMER,
                COLD_GREATER_HEAT.location(),
                Season.SUMMER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.GREATER_HEAT.getIconWidth(),
                        SolarTerm.GREATER_HEAT.getIconHeight(),
                        SolarTerm.GREATER_HEAT.getIconAtlasSize(),
                        SolarTerm.GREATER_HEAT.getIconPosition().getKey(),
                        SolarTerm.GREATER_HEAT.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.GREATER_HEAT.getFontLabel())
        ));

        context.register(COLD_BEGINNING_OF_AUTUMN, new SeasonPhase(Season.AUTUMN,
                COLD_BEGINNING_OF_AUTUMN.location(),
                Season.AUTUMN.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.BEGINNING_OF_AUTUMN.getIconWidth(),
                        SolarTerm.BEGINNING_OF_AUTUMN.getIconHeight(),
                        SolarTerm.BEGINNING_OF_AUTUMN.getIconAtlasSize(),
                        SolarTerm.BEGINNING_OF_AUTUMN.getIconPosition().getKey(),
                        SolarTerm.BEGINNING_OF_AUTUMN.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.BEGINNING_OF_AUTUMN.getFontLabel())
        ));

        context.register(COLD_END_OF_HEAT, new SeasonPhase(Season.AUTUMN,
                COLD_END_OF_HEAT.location(),
                Season.AUTUMN.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.END_OF_HEAT.getIconWidth(),
                        SolarTerm.END_OF_HEAT.getIconHeight(),
                        SolarTerm.END_OF_HEAT.getIconAtlasSize(),
                        SolarTerm.END_OF_HEAT.getIconPosition().getKey(),
                        SolarTerm.END_OF_HEAT.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.END_OF_HEAT.getFontLabel())
        ));

        context.register(COLD_WHITE_DEW, new SeasonPhase(Season.AUTUMN,
                COLD_WHITE_DEW.location(),
                Season.AUTUMN.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.WHITE_DEW.getIconWidth(),
                        SolarTerm.WHITE_DEW.getIconHeight(),
                        SolarTerm.WHITE_DEW.getIconAtlasSize(),
                        SolarTerm.WHITE_DEW.getIconPosition().getKey(),
                        SolarTerm.WHITE_DEW.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.WHITE_DEW.getFontLabel())
        ));

        context.register(COLD_AUTUMNAL_EQUINOX, new SeasonPhase(Season.AUTUMN,
                COLD_AUTUMNAL_EQUINOX.location(),
                Season.AUTUMN.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.AUTUMNAL_EQUINOX.getIconWidth(),
                        SolarTerm.AUTUMNAL_EQUINOX.getIconHeight(),
                        SolarTerm.AUTUMNAL_EQUINOX.getIconAtlasSize(),
                        SolarTerm.AUTUMNAL_EQUINOX.getIconPosition().getKey(),
                        SolarTerm.AUTUMNAL_EQUINOX.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.AUTUMNAL_EQUINOX.getFontLabel())
        ));

        context.register(COLD_COLD_DEW, new SeasonPhase(Season.AUTUMN,
                COLD_COLD_DEW.location(),
                Season.AUTUMN.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.COLD_DEW.getIconWidth(),
                        SolarTerm.COLD_DEW.getIconHeight(),
                        SolarTerm.COLD_DEW.getIconAtlasSize(),
                        SolarTerm.COLD_DEW.getIconPosition().getKey(),
                        SolarTerm.COLD_DEW.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.COLD_DEW.getFontLabel())
        ));

        context.register(COLD_FIRST_FROST, new SeasonPhase(Season.AUTUMN,
                COLD_FIRST_FROST.location(),
                Season.AUTUMN.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.FIRST_FROST.getIconWidth(),
                        SolarTerm.FIRST_FROST.getIconHeight(),
                        SolarTerm.FIRST_FROST.getIconAtlasSize(),
                        SolarTerm.FIRST_FROST.getIconPosition().getKey(),
                        SolarTerm.FIRST_FROST.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.FIRST_FROST.getFontLabel())
        ));

        context.register(COLD_BEGINNING_OF_WINTER, new SeasonPhase(Season.WINTER,
                COLD_BEGINNING_OF_WINTER.location(),
                Season.WINTER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.BEGINNING_OF_WINTER.getIconWidth(),
                        SolarTerm.BEGINNING_OF_WINTER.getIconHeight(),
                        SolarTerm.BEGINNING_OF_WINTER.getIconAtlasSize(),
                        SolarTerm.BEGINNING_OF_WINTER.getIconPosition().getKey(),
                        SolarTerm.BEGINNING_OF_WINTER.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.BEGINNING_OF_WINTER.getFontLabel())
        ));

        context.register(COLD_LIGHT_SNOW, new SeasonPhase(Season.WINTER,
                COLD_LIGHT_SNOW.location(),
                Season.WINTER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.LIGHT_SNOW.getIconWidth(),
                        SolarTerm.LIGHT_SNOW.getIconHeight(),
                        SolarTerm.LIGHT_SNOW.getIconAtlasSize(),
                        SolarTerm.LIGHT_SNOW.getIconPosition().getKey(),
                        SolarTerm.LIGHT_SNOW.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.LIGHT_SNOW.getFontLabel())
        ));

        context.register(COLD_HEAVY_SNOW, new SeasonPhase(Season.WINTER,
                COLD_HEAVY_SNOW.location(),
                Season.WINTER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.HEAVY_SNOW.getIconWidth(),
                        SolarTerm.HEAVY_SNOW.getIconHeight(),
                        SolarTerm.HEAVY_SNOW.getIconAtlasSize(),
                        SolarTerm.HEAVY_SNOW.getIconPosition().getKey(),
                        SolarTerm.HEAVY_SNOW.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.HEAVY_SNOW.getFontLabel())
        ));

        context.register(COLD_WINTER_SOLSTICE, new SeasonPhase(Season.WINTER,
                COLD_WINTER_SOLSTICE.location(),
                Season.WINTER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.WINTER_SOLSTICE.getIconWidth(),
                        SolarTerm.WINTER_SOLSTICE.getIconHeight(),
                        SolarTerm.WINTER_SOLSTICE.getIconAtlasSize(),
                        SolarTerm.WINTER_SOLSTICE.getIconPosition().getKey(),
                        SolarTerm.WINTER_SOLSTICE.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.WINTER_SOLSTICE.getFontLabel())
        ));

        context.register(COLD_LESSER_COLD, new SeasonPhase(Season.WINTER,
                COLD_LESSER_COLD.location(),
                Season.WINTER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.LESSER_COLD.getIconWidth(),
                        SolarTerm.LESSER_COLD.getIconHeight(),
                        SolarTerm.LESSER_COLD.getIconAtlasSize(),
                        SolarTerm.LESSER_COLD.getIconPosition().getKey(),
                        SolarTerm.LESSER_COLD.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.LESSER_COLD.getFontLabel())
        ));

        context.register(COLD_GREATER_COLD, new SeasonPhase(Season.WINTER,
                COLD_GREATER_COLD.location(),
                Season.WINTER.getColor(),
                Optional.of(new SeasonPhase.Icon(
                        coldSeasonsIcons,
                        SolarTerm.GREATER_COLD.getIconWidth(),
                        SolarTerm.GREATER_COLD.getIconHeight(),
                        SolarTerm.GREATER_COLD.getIconAtlasSize(),
                        SolarTerm.GREATER_COLD.getIconPosition().getKey(),
                        SolarTerm.GREATER_COLD.getIconPosition().getValue()
                )),
                new SeasonPhase.FontIcon(solarIcons, SolarTerm.GREATER_COLD.getFontLabel())
        ));


    }
}
