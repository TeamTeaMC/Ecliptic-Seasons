package com.teamtea.eclipticseasons.client.gui.screen.config;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import com.teamtea.eclipticseasons.api.constant.simulation.SnowBehavior;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.callback.CallbackBooleanEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.callback.CallbackEnumEntry;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * Registry that assigns every configuration value into one of the five
 * {@link ConfigCategory} categories, using each tab's internal
 * {@code Map<Component, List<ConfigEntry>>} for the second-level sections.
 */
public class ConfigRegistry {

    // ---------------------------------------------------------------------
    // Second-level section titles
    // ---------------------------------------------------------------------
    private static final Component RECOMMENDED = Component.translatable("eclipticseasons.options.general.recommended");

    private static final Component SEASON = Component.translatable("eclipticseasons.options.season");
    private static final Component WEATHER = Component.translatable("eclipticseasons.options.weather");
    private static final Component SNOW = Component.translatable("eclipticseasons.options.snow_related");

    private static final Component CROP = Component.translatable("eclipticseasons.options.crop");
    private static final Component ANIMAL = Component.translatable("eclipticseasons.options.animal");
    private static final Component SURVIVAL = Component.translatable("eclipticseasons.options.survival");

    private static final Component RENDER = Component.translatable("eclipticseasons.options.renderer");
    private static final Component PARTICLE = Component.translatable("eclipticseasons.options.particle");
    private static final Component GUI = Component.translatable("eclipticseasons.options.gui");

    private static final Component COMPAT = Component.translatable("eclipticseasons.options.compat");
    private static final Component DEBUG = Component.translatable("eclipticseasons.options.debug");

    public static void register(ESModConfigScreen screen) {
        registerGeneral(screen);
        registerEnvironment(screen);
        registerGamePlay(screen);
        registerVisual(screen);
        registerAdvanced(screen);
    }

    private static void registerGeneral(ESModConfigScreen screen) {
        // These CallbackEntry instances carry special callbacks and must keep their behavior.
        screen.markClassified(ConfigCategory.GENERAL, ClientConfig.Sound.naturalSound);
        // screen.markClassified(ConfigCategory.GENERAL, StartConfig.Resource.extraSnow);
        // screen.markClassified(ConfigCategory.GENERAL, CommonConfig.Resource.springGrass);
        screen.markClassified(ConfigCategory.GENERAL, CommonConfig.Season.seasonalSimulationLevel);

        screen.addToTab(ConfigCategory.GENERAL, RECOMMENDED, new CallbackEnumEntry<>(
                "eclipticseasons.configuration.SeasonalSimulationLevel",
                () -> EclipticSeasonsApi.getInstance().getSeasonalSimulationLevel(),
                SeasonalSimulationLevel::onSeasonalSimulationLevelChange,
                List.of(SeasonalSimulationLevel.values())
        ));

        screen.addToTab(ConfigCategory.GENERAL, RECOMMENDED, new CallbackEnumEntry<>(
                "eclipticseasons.configuration.SnowMode",
                SnowBehavior::getSnowBehavior,
                SnowBehavior::setSnowBehavior,
                List.of(SnowBehavior.values())
        ));

        screen.addToTab(ConfigCategory.GENERAL, RECOMMENDED, new CallbackBooleanEntry(
                "eclipticseasons.configuration.LegacyGreenhouseMode",
                () -> CommonConfig.Crop.simpleGreenHouse.get() && CommonConfig.Crop.greenHouseCheckMode.get() == CropGrowthHandler.GreenHouseCheckMode.TOP_ONLY,
                (b) -> {
                    CommonConfig.Crop.simpleGreenHouse.set(b);
                    CommonConfig.Crop.greenHouseCheckMode.set(b ? CropGrowthHandler.GreenHouseCheckMode.TOP_ONLY : CropGrowthHandler.GreenHouseCheckMode.FULL);
                }));

        screen.addToTab(ConfigCategory.GENERAL, RECOMMENDED, new CallbackBooleanEntry(
                "eclipticseasons.configuration.DebugInfo",
                ClientConfig.Debug.debugInfo,
                b -> ClientConfig.Debug.debugInfo.set(b)).setSyncType(SyncType.CLIENT));

        screen.addToTab(ConfigCategory.GENERAL, RECOMMENDED, new CallbackBooleanEntry(
                "eclipticseasons.configuration.NaturalSound",
                ClientConfig.Sound.naturalSound,
                b -> {
                    ClientConfig.Sound.naturalSound.set(b);
                    ClientConfig.Sound.naturalSound.clearCache();
                }).setRestartType(ModConfigSpec.RestartType.WORLD)
                .setSyncType(SyncType.CLIENT));

        screen.addToTab(ConfigCategory.GENERAL, RECOMMENDED, new CallbackBooleanEntry(
                "eclipticseasons.configuration.ExtraSnowLayer",
                ClientConfig.Renderer.extraSnowLayer,
                b -> ClientConfig.Renderer.extraSnowLayer.set(b)).setSyncType(SyncType.CLIENT));

        // screen.addToTab(ConfigCategory.GENERAL, RECOMMENDED, new CallbackBooleanEntry(
        //         "eclipticseasons.configuration.ExtraSnowDefinitions",
        //         StartConfig.Resource.extraSnow,
        //         b -> {
        //             StartConfig.Resource.extraSnow.set(b);
        //             StartConfig.Resource.extraSnow.clearCache();
        //         }).setRestartType(ModConfigSpec.RestartType.GAME)
        //         .setSyncType(SyncType.STARTUP));

        screen.addToTab(ConfigCategory.GENERAL, RECOMMENDED, new CallbackBooleanEntry(
                "eclipticseasons.configuration.FrozenWater",
                ClientConfig.Debug.frozenWater,
                b -> ClientConfig.Debug.frozenWater.set(b))
                .setSyncType(SyncType.CLIENT));

        // screen.addToTab(ConfigCategory.GENERAL, RECOMMENDED, new CallbackBooleanEntry(
        //         "eclipticseasons.configuration.SpringGrass",
        //         CommonConfig.Resource.springGrass,
        //         b -> {
        //             CommonConfig.Resource.springGrass.set(b);
        //             CommonConfig.Resource.springGrass.clearCache();
        //         }).setRestartType(ModConfigSpec.RestartType.WORLD));
    }

    private static void registerEnvironment(ESModConfigScreen screen) {
        screen.put(ConfigCategory.ENVIRONMENT, SEASON,
                // CommonConfig.Season.seasonalSimulationLevel,
                CommonConfig.Season.enableInform,
                CommonConfig.Season.validDimensions,
                CommonConfig.Season.lastingDaysOfEachTerm,
                CommonConfig.Season.initialSolarTermIndex,
                CommonConfig.Season.monthOffset,
                CommonConfig.Season.dayOffset,
                CommonConfig.Season.daylightChange,
                CommonConfig.Season.springDayTimes,
                CommonConfig.Season.summerDayTimes,
                CommonConfig.Season.autumnDayTimes,
                CommonConfig.Season.winterDayTimes,
                CommonConfig.Season.noneDayTimes,
                CommonConfig.Season.dynamicSnowTerm,
                CommonConfig.Season.realWorldSolarTerms
        );

        screen.put(ConfigCategory.ENVIRONMENT, WEATHER,
                // CommonConfig.Resource.NotIgnoreRiver,
                // CommonConfig.Weather.notRainInDesert,
                CommonConfig.Weather.shouldInitSnowForExtremeColdBiomes,
                CommonConfig.Weather.rainChanceMultiplier,
                CommonConfig.Weather.thunderChanceMultiplier,
                ClientConfig.Weather.tweakPrecipitationParticleTexture
        );

        screen.put(ConfigCategory.ENVIRONMENT, SNOW,
                CommonConfig.Temperature.iceMelt,
                CommonConfig.Temperature.snowDown,
                CommonConfig.Snow.snowyWinter,
                CommonConfig.Snow.blocksNotSnowy,
                CommonConfig.Snow.snowInWorld,
                CommonConfig.Resource.SnowTogether,
                CommonConfig.Resource.RegionalSnowTime,
                CommonConfig.Weather.snowAccumulationSpeedMultiplier,
                CommonConfig.Weather.snowMeltSpeedMultiplier
        );
    }

    private static void registerGamePlay(ESModConfigScreen screen) {
        screen.put(ConfigCategory.GAMEPLAY, CROP,
                CommonConfig.Crop.enableCrop,
                CommonConfig.Crop.enableCropHumidityControl,
                CommonConfig.Crop.restrictBoneMeal,
                CommonConfig.Crop.greenHouseMaxDiameter,
                CommonConfig.Crop.greenHouseMaxHeight,
                CommonConfig.Crop.greenHouseCheckMode,
                CommonConfig.Crop.forceCompatMode,
                CommonConfig.Crop.simpleGreenHouse,
                CommonConfig.Crop.seasonalPrayerRitualTimeCost
        );

        screen.put(ConfigCategory.GAMEPLAY, ANIMAL,
                CommonConfig.Animal.enableBreed,
                CommonConfig.Animal.enableTimeBreed,
                CommonConfig.Animal.enableBee,
                CommonConfig.Animal.enableFishing,
                CommonConfig.Animal.beePollinateSeasons,
                CommonConfig.Animal.beeActiveSeasons,
                CommonConfig.Animal.fishingSeasons
        );

        screen.put(ConfigCategory.GAMEPLAY, SURVIVAL,
                CommonConfig.Temperature.heatStroke
        );
    }

    private static void registerVisual(ESModConfigScreen screen) {
        screen.put(ConfigCategory.VISUAL, RENDER,
                ClientConfig.Renderer.forceChunkRenderUpdate,
                ClientConfig.Renderer.enhancementChunkRenderUpdate,
                ClientConfig.Renderer.flowerOnGrass,
                ClientConfig.Renderer.seasonalGrassColorChange,
                ClientConfig.Renderer.seasonalColorChangeExtend,
                // ClientConfig.Renderer.smootherSeasonalGrassColorChange,
                // ClientConfig.Renderer.snowInFence,
                ClientConfig.Renderer.extraSnowLayer
        );

        screen.put(ConfigCategory.VISUAL, PARTICLE,
                ClientConfig.Particle.seasonParticle,
                ClientConfig.Particle.snowLeafParticles
        );

        screen.put(ConfigCategory.VISUAL, GUI,
                ClientConfig.GUI.simpleSeasonHud,
                ClientConfig.GUI.showGregorianYear
        );
    }

    private static void registerAdvanced(ESModConfigScreen screen) {
        screen.put(ConfigCategory.ADVANCED, COMPAT,
                CompatModule.CommonConfig.sereneSeasons,
                CompatModule.CommonConfig.DistantHorizonsWinterLOD,
                CompatModule.ClientConfig.DistantHorizonsWinterLODForceUpdateAll
        );

        screen.put(ConfigCategory.ADVANCED, DEBUG,
                ClientConfig.Debug.debugInfo,
                ClientConfig.Debug.smoothSnowyEdges,
                ClientConfig.Debug.frozenWater
        );
    }
}
