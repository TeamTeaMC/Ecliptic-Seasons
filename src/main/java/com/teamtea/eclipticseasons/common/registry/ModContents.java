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
import com.teamtea.eclipticseasons.api.data.weather.WeatherRegion;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.common.block.BlockInCopperGrateBlock;
import com.teamtea.eclipticseasons.common.resource.FakeResourceManagerHelperUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.StartConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("removal")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
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
    }


    @SubscribeEvent
    public static void onRegisterCapabilitiesEvent(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntityRegistry.block_in_copper_grate_block_entity_type.get(),
                (blockEntity, direction) -> blockEntity.isRemoved() ? null :
                        (blockEntity.getItemStackHandler()));

        event.registerBlock(Capabilities.ItemHandler.BLOCK, (Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Direction side) -> new ItemStackHandler(1) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return BlockInCopperGrateBlock.validItemAndBlock(level, stack, state);
            }

            @Override
            protected void onContentsChanged(int slot) {
                if (slot == 0 && getStackInSlot(0).getItem() instanceof BlockItem blockItem) {
                    BlockInCopperGrateBlock.setNewBlock(level, pos, state, blockItem);
                }
            }
        }, BlockRegistry.getAllGrateBlocks().toArray(Block[]::new));
    }

    @SubscribeEvent
    public static void registerBuiltinResourcePacks(AddPackFindersEvent event) {
        Optional<ModFile> modContainer = Optional.ofNullable(FMLLoader.getLoadingModList().getModFileById(EclipticSeasonsApi.MODID).getFile());
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
                if (CommonConfig.Resource.RainTogether.get()) FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                        event,
                        EclipticSeasonsApi.MODID, "Rain Together", modFile,
                        Component.translatable(EclipticSeasons.rl("rain_together").toLanguageKey("pack")),
                        PackType.SERVER_DATA, PackSource.FEATURE, new PackSelectionConfig(true, Pack.Position.BOTTOM, false));
                if (CommonConfig.Resource.SnowTogether.get()) FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                        event,
                        EclipticSeasonsApi.MODID, "Snow Together", modFile,
                        Component.translatable(EclipticSeasons.rl("snow_together").toLanguageKey("pack")),
                        PackType.SERVER_DATA, PackSource.FEATURE, new PackSelectionConfig(true, Pack.Position.BOTTOM, false));
            }
        }
    }
}
