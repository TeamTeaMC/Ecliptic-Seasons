package com.teamtea.eclipticseasons.data.general.lang;

import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.common.registry.SeasonQuestRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;


public class Lang_EN extends LangHelper {
    public Lang_EN(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, EclipticSeasonsApi.MODID, "en_us");
    }


    @Override
    protected void addTranslations() {



        add("itemGroup." + EclipticSeasonsApi.MODID + ".core", "Ecliptic Seasons");

        add(BlockRegistry.calendar.get(), "Calendar");
        add("item.eclipticseasons.calendar.pop_hint", "%1$s, %2$s/%3$s");
        // add(EclipticSeasons.ModContents.wind_chimes.get(), "Wind Chimes");
        // add(EclipticSeasons.ModContents.paper_wind_chimes.get(), "Paper Wind Chimes");
        // add(EclipticSeasons.ModContents.bamboo_wind_chimes.get(), "Bamboo Wind Chimes");
        // add(EclipticSeasons.ModContents.pinwheel_blue.get(), "Blue Pinwheel");
        // add(EclipticSeasons.ModContents.pinwheel_lime.get(), "Lime Pinwheel");
        // add(EclipticSeasons.ModContents.pinwheel_orange.get(), "Orange Pinwheel");
        // add(EclipticSeasons.ModContents.snowy_maker_item.get(), "Ice Wand");
        add(ItemRegistry.broom.get(), "Broom");
        add(ItemRegistry.thermometer.get(), "Thermometer");
        add(ItemRegistry.hyetometer.get(), "Hyetometer");
        add(BlockRegistry.hygrometer.get(), "Hygrometer");

        add(BlockRegistry.greenhouse_core_container.get(), "Container of Greenhouse Core");

        add(BlockRegistry.spring_greenhouse_core.get(), "Spring Greenhouse Core");
        add(BlockRegistry.summer_greenhouse_core.get(), "Summer Greenhouse Core");
        add(BlockRegistry.autumn_greenhouse_core.get(), "Autumn Greenhouse Core");
        add(BlockRegistry.winter_greenhouse_core.get(), "Winter Greenhouse Core");

        add(ItemRegistry.spring_greenhouse_essence_item.get(), "Spring Greenhouse Essence");
        add(ItemRegistry.summer_greenhouse_essence_item.get(), "Summer Greenhouse Essence");
        add(ItemRegistry.autumn_greenhouse_essence_item.get(), "Autumn Greenhouse Essence");
        add(ItemRegistry.winter_greenhouse_essence_item.get(), "Winter Greenhouse Essence");

        add(ItemRegistry.growth_detector.get(), "Growth Detector");

        add(BlockRegistry.season_quest_wall_hanging_sign.get(), "Season Quest Sign");
        add(BlockRegistry.season_quest_ceiling_hanging_sign.get(), "Season Quest Sign");

        add(BlockRegistry.block_in_wooden_grate_block.get(), "Wooden Grate Humidifier");

        add(ItemRegistry.seasonal_prayer_scroll_item.get(), "Seasonal Prayer Scroll");

        add("info.eclipticseasons.environment.temperature.under_freezing", "Under Freezing");
        add("info.eclipticseasons.environment.temperature.freezing", "Freezing");
        add("info.eclipticseasons.environment.temperature.cold", "Cold");
        add("info.eclipticseasons.environment.temperature.cool", "Cool");
        add("info.eclipticseasons.environment.temperature.warm", "Warm");
        add("info.eclipticseasons.environment.temperature.hot", "Hot");
        add("info.eclipticseasons.environment.temperature.heat", "Heat");
        add("info.eclipticseasons.environment.temperature.over_heat", "Over Heat");

        add("info.eclipticseasons.environment.rainfall.rare", "Rare");
        add("info.eclipticseasons.environment.rainfall.scarce", "Scarce");
        add("info.eclipticseasons.environment.rainfall.moderate", "Moderate");
        add("info.eclipticseasons.environment.rainfall.adequate", "Adequate");
        add("info.eclipticseasons.environment.rainfall.abundant", "Abundant");

        add("info.eclipticseasons.environment.humidity.arid", "Arid");
        add("info.eclipticseasons.environment.humidity.dry", "Dry");
        add("info.eclipticseasons.environment.humidity.average", "Average");
        add("info.eclipticseasons.environment.humidity.moist", "Moist");
        add("info.eclipticseasons.environment.humidity.humid", "Humid");
        add("info.eclipticseasons.environment.humidity", "Suitable Humidity:");
        add("info.eclipticseasons.environment.season", "Suitable Season:");
        add("info.eclipticseasons.environment.season.feed", "Breeding Season:");
        add("info.eclipticseasons.environment.season.spring", "Spring");
        add("info.eclipticseasons.environment.season.summer", "Summer");
        add("info.eclipticseasons.environment.season.autumn", "Autumn");
        add("info.eclipticseasons.environment.season.winter", "Winter");
        add("info.eclipticseasons.environment.season.none", "All the year");
        add("info.eclipticseasons.environment.solar_term.beginning_of_spring", "Beginning of Spring");
        add("info.eclipticseasons.environment.solar_term.rain_water", "Rain Water");
        add("info.eclipticseasons.environment.solar_term.insects_awakening", "Insects Awakening");
        add("info.eclipticseasons.environment.solar_term.spring_equinox", "Spring Equinox");
        add("info.eclipticseasons.environment.solar_term.fresh_green", "Fresh Green");
        add("info.eclipticseasons.environment.solar_term.grain_rain", "Grain Rain");
        add("info.eclipticseasons.environment.solar_term.beginning_of_summer", "Beginning of Summer");
        add("info.eclipticseasons.environment.solar_term.lesser_fullness", "Lesser Fullness");
        add("info.eclipticseasons.environment.solar_term.grain_in_ear", "Grain in Ear");
        add("info.eclipticseasons.environment.solar_term.summer_solstice", "Summer Solstice");
        add("info.eclipticseasons.environment.solar_term.lesser_heat", "Lesser Heat");
        add("info.eclipticseasons.environment.solar_term.greater_heat", "Greater Heat");
        add("info.eclipticseasons.environment.solar_term.beginning_of_autumn", "Beginning of Autumn");
        add("info.eclipticseasons.environment.solar_term.end_of_heat", "End of Heat");
        add("info.eclipticseasons.environment.solar_term.white_dew", "White Dew");
        add("info.eclipticseasons.environment.solar_term.autumnal_equinox", "Autumnal Equinox");
        add("info.eclipticseasons.environment.solar_term.cold_dew", "Cold Dew");
        add("info.eclipticseasons.environment.solar_term.first_frost", "First Frost");
        add("info.eclipticseasons.environment.solar_term.beginning_of_winter", "Beginning of Winter");
        add("info.eclipticseasons.environment.solar_term.light_snow", "Light Snow");
        add("info.eclipticseasons.environment.solar_term.heavy_snow", "Heavy Snow");
        add("info.eclipticseasons.environment.solar_term.winter_solstice", "Winter Solstice");
        add("info.eclipticseasons.environment.solar_term.lesser_cold", "Lesser Cold");
        add("info.eclipticseasons.environment.solar_term.greater_cold", "Greater Cold");
        add("info.eclipticseasons.environment.solar_term.none", "None");
        add("info.eclipticseasons.environment.solar_term.message", "[Solar Term Tip] %s");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_spring", "Spring coming back, all things awaken.");
        add("info.eclipticseasons.environment.solar_term.alternation.rain_water", "Moisten with drizzle, grass loomed.");
        add("info.eclipticseasons.environment.solar_term.alternation.insects_awakening", "Thunder rumbling, insects awaken.");
        add("info.eclipticseasons.environment.solar_term.alternation.spring_equinox", "It's just as warm as it is cold. Day and night are as long as each other.");
        add("info.eclipticseasons.environment.solar_term.alternation.fresh_green", "Swallows return, and pear blossoms wither.");
        add("info.eclipticseasons.environment.solar_term.alternation.grain_rain", "It rains noiselessly, but cuckoo crows.");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_summer", "The last spring has gone and the summer is coming.");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_fullness", "Crops are growing, maturing and waiting for the harvest.");
        add("info.eclipticseasons.environment.solar_term.alternation.grain_in_ear", "Winds graze the field. Insects chirp.");
        add("info.eclipticseasons.environment.solar_term.alternation.summer_solstice", "Oh, the longest day is coming.");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_heat", "The height of summer begins.");
        add("info.eclipticseasons.environment.solar_term.alternation.greater_heat", "Sun shining, summer heat rises.");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_autumn", "The color of summer has faded.");
        add("info.eclipticseasons.environment.solar_term.alternation.end_of_heat", "The summer heat has been swept away as the autumn rains are ready.");
        add("info.eclipticseasons.environment.solar_term.alternation.white_dew", "Cool winds graze. White dew gathers.");
        add("info.eclipticseasons.environment.solar_term.alternation.autumnal_equinox", "The daytime decreases when the nighttime increases.");
        add("info.eclipticseasons.environment.solar_term.alternation.cold_dew", "The aura of autumn is getting thicker and thicker.");
        add("info.eclipticseasons.environment.solar_term.alternation.first_frost", "The dew is frosting.");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_winter", "Winds blowing, winter comes.");
        add("info.eclipticseasons.environment.solar_term.alternation.light_snow", "With the winter rain, the weather grows much colder.");
        add("info.eclipticseasons.environment.solar_term.alternation.heavy_snow", "Snow begins to fall and decorate the world.");
        add("info.eclipticseasons.environment.solar_term.alternation.winter_solstice", "Shadows become longer. The endless long night comes.");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_cold", "Severe cold in the depth of winter.");
        add("info.eclipticseasons.environment.solar_term.alternation.greater_cold", "Winds died away, but icy coldness still.");
        add("info.eclipticseasons.environment.solar_term.alternation.none", "...");

        add("commands.eclipticseasons.solar.set", "Set the solar day to %s");
        add("effect.eclipticseasons.heat_stroke", "Heat Stroke");
        add("effect.eclipticseasons.heat_stroke.description", "The scorching heat is unbearable, and your vision gradually blurs.");

        add(AgroClimateRegistry.COLD, "Cold Region");
        add(AgroClimateRegistry.TEMPERATE, "Warm Region");
        add(AgroClimateRegistry.HOT, "Hot Region");
        // add(AgroClimateRegistry.DESERT, "Desert");
        add(AgroClimateRegistry.NETHER, "Nether");
        add(AgroClimateRegistry.END, "End");

        add(SeasonQuestRegistry.SPRING_CORE, "Spring Core Quest");
        add(SeasonQuestRegistry.SUMMER_CORE, "Summer Core Quest");
        add(SeasonQuestRegistry.AUTUMN_CORE, "Autumn Core Quest");
        add(SeasonQuestRegistry.WINTER_CORE, "Winter Core Quest");

        addAdvancements();
        addSeasonQuest();
        addGrowthDetector();

        addJade();
        addInfo();
        addTouhouLittleMaid();

        addExtraEnvInfo();
    }

    private void addExtraEnvInfo() {
        add("info.eclipticseasons.environment.solar_term.hint2", "Year %s :");
        add("info.eclipticseasons.environment.solar_term.hint3", "In %s days:");
        add("info.eclipticseasons.environment.solar_term.hint", "Term Today:");

        add("info.eclipticseasons.environment.season_phase.hint", "Phase Today:");

        add("info.eclipticseasons.environment.season_phase.dry_start", "Dry");
        add("info.eclipticseasons.environment.season_phase.dry_middle", "Dry");
        add("info.eclipticseasons.environment.season_phase.dry_end", "Dry");
        add("info.eclipticseasons.environment.season_phase.alternation.dry_start", "A weary breeze, the sun rests upon the hills.");
        add("info.eclipticseasons.environment.season_phase.alternation.dry_middle", "Dust veils the fields. Green leaves wither.");
        add("info.eclipticseasons.environment.season_phase.alternation.dry_end", "The rivers runs bare and dry.");

        add("info.eclipticseasons.environment.season_phase.rain_start", "Rainy");
        add("info.eclipticseasons.environment.season_phase.rain_middle", "Rainy");
        add("info.eclipticseasons.environment.season_phase.rain_end", "Rainy");
        add("info.eclipticseasons.environment.season_phase.alternation.rain_start", "Drizzling rains, green life awakens.");
        add("info.eclipticseasons.environment.season_phase.alternation.rain_middle", "Rain pours from the sky. Thunders roar like dragons.");
        add("info.eclipticseasons.environment.season_phase.alternation.rain_end", "Heavy raindrops fall. Streams swell into rivers.");

        add("info.eclipticseasons.environment.season_phase.wet_start", "Wet");
        add("info.eclipticseasons.environment.season_phase.wet_middle", "Wet");
        add("info.eclipticseasons.environment.season_phase.wet_end", "Wet");
        add("info.eclipticseasons.environment.season_phase.alternation.wet_start", "Moisture hangs heavy, and the heat is suffocating.");
        add("info.eclipticseasons.environment.season_phase.alternation.wet_middle", "The world steams, and all trees thrive.");
        add("info.eclipticseasons.environment.season_phase.alternation.wet_end", "Rains grow sparse. Winds gradually calm.");

        add("info.eclipticseasons.environment.season_phase.pattern.dry_start", "%s (Early)");
        add("info.eclipticseasons.environment.season_phase.pattern.dry_middle", "%s (Mid)");
        add("info.eclipticseasons.environment.season_phase.pattern.dry_end", "%s (Late)");
        add("info.eclipticseasons.environment.season_phase.pattern.rain_start", "%s (Early)");
        add("info.eclipticseasons.environment.season_phase.pattern.rain_middle", "%s (Mid)");
        add("info.eclipticseasons.environment.season_phase.pattern.rain_end", "%s (Late)");
        add("info.eclipticseasons.environment.season_phase.pattern.wet_start", "%s (Early)");
        add("info.eclipticseasons.environment.season_phase.pattern.wet_middle", "%s (Mid)");
        add("info.eclipticseasons.environment.season_phase.pattern.wet_end", "%s (Late)");

        add("info.eclipticseasons.environment.season_phase.dry", "Dry");
        add("info.eclipticseasons.environment.season_phase.alternation.dry", "Dust fills the air, and not a drop of rain in sight.");
        add("info.eclipticseasons.environment.season_phase.pattern.dry", "%s (All year)");

        add("info.eclipticseasons.environment.season_phase.rain", "Rainy");
        add("info.eclipticseasons.environment.season_phase.alternation.rain", "Rain falls for days, and clear skies are rare.");
        add("info.eclipticseasons.environment.season_phase.pattern.rain", "%s (All year)");

        add("info.eclipticseasons.environment.season_phase.wet", "Wet");
        add("info.eclipticseasons.environment.season_phase.alternation.wet", "Heavy humidity lingers, and the heat is unbearable.");
        add("info.eclipticseasons.environment.season_phase.pattern.wet", "%s (All year)");

        add("info.eclipticseasons.environment.season_phase.cold_beginning_of_spring", "Beginning of Spring");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_beginning_of_spring", "The old year has passed, snow fills the door.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_beginning_of_spring", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.cold_rain_water", "Rain Water");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_rain_water", "Cold winter wanes, spring is still faint.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_rain_water", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.cold_insects_awakening", "Insects Awakening");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_insects_awakening", "Flowers meet a chilly spring.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_insects_awakening", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.cold_spring_equinox", "Spring Equinox");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_spring_equinox", "It's just as warm as it is cold. Day and night are as long as each other.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_spring_equinox", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.cold_fresh_green", "Fresh Green");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_fresh_green", "Swallows return as spring renews the land.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_fresh_green", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.cold_grain_rain", "Grain Rain");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_grain_rain", "Soft rain falls unseen, as birdsong fills the air.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_grain_rain", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.cold_beginning_of_summer", "Beginning of Summer");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_beginning_of_summer", "Fileds in bloom, spring holds its breath.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_beginning_of_summer", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.cold_lesser_fullness", "Lesser Fullness");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_lesser_fullness", "The warmer summer is coming.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_lesser_fullness", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.cold_grain_in_ear", "Grain in Ear");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_grain_in_ear", "Winds graze the field. Insects chirp.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_grain_in_ear", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.cold_summer_solstice", "Summer Solstice");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_summer_solstice", "Oh, the longest day is coming.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_summer_solstice", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.cold_lesser_heat", "Lesser Heat");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_lesser_heat", "Summer warmth insufficient, hints of autumn.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_lesser_heat", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.cold_greater_heat", "Greater Heat");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_greater_heat", "The color of warm summer has faded.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_greater_heat", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.cold_beginning_of_autumn", "Beginning of Autumn");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_beginning_of_autumn", "Cool breeze arrives, autumn fills the sky.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_beginning_of_autumn", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.cold_end_of_heat", "End of Heat");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_end_of_heat", "The sense of autumn wanes, and chill begins to rise.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_end_of_heat", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.cold_white_dew", "White Dew");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_white_dew", "The aura of winter is getting thicker and thicker.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_white_dew", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.cold_autumnal_equinox", "Autumnal Equinox");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_autumnal_equinox", "The daytime decreases when the nighttime increases.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_autumnal_equinox", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.cold_cold_dew", "Cold Dew");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_cold_dew", "Frost surprises the evening, cold lights the forest tips.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_cold_dew", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.cold_first_frost", "First Frost");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_first_frost", "With the winter rain, the weather grows much colder.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_first_frost", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.cold_beginning_of_winter", "Beginning of Winter");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_beginning_of_winter", "Winds wither white grass, sky dim and dark.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_beginning_of_winter", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.cold_light_snow", "Light Snow");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_light_snow", "Cold wind roars fiercely, snow falls thick.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_light_snow", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.cold_heavy_snow", "Heavy Snow");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_heavy_snow", "Snow fills the world, as if trees were blooming with pear blossoms.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_heavy_snow", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.cold_winter_solstice", "Winter Solstice");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_winter_solstice", "Shadows become longer. The endless long night comes.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_winter_solstice", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.cold_lesser_cold", "Lesser Cold");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_lesser_cold", "Severe cold in the depth of winter.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_lesser_cold", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.cold_greater_cold", "Greater Cold");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_greater_cold", "Winds died away, but icy coldness still.");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_greater_cold", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.hot_beginning_of_spring", "Beginning of Spring");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_beginning_of_spring", "Gentle winds warm, chill retreats.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_beginning_of_spring", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.hot_rain_water", "Rain Water");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_rain_water", "Soft spring rains, flowers bloom full.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_rain_water", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.hot_insects_awakening", "Insects Awakening");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_insects_awakening", "Insect calls stir, thunder rumbles near.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_insects_awakening", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.hot_spring_equinox", "Spring Equinox");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_spring_equinox", "It's just warm enough. Day and night are as long as each other.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_spring_equinox", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.hot_fresh_green", "Fresh Green");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_fresh_green", "Swallows fly, blossoms fall.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_fresh_green", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.hot_grain_rain", "Grain Rain");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_grain_rain", "Dark skies hang, soft wind and wet air.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_grain_rain", "%s (SP)");

        add("info.eclipticseasons.environment.season_phase.hot_beginning_of_summer", "Beginning of Summer");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_beginning_of_summer", "Clearing rains, heat arrives.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_beginning_of_summer", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.hot_lesser_fullness", "Lesser Fullness");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_lesser_fullness", "The hotter summer is now.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_lesser_fullness", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.hot_grain_in_ear", "Grain in Ear");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_grain_in_ear", "Winds graze the field. Insects chirp.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_grain_in_ear", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.hot_summer_solstice", "Summer Solstice");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_summer_solstice", "Oh, the longest day is coming.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_summer_solstice", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.hot_lesser_heat", "Lesser Heat");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_lesser_heat", "The height of summer begins.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_lesser_heat", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.hot_greater_heat", "Greater Heat");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_greater_heat", "Sun shining, summer heat rises.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_greater_heat", "%s (SU)");

        add("info.eclipticseasons.environment.season_phase.hot_beginning_of_autumn", "Beginning of Autumn");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_beginning_of_autumn", "Winds dry the grass, leaves droop low.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_beginning_of_autumn", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.hot_end_of_heat", "End of Heat");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_end_of_heat", "Summer heat fades, autumn rains near.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_end_of_heat", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.hot_white_dew", "White Dew");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_white_dew", "Heat withdraws, new coolness follows rain.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_white_dew", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.hot_autumnal_equinox", "Autumnal Equinox");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_autumnal_equinox", "The daytime decreases when the nighttime increases.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_autumnal_equinox", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.hot_cold_dew", "Cold Dew");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_cold_dew", "Clear skies, south geese arrive.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_cold_dew", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.hot_first_frost", "First Frost");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_first_frost", "Greens fade, parasol leaves yellow.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_first_frost", "%s (AU)");

        add("info.eclipticseasons.environment.season_phase.hot_beginning_of_winter", "Beginning of Winter");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_beginning_of_winter", "Heat departs, yet autumn’s breath remains.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_beginning_of_winter", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.hot_light_snow", "Light Snow");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_light_snow", "Maple leaves red, chilly winds rise.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_light_snow", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.hot_heavy_snow", "Heavy Snow");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_heavy_snow", "Sparse cold rains, leisurely tapping leaves.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_heavy_snow", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.hot_winter_solstice", "Winter Solstice");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_winter_solstice", "Shadows become longer. The endless long night comes.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_winter_solstice", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.hot_lesser_cold", "Lesser Cold");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_lesser_cold", "North winds chill, occasional snow falls.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_lesser_cold", "%s (WI)");

        add("info.eclipticseasons.environment.season_phase.hot_greater_cold", "Greater Cold");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_greater_cold", "Winds died away, but icy coldness still.");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_greater_cold", "%s (WI)");

    }


    private void addJade() {
        add("config.jade.plugin_eclipticseasons.crop", "Crop");
        add("config.jade.plugin_eclipticseasons.animal", "Animal");

        add("config.jade.plugin_eclipticseasons.crop.shift_hint", "Shift Key Hint");
        add("hint.jade.plugin_eclipticseasons.crop.show", "§o<..Hold shift to see more..>");

        add("config.jade.plugin_eclipticseasons.snowy_status", "Show Snowy Status");
        add("hint.jade.plugin_eclipticseasons.snowy_status.snowy", "§7Covered with Snow");
    }

    private void addTouhouLittleMaid() {
        add("task.eclipticseasons.clean_snow", "Clean Snow");
        add("task.eclipticseasons.clean_snow.desc", "Applied to snow-covered blocks from Ecliptic Season");
        add("task.eclipticseasons.clean_snow.condition.has_broom", "Has Broom");
    }

    private void addInfo() {
        add("patchouli_books.eclipticseasons.seasons_chronicle.name", "Seasons Chronicle");
        add("patchouli_books.eclipticseasons.seasons_chronicle.landing_text", "Each season leaves a mark — the \"Chronicle of the Seasons\" records them all.");

        add("info.eclipticseasons.humidity_control", "Humidity Control");
        add("info.eclipticseasons.season_quest", "Seasonal Quest");
        add("info.eclipticseasons.humidity_control.below_need", "Needs %s below");
        add("info.eclipticseasons.humidity_control.common_need", "Requires %s");
        add("info.eclipticseasons.humidity_control.extra_hint", "§7 - Alternative placements:");

        add("info.eclipticseasons.bone_meal.failure", "Cannot accelerate growth under current conditions.");

        add("info.eclipticseasons.calendar.model", "Switched to %s display mode");
        add("info.eclipticseasons.calendar.model.normal", "default");
        add("info.eclipticseasons.calendar.model.year", "year");
        add("info.eclipticseasons.calendar.model.next", "next");

        add("pack.eclipticseasons.extra_snow","Extra Snow Resourcepack");


        add("info.eclipticseasons.config.inactive", "§7Not work under the current configuration.");
        add("info.eclipticseasons.greenhouse_core.effect", "Boosts %2$s crops within %1$s blocks in a greenhouse");

        add("info.eclipticseasons.show.shift", "§o<..Hold shift..>");
        add("info.eclipticseasons.greenhouse_essence.source", "Obtained by completing %1$s advancements or quests");

        add("info.eclipticseasons.seasonal_prayer_scroll.use", "Activate a hanging sign as a seasonal quest sign for timed quests");


    }

    private void addAdvancements() {
        add("advancement.eclipticseasons.base", "Ecliptic Seasons");
        add("advancement.eclipticseasons.base.desc", "");

        add("advancement.eclipticseasons.root", "Spring After Autumn");
        add("advancement.eclipticseasons.root.desc", "Span a year with twenty-four solar terms");

        add("advancement.eclipticseasons.heat_stroke", "First Heat Stroke");
        add("advancement.eclipticseasons.heat_stroke.desc", "Do not walk directly under the sun in hot communities during the summer afternoon. If necessary, bring heat-resistant gear or take ice and snow to cool down");

        add("advancement.eclipticseasons.green_house", "Greenhouse Architect");
        add("advancement.eclipticseasons.green_house.desc", "Build a sealed space that traps warmth and air—just like nature's cheat code!");

        add("advancement.eclipticseasons.greenhouse_core_container", "Out-of-Season Grower");
        add("advancement.eclipticseasons.greenhouse_core_container.desc", "Install a Greenhouse Core Container, then grow what nature says you shouldn't");
        add("advancement.eclipticseasons.greenhouse_core", "Essence Igniter");
        add("advancement.eclipticseasons.greenhouse_core.desc", "Power up your greenhouse by placing a Greenhouse Essence into its heart");

        add("advancement.eclipticseasons.copper_grate", "Craft a Grate");
        add("advancement.eclipticseasons.copper_grate.desc", "Maybe something can be placed in the grate to alter the environment?");
        add("advancement.eclipticseasons.block_in_copper_grate", "Change Humidity");
        add("advancement.eclipticseasons.block_in_copper_grate.desc", "Some blocks that affect humidity could be placed in the grate—beware, for they may transform with time or need warmth to fade into vapor~");

        add("advancement.eclipticseasons.seasonal_prayer_scroll", "Seasonal Prayer Scroll");
        add("advancement.eclipticseasons.seasonal_prayer_scroll.desc", "Craft a Seasonal Prayer Scroll to obtain Greenhouse Essences");
        add("advancement.eclipticseasons.decorate_oak_hanging_sign", "Endless Quest");
        add("advancement.eclipticseasons.decorate_oak_hanging_sign.desc", "Right-click a Hanging Sign with the Seasonal Prayer Scroll to turn it into a Seasonal Quest Sign, and complete endless quests to earn Greenhouse Essences");

        add("advancement.eclipticseasons.quest", "Seasonal Quest");
        add("advancement.eclipticseasons.quest.desc", "Complete seasonal quests to earn Greenhouse Essence rewards");

        add("advancement.eclipticseasons.spring_start", "Spring Quest");
        add("advancement.eclipticseasons.spring_start.desc", "Plant wheat as all things grow");
        add("advancement.eclipticseasons.spring_harvest", "Spring Harvest");
        add("advancement.eclipticseasons.spring_harvest.desc", "Harvest wheat in the lingering spring");
        add("advancement.eclipticseasons.spring_feed", "Feed Animals");
        add("advancement.eclipticseasons.spring_feed.desc", "Feed sheep, cows, or chickens");
        add("advancement.eclipticseasons.spring_seed", "Spring Seeds");
        add("advancement.eclipticseasons.spring_seed.desc", "Collect wheat seeds to prepare for the next spring");
        add("advancement.eclipticseasons.spring_bread", "Bake Bread");
        add("advancement.eclipticseasons.spring_bread.desc", "After hard work, enjoy a hearty meal~");
        add("advancement.eclipticseasons.spring_hay", "Craft Hay Bale");
        add("advancement.eclipticseasons.spring_hay.desc", "Craft hay bales to store your harvest");
        // add("advancement.eclipticseasons.spring_end", "Spring Greenhouse Core");
        // add("advancement.eclipticseasons.spring_end.desc", "Place the Spring Greenhouse Essence into the Greenhouse Core Container");

        add("advancement.eclipticseasons.summer_start", "Summer Quest");
        add("advancement.eclipticseasons.summer_start.desc", "Prepare to enjoy watermelons in the heat of summer~");
        add("advancement.eclipticseasons.summer_harvest", "Summer Harvest");
        add("advancement.eclipticseasons.summer_harvest.desc", "Cool off with watermelons in the scorching summer");
        add("advancement.eclipticseasons.summer_melon_slice", "Craft Melon Slices");
        add("advancement.eclipticseasons.summer_melon_slice.desc", "Can anyone eat a whole watermelon?");
        add("advancement.eclipticseasons.summer_seed", "Summer Seeds");
        add("advancement.eclipticseasons.summer_seed.desc", "Collect watermelon seeds to prepare for the next summer");
        add("advancement.eclipticseasons.summer_glistering_melon_slice", "Glistering Melon Slice");
        add("advancement.eclipticseasons.summer_glistering_melon_slice.desc", "Craft a glistering melon slice. What is this?");
        add("advancement.eclipticseasons.summer_eat_glistering_melon_slice", "Eat Glistering Melon Slice");
        add("advancement.eclipticseasons.summer_eat_glistering_melon_slice.desc", "Is it really edible?");
        // add("advancement.eclipticseasons.summer_end", "Summer Greenhouse Core");
        // add("advancement.eclipticseasons.summer_end.desc", "Place the Summer Greenhouse Essence into the Greenhouse Core Container");

        add("advancement.eclipticseasons.autumn_start", "Autumn Quest");
        add("advancement.eclipticseasons.autumn_start.desc", "Plant pumpkins");
        add("advancement.eclipticseasons.autumn_harvest", "Autumn Harvest");
        add("advancement.eclipticseasons.autumn_harvest.desc", "It's a giant pumpkin!");
        add("advancement.eclipticseasons.autumn_seed", "Autumn Seeds");
        add("advancement.eclipticseasons.autumn_seed.desc", "Collect pumpkin seeds to prepare for the next autumn");
        add("advancement.eclipticseasons.autumn_carved_pumpkin", "Carved Pumpkin");
        add("advancement.eclipticseasons.autumn_carved_pumpkin.desc", "A mysterious festival is approaching");
        add("advancement.eclipticseasons.autumn_jack_o_lantern", "Craft Jack o'Lantern");
        add("advancement.eclipticseasons.autumn_jack_o_lantern.desc", "Shine bright today~");
        add("advancement.eclipticseasons.autumn_pumpkin_pie", "Bake Pumpkin Pie");
        add("advancement.eclipticseasons.autumn_pumpkin_pie.desc", "It's pie, not π");
        // add("advancement.eclipticseasons.autumn_end", "Autumn Greenhouse Core");
        // add("advancement.eclipticseasons.autumn_end.desc", "Place the Autumn Greenhouse Essence into the Greenhouse Core Container");

        add("advancement.eclipticseasons.winter_start", "Winter Quest");
        add("advancement.eclipticseasons.winter_start.desc", "Get ready to grab a broom and sweep the snow~");
        add("advancement.eclipticseasons.winter_harvest", "Harvest Powder Snow");
        add("advancement.eclipticseasons.winter_harvest.desc", "Collect powder snow using a cauldron when it snows");
        add("advancement.eclipticseasons.winter_campfire", "Winter Campfire");
        add("advancement.eclipticseasons.winter_campfire.desc", "Snowy night, warm fire, and me");
        add("advancement.eclipticseasons.winter_milk", "Drink Milk");
        add("advancement.eclipticseasons.winter_milk.desc", "Warm yourself up");
        add("advancement.eclipticseasons.winter_carpet", "Craft Carpet");
        add("advancement.eclipticseasons.winter_carpet.desc", "Oh dear, don't catch a cold");
        add("advancement.eclipticseasons.winter_cake", "Bake Cake");
        add("advancement.eclipticseasons.winter_cake.desc", "Let's celebrate a year of good harvest~");
        // add("advancement.eclipticseasons.winter_end", "Winter Greenhouse Core");
        // add("advancement.eclipticseasons.winter_end.desc", "Place the Winter Greenhouse Essence into the Greenhouse Core Container");
    }

    private void addSeasonQuest() {
        add("eclipticseasons.season_quest.hint.loading", "Seems empty");
        add("eclipticseasons.season_quest.hint.item_count", "%s x %s");
    }

    private void addGrowthDetector() {
        add("item.eclipticseasons.growth_detector.hint.title", "§lDetection Result: ");

        add("item.eclipticseasons.growth_detector.hint.agro_climatic_zone", "Current agro climatic zone is %s, ");

        add("item.eclipticseasons.growth_detector.hint.greenroom_1", "%s is in the greenhouse, ");
        add("item.eclipticseasons.growth_detector.hint.greenroom_2", "%s might be in the greenhouse, ");
        add("item.eclipticseasons.growth_detector.hint.greenroom_3", "%s is not in the greenhouse, ");

        add("item.eclipticseasons.growth_detector.hint.season_core", "seasonal greenhouse core is missing, ");
        add("item.eclipticseasons.growth_detector.hint.humidity", "humidity conditions are unsuitable, ");

        add("item.eclipticseasons.growth_detector.hint.grow_chance_1", "will grow rapidly");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_2", "will grow faster");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_3", "will grow normally");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_4", "will grow slowly");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_5", "will hardly grow");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_6", "will not grow");
    }
}
