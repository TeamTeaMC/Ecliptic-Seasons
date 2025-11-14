package com.teamtea.eclipticseasons.common.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.api.data.season.SeasonCycle;
import com.teamtea.eclipticseasons.api.data.season.definition.SeasonDefinition;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.data.weather.WeatherRegion;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.common.resource.FakeResourceManagerHelperUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
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
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
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
                                                Item value = itemDeferredHolder.get();
                                                if (value != ItemRegistry.hyetometer.get()
                                                        && value != ItemRegistry.thermometer.get()) {
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
        event.dataPackRegistry(ESRegistries.WEATHER_REGION, WeatherRegion.CODEC);
        event.dataPackRegistry(ESRegistries.SEASON_DEFINITION, SeasonDefinition.CODEC);

        // sync safely
        event.dataPackRegistry(ESRegistries.EXTRA_INFO, ESSortInfo.CODEC, ESSortInfo.CODEC);

        event.dataPackRegistry(ESRegistries.WEATHER_EFFECT, WeatherEffect.CODEC, WeatherEffect.CODEC);
    }

    @SubscribeEvent
    public static void registerBuiltinResourcePacks(AddPackFindersEvent event) {
        Optional<ModFile> modContainer = Optional.ofNullable(FMLLoader.getLoadingModList().getModFileById(EclipticSeasons.MODID).getFile());
        if (modContainer.isPresent()) {
            ModFile modFile = modContainer.get();
            try {
                if (event.getPackType() == PackType.CLIENT_RESOURCES
                        || CommonConfig.Resource.extraSnow.get()) {
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
            } catch (Exception e) {
                EclipticSeasons.logger(e);
            }

            if (event.getPackType() == PackType.SERVER_DATA) {
                if (CommonConfig.Resource.RainTogether.get()) FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                        event,
                        EclipticSeasonsApi.MODID, "Rain Together", modFile,
                        Component.translatable(EclipticSeasons.rl("rain_together").toLanguageKey("pack")),
                        PackType.SERVER_DATA, PackSource.FEATURE,  Pack.Position.BOTTOM, true);
                if (CommonConfig.Resource.SnowTogether.get()) FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                        event,
                        EclipticSeasonsApi.MODID, "Snow Together", modFile,
                        Component.translatable(EclipticSeasons.rl("snow_together").toLanguageKey("pack")),
                        PackType.SERVER_DATA, PackSource.FEATURE,  Pack.Position.BOTTOM, true);
            }
        }
    }
}
