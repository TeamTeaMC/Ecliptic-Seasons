package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.season.SpecialDays;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public class SpecialDaysRegistry {

    public static final ResourceKey<SpecialDays> SPRING_FESTIVAL = createKey("spring_festival");
    public static final ResourceKey<SpecialDays> FLOWER_FESTIVAL = createKey("flower_festival");
    public static final ResourceKey<SpecialDays> SPRING_OUTING = createKey("spring_outing");
    public static final ResourceKey<SpecialDays> EASTER = createKey("easter");
    public static final ResourceKey<SpecialDays> CHINESE_VALENTINES_DAY = createKey("chinese_valentines_day");
    public static final ResourceKey<SpecialDays> MID_AUTUMN = createKey("mid_autumn");
    public static final ResourceKey<SpecialDays> CHRISTMAS = createKey("christmas");
    public static final ResourceKey<SpecialDays> NEW_YEAR = createKey("new_year");

    private static ResourceKey<SpecialDays> createKey(String name) {
        return ResourceKey.create(ESRegistries.SPECIAL_DAYS, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstrapContext<SpecialDays> context) {
        context.register(FLOWER_FESTIVAL, SpecialDays.builder()
                .term(SolarTerm.INSECTS_AWAKENING)
                .start(0.75f)
                .end(1.0f)
                .title(Component.translatable(FLOWER_FESTIVAL.identifier().toLanguageKey(ESRegistries.SPECIAL_DAYS.identifier().getPath())))
                .build());

        context.register(SPRING_FESTIVAL, SpecialDays.builder()
                .term(SolarTerm.BEGINNING_OF_SPRING)
                .start(0.0f)
                .end(1.0f)
                .title(Component.translatable(SPRING_FESTIVAL.identifier().toLanguageKey(ESRegistries.SPECIAL_DAYS.identifier().getPath())))
                .build());

        context.register(SPRING_OUTING, SpecialDays.builder()
                .term(SolarTerm.FRESH_GREEN)
                .start(0.0f)
                .end(0.15f)
                .title(Component.translatable(SPRING_OUTING.identifier().toLanguageKey(ESRegistries.SPECIAL_DAYS.identifier().getPath())))
                .build());

        context.register(EASTER, SpecialDays.builder()
                .term(SolarTerm.SPRING_EQUINOX)
                .start(0.2f)
                .end(0.35f)
                .title(Component.translatable(EASTER.identifier().toLanguageKey(ESRegistries.SPECIAL_DAYS.identifier().getPath())))
                .build());

        context.register(CHINESE_VALENTINES_DAY, SpecialDays.builder()
                .term(SolarTerm.BEGINNING_OF_AUTUMN)
                .start(0.3f)
                .end(0.45f)
                .title(Component.translatable(CHINESE_VALENTINES_DAY.identifier().toLanguageKey(ESRegistries.SPECIAL_DAYS.identifier().getPath())))
                .build());

        context.register(MID_AUTUMN, SpecialDays.builder()
                .term(SolarTerm.WHITE_DEW)
                .start(0.7f)
                .end(1.0f)
                .title(Component.translatable(MID_AUTUMN.identifier().toLanguageKey(ESRegistries.SPECIAL_DAYS.identifier().getPath())))
                .build());

        context.register(CHRISTMAS, SpecialDays.builder()
                .term(SolarTerm.WINTER_SOLSTICE)
                .start(0.25f)
                .end(0.4f)
                .title(Component.translatable(CHRISTMAS.identifier().toLanguageKey(ESRegistries.SPECIAL_DAYS.identifier().getPath())))
                .build());

        context.register(NEW_YEAR, SpecialDays.builder()
                .term(SolarTerm.WINTER_SOLSTICE)
                .start(0.7f)
                .end(0.85f)
                .title(Component.translatable(NEW_YEAR.identifier().toLanguageKey(ESRegistries.SPECIAL_DAYS.identifier().getPath())))
                .build());
    }

}
