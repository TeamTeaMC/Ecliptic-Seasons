package com.teamtea.eclipticseasons.client;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.client.model.ESBlockModelDefinition;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.color.season.FoliageColorSource;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.gui.GuiBlockRenderState;
import com.teamtea.eclipticseasons.client.gui.GuiBlockRenderer;
import com.teamtea.eclipticseasons.client.gui.GuiFluidRenderState;
import com.teamtea.eclipticseasons.client.gui.GuiFluidRenderer;
import com.teamtea.eclipticseasons.client.itemproperties.CounterModelProperty;
import com.teamtea.eclipticseasons.client.particle.*;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.render.ber.*;
import com.teamtea.eclipticseasons.client.render.item.GreenHouseCoreSpecialRenderer;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientClientAgent;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.*;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.common.util.ConcatenatedListView;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void addRegisterRangeSelectItemModelPropertyEvent(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(EclipticSeasons.rl("meter"), CounterModelProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void addRegisterBlockStateModels(RegisterBlockStateModels event) {
        event.registerDefinition(EclipticSeasons.rl("model_definitions"), ESBlockModelDefinition.CODEC);
    }

    @SubscribeEvent
    public static void addRegisterPictureInPictureRenderersEvent(RegisterPictureInPictureRenderersEvent event) {
        event.register(GuiBlockRenderState.class, GuiBlockRenderer::new);
        event.register(GuiFluidRenderState.class, GuiFluidRenderer::new);
    }

    @SubscribeEvent
    public static void addTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
    }

    @SubscribeEvent
    public static void blockRegister(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.FIREFLY, (spriteSet) ->
                (particleType, level, x, y, z, xAux, yAux, zAux, random) ->
                        new FireflyParticle(level, x, y, z, spriteSet));
        event.registerSpriteSet(ParticleRegistry.WILD_GOOSE, (spriteSet) ->
                (particleType, level, x, y, z, xAux, yAux, zAux, random) ->
                        new WildGooseParticle(level, x, y, z, 0.01, 0.01, 0.01, spriteSet));
        event.registerSpriteSet(ParticleRegistry.BUTTERFLY, (spriteSet) ->
                (particleType, level, x, y, z, xAux, yAux, zAux, random) ->
                        new ButterflyParticle(level, x, y, z, spriteSet));
        event.registerSpriteSet(ParticleRegistry.FALLEN_LEAVES, (spriteSet) ->
                (particleType, level, x, y, z, xAux, yAux, zAux, random) ->
                        new FallenLeavesParticle(level, x, y, z, xAux, yAux, zAux, particleType, spriteSet));
        event.registerSpriteSet(ParticleRegistry.FLYING_BLOOM, (spriteSet) ->
                (particleType, level, x, y, z, xAux, yAux, zAux, random) ->
                        new FallenLeavesParticle(level, x, y, z, xAux, yAux, zAux, particleType, spriteSet));
        event.registerSpriteSet(ParticleRegistry.GREENHOUSE, (spriteSet) ->
                (particleType, level, x, y, z, xAux, yAux, zAux, random) ->
                        new GreenHouseParticle(level, x, y, z, xAux, yAux, zAux, particleType, spriteSet));
    }


    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        // EclipticSeasons.logger("Register Client");
        event.enqueueWork(() -> {
            BiomeColors.GRASS_COLOR_RESOLVER = BiomeColorsHandler.GRASS_COLOR;
            BiomeColors.FOLIAGE_COLOR_RESOLVER = BiomeColorsHandler.FOLIAGE_COLOR;
            BiomeColors.DRY_FOLIAGE_COLOR_RESOLVER = BiomeColorsHandler.DRY_FOLIAGE_COLOR;

            ClientCon.agent = new ClientClientAgent();
        });
    }


    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.calendar_entity_type.get(), CalendarBlockEntityRenderer::new);
        // event.registerBlockEntityRenderer(BlockEntityRegistry.pinwheel_entity_type.get(), PinWheelRenderer::new);
        // event.registerBlockEntityRenderer(BlockEntityRegistry.wind_chimes_entity_type.get(), WindChimesRenderer::new);

        event.registerBlockEntityRenderer(BlockEntityRegistry.greenhouse_core_container_entity_type.get(), GreenHouseCoreFrameRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.greenhouse_core_entity_type.get(), GreenHouseCoreRenderer::new);

        event.registerBlockEntityRenderer(BlockEntityRegistry.season_quest_hanging_sign_entity_type.get(), QuestSignRenderer::new);

        event.registerBlockEntityRenderer(BlockEntityRegistry.block_in_copper_grate_block_entity_type.get(), BlockInBlockRender::new);

    }

    // In some event handler class
    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(
                // The name to reference as the type
                EclipticSeasons.rl("spring_greenhouse_core"),

                // The map codec
                GreenHouseCoreSpecialRenderer.Unbaked.MAP_CODEC
        );
    }

    // For rendering a block in an item-like context
// Assume some DeferredBlock<ExampleBlock> EXAMPLE_BLOCK
//     @SubscribeEvent // on the mod event bus only on the physical client
//     public static void registerSpecialBlockRenderers(RegisterSpecialBlockModelRendererEvent event) {
//         event.register(
//                 ItemRegistry.spring_greenhouse_core_item.get()
//                 , ItemRegistry.summer_greenhouse_core_item.get()
//                 , ItemRegistry.autumn_greenhouse_core_item.get()
//                 , ItemRegistry.winter_greenhouse_core_item.get(),
//                 new ExampleSpecialRenderer.Unbaked(Identifier.fromNamespaceAndPath("examplemod", "entity/example_special"))
//         );
//     }

    @SubscribeEvent
    public static void onClientEvent(RegisterClientExtensionsEvent event) {
        // event.registerItem(new ClientGreenHouseItem(new GreenHouseCoreItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()))
        //        , ItemRegistry.spring_greenhouse_core_item.get()
        //        , ItemRegistry.summer_greenhouse_core_item.get()
        //        , ItemRegistry.autumn_greenhouse_core_item.get()
        //        , ItemRegistry.winter_greenhouse_core_item.get());
        // event.registerItem(new ClientGreenHouseItem(new GreenHouseCoreCoreItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()))
        //        , ItemRegistry.spring_greenhouse_essence_item.get()
        //        , ItemRegistry.summer_greenhouse_essence_item.get()
        //        , ItemRegistry.autumn_greenhouse_essence_item.get()
        //        , ItemRegistry.winter_greenhouse_essence_item.get());
        // event.registerItem(new ClientGreenHouseItem(new GreenHouseCoreFrameItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()))
        //        , ItemRegistry.greenhouse_core_container_item.get());
    }

    @SubscribeEvent
    public static void registerExtraModels(ModelEvent.RegisterStandalone event) {
        ExtraModelManager.registerExtraSnowyModels(event::register);
        // event.register();
        // Minecraft.getInstance().getResourceManager().listPacks().toList().get(0).getResource(PackType.CLIENT_RESOURCES, ResourceLocation.withDefaultNamespace("textures/block/snow.png")).get()
        // IOUtils.toString(Minecraft.getInstance().getResourceManager().listPacks().toList().get(0).getResource(PackType.SERVER_DATA, ResourceLocation.withDefaultNamespace("recipe/yellow_terracotta.json")).get(), StandardCharsets.UTF_8)        event.register(ModelManager.snowy_fern);
        registerStandalone(event, ExtraModelManager.snowy_custom);
        registerStandalone(event, ExtraModelManager.snowy_custom_ao);

        registerStandalone(event, ExtraModelManager.stairs_top);
        registerStandalone(event, ExtraModelManager.snowy_leaves_attach);
        registerStandalone(event, ExtraModelManager.snowy_leaves_top);
        registerStandalone(event, ExtraModelManager.snowy_fern);
        registerStandalone(event, ExtraModelManager.snowy_grass);
        registerStandalone(event, ExtraModelManager.snowy_tall_grass_top);
        registerStandalone(event, ExtraModelManager.snowy_tall_grass_bottom);
        registerStandalone(event, ExtraModelManager.snowy_large_fern_top);
        // 注意这里使用地址和model地址效果不同，后者需要写blockstate
        registerStandalone(event, ExtraModelManager.snowy_large_fern_bottom);
        registerStandalone(event, ExtraModelManager.overlay_2);
        registerStandalone(event, ExtraModelManager.snow_height2);
        registerStandalone(event, ExtraModelManager.snow_height2_top);
        registerStandalone(event, ExtraModelManager.grass_flower);

        for (var flowerOnGrass : ConcatenatedListView.of(ExtraModelManager.flower_on_grass,
                ExtraModelManager.fourleaf_clovers,
                ExtraModelManager.snow_edge_overlays,
                ExtraModelManager.leaf_piles)) {
            registerStandalone(event, flowerOnGrass);
        }

        registerStandalone(event, ExtraModelManager.ice);
    }

    private static void registerStandalone(ModelEvent.RegisterStandalone event, StandaloneModelKey<BlockStateModel> snowyCustom) {
        event.register(snowyCustom, SimpleUnbakedStandaloneModel.blockStateModel(Identifier.parse(snowyCustom.getName())));
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onModelBaked(ModelEvent.ModifyBakingResult event) {
        ParticleUtil.onReloadResource();

        var modelRegistry = event.getBakingResult();
        ExtraModelManager.clearForRebaked(modelRegistry);

        List<StandaloneModelKey<BlockStateModel>> bakedModels =
                new ArrayList<>(List.of(
                        ExtraModelManager.snowy_custom,
                        ExtraModelManager.snowy_custom_ao,
                        ExtraModelManager.stairs_top,
                        ExtraModelManager.snowy_leaves_attach,
                        ExtraModelManager.snowy_leaves_top,
                        ExtraModelManager.stairs_top,
                        ExtraModelManager.snowy_fern,
                        ExtraModelManager.snowy_grass,
                        ExtraModelManager.snowy_tall_grass_top,
                        ExtraModelManager.snowy_tall_grass_bottom,
                        ExtraModelManager.snowy_large_fern_top,
                        ExtraModelManager.snowy_large_fern_bottom,
                        ExtraModelManager.overlay_2,
                        ExtraModelManager.snow_height2,
                        ExtraModelManager.snow_height2_top
                        // ,
                        // ExtraModelManager.snowOverlayLeaves,
                        // ExtraModelManager.snowySlabBottom,
                        // ExtraModelManager.snowOverlayBlock
                ));
        // List.of(BlockRegistry.snowyBlock, BlockRegistry.snowyLeaves, BlockRegistry.snowySlab, BlockRegistry.snowyStairs, BlockRegistry.snowyVine)
        //         .forEach(bh -> bakedModels.addAll(bh.get().getStateDefinition().getPossibleStates().stream()
        //                 .map(BlockModelShaper::stateToModelLocation).toList()));
        // bakedModels.addAll(ExtraModelManager.snow_edge_overlays);

        // for (var modelResourceLocation : bakedModels) {
        //     BlockStateModel bakedModel1 = modelRegistry.standaloneModels().get(modelResourceLocation);
        //     if (bakedModel1 != null) {
        //         modelRegistry.put(modelResourceLocation, new SnowyBakedModelWrapper<>(bakedModel1));
        //     } else {
        //         EclipticSeasons.logger("Missing Model", modelResourceLocation);
        //     }
        // }

        // for (var holder : List.of(
        //         ItemRegistry.greenhouse_core_container_item,
        //         ItemRegistry.spring_greenhouse_core_item,
        //         ItemRegistry.summer_greenhouse_core_item,
        //         ItemRegistry.autumn_greenhouse_core_item,
        //         ItemRegistry.winter_greenhouse_core_item,
        //         ItemRegistry.spring_greenhouse_essence_item,
        //         ItemRegistry.summer_greenhouse_essence_item,
        //         ItemRegistry.autumn_greenhouse_essence_item,
        //         ItemRegistry.winter_greenhouse_essence_item)) {
        //     ModelResourceLocation inventory = ModelResourceLocation.inventory(holder.getId());
        //     BakedModel itemModel = modelRegistry.getOrDefault(inventory, null);
        //     if (itemModel != null) {
        //         modelRegistry.put(inventory, new ItemRenderModel<>(itemModel));
        //     }
        // }

    }


    @SubscribeEvent
    public static void onRegisterColorHandlersEvent_Block(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(new FoliageColorSource()),
                Blocks.SPRUCE_LEAVES,
                Blocks.BIRCH_LEAVES,
                Blocks.MANGROVE_LEAVES);
    }


    @SubscribeEvent
    public static void onRegisterClientReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(EclipticSeasons.rl(ClientJsonCacheListener.DIRECTORY_BIOME.substring(16)), ClientJsonCacheListener.biomeCache);
        event.addListener(EclipticSeasons.rl(ClientJsonCacheListener.DIRECTORY_LEAF.substring(16)), ClientJsonCacheListener.leafCache);
        event.addListener(EclipticSeasons.rl(ClientJsonCacheListener.DIRECTORY_SNOW_DEFINITION.substring(16)), ClientJsonCacheListener.snowDefOverrideCache);
        event.addListener(EclipticSeasons.rl(ClientJsonCacheListener.DIRECTORY_AMBIENT.substring(16)), ClientJsonCacheListener.ambientCache);
        event.addListener(EclipticSeasons.rl(ClientJsonCacheListener.DIRECTORY_MODEL_DEFINITION.substring(16)), ClientJsonCacheListener.modelDefCache);
        event.addListener(EclipticSeasons.rl(ClientJsonCacheListener.DIRECTORY_SEASON_TEXTURES.substring(16)), ClientJsonCacheListener.textureReMappingsCache);
        event.addListener(EclipticSeasons.rl(ClientJsonCacheListener.DIRECTORY_SEASON_DEFINITION.substring(16)), ClientJsonCacheListener.seasonDefCache);
        event.addListener(EclipticSeasons.rl(ClientJsonCacheListener.DIRECTORY_UI_PARSER.substring(16)), ClientJsonCacheListener.uiParserCache);
    }

}
