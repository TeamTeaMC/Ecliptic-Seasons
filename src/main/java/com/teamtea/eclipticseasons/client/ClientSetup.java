package com.teamtea.eclipticseasons.client;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.itemproperties.CounterItemProperty;
import com.teamtea.eclipticseasons.client.render.ber.CalendarBlockEntityRenderer;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.particle.FireflyParticle;
import com.teamtea.eclipticseasons.client.particle.WildGooseParticle;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.RenderType;

import net.minecraft.item.ItemModelsProperties;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;


import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ModelManager;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    // does the Glass Lantern render in the given layer (RenderType) - used as Predicate<RenderType> lambda for setRenderLayer
    public static boolean isGlassLanternValidLayer(RenderType layerToCheck) {
        return layerToCheck == RenderType.cutoutMipped() || layerToCheck == RenderType.translucent();
    }

    @SubscribeEvent
    public static void blockRegister(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.FIREFLY, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new FireflyParticle(level, x, y, z, p_277215_));
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.WILD_GOOSE, (p_277215_) ->
                (particleType, level, x, y, z, p_277222_, p_277223_, p_277224_) ->
                        new WildGooseParticle(level, x, y, z, 0.01, 0.01, 0.01, p_277215_));
    }

    @SubscribeEvent
    public static void registerExtraModels(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(ModelManager.snowy_fern);
        ModelLoader.addSpecialModel(ModelManager.snowy_grass);
        ModelLoader.addSpecialModel(ModelManager.snowy_tall_grass_top);
        ModelLoader.addSpecialModel(ModelManager.snowy_tall_grass_bottom);
        ModelLoader.addSpecialModel(ModelManager.snowy_large_fern_top);
        // 注意这里使用地址和model地址效果不同，后者需要写blockstate
        ModelLoader.addSpecialModel(ModelManager.snowy_large_fern_bottom);
        ModelLoader.addSpecialModel(ModelManager.overlay_2);
        ModelLoader.addSpecialModel(ModelManager.snow_height2);
        ModelLoader.addSpecialModel(ModelManager.snow_height2_top);
        ModelLoader.addSpecialModel(ModelManager.grass_flower);
        ModelLoader.addSpecialModel(ModelManager.butterfly1);
        ModelLoader.addSpecialModel(ModelManager.butterfly2);
        ModelLoader.addSpecialModel(ModelManager.butterfly3);

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

            ClientRegistry.bindTileEntityRenderer(BlockEntityRegistry.calendar_entity_type.get(),
                    CalendarBlockEntityRenderer::new);

            ItemModelsProperties.register(ItemRegistry.hyetometer.get(), ItemRegistry.hyetometer.getId(), new CounterItemProperty(EclipticUtil::getRainfallAt, Rainfall.collectValues().length));
            ItemModelsProperties.register(ItemRegistry.hygrometer.get(), ItemRegistry.hygrometer.getId(), new CounterItemProperty(EclipticUtil::getHumidityAt, Humidity.collectValues().length));
            ItemModelsProperties.register(ItemRegistry.thermometer.get(), ItemRegistry.thermometer.getId(), new CounterItemProperty(EclipticUtil::getTemperatureAt, Temperature.collectValues().length));

        });
    }


    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        ModelManager.models = event.getModelRegistry();

        ModelManager.quadMap.clear();
        ModelManager.quadMap_1.clear();
    }

}
