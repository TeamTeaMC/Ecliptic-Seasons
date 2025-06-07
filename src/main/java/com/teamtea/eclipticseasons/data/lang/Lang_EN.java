package com.teamtea.eclipticseasons.data.lang;

import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.SeasonQuestRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;


public class Lang_EN extends LangHelper {
    public Lang_EN(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, EclipticSeasonsApi.MODID, "en_us");
    }


    @Override
    protected void addTranslations() {
        addAdvancements();

        add("info.eclipticseasons.environment.solar_term.hint", "Solar Term Today:");

        add("itemGroup." + EclipticSeasonsApi.MODID + ".core", "Ecliptic Seasons");

        add(BlockRegistry.calendar.get(), "Calendar");
        add("item.eclipticseasons.calendar.pop_hint", "%1$s, %2$s/%3$s");
        add(BlockRegistry.wind_chimes.get(), "Wind Chimes");
        add(BlockRegistry.paper_wind_chimes.get(), "Paper Wind Chimes");
        add(BlockRegistry.bamboo_wind_chimes.get(), "Bamboo Wind Chimes");
        add(BlockRegistry.pinwheel_blue.get(), "Blue Pinwheel");
        add(BlockRegistry.pinwheel_lime.get(), "Lime Pinwheel");
        add(BlockRegistry.pinwheel_orange.get(), "Orange Pinwheel");
        add(ItemRegistry.ice_wand.get(), "Ice Wand");
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

        add(BlockRegistry.block_in_copper_grate_block.get(), "Copper Grate Humidifier");
        add(BlockRegistry.block_in_exposed_copper_grate_block.get(), "Exposed Copper Grate Humidifier");
        add(BlockRegistry.block_in_weathered_copper_grate_block.get(), "Weathered Copper Grate Humidifier");
        add(BlockRegistry.block_in_oxidized_copper_grate_block.get(), "Oxidized Copper Grate Humidifier");
        add(BlockRegistry.block_in_waxed_copper_grate_block.get(), "Waxed Copper Grate Humidifier");
        add(BlockRegistry.block_in_waxed_exposed_copper_grate_block.get(), "Waxed Exposed Copper Grate Humidifier");
        add(BlockRegistry.block_in_waxed_weathered_copper_grate_block.get(), "Waxed Weathered Copper Grate Humidifier");
        add(BlockRegistry.block_in_waxed_oxidized_copper_grate_block.get(), "Waxed Oxidized Copper Grate Humidifier");

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


        add(AgroClimateRegistry.COLD, "Cold Zone");
        add(AgroClimateRegistry.TEMPERATE, "Temperate Zone");
        add(AgroClimateRegistry.HOT, "Tropical Zone");
        add(AgroClimateRegistry.DESERT, "Desert");
        add(AgroClimateRegistry.NETHER, "Nether");
        add(AgroClimateRegistry.END, "End");

        add(SeasonQuestRegistry.SPRING_CORE, "Spring Core Quest");
        add(SeasonQuestRegistry.SUMMER_CORE, "Summer Core Quest");
        add(SeasonQuestRegistry.AUTUMN_CORE, "Autumn Core Quest");
        add(SeasonQuestRegistry.WINTER_CORE, "Winter Core Quest");

        addGrowthDetector();
        addSeasonQuest();
        addConfigLang();
        addInfo();

        addJade();
    }

    private void addJade() {
        add("config.jade.plugin_eclipticseasons.crop","Crop");
        add("config.jade.plugin_eclipticseasons.crop.shift_hint","Shift Key Hint");
        add("hint.jade.plugin_eclipticseasons.crop.show", "§o<..Hold shift to see more..>");

        add("config.jade.plugin_eclipticseasons.snowy_status","Show Snowy Status");
        add("hint.jade.plugin_eclipticseasons.snowy_status.snowy", "§7Covered with Snow");
    }

    private void addInfo() {
        add("info.eclipticseasons.humidity_control", "Humidity Control");
        add("info.eclipticseasons.season_quest", "Seasonal Quest");
        add("info.eclipticseasons.humidity_control.below_need", "Needs %s below");
        add("info.eclipticseasons.humidity_control.common_need", "Requires %s");
        add("info.eclipticseasons.humidity_control.extra_hint", "§7 - Alternative placements:");
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

        add("advancement.eclipticseasons.copper_grate", "Craft a Copper Grate");
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

        add("item.eclipticseasons.growth_detector.hint.grow_chance_1", "Crops will grow rapidly");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_2", "Crops will grow faster");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_3", "Crops will grow normally");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_4", "Crops will grow slowly");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_5", "Crops will hardly grow");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_6", "Crops will not grow");
    }

    private void addConfigLang() {
        add("eclipticseasons.configuration.Compat", "Compatibility");
        add("eclipticseasons.configuration.GUI", "GUI");
        add("eclipticseasons.configuration.EnableCropHumidityControl", "Enable Crop Humidity Control");
        add("eclipticseasons.configuration.Particle", "Particle");
        add("eclipticseasons.configuration.ServerRealisticSnowyChange", "Server Realistic Snowy Change");
        add("eclipticseasons.configuration.Animal", "Animal");
        add("eclipticseasons.configuration.FlowerOnGrass", "Flower On Grass");
        add("eclipticseasons.configuration.EnableSeasonalBreed", "Enable Seasonal Breed");
        add("eclipticseasons.configuration.EnhancementChunkRenderUpdate", "Enhancement Chunk Render Update");
        add("eclipticseasons.configuration.DynamicDaylightDuration", "Dynamic Daylight Duration");
        add("eclipticseasons.configuration.Sound", "Sound");
        add("eclipticseasons.configuration.Map", "Map");
        add("eclipticseasons.configuration.LogIllegalUse", "Log Illegal Use");
        add("eclipticseasons.configuration.Crop", "Crop");
        add("eclipticseasons.configuration.HeatStroke", "Heat Stroke");
        add("eclipticseasons.configuration.WildGooseSpawnWeight", "Wild Goose Spawn Weight");
        add("eclipticseasons.configuration.SnowOverlayGlowingBlock", "Snow Overlay on Glowing Blocks");
        add("eclipticseasons.configuration.SnowyWinter", "Snowy Winter");
        add("eclipticseasons.configuration.WildGoose", "Wild Goose");
        add("eclipticseasons.configuration.UseVanillaCheck", "Use Vanilla Check");
        add("eclipticseasons.configuration.SeasonParticle", "Season Particle");
        add("eclipticseasons.configuration.Season", "Season");
        add("eclipticseasons.configuration.FireflySpawnWeight", "Firefly Spawn Weight");
        add("eclipticseasons.configuration.LastingDaysOfEachTerm", "Lasting Days of Each Term");
        add("eclipticseasons.configuration.ThunderChancePercentMultiplier", "Thunder Chance Percent Multiplier");
        add("eclipticseasons.configuration.ValidDimensions", "Valid Dimensions");
        add("eclipticseasons.configuration.Temperature", "Temperature");
        add("eclipticseasons.configuration.Firefly", "Firefly");
        add("eclipticseasons.configuration.MinChunkCompileWaringTime", "Minimum Chunk Compile Warning Time");
        add("eclipticseasons.configuration.LegacyIceAndSnowAccumulationMelt", "Legacy Ice and Snow Accumulation Melt");
        add("eclipticseasons.configuration.RealisticSnowyChange", "Realistic Snowy Change");
        add("eclipticseasons.configuration.NotSnowyNearGlowingBlockLevel", "No Snow Near Glowing Block Level");
        add("eclipticseasons.configuration.Butterfly", "Butterfly");
        add("eclipticseasons.configuration.Debug", "Debug");
        add("eclipticseasons.configuration.UseDefaultValue", "Use Default Value");
        add("eclipticseasons.configuration.SeasonalGrassColorChange", "Seasonal Grass Color Change");
        add("eclipticseasons.configuration.RainChancePercentMultiplier", "Rain Chance Percent Multiplier");
        add("eclipticseasons.configuration.Weather", "Weather");
        add("eclipticseasons.configuration.FallenLeaves", "Fallen Leaves");
        add("eclipticseasons.configuration.ForceChunkRenderUpdate", "Force Chunk Render Update");
        add("eclipticseasons.configuration.EnableInform", "Enable Inform");
        add("eclipticseasons.configuration.AgriculturalInformation", "Agricultural Information");
        add("eclipticseasons.configuration.NoSnowyUnderLight0", "No Snow Under Light Level 0");
        add("eclipticseasons.configuration.NotSnowyNearGlowingBlock", "No Snow Near Glowing Blocks");
        add("eclipticseasons.configuration.DebugInfo", "Debug Info");
        add("eclipticseasons.configuration.EnableSeasonalBee", "Enable Seasonal Bee");
        add("eclipticseasons.configuration.UseSolarWeather", "Use Solar Weather");
        add("eclipticseasons.configuration.DisableSnowOverlayControlTag", "Disable Snow Overlay Control Tag");
        add("eclipticseasons.configuration.SereneSeasonsCropTag", "Serene Seasons Crop Tag");
        add("eclipticseasons.configuration.Renderer", "Renderer");
        add("eclipticseasons.configuration.SnowUnderFence", "Snow Under Fence");
        add("eclipticseasons.configuration.NaturalSound", "Natural Sound");
        add("eclipticseasons.configuration.butterflySpawnWeight", "Butterfly Spawn Weight");
        add("eclipticseasons.configuration.EnableSeasonalFishing", "Enable Seasonal Fishing");
        add("eclipticseasons.configuration.EnableSeasonalCrop", "Enable Seasonal Crop");
        add("eclipticseasons.configuration.WeatherBufferDistance", "Weather Buffer Distance");
        add("eclipticseasons.configuration.InitialSolarTermIndex", "Initial Solar Term Index");
        add("eclipticseasons.configuration.FallenLeavesDropWeight", "Fallen Leaves Drop Weight");
        add("eclipticseasons.configuration.SnowyFullCollisionShape", "Snowy if Full Collision Shape");
        add("eclipticseasons.configuration.CropGrowChanceInWrongSeason", "Crop Grow Chance in Wrong Season");
        add("eclipticseasons.configuration.CropGrowChanceInWrongHumidity", "Crop Grow Chance in Wrong Humidity");
        add("eclipticseasons.configuration.GreenHouseMaxDiameter", "Max Diameter of Green House");
        add("eclipticseasons.configuration.ComplexGreenHouseCheck", "Complex Green House Check");
        add("eclipticseasons.configuration.Compat.button", "Compatibility");
        add("eclipticseasons.configuration.Weather.button", "Weather");
        add("eclipticseasons.configuration.Particle.button", "Particle");
        add("eclipticseasons.configuration.Animal.button", "Animal");
        add("eclipticseasons.configuration.ValidDimensions.button", "Valid Dimensions");
        add("eclipticseasons.configuration.Sound.button", "Sound");
        add("eclipticseasons.configuration.Season.button", "Season");
        add("eclipticseasons.configuration.Temperature.button", "Temperature");
        add("eclipticseasons.configuration.Renderer.button", "Renderer");
        add("eclipticseasons.configuration.Crop.button", "Crop");
        add("eclipticseasons.configuration.GUI.button", "GUI");
        add("eclipticseasons.configuration.Map.button", "Map");
        add("eclipticseasons.configuration.title", "Title");
        add("eclipticseasons.configuration.Debug.button", "Debug");
        add("eclipticseasons.configuration.EnableInformIcon", "Enable Solar Term Icon Display in Inform");
        add("eclipticseasons.configuration.CalendarItemHint", "Calendar Item Pop Hint");


        add("eclipticseasons.configuration.AutumnTemps", "Autumn Temps");
        add("eclipticseasons.configuration.SummerTemps", "Summer Temps");
        add("eclipticseasons.configuration.SpringTemps", "Spring Temps");
        add("eclipticseasons.configuration.ColdSweat", "Cold Sweat");
        add("eclipticseasons.configuration.WinterTemps", "Winter Temps");
        add("eclipticseasons.configuration.SpringTemps.button", "Spring Temps");
        add("eclipticseasons.configuration.WinterTemps.button", "Winter Temps");
        add("eclipticseasons.configuration.SummerTemps.button", "Summer Temps");
        add("eclipticseasons.configuration.AutumnTemps.button", "Autumn Temps");
        add("eclipticseasons.configuration.ColdSweat.button", "Cold Sweat");

        add("eclipticseasons.configuration.JourneyMap", "Journey Map");
        add("eclipticseasons.configuration.ShowSnowyBlock", "Show Snowy Block");
        add("eclipticseasons.configuration.JourneyMap.button", "Journey Map");
    }
}
