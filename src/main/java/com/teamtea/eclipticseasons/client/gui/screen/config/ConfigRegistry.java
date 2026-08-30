package com.teamtea.eclipticseasons.client.gui.screen.config;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import com.teamtea.eclipticseasons.api.constant.simulation.SnowBehavior;
import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.entry.callback.CallbackBooleanEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.callback.CallbackEnumEntry;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers ES's built-in entries and optional plugins that extend the
 * default ES configuration screen.
 */
public class ConfigRegistry {
    protected Map<ResourceLocation, ConfigScreenPlugin> plugins = new LinkedHashMap<>();

    // ---------------------------------------------------------------------
    // Second-level section titles
    // ---------------------------------------------------------------------
    protected Component recommended = Component.translatable("eclipticseasons.options.general.recommended");

    protected Component season = Component.translatable("eclipticseasons.options.season");
    protected Component weather = Component.translatable("eclipticseasons.options.weather");
    protected Component snow = Component.translatable("eclipticseasons.options.snow_related");

    protected Component crop = Component.translatable("eclipticseasons.options.crop");
    protected Component animal = Component.translatable("eclipticseasons.options.animal");
    protected Component survival = Component.translatable("eclipticseasons.options.survival");

    protected Component render = Component.translatable("eclipticseasons.options.renderer");
    protected Component particle = Component.translatable("eclipticseasons.options.particle");
    protected Component gui = Component.translatable("eclipticseasons.options.gui");

    protected Component compat = Component.translatable("eclipticseasons.options.compat");
    protected Component debug = Component.translatable("eclipticseasons.options.debug");

    public void registerPlugin(ResourceLocation id, ConfigScreenPlugin plugin) {
        plugins.put(id, plugin);
    }

    public void apply(ConfigScreenContext context) {
        registerBuiltIn(context);
        plugins.values().forEach(plugin -> plugin.register(context));
    }

    protected void registerBuiltIn(ConfigScreenContext context) {
        registerGeneral(context);
        registerEnvironment(context);
        registerGamePlay(context);
        registerVisual(context);
        registerAdvanced(context);
    }

    protected void registerGeneral(ConfigScreenContext context) {
        // These CallbackEntry instances carry special callbacks and must keep their behavior.
        context.add(ConfigCategory.GENERAL, recommended, new CallbackEnumEntry<>(
                "eclipticseasons.configuration.SeasonalSimulationLevel",
                () -> EclipticSeasonsApi.getInstance().getSeasonalSimulationLevel(),
                SeasonalSimulationLevel::onSeasonalSimulationLevelChange,
                () -> SeasonalSimulationLevel.AGRICULTURE,
                List.of(SeasonalSimulationLevel.values())
        ), context.ownerOf(CommonConfig.COMMON_CONFIG));

        context.add(ConfigCategory.GENERAL, recommended, new CallbackEnumEntry<>(
                "eclipticseasons.configuration.SnowMode",
                SnowBehavior::getSnowBehavior,
                SnowBehavior::setSnowBehavior,
                () -> SnowBehavior.RENDER,
                List.of(SnowBehavior.values())
        ), context.ownerOf(CommonConfig.COMMON_CONFIG));

        context.add(ConfigCategory.GENERAL, recommended, new CallbackBooleanEntry(
                "eclipticseasons.configuration.LegacyGreenhouseMode",
                () -> CommonConfig.Crop.simpleGreenHouse.get() && CommonConfig.Crop.greenHouseCheckMode.get() == CropGrowthHandler.GreenHouseCheckMode.TOP_ONLY,
                (b) -> {
                    CommonConfig.Crop.simpleGreenHouse.set(b);
                    CommonConfig.Crop.greenHouseCheckMode.set(b ? CropGrowthHandler.GreenHouseCheckMode.TOP_ONLY : CropGrowthHandler.GreenHouseCheckMode.FULL);
                },
                () -> false), context.ownerOf(CommonConfig.COMMON_CONFIG));

        context.add(ConfigCategory.GENERAL, recommended, new CallbackBooleanEntry(
                "eclipticseasons.configuration.DebugInfo",
                () -> ClientConfig.Debug.debugInfo.get(),
                b -> ClientConfig.Debug.debugInfo.set(b),
                () -> false).setSyncType(SyncType.CLIENT), context.ownerOf(ClientConfig.CLIENT_CONFIG));

        context.add(ConfigCategory.GENERAL, recommended, new CallbackBooleanEntry(
                "eclipticseasons.configuration.NaturalSound",
                () -> ClientConfig.Sound.naturalSound.get(),
                b -> {
                    ClientConfig.Sound.naturalSound.set(b);
                    ClientConfig.Sound.naturalSound.clearCache();
                },
                () -> true).setRestartType(ModConfigSpec.RestartType.WORLD)
                .setSyncType(SyncType.CLIENT), context.ownerOf(ClientConfig.CLIENT_CONFIG));

        context.add(ConfigCategory.GENERAL, recommended, new CallbackBooleanEntry(
                "eclipticseasons.configuration.ExtraSnowLayer",
                () -> ClientConfig.Renderer.extraSnowLayer.get(),
                b -> ClientConfig.Renderer.extraSnowLayer.set(b),
                () -> false).setSyncType(SyncType.CLIENT), context.ownerOf(ClientConfig.CLIENT_CONFIG));

        // context.add(ConfigCategory.GENERAL, recommended, new CallbackBooleanEntry(
        //         "eclipticseasons.configuration.ExtraSnowDefinitions",
        //         StartConfig.Resource.extraSnow,
        //         b -> {
        //             StartConfig.Resource.extraSnow.set(b);
        //             StartConfig.Resource.extraSnow.clearCache();
        //         }).setRestartType(ModConfigSpec.RestartType.GAME)
        //         .setSyncType(SyncType.STARTUP));

        context.add(ConfigCategory.GENERAL, recommended, new CallbackBooleanEntry(
                "eclipticseasons.configuration.FrozenWater",
                () -> ClientConfig.Debug.frozenWater.get(),
                b -> ClientConfig.Debug.frozenWater.set(b),
                () -> false)
                .setSyncType(SyncType.CLIENT), context.ownerOf(ClientConfig.CLIENT_CONFIG));

        // context.add(ConfigCategory.GENERAL, recommended, new CallbackBooleanEntry(
        //         "eclipticseasons.configuration.SpringGrass",
        //         CommonConfig.Resource.springGrass,
        //         b -> {
        //             CommonConfig.Resource.springGrass.set(b);
        //             CommonConfig.Resource.springGrass.clearCache();
        //         },
        //         () -> false).setRestartType(ModConfigSpec.RestartType.WORLD),
        //         context.ownerOf(CommonConfig.COMMON_CONFIG));
    }

    protected void registerEnvironment(ConfigScreenContext context) {
        context.put(ConfigCategory.ENVIRONMENT, season,
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

        context.put(ConfigCategory.ENVIRONMENT, weather,
                // CommonConfig.Resource.NotIgnoreRiver,
                // CommonConfig.Weather.notRainInDesert,
                CommonConfig.Weather.shouldInitSnowForExtremeColdBiomes,
                CommonConfig.Weather.rainChanceMultiplier,
                CommonConfig.Weather.thunderChanceMultiplier,
                ClientConfig.Weather.tweakPrecipitationParticleTexture
        );

        context.put(ConfigCategory.ENVIRONMENT, snow,
                CommonConfig.Temperature.iceMelt,
                CommonConfig.Temperature.snowDown,
                CommonConfig.Snow.snowyWinter,
                CommonConfig.Snow.blocksNotSnowy,
                CommonConfig.Snow.snowInWorld,
                CommonConfig.Resource.synchronizedBiomeSnowfall,
                CommonConfig.Resource.climateZoneSnowfallTiming,
                CommonConfig.Weather.snowAccumulationSpeedMultiplier,
                CommonConfig.Weather.snowMeltSpeedMultiplier
        );
    }

    protected void registerGamePlay(ConfigScreenContext context) {
        context.put(ConfigCategory.GAMEPLAY, crop,
                CommonConfig.Crop.enableCrop,
                CommonConfig.Crop.enableCropHumidityControl,
                CommonConfig.Crop.restrictBoneMeal,
                CommonConfig.Crop.greenHouseMaxRadius,
                CommonConfig.Crop.greenHouseMaxHeight,
                CommonConfig.Crop.greenHouseCheckMode,
                CommonConfig.Crop.forceCompatMode,
                CommonConfig.Crop.simpleGreenHouse,
                CommonConfig.Crop.seasonalPrayerRitualTimeCost
        );

        context.put(ConfigCategory.GAMEPLAY, animal,
                CommonConfig.Animal.enableBreed,
                CommonConfig.Animal.enableTimeBreed,
                CommonConfig.Animal.enableBee,
                CommonConfig.Animal.enableFishing,
                CommonConfig.Animal.beePollinateSeasons,
                CommonConfig.Animal.beeActiveSeasons,
                CommonConfig.Animal.fishingSeasons
        );

        context.put(ConfigCategory.GAMEPLAY, survival,
                CommonConfig.Temperature.heatStroke
        );
    }

    protected void registerVisual(ConfigScreenContext context) {
        context.put(ConfigCategory.VISUAL, render,
                ClientConfig.Renderer.forceChunkRenderUpdate,
                ClientConfig.Renderer.enhancementChunkRenderUpdate,
                ClientConfig.Renderer.flowerOnGrass,
                ClientConfig.Renderer.seasonalGrassColorChange,
                ClientConfig.Renderer.seasonalColorChangeExtend,
                // ClientConfig.Renderer.smootherSeasonalGrassColorChange,
                // ClientConfig.Renderer.snowInFence,
                ClientConfig.Renderer.extraSnowLayer
        );

        context.put(ConfigCategory.VISUAL, particle,
                ClientConfig.Particle.seasonParticle,
                ClientConfig.Particle.snowLeafParticles
        );

        context.put(ConfigCategory.VISUAL, gui,
                ClientConfig.GUI.simpleSeasonHud,
                ClientConfig.GUI.showGregorianYear
        );
    }

    protected void registerAdvanced(ConfigScreenContext context) {
        context.put(ConfigCategory.ADVANCED, compat,
                CompatModule.CommonConfig.sereneSeasons,
                CompatModule.CommonConfig.DistantHorizonsWinterLOD,
                CompatModule.ClientConfig.DistantHorizonsWinterLODForceUpdateAll
                // CompatModule.CommonConfig.voxyCompatibility
        );

        context.put(ConfigCategory.ADVANCED, debug,
                ClientConfig.Debug.debugInfo,
                ClientConfig.Debug.smoothSnowyEdges,
                ClientConfig.Debug.frozenWater
        );
    }
}
