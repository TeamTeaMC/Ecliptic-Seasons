package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.season.SeasonCycle;
import com.teamtea.eclipticseasons.api.data.season.definition.SeasonDefinition;
import com.teamtea.eclipticseasons.api.data.season.SeasonPhase;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.craft.WetterStructure;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.data.weather.WeatherDimension;
import com.teamtea.eclipticseasons.api.data.weather.WeatherRegion;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.common.block.BlockInCopperGrateBlock;
import com.teamtea.eclipticseasons.common.block.IceOrSnowCauldronBlock;
import com.teamtea.eclipticseasons.common.resource.FakeResourceManagerHelperUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.StartConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFile;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCauldronInteractionEvent;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import org.jspecify.annotations.Nullable;
import java.util.Optional;

@SuppressWarnings("removal")
@EventBusSubscriber
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

    @SubscribeEvent
    public static void onNewRegistryCauldronInteraction(RegisterCauldronInteractionEvent.Dispatcher event) {
        event.register(IceOrSnowCauldronBlock.empty, IceOrSnowCauldronBlock.EMPTY);
    }

    @SubscribeEvent
    public static void onNewRegistryCauldronInteraction(RegisterCauldronInteractionEvent.Interaction event) {
        event.register(Identifier.withDefaultNamespace("empty"),
                Items.ICE, (state, level, pos, player, hand, stack) -> {
                    IceOrSnowCauldronBlock.fillEmptyCauldron(level, pos, player, hand, stack, BlockRegistry.ice_cauldron.get().defaultBlockState(), SoundEvents.GLASS_PLACE);
                    return InteractionResult.SUCCESS_SERVER;
                });
        event.register(Identifier.withDefaultNamespace("empty"),
                Items.SNOW_BLOCK, (state, level, pos, player, hand, stack) -> {
                    IceOrSnowCauldronBlock.fillEmptyCauldron(level, pos, player, hand, stack, BlockRegistry.snow_cauldron.get().defaultBlockState(), SoundEvents.SNOW_PLACE);
                    return InteractionResult.SUCCESS_SERVER;
                });
    }

    @SubscribeEvent
    public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ESRegistries.WETTER, WetterStructure.CODEC, WetterStructure.CODEC);
        event.dataPackRegistry(ESRegistries.BIOME_CLIMATE_SETTING, BiomesClimateSettings.CODEC, BiomesClimateSettings.CODEC);
        event.dataPackRegistry(ESRegistries.CROP, CropGrowControlBuilder.CODEC, CropGrowControlBuilder.CODEC);
        event.dataPackRegistry(ESRegistries.AGRO_CLIMATE, AgroClimaticZone.CODEC, AgroClimaticZone.CODEC);
        event.dataPackRegistry(ESRegistries.SEASON_QUEST, SeasonQuest.CODEC, SeasonQuest.CODEC);
        event.dataPackRegistry(ESRegistries.HUMIDITY_CONTROL, HumidityControl.CODEC, HumidityControl.CODEC);
        event.dataPackRegistry(ESRegistries.SNOW_DEFINITIONS, SnowDefinition.CODEC, SnowDefinition.CODEC);
        event.dataPackRegistry(ESRegistries.SEASON_PHASE, SeasonPhase.CODEC, SeasonPhase.CODEC);
        event.dataPackRegistry(ESRegistries.SEASON_CYCLE, SeasonCycle.CODEC, SeasonCycle.CODEC);
        event.dataPackRegistry(ESRegistries.SNOW_TERM, CustomSnowTerm.CODEC, CustomSnowTerm.CODEC);
        event.dataPackRegistry(ESRegistries.SEASON_DEFINITION, SeasonDefinition.CODEC, SeasonDefinition.CODEC);
        event.dataPackRegistry(ESRegistries.WEATHER_REGION, WeatherRegion.CODEC, WeatherRegion.CODEC);
        event.dataPackRegistry(ESRegistries.EXTRA_INFO, ESSortInfo.CODEC, ESSortInfo.CODEC);
        event.dataPackRegistry(ESRegistries.WEATHER_EFFECT, WeatherEffect.CODEC, WeatherEffect.CODEC);
        event.dataPackRegistry(ESRegistries.BIOME_RAIN, CustomRainBuilder.CODEC, CustomRainBuilder.CODEC);
        event.dataPackRegistry(ESRegistries.WEATHER_DIMENSION, WeatherDimension.CODEC, WeatherDimension.CODEC);
    }


    @SubscribeEvent
    public static void onRegisterCapabilitiesEvent(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, BlockEntityRegistry.block_in_copper_grate_block_entity_type.get(),
                (blockEntity, context) -> blockEntity.isRemoved() ? null :
                        (blockEntity.getItemStackHandler())
        );

        event.registerBlock(Capabilities.Item.BLOCK, (Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Direction side) -> new ItemStacksResourceHandler(1) {

            @Override
            protected int getCapacity(int index, ItemResource resource) {
                return 1;
            }

            @Override
            public boolean isValid(int index, ItemResource resource) {
                return BlockInCopperGrateBlock.validItemAndBlock(level, resource.toStack(), state);
            }

            @Override
            protected void onContentsChanged(int index, ItemStack previousContents) {
                if (index == 0 && getResource(0).getItem() instanceof BlockItem blockItem) {
                    BlockInCopperGrateBlock.setNewBlock(level, pos, state, blockItem);
                }
            }

        }, BlockRegistry.getAllGrateBlocks().toArray(Block[]::new));
    }

    @SubscribeEvent
    public static void registerBuiltinResourcePacks(AddPackFindersEvent event) {
        Optional<ModFile> modContainer = Optional.ofNullable(FMLLoader.getCurrentOrNull().getLoadingModList().getModFileById(EclipticSeasonsApi.MODID).getFile());
        if (modContainer.isPresent()) {
            ModFile modFile = modContainer.get();
            try {
                if (StartConfig.Resource.extraSnow.get()) {
                    FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                            event,
                            EclipticSeasons.rl("extra_snow"),
                            modFile, PackSource.DEFAULT);
                }

            } catch (Exception e) {
                EclipticSeasons.logger(e);
            }

            if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                        event,
                        EclipticSeasonsApi.MODID, "EclipticSeasonsLegacySnowyBlock", modFile,
                        Component.translatable(EclipticSeasons.rl("legacy_snowy_block").toLanguageKey("pack")),
                        event.getPackType(), PackSource.FEATURE, new PackSelectionConfig(false, Pack.Position.TOP, false));
            }
            if (event.getPackType() == PackType.SERVER_DATA) {
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.RainTogether, "Rain Together", "rain_together");
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.RegionalSnowTime, "Regional Snow Time", "regional_snow_time");
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.SnowTogether, "Snow Together", "snow_together");
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.VanillaBiomeClimateSettings, "Vanilla Biome Climate Settings", "vanilla_biome_climate_settings");
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.NotIgnoreRiver, "Not Ignore River", "not_ignore_river");
                addPackIfEnabled(event, modFile,
                        CommonConfig.Resource.springGrass, "spring_grass", "spring_grass");
            }
        }
    }

    private static void addPackIfEnabled(AddPackFindersEvent event, ModFile modFile, ModConfigSpec.BooleanValue booleanValue, String name, String pack_id) {
        if (booleanValue.get())
            FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                    event, EclipticSeasonsApi.MODID + "/",
                    EclipticSeasonsApi.MODID, name, modFile,
                    Component.translatable(EclipticSeasons.rl(pack_id).toLanguageKey("pack")),
                    PackType.SERVER_DATA, PackSource.FEATURE, new PackSelectionConfig(true, Pack.Position.BOTTOM, false));
    }
}
