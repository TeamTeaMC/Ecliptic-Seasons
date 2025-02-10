package com.teamtea.eclipticseasons.client;

import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.particle.FireflyParticle;
import com.teamtea.eclipticseasons.client.particle.WildGooseParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ModelManager;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    // does the Glass Lantern render in the given layer (RenderType) - used as Predicate<RenderType> lambda for setRenderLayer
    public static boolean isGlassLanternValidLayer(RenderType layerToCheck) {
        return layerToCheck == RenderType.cutoutMipped() || layerToCheck == RenderType.translucent();
    }

    @SubscribeEvent
    public static void blockRegister(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(EclipticSeasons.ParticleRegistry.FIREFLY, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new FireflyParticle(level, x, y, z, p_277215_));
        Minecraft.getInstance().particleEngine.register(EclipticSeasons.ParticleRegistry.WILD_GOOSE, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new WildGooseParticle(level, x, y, z,0.01,0.01,0.01, p_277215_));
    }

    @SubscribeEvent
    public static void registerExtraModels(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(ModelManager.snowy_fern);
        ForgeModelBakery.addSpecialModel(ModelManager.snowy_grass);
        ForgeModelBakery.addSpecialModel(ModelManager.snowy_tall_grass_top);
        ForgeModelBakery.addSpecialModel(ModelManager.snowy_tall_grass_bottom);
        ForgeModelBakery.addSpecialModel(ModelManager.snowy_large_fern_top);
        // 注意这里使用地址和model地址效果不同，后者需要写blockstate
        ForgeModelBakery.addSpecialModel(ModelManager.snowy_large_fern_bottom);
        ForgeModelBakery.addSpecialModel(ModelManager.snowy_dandelion);
        ForgeModelBakery.addSpecialModel(ModelManager.dandelion_top);
        ForgeModelBakery.addSpecialModel(ModelManager.overlay_2);
        ForgeModelBakery.addSpecialModel(ModelManager.snow_height2);
        ForgeModelBakery.addSpecialModel(ModelManager.snow_height2_top);
        ForgeModelBakery.addSpecialModel(ModelManager.grass_flower);
        ForgeModelBakery.addSpecialModel(ModelManager.butterfly1);
        ForgeModelBakery.addSpecialModel(ModelManager.butterfly2);
        ForgeModelBakery.addSpecialModel(ModelManager.butterfly3);

    }

    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        EclipticSeasons.logger("Register Client");
        event.enqueueWork(() -> {
            // ItemBlockRenderTypes.setRenderLayer(ModContents.fluiddrawer.get(), ClientSetup::isGlassLanternValidLayer);
            // MenuScreens.register(ModContents.containerType.get(), Screen.Slot1::new);
            //
            // ItemBlockRenderTypes.setRenderLayer(ModContents.RiceSeedlingBlock.get(),RenderType.cutout());
            // fix json file instead
            BiomeColors.GRASS_COLOR_RESOLVER = BiomeColorsHandler.GRASS_COLOR;
            BiomeColors.FOLIAGE_COLOR_RESOLVER = BiomeColorsHandler.FOLIAGE_COLOR;


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


    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        ModelManager.models=event.getModelRegistry();

        ModelManager.quadMap.clear();
        ModelManager.quadMap_1.clear();
    }

}
