package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import net.minecraft.ChatFormatting;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

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

    // public static final ResourceKey<SeasonPhase> DRY = createKey("dry");
    // public static final ResourceKey<SeasonPhase> WET = createKey("wet");
    // public static final ResourceKey<SeasonPhase> RAIN = createKey("rain");

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

    public static final ResourceKey<SeasonPhase> HOT_BEGINNING_OF_SPRING = createKey("hot_beginning_of_spring");  // 立春
    public static final ResourceKey<SeasonPhase> HOT_RAIN_WATER = createKey("hot_rain_water");  // 雨水
    public static final ResourceKey<SeasonPhase> HOT_INSECTS_AWAKENING = createKey("hot_insects_awakening");  // 惊蛰
    public static final ResourceKey<SeasonPhase> HOT_SPRING_EQUINOX = createKey("hot_spring_equinox");  // 春分
    public static final ResourceKey<SeasonPhase> HOT_FRESH_GREEN = createKey("hot_fresh_green");  // 清明
    public static final ResourceKey<SeasonPhase> HOT_GRAIN_RAIN = createKey("hot_grain_rain");  // 谷雨

    public static final ResourceKey<SeasonPhase> HOT_BEGINNING_OF_SUMMER = createKey("hot_beginning_of_summer");  // 立夏
    public static final ResourceKey<SeasonPhase> HOT_LESSER_FULLNESS = createKey("hot_lesser_fullness");  // 小满
    public static final ResourceKey<SeasonPhase> HOT_GRAIN_IN_EAR = createKey("hot_grain_in_ear");  // 芒种
    public static final ResourceKey<SeasonPhase> HOT_SUMMER_SOLSTICE = createKey("hot_summer_solstice");  // 夏至
    public static final ResourceKey<SeasonPhase> HOT_LESSER_HEAT = createKey("hot_lesser_heat");  // 小暑
    public static final ResourceKey<SeasonPhase> HOT_GREATER_HEAT = createKey("hot_greater_heat");  // 大暑

    public static final ResourceKey<SeasonPhase> HOT_BEGINNING_OF_AUTUMN = createKey("hot_beginning_of_autumn");  // 立秋
    public static final ResourceKey<SeasonPhase> HOT_END_OF_HEAT = createKey("hot_end_of_heat");  // 处暑
    public static final ResourceKey<SeasonPhase> HOT_WHITE_DEW = createKey("hot_white_dew");  // 白露
    public static final ResourceKey<SeasonPhase> HOT_AUTUMNAL_EQUINOX = createKey("hot_autumnal_equinox");  // 秋分
    public static final ResourceKey<SeasonPhase> HOT_COLD_DEW = createKey("hot_cold_dew");  // 寒露
    public static final ResourceKey<SeasonPhase> HOT_FIRST_FROST = createKey("hot_first_frost");  // 霜降

    public static final ResourceKey<SeasonPhase> HOT_BEGINNING_OF_WINTER = createKey("hot_beginning_of_winter");  // 立冬
    public static final ResourceKey<SeasonPhase> HOT_LIGHT_SNOW = createKey("hot_light_snow");  // 小雪
    public static final ResourceKey<SeasonPhase> HOT_HEAVY_SNOW = createKey("hot_heavy_snow");  // 大雪
    public static final ResourceKey<SeasonPhase> HOT_WINTER_SOLSTICE = createKey("hot_winter_solstice");  // 冬至
    public static final ResourceKey<SeasonPhase> HOT_LESSER_COLD = createKey("hot_lesser_cold");  // 小寒
    public static final ResourceKey<SeasonPhase> HOT_GREATER_COLD = createKey("hot_greater_cold");  // 大寒

    private static ResourceKey<SeasonPhase> createKey(String name) {
        return ResourceKey.create(ESRegistries.SEASON_PHASE, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstrapContext<SeasonPhase> context) {

        Identifier monsoonIcons = EclipticSeasons.rl("monsoon_icons");

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


        // context.register(DRY, new SeasonPhase(Season.SUMMER,
        //         EclipticSeasons.rl("dry"),
        //         ChatFormatting.GOLD,
        //         Optional.of(new SeasonPhase.Icon(EclipticSeasons.rl("dry_middle").withPrefix(ESRegistries.SEASON_PHASE.identifier().getPath() + "/"))),
        //         new SeasonPhase.FontIcon(monsoonIcons, "b")
        // ));
        //
        // context.register(RAIN, new SeasonPhase(Season.SUMMER,
        //         EclipticSeasons.rl("rain"),
        //         ChatFormatting.BLUE,
        //         Optional.of(new SeasonPhase.Icon(EclipticSeasons.rl("rain_middle").withPrefix(ESRegistries.SEASON_PHASE.identifier().getPath() + "/"))),
        //         new SeasonPhase.FontIcon(monsoonIcons, "e")
        // ));
        //
        // context.register(WET, new SeasonPhase(Season.SUMMER,
        //         EclipticSeasons.rl("wet"),
        //         ChatFormatting.GREEN,
        //         Optional.of(new SeasonPhase.Icon(EclipticSeasons.rl("wet_middle").withPrefix(ESRegistries.SEASON_PHASE.identifier().getPath() + "/"))),
        //         new SeasonPhase.FontIcon(monsoonIcons, "h")
        // ));

        Identifier solarIcons = EclipticSeasons.rl("solar_icons");
        Identifier seasonsIcons = EclipticSeasons.rl("seasons_icons").withPrefix("font/");

        for (SolarTerm solarTerm : SolarTerm.collectValidValues()) {
            ResourceKey<SeasonPhase> coldKey = createKey("cold_" + solarTerm.getName());
            context.register(coldKey, new SeasonPhase(solarTerm.getSeason(),
                    coldKey.identifier(),
                    solarTerm.getSeason().getColor(),
                    Optional.of(new SeasonPhase.Icon(
                            seasonsIcons,
                            solarTerm.getIconWidth(),
                            solarTerm.getIconHeight(),
                            solarTerm.getIconAtlasSize(),
                            solarTerm.getIconPosition().getFirst(),
                            solarTerm.getIconPosition().getSecond()
                    )),
                    new SeasonPhase.FontIcon(solarIcons, solarTerm.getFontLabel())
            ));

            ResourceKey<SeasonPhase> hotKey = createKey("hot_" + solarTerm.getName());
            context.register(hotKey, new SeasonPhase(solarTerm.getSeason(),
                    hotKey.identifier(),
                    solarTerm.getSeason().getColor(),
                    Optional.of(new SeasonPhase.Icon(
                            seasonsIcons,
                            solarTerm.getIconWidth(),
                            solarTerm.getIconHeight(),
                            solarTerm.getIconAtlasSize(),
                            solarTerm.getIconPosition().getFirst(),
                            solarTerm.getIconPosition().getSecond()
                    )),
                    new SeasonPhase.FontIcon(solarIcons, solarTerm.getFontLabel())
            ));
        }

    }
}
