package com.teamtea.eclipticseasons.data.lang;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;


public class Lang_EN extends LangHelper {
    public Lang_EN(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, EclipticSeasonsApi.MODID, "en_us");
    }


    @Override
    protected void addTranslations() {
        add("advancement.eclipticseasons.root", "Ecliptic Seasons : Spring After Autumn");
        add("advancement.eclipticseasons.root.desc", "Span a year with 24 solar terms.");

        add("advancement.eclipticseasons.heat_stroke", "First Heat Stroke");
        add("advancement.eclipticseasons.heat_stroke.desc", "Do not walk directly under the sun in hot communities during the summer afternoon. If necessary, bring heat-resistant gear or take ice and snow to cool down.");

        add("info.eclipticseasons.environment.solar_term.hint", "Solar Term Today:");

        add("itemGroup." + EclipticSeasonsApi.MODID + ".core", "Ecliptic Seasons");

        add(EclipticSeasons.ModContents.calendar.get(), "calendar");
        add(EclipticSeasons.ModContents.wind_chimes.get(), "Wind Chimes");
        add(EclipticSeasons.ModContents.paper_wind_chimes.get(), "Paper Wind Chimes");
        add(EclipticSeasons.ModContents.bamboo_wind_chimes.get(), "Bamboo Wind Chimes");
        add(EclipticSeasons.ModContents.pinwheel_blue.get(), "Blue Pinwheel");
        add(EclipticSeasons.ModContents.pinwheel_lime.get(), "Lime Pinwheel");
        add(EclipticSeasons.ModContents.pinwheel_orange.get(), "Orange Pinwheel");
        add(EclipticSeasons.ModContents.snowy_maker_item.get(), "Ice Wand");
        add(EclipticSeasons.ModContents.broom_item.get(), "Broom");

        add("info.eclipticseasons.environment.temperature.under_freezing","Under Freezing");
        add("info.eclipticseasons.environment.temperature.freezing","Freezing");
        add("info.eclipticseasons.environment.temperature.cold","Cold");
        add("info.eclipticseasons.environment.temperature.cool","Cool");
        add("info.eclipticseasons.environment.temperature.warm","Warm");
        add("info.eclipticseasons.environment.temperature.hot","Hot");
        add("info.eclipticseasons.environment.temperature.heat","Heat");
        add("info.eclipticseasons.environment.temperature.over_heat","Over Heat");
        add("info.eclipticseasons.environment.humidity.arid","Arid");
        add("info.eclipticseasons.environment.humidity.dry","Dry");
        add("info.eclipticseasons.environment.humidity.average","Average");
        add("info.eclipticseasons.environment.humidity.moist","Moist");
        add("info.eclipticseasons.environment.humidity.humid","Humid");
        add("info.eclipticseasons.environment.humidity","Suitable Humidity:");
        add("info.eclipticseasons.environment.season","Suitable Season:");
        add("info.eclipticseasons.environment.season.spring","Spring");
        add("info.eclipticseasons.environment.season.summer","Summer");
        add("info.eclipticseasons.environment.season.autumn","Autumn");
        add("info.eclipticseasons.environment.season.winter","Winter");
        add("info.eclipticseasons.environment.season.none","All the year");
        add("info.eclipticseasons.environment.solar_term.beginning_of_spring","Beginning of Spring");
        add("info.eclipticseasons.environment.solar_term.rain_water","Rain Water");
        add("info.eclipticseasons.environment.solar_term.insects_awakening","Insects Awakening");
        add("info.eclipticseasons.environment.solar_term.spring_equinox","Spring Equinox");
        add("info.eclipticseasons.environment.solar_term.fresh_green","Fresh Green");
        add("info.eclipticseasons.environment.solar_term.grain_rain","Grain Rain");
        add("info.eclipticseasons.environment.solar_term.beginning_of_summer","Beginning of Summer");
        add("info.eclipticseasons.environment.solar_term.lesser_fullness","Lesser Fullness");
        add("info.eclipticseasons.environment.solar_term.grain_in_ear","Grain in Ear");
        add("info.eclipticseasons.environment.solar_term.summer_solstice","Summer Solstice");
        add("info.eclipticseasons.environment.solar_term.lesser_heat","Lesser Heat");
        add("info.eclipticseasons.environment.solar_term.greater_heat","Greater Heat");
        add("info.eclipticseasons.environment.solar_term.beginning_of_autumn","Beginning of Autumn");
        add("info.eclipticseasons.environment.solar_term.end_of_heat","End of Heat");
        add("info.eclipticseasons.environment.solar_term.white_dew","White Dew");
        add("info.eclipticseasons.environment.solar_term.autumnal_equinox","Autumnal Equinox");
        add("info.eclipticseasons.environment.solar_term.cold_dew","Cold Dew");
        add("info.eclipticseasons.environment.solar_term.first_frost","First Frost");
        add("info.eclipticseasons.environment.solar_term.beginning_of_winter","Beginning of Winter");
        add("info.eclipticseasons.environment.solar_term.light_snow","Light Snow");
        add("info.eclipticseasons.environment.solar_term.heavy_snow","Heavy Snow");
        add("info.eclipticseasons.environment.solar_term.winter_solstice","Winter Solstice");
        add("info.eclipticseasons.environment.solar_term.lesser_cold","Lesser Cold");
        add("info.eclipticseasons.environment.solar_term.greater_cold","Greater Cold");
        add("info.eclipticseasons.environment.solar_term.message","[Solar Term Tip] %s");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_spring","Spring coming back, all things awaken.");
        add("info.eclipticseasons.environment.solar_term.alternation.rain_water","Moisten with drizzle, grass loomed.");
        add("info.eclipticseasons.environment.solar_term.alternation.insects_awakening","Thunder rumbling, insects awaken.");
        add("info.eclipticseasons.environment.solar_term.alternation.spring_equinox","It's just as warm as it is cold. Day and night are as long as each other.");
        add("info.eclipticseasons.environment.solar_term.alternation.fresh_green","Swallows return, and pear blossoms wither.");
        add("info.eclipticseasons.environment.solar_term.alternation.grain_rain","It rains noiselessly, but cuckoo crows.");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_summer","The last spring has gone and the summer is coming.");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_fullness","Crops are growing, maturing and waiting for the harvest.");
        add("info.eclipticseasons.environment.solar_term.alternation.grain_in_ear","Winds graze the field. Insects chirp.");
        add("info.eclipticseasons.environment.solar_term.alternation.summer_solstice","Oh, the longest day is coming.");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_heat","The height of summer begins.");
        add("info.eclipticseasons.environment.solar_term.alternation.greater_heat","Sun shining, summer heat rises.");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_autumn","The color of summer has faded.");
        add("info.eclipticseasons.environment.solar_term.alternation.end_of_heat","The summer heat has been swept away as the autumn rains are ready.");
        add("info.eclipticseasons.environment.solar_term.alternation.white_dew","Cool winds graze. White dew gathers.");
        add("info.eclipticseasons.environment.solar_term.alternation.autumnal_equinox","The daytime decreases when the nighttime increases.");
        add("info.eclipticseasons.environment.solar_term.alternation.cold_dew","The aura of autumn is getting thicker and thicker.");
        add("info.eclipticseasons.environment.solar_term.alternation.first_frost","The dew is frosting.");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_winter","Winds blowing, winter comes.");
        add("info.eclipticseasons.environment.solar_term.alternation.light_snow","With the winter rain, the weather grows much colder.");
        add("info.eclipticseasons.environment.solar_term.alternation.heavy_snow","Snow begins to fall and decorate the world.");
        add("info.eclipticseasons.environment.solar_term.alternation.winter_solstice","Shadows become longer. The endless long night comes.");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_cold","Severe cold in the depth of winter.");
        add("info.eclipticseasons.environment.solar_term.alternation.greater_cold","Winds died away, but icy coldness still.");
        add("commands.eclipticseasons.solar.set","Set the solar day to %s");
        add("effect.eclipticseasons.heat_stroke","Heat Stroke");

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

    }
}
