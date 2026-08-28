package com.teamtea.eclipticseasons.common.registry;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import com.teamtea.eclipticseasons.api.constant.tag.ESItemTags;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.api.data.season.SeasonCycle;
import com.teamtea.eclipticseasons.api.data.season.SpecialDays;
import com.teamtea.eclipticseasons.api.data.season.definition.SeasonDefinition;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.data.weather.WeatherRegion;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.common.resource.FakeResourceManagerHelperUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
import joptsimple.internal.Strings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {

    @SubscribeEvent
    public static void blockRegister(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB)
            event.register(Registries.CREATIVE_MODE_TAB, helper -> {
                helper.register(EclipticSeasons.rl(EclipticSeasonsApi.MODID),
                        CreativeModeTab.builder().icon(() -> new ItemStack(ItemRegistry.calendar_item.get()))
                                .title(Component.translatable("itemGroup." + EclipticSeasonsApi.MODID + ".core"))
                                .displayItems((params, output) -> {
                                    ItemRegistry.ITEM_DEFERRED_REGISTER.getEntries().forEach(
                                            itemDeferredHolder ->
                                            {
                                                if (!CommonConfig.Snow.snowInWorld.get() && itemDeferredHolder == ItemRegistry.broom)
                                                    return;
                                                Item value = itemDeferredHolder.get();
                                                if (value != ItemRegistry.hyetometer.get()
                                                        && value != ItemRegistry.thermometer.get()) {
                                                    if (!EclipticSeasonsApi.getInstance().getSeasonalSimulationLevel().enable(SeasonalSimulationLevel.AGRICULTURE)
                                                            && value.builtInRegistryHolder().is(ESItemTags.AGRICULTURE_CONTENT)) {
                                                        return;
                                                    }
                                                    output.accept(value);
                                                }
                                            }
                                    );
                                })
                                .build());
            });
    }


    // can not sync in network due to ClientBoundLogin limit dynamic data holderset
    @SubscribeEvent
    public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        Map<ResourceKey<? extends Registry<?>>, Pair<Codec<?>, Codec<?>>> objects = new HashMap<>();

        event.dataPackRegistry(ESRegistries.WETTER, WetterStructure.CODEC, WetterStructure.DIRECT_CODEC);

        event.dataPackRegistry(ESRegistries.BIOME_CLIMATE_SETTING, BiomesClimateSettings.CODEC, BiomesClimateSettings.DIRECT_CODEC);
        event.dataPackRegistry(ESRegistries.CROP, CropGrowControlBuilder.CODEC, CropGrowControlBuilder.DIRECT_CODEC);
        event.dataPackRegistry(ESRegistries.AGRO_CLIMATE, AgroClimaticZone.CODEC, AgroClimaticZone.DIRECT_CODEC);
        event.dataPackRegistry(ESRegistries.SEASON_QUEST, SeasonQuest.CODEC, SeasonQuest.DIRECT_CODEC);
        event.dataPackRegistry(ESRegistries.HUMIDITY_CONTROL, HumidityControl.CODEC, HumidityControl.DIRECT_CODEC);
        event.dataPackRegistry(ESRegistries.SNOW_DEFINITIONS, SnowDefinition.CODEC, SnowDefinition.DIRECT_CODEC);
        event.dataPackRegistry(ESRegistries.SEASON_PHASE, SeasonPhase.CODEC, SeasonPhase.CODEC);
        event.dataPackRegistry(ESRegistries.SEASON_CYCLE, SeasonCycle.CODEC, SeasonCycle.DIRECT_CODEC);
        event.dataPackRegistry(ESRegistries.BIOME_RAIN, CustomRainBuilder.CODEC, CustomRainBuilder.DIRECT_CODEC);
        event.dataPackRegistry(ESRegistries.SNOW_TERM, CustomSnowTerm.CODEC, CustomSnowTerm.DIRECT_CODEC);

        // not sync
        event.dataPackRegistry(ESRegistries.SEASON_DEFINITION, SeasonDefinition.CODEC);

        // sync safely
        event.dataPackRegistry(ESRegistries.EXTRA_INFO, ESSortInfo.CODEC, ESSortInfo.CODEC);
        event.dataPackRegistry(ESRegistries.WEATHER_EFFECT, WeatherEffect.CODEC, WeatherEffect.CODEC);
        event.dataPackRegistry(ESRegistries.SPECIAL_DAYS, SpecialDays.CODEC, SpecialDays.CODEC);

    }

    @SubscribeEvent
    public static void registerBuiltinResourcePacks(AddPackFindersEvent event) {
        Optional<ModFile> modContainer = Optional.ofNullable(FMLLoader.getLoadingModList().getModFileById(EclipticSeasons.MODID).getFile());
        if (modContainer.isPresent()) {
            ModFile modFile = modContainer.get();
            boolean extraSnow;
            try {
                extraSnow = CommonConfig.Resource.extraSnow.get();
            } catch (java.lang.IllegalStateException illegalStateException) {
                CommentedFileConfig oldConfig = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(EclipticSeasons.defaultConfigName(ModConfig.Type.COMMON, EclipticSeasonsApi.MODID)))
                        .preserveInsertionOrder().build();
                oldConfig.load();
                extraSnow = oldConfig.getOrElse(Strings.join(CommonConfig.Resource.extraSnow.getPath(), "."), false);
                oldConfig.close();
            }

            if (extraSnow) {
                FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                        event,
                        EclipticSeasons.rl("extra_snow"), modFile,
                        Component.translatable(EclipticSeasons.rl("extra_snow").toLanguageKey("pack")),
                        event.getPackType(), PackSource.BUILT_IN, true);
            }

            if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                        event,
                        EclipticSeasons.MODID, "EclipticSeasonsLegacySnowyBlock", modFile,
                        Component.translatable(EclipticSeasons.rl("legacy_snowy_block").toLanguageKey("pack")),
                        event.getPackType(), PackSource.FEATURE, Pack.Position.TOP, false);
            }

            if (event.getPackType() == PackType.SERVER_DATA) {
                addPackIfEnabled(event, modFile,
                        "Rain Together", "rain_together");
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.climateZoneSnowfallTiming, "Regional Snow Time", "regional_snow_time");
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.synchronizedBiomeSnowfall, "Snow Together", "snow_together");
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.VanillaBiomeClimateSettings, "Vanilla Biome Climate Settings", "vanilla_biome_climate_settings");
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.NotIgnoreRiver, "Not Ignore River", "not_ignore_river");
            }
        }
    }

    private static void addPackIfEnabled(AddPackFindersEvent event, ModFile modFile, ForgeConfigSpec.BooleanValue booleanValue, String name, String pack_id) {
        if (booleanValue.get())
            addPackIfEnabled(event, modFile, name, pack_id);
    }


    private static void addPackIfEnabled(AddPackFindersEvent event, ModFile modFile, String name, String pack_id) {
        FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                event, EclipticSeasonsApi.MODID + "/",
                EclipticSeasonsApi.MODID, name, modFile,
                Component.translatable(EclipticSeasons.rl(pack_id).toLanguageKey("pack")),
                PackType.SERVER_DATA, PackSource.FEATURE, pack_id.equals("not_ignore_river") ? Pack.Position.TOP : Pack.Position.BOTTOM, true);
    }
}
