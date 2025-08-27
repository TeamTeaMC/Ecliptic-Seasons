package com.teamtea.eclipticseasons.client;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.itemproperties.CounterItemProperty;
import com.teamtea.eclipticseasons.client.model.ItemRenderModel;
import com.teamtea.eclipticseasons.client.particle.*;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.render.ber.*;
import com.teamtea.eclipticseasons.client.util.ClientClientAgent;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientExtraUtil;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void blockRegister(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.FIREFLY, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new FireflyParticle(level, x, y, z, p_277215_));
        event.registerSpriteSet(ParticleRegistry.WILD_GOOSE, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new WildGooseParticle(level, x, y, z, 0.01, 0.01, 0.01, p_277215_));

        event.registerSpriteSet(ParticleRegistry.BUTTERFLY, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new ButterflyParticle(level, x, y, z, p_277215_));
        event.registerSpriteSet(ParticleRegistry.FALLEN_LEAVES, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new FallenLeavesParticle(level, x, y, z, p_277222_, p_277223_, p_277224_, particleType, p_277215_));
        event.registerSpriteSet(ParticleRegistry.GREENHOUSE, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new GreenHouseParticle(level, x, y, z, p_277222_, p_277223_, p_277224_, particleType, p_277215_));
    }

    @SubscribeEvent
    public static void registerExtraModels(ModelEvent.RegisterAdditional event) {
        event.register(ExtraModelManager.stairs_top);
        event.register(ExtraModelManager.snowy_custom);
        event.register(ExtraModelManager.snowy_fern);
        event.register(ExtraModelManager.snowy_grass);
        event.register(ExtraModelManager.snowy_tall_grass_top);
        event.register(ExtraModelManager.snowy_tall_grass_bottom);
        event.register(ExtraModelManager.snowy_large_fern_top);
        // 注意这里使用地址和model地址效果不同，后者需要写blockstate
        event.register(ExtraModelManager.snowy_large_fern_bottom);
        event.register(ExtraModelManager.overlay_2);
        event.register(ExtraModelManager.snow_height2);
        event.register(ExtraModelManager.snow_height2_top);
        event.register(ExtraModelManager.grass_flower);

        event.register(ExtraModelManager.snowy_leaves_attach);
        event.register(ExtraModelManager.snowy_leaves_top);

        ExtraModelManager.flower_on_grass.forEach(event::register);
        ExtraModelManager.fourleaf_clovers.forEach(event::register);
        ExtraModelManager.snow_edge_overlays.forEach(event::register);
    }

    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        // EclipticSeasons.logger("Register Client");
        event.enqueueWork(() -> {

            BiomeColors.GRASS_COLOR_RESOLVER = BiomeColorsHandler.GRASS_COLOR;
            BiomeColors.FOLIAGE_COLOR_RESOLVER = BiomeColorsHandler.FOLIAGE_COLOR;

            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.snowyBlock.get(), RenderType.cutoutMipped());

            ItemProperties.register(ItemRegistry.hyetometer.get(), ItemRegistry.hyetometer.getId(), new CounterItemProperty(EclipticUtil::getRainfallAt, Rainfall.collectValues().length));
            ItemProperties.register(ItemRegistry.hygrometer.get(), ItemRegistry.hygrometer.getId(), new CounterItemProperty((level, pos) -> {
                float humidityAt = EclipticUtil.getHumidityLevelAt(level, pos);
                humidityAt = ClientExtraUtil.modifyHumidity(level, pos, humidityAt);
                return Humidity.getHumid(humidityAt);
            }, Humidity.collectValues().length));
            ItemProperties.register(ItemRegistry.thermometer.get(), ItemRegistry.thermometer.getId(), new CounterItemProperty(EclipticUtil::getTemperatureAt, Temperature.collectValues().length));

            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.block_in_wooden_grate_block.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(BlockRegistry.hygrometer.get(), RenderType.cutoutMipped());

            ClientCon.agent = new ClientClientAgent();
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.calendar_entity_type.get(), CalendarBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(BlockEntityRegistry.greenhouse_core_container_entity_type.get(), GreenHouseCoreFrameRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.greenhouse_core_entity_type.get(), GreenHouseCoreRenderer::new);

        event.registerBlockEntityRenderer(BlockEntityRegistry.season_quest_hanging_sign_entity_type.get(), QuestSignRenderer::new);

        event.registerBlockEntityRenderer(BlockEntityRegistry.block_in_copper_grate_block_entity_type.get(), BlockInBlockRender::new);

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onModelBaked(ModelEvent.ModifyBakingResult event) {
        ParticleUtil.onReloadResource();

        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
        ExtraModelManager.clearForRebaked(modelRegistry);

        List<ResourceLocation> bakedModels =
                new ArrayList<>(List.of(
                        ExtraModelManager.snowy_leaves_attach,
                        ExtraModelManager.snowy_leaves_top,
                        ExtraModelManager.snowy_custom,
                        ExtraModelManager.stairs_top,
                        ExtraModelManager.snowy_fern,
                        ExtraModelManager.snowy_grass,
                        ExtraModelManager.snowy_tall_grass_top,
                        ExtraModelManager.snowy_tall_grass_bottom,
                        ExtraModelManager.snowy_large_fern_top,
                        ExtraModelManager.snowy_large_fern_bottom,
                        ExtraModelManager.overlay_2,
                        ExtraModelManager.snow_height2,
                        ExtraModelManager.snow_height2_top,
                        ExtraModelManager.snowOverlayLeaves,
                        ExtraModelManager.snowySlabBottom,
                        ExtraModelManager.snowOverlayBlock
                ));
        bakedModels.addAll(BlockRegistry.snowyStairs.get().getStateDefinition().getPossibleStates().stream()
                .map(BlockModelShaper::stateToModelLocation).toList());
        bakedModels.addAll(ExtraModelManager.snow_edge_overlays);

        for (ResourceLocation modelResourceLocation : bakedModels) {
            BakedModel bakedModel1 = modelRegistry.get(modelResourceLocation);
            if (bakedModel1 != null) {
                modelRegistry.put(modelResourceLocation, new SnowyBakedModelWrapper<>(bakedModel1));
            } else {
                EclipticSeasons.logger("Missing Model", modelResourceLocation);
            }
        }

        for (RegistryObject<Item> holder : List.of(
                ItemRegistry.greenhouse_core_container_item,
                ItemRegistry.spring_greenhouse_core_item,
                ItemRegistry.summer_greenhouse_core_item,
                ItemRegistry.autumn_greenhouse_core_item,
                ItemRegistry.winter_greenhouse_core_item,
                ItemRegistry.spring_greenhouse_essence_item,
                ItemRegistry.summer_greenhouse_essence_item,
                ItemRegistry.autumn_greenhouse_essence_item,
                ItemRegistry.winter_greenhouse_essence_item)) {
            ModelResourceLocation inventory = new ModelResourceLocation(holder.getId(), "inventory");
            BakedModel itemModel = modelRegistry.getOrDefault(inventory, null);
            if (itemModel != null) {
                modelRegistry.put(inventory, new ItemRenderModel<>(itemModel));
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterColorHandlersEvent_Block(RegisterColorHandlersEvent.Block event) {
        // BlockState birchLeaves = Blocks.BIRCH_LEAVES.defaultBlockState();
        // BlockColors blockColors = event.getBlockColors();


        event.register(BiomeColorsHandler::getSpruceColor, Blocks.SPRUCE_LEAVES);
        event.register(BiomeColorsHandler::getBirchColor, Blocks.BIRCH_LEAVES);
        event.register(BiomeColorsHandler::getMangroveColor, Blocks.MANGROVE_LEAVES);
    }

    @SubscribeEvent
    public static void onRegisterColorHandlersEvent_Item(RegisterColorHandlersEvent.Item event) {

    }

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ClientJsonCacheListener.leafCache);
        event.registerReloadListener(ClientJsonCacheListener.biomeCache);

        event.registerReloadListener(ClientJsonCacheListener.snowDefOverrideCache);
        event.registerReloadListener(ClientJsonCacheListener.ambientCache);
        // event.registerReloadListener(ClientJsonCacheListener.modelDefCache);
        event.registerReloadListener(ClientJsonCacheListener.seasonDefCache);
    }
}
