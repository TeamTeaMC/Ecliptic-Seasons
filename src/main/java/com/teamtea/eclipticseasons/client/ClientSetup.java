package com.teamtea.eclipticseasons.client;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.client.particle.ButterflyParticle;
import com.teamtea.eclipticseasons.client.particle.FallenLeavesParticle;
import com.teamtea.eclipticseasons.client.particle.FireflyParticle;
import com.teamtea.eclipticseasons.client.particle.WildGooseParticle;
import com.teamtea.eclipticseasons.client.render.ber.CalendarBlockEntityRenderer;
import com.teamtea.eclipticseasons.client.render.ber.PinWheelBlockEntityRenderer;
import com.teamtea.eclipticseasons.client.render.ber.WindChimesBlockEntityRenderer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.*;

import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void addTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ClientEventHandler.mccc.class, ClientEventHandler.ccc::new);
    }

    @SubscribeEvent
    public static void blockRegister(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(EclipticSeasons.ParticleRegistry.FIREFLY, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new FireflyParticle(level, x, y, z, p_277215_));
        event.registerSpriteSet(EclipticSeasons.ParticleRegistry.WILD_GOOSE, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new WildGooseParticle(level, x, y, z, 0.01, 0.01, 0.01, p_277215_));
        event.registerSpriteSet(EclipticSeasons.ParticleRegistry.BUTTERFLY, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new ButterflyParticle(level, x, y, z, p_277215_));
        event.registerSpriteSet(EclipticSeasons.ParticleRegistry.FALLEN_LEAVES, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new FallenLeavesParticle(level, x, y, z, p_277222_, p_277223_, p_277224_, particleType, p_277215_));

    }

    @SubscribeEvent
    public static void registerExtraModels(ModelEvent.RegisterAdditional event) {
        // Minecraft.getInstance().getResourceManager().listPacks().toList().get(0).getResource(PackType.CLIENT_RESOURCES, ResourceLocation.withDefaultNamespace("textures/block/snow.png")).get()
        // IOUtils.toString(Minecraft.getInstance().getResourceManager().listPacks().toList().get(0).getResource(PackType.SERVER_DATA, ResourceLocation.withDefaultNamespace("recipe/yellow_terracotta.json")).get(), StandardCharsets.UTF_8)        event.register(ModelManager.snowy_fern);
        event.register(ModelManager.stairs_top);
        event.register(ModelManager.snowy_fern);
        event.register(ModelManager.snowy_grass);
        event.register(ModelManager.snowy_tall_grass_top);
        event.register(ModelManager.snowy_tall_grass_bottom);
        event.register(ModelManager.snowy_large_fern_top);
        // 注意这里使用地址和model地址效果不同，后者需要写blockstate
        event.register(ModelManager.snowy_large_fern_bottom);
        event.register(ModelManager.overlay_2);
        event.register(ModelManager.snow_height2);
        event.register(ModelManager.snow_height2_top);
        event.register(ModelManager.grass_flower);
        for (ModelResourceLocation flowerOnGrass : ModelManager.flower_on_grass) {
            event.register(flowerOnGrass);
        }
    }

    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        EclipticSeasons.logger("Register Client");
        event.enqueueWork(() -> {
            BiomeColors.GRASS_COLOR_RESOLVER = BiomeColorsHandler.GRASS_COLOR;
            BiomeColors.FOLIAGE_COLOR_RESOLVER = BiomeColorsHandler.FOLIAGE_COLOR;

            ItemBlockRenderTypes.setRenderLayer(EclipticSeasons.ModContents.bamboo_wind_chimes.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(EclipticSeasons.ModContents.paper_wind_chimes.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(EclipticSeasons.ModContents.wind_chimes.get(), RenderType.cutoutMipped());

            ItemBlockRenderTypes.setRenderLayer(EclipticSeasons.ModContents.pinwheel_blue.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(EclipticSeasons.ModContents.pinwheel_lime.get(), RenderType.cutoutMipped());
            ItemBlockRenderTypes.setRenderLayer(EclipticSeasons.ModContents.pinwheel_orange.get(), RenderType.cutoutMipped());

        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EclipticSeasons.ModContents.calendar_entity_type.get(), CalendarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(EclipticSeasons.ModContents.pinwheel_entity_type.get(), PinWheelBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(EclipticSeasons.ModContents.wind_chimes_entity_type.get(), WindChimesBlockEntityRenderer::new);
    }

    // public static Map<ResourceLocation, BakedModel> BakedSnowModels=new HashMap<>();

    @SubscribeEvent
    public static void onModelBaked(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
        ModelManager.clearForRebaked(modelRegistry);

        List<ModelResourceLocation> bakedModels =
                new ArrayList<>(List.of(
                        ModelManager.stairs_top,
                        ModelManager.snowy_fern,
                        ModelManager.snowy_grass,
                        ModelManager.snowy_tall_grass_top,
                        ModelManager.snowy_tall_grass_bottom,
                        ModelManager.snowy_large_fern_top,
                        ModelManager.snowy_large_fern_bottom,
                        ModelManager.overlay_2,
                        ModelManager.snow_height2,
                        ModelManager.snow_height2_top,
                        ModelManager.snowOverlayLeaves,
                        ModelManager.snowySlabBottom,
                        ModelManager.snowOverlayBlock
                ));
        bakedModels.addAll(EclipticSeasons.ModContents.snowyStairs.get().getStateDefinition().getPossibleStates().stream()
                .map(BlockModelShaper::stateToModelLocation).toList());
        for (ModelResourceLocation modelResourceLocation : bakedModels) {
            BakedModel bakedModel1 = modelRegistry.get(modelResourceLocation);
            if (bakedModel1 != null) {
                modelRegistry.put(modelResourceLocation, new SnowyBakedModelWrapper<>(bakedModel1));
            } else {
                EclipticSeasons.logger("Missing Model", modelResourceLocation);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterColorHandlersEvent_Block(RegisterColorHandlersEvent.Block event) {
        // BlockState birchLeaves = Blocks.BIRCH_LEAVES.defaultBlockState();
        // BlockColors blockColors = event.getBlockColors();

        event.register((state, blockAndTintGetter, pos, i) -> {
            if (i == 1) {
                return blockAndTintGetter != null && pos != null ? BiomeColors.getAverageGrassColor(blockAndTintGetter, pos) : GrassColor.getDefaultColor();
            } else {
                return -1;
            }
        }, Blocks.DANDELION);

        event.register(BiomeColorsHandler::getSpruceColor, Blocks.SPRUCE_LEAVES);
        event.register(BiomeColorsHandler::getBirchColor, Blocks.BIRCH_LEAVES);
        event.register(BiomeColorsHandler::getMangroveColor, Blocks.MANGROVE_LEAVES);
    }

    @SubscribeEvent
    public static void onRegisterColorHandlersEvent_Item(RegisterColorHandlersEvent.Item event) {
    }

    @SubscribeEvent
    public static void onRegisterShader(RegisterShadersEvent event) {

    }
}
