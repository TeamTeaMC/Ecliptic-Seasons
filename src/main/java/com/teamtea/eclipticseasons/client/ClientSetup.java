package com.teamtea.eclipticseasons.client;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.client.model.ESBlockModelDefinition;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.color.season.FoliageColorSource;
import com.teamtea.eclipticseasons.client.core.AttachModelManager;
import com.teamtea.eclipticseasons.client.gui.GuiBlockRenderState;
import com.teamtea.eclipticseasons.client.gui.GuiBlockRenderer;
import com.teamtea.eclipticseasons.client.gui.GuiFluidRenderState;
import com.teamtea.eclipticseasons.client.gui.GuiFluidRenderer;
import com.teamtea.eclipticseasons.client.itemproperties.CounterModelProperty;
import com.teamtea.eclipticseasons.client.particle.*;
import com.teamtea.eclipticseasons.client.registry.KeyMappingRegistry;
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
    public static void onRegisterKeyMappingsEvent(RegisterKeyMappingsEvent event) {
        event.registerCategory(KeyMappingRegistry.MAIN);
        event.register(KeyMappingRegistry.DEBUG_KEY);
    }

    @SubscribeEvent
    public static void addTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
    }

    @SubscribeEvent
    public static void onParticleProviderRegistry(RegisterParticleProvidersEvent event) {
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
                EclipticSeasons.rl("greenhouse_core"),
                // The map codec
                GreenHouseCoreSpecialRenderer.Unbaked.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void onClientEvent(RegisterClientExtensionsEvent event) {
    }

    @SubscribeEvent
    public static void registerExtraModels(ModelEvent.RegisterStandalone event) {
        AttachModelManager.registerExtraSnowyModels(event::register);
        // event.register();
        // Minecraft.getInstance().getResourceManager().listPacks().toList().get(0).getResource(PackType.CLIENT_RESOURCES, ResourceLocation.withDefaultNamespace("textures/block/snow.png")).get()
        // IOUtils.toString(Minecraft.getInstance().getResourceManager().listPacks().toList().get(0).getResource(PackType.SERVER_DATA, ResourceLocation.withDefaultNamespace("recipe/yellow_terracotta.json")).get(), StandardCharsets.UTF_8)        event.register(ModelManager.snowy_fern);
        registerStandalone(event, AttachModelManager.snowy_custom);
        registerStandalone(event, AttachModelManager.snowy_custom_ao);

        registerStandalone(event, AttachModelManager.stairs_top);
        registerStandalone(event, AttachModelManager.snowy_leaves_attach);
        registerStandalone(event, AttachModelManager.snowy_leaves_top);
        registerStandalone(event, AttachModelManager.snowy_fern);
        registerStandalone(event, AttachModelManager.snowy_grass);
        registerStandalone(event, AttachModelManager.snowy_tall_grass_top);
        registerStandalone(event, AttachModelManager.snowy_tall_grass_bottom);
        registerStandalone(event, AttachModelManager.snowy_large_fern_top);
        // 注意这里使用地址和model地址效果不同，后者需要写blockstate
        registerStandalone(event, AttachModelManager.snowy_large_fern_bottom);
        registerStandalone(event, AttachModelManager.overlay_2);
        registerStandalone(event, AttachModelManager.snow_height2);
        registerStandalone(event, AttachModelManager.snow_height2_top);
        registerStandalone(event, AttachModelManager.grass_flower);

        for (var flowerOnGrass : ConcatenatedListView.of(AttachModelManager.flower_on_grass,
                AttachModelManager.fourleaf_clovers,
                AttachModelManager.snow_edge_overlays,
                AttachModelManager.leaf_piles)) {
            registerStandalone(event, flowerOnGrass);
        }

        registerStandalone(event, AttachModelManager.ice);
    }

    private static void registerStandalone(ModelEvent.RegisterStandalone event, StandaloneModelKey<BlockStateModel> snowyCustom) {
        event.register(snowyCustom, SimpleUnbakedStandaloneModel.blockStateModel(Identifier.parse(snowyCustom.getName())));
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onModelBaked(ModelEvent.ModifyBakingResult event) {
        ParticleUtil.onReloadResource();

        var modelRegistry = event.getBakingResult();
        AttachModelManager.clearForRebaked(modelRegistry);

        List<StandaloneModelKey<BlockStateModel>> bakedModels =
                new ArrayList<>(List.of(
                        AttachModelManager.snowy_custom,
                        AttachModelManager.snowy_custom_ao,
                        AttachModelManager.stairs_top,
                        AttachModelManager.snowy_leaves_attach,
                        AttachModelManager.snowy_leaves_top,
                        AttachModelManager.stairs_top,
                        AttachModelManager.snowy_fern,
                        AttachModelManager.snowy_grass,
                        AttachModelManager.snowy_tall_grass_top,
                        AttachModelManager.snowy_tall_grass_bottom,
                        AttachModelManager.snowy_large_fern_top,
                        AttachModelManager.snowy_large_fern_bottom,
                        AttachModelManager.overlay_2,
                        AttachModelManager.snow_height2,
                        AttachModelManager.snow_height2_top
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
