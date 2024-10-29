package com.teamtea.eclipticseasons.client;

import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.particle.ButterflyParticle;
import com.teamtea.eclipticseasons.client.particle.FallenLeavesParticle;
import com.teamtea.eclipticseasons.client.particle.FireflyParticle;
import com.teamtea.eclipticseasons.client.particle.WildGooseParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ModelManager;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

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
        event.register(ModelManager.snowy_fern);
        event.register(ModelManager.snowy_grass);
        event.register(ModelManager.snowy_tall_grass_top);
        event.register(ModelManager.snowy_tall_grass_bottom);
        event.register(ModelManager.snowy_large_fern_top);
        // 注意这里使用地址和model地址效果不同，后者需要写blockstate
        event.register(ModelManager.snowy_large_fern_bottom);
        event.register(ModelManager.snowy_dandelion);
        event.register(ModelManager.dandelion_top);
        event.register(ModelManager.overlay_2);
        event.register(ModelManager.snow_height2);
        event.register(ModelManager.snow_height2_top);
        event.register(ModelManager.grass_flower);
        event.register(ModelManager.butterfly1);
        event.register(ModelManager.butterfly2);
        event.register(ModelManager.butterfly3);

        ModelManager.flower_on_grass.forEach(event::register);

    }

    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        EclipticSeasons.logger("Register Client");
        event.enqueueWork(() -> {

            BiomeColors.GRASS_COLOR_RESOLVER = BiomeColorsHandler.GRASS_COLOR;
            BiomeColors.FOLIAGE_COLOR_RESOLVER = BiomeColorsHandler.FOLIAGE_COLOR;

            ItemBlockRenderTypes.setRenderLayer(EclipticSeasons.ModContents.snowyBlock.get(), RenderType.cutoutMipped());

        });
    }

    //    注意static是单次，比如启动类，没有比如右击事件
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // FluidDrawersLegacyMod.logger("Register Renderer");
        // ModContents.DRBlockEntities.getEntries().forEach((reg) -> {
        //     event.registerBlockEntityRenderer((BlockEntityType<BlockEntityFluidDrawer>)reg.get(),
        //             TESRFluidDrawer::new);
        // });
    }

    // public static Map<ResourceLocation, BakedModel> BakedSnowModels=new HashMap<>();

    @SubscribeEvent
    public static void onModelBaked(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
        ModelManager.models = modelRegistry;
        // snowModel.resolve();
        // ModelManager.snowySlabBottom.resolve();
        // ModelManager.snowOverlayLeaves.resolve();
        // var test = ModelManager.snowOverlayBlock.resolve().get();
        // EclipticSeasons.logger(test);

        ModelManager.quadMap.clear();
        ModelManager.quadMap_1.clear();
        // net.minecraft.client.resources.model.ModelManager.reload
        // p_251134_.listPacks().toList().get(0).getResource(PackType.CLIENT_RESOURCES, completablefuture.get().entrySet().stream().toList().get(0).getKey()).get().readAllBytes()
        // p_251134_.listPacks().toList().get(1).getResource(PackType.CLIENT_RESOURCES, Ecliptic.rl("textures/block/icon.png")).get().readAllBytes()

        // for (Map.Entry<ResourceKey<Block>, Block> resourceKeyBlockEntry : BuiltInRegistries.BLOCK.entrySet()) {
        //     Block block = resourceKeyBlockEntry.getValue();
        //     for (BlockState state : block.getStateDefinition().getPossibleStates()) {
        //         for (Direction direction : List.of(Direction.UP, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST, Direction.DOWN)) {
        //     }
        // }

        // models.entrySet().stream().filter(e->e.getKey().namespace.equals(Ecliptic.MODID)&&e.getKey().toString().contains("slab")).toList();

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
}
