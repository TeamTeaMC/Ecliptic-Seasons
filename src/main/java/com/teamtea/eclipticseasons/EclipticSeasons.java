package com.teamtea.eclipticseasons;


import com.mojang.serialization.JsonOps;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.*;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.StartConfig;
import com.teamtea.eclipticseasons.data.start;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
// import xueluoanping.fluiddrawerslegacy.handler.ControllerFluidCapabilityHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EclipticSeasonsApi.MODID)
public class EclipticSeasons {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(EclipticSeasonsApi.MODID);
    public static final String NETWORK_VERSION = "1.0";

    public EclipticSeasons(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(CommonConfig::UpdateConfig);
        modEventBus.addListener(ClientConfig::UpdateConfig);
        modEventBus.addListener(this::FMLCommonSetup);
        modEventBus.addListener(this::FMLCommonSetup);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(CompatModule::onInterModEnqueue);
        BlockRegistry.BLOCK_DEFERRED_REGISTER.register(modEventBus);
        ItemRegistry.ITEM_DEFERRED_REGISTER.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register(modEventBus);
        ModAdvancements.TRIGGER_DEFERRED_REGISTER.register(modEventBus);
        AttachmentRegistry.ATTACHMENT_TYPES.register(modEventBus);

        TestContents.weathers.register(modEventBus);

        LootItemConditionRegistry.LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);

        modContainer.registerConfig(ModConfig.Type.STARTUP, StartConfig.START_CONFIG);


        if (FMLLoader.getDist() == Dist.CLIENT)
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        CompatModule.register(NeoForge.EVENT_BUS, modEventBus);
    }


    public static ResourceLocation rl(String id) {
        return ResourceLocation.fromNamespaceAndPath(EclipticSeasonsApi.MODID, id);
    }

    public static ResourceLocation parse(String id) {
        return ResourceLocation.parse(id);
    }


    public void FMLCommonSetup(final FMLCommonSetupEvent event) {
        // SimpleNetworkHandler.init();
        // CompatModule.init();
        event.enqueueWork(CompatModule::setup);
    }


    public void gatherData(final GatherDataEvent event) {
        start.dataGen(event);
    }

    public static void logger(Exception exception) {
        LOGGER.error(exception);
    }


    public static void logger(String x) {

        // 通过它可以判断是否在哪个服务器
        // ServerLifecycleHooks.getCurrentServer()
        // if (!FMLEnvironment.production||General.bool.get())
        {
//            LOGGER.debug(x);
            LOGGER.info(x);
        }
    }

    public static void logger(Object... x) {
        extraLogger(false, x);
    }

    public static void extraLogger(boolean debug, Object... x) {

        // if (!FMLEnvironment.production||General.bool.get())
        {
            StringBuilder output = new StringBuilder();

            for (Object i : x) {
                if (i == null) output.append(", ").append("null");
                else if (i.getClass().isArray()) {
                    output.append(", [");
                    if (i instanceof Object[] objects) {
                        for (Object c : objects) {
                            output.append(c).append(",");
                        }
                    } else if (i instanceof float[] objects) {
                        for (float c : objects) {
                            output.append(c).append(",");
                        }
                    } else if (i instanceof int[] objects) {
                        for (int c : objects) {
                            output.append(c).append(",");
                        }
                    } else if (i instanceof double[] objects) {
                        for (double c : objects) {
                            output.append(c).append(",");
                        }
                    } else if (i instanceof long[] objects) {
                        for (long c : objects) {
                            output.append(c).append(",");
                        }
                    } else if (i instanceof boolean[] objects) {
                        for (boolean c : objects) {
                            output.append(c).append(",");
                        }
                    }
                    output.append("]");
                } else if (i instanceof List list) {
                    output.append(", [");
                    for (Object c : list) {
                        output.append(c);
                    }
                    output.append("]");
                } else
                    output.append(", ").append(i);
            }
            if (debug) {
                LOGGER.debug(output.substring(1));
            } else {
                LOGGER.info(output.substring(1));
            }
        }

    }

}
