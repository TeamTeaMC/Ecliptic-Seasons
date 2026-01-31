package com.teamtea.eclipticseasons;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.IceOrSnowCauldronBlock;
import com.teamtea.eclipticseasons.common.registry.*;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.data.start;
import com.teamtea.eclipticseasons.compat.eclipticseasons_bundles.EclipticSeasonsBundles;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;
// import xueluoanping.fluiddrawerslegacy.handler.ControllerFluidCapabilityHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EclipticSeasons.MODID)
public class EclipticSeasons {
    public static final String MODID = "eclipticseasons";
    public static final String SMODID = "ecliptic";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(EclipticSeasons.MODID);
    public static final String NETWORK_VERSION = "1.0";

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


    @SuppressWarnings("removal")
    public EclipticSeasons() {

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BlockRegistry.BLOCK_DEFERRED_REGISTER.register(modEventBus);
        ItemRegistry.ITEM_DEFERRED_REGISTER.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register(modEventBus);
        LootItemConditionRegistry.LOOT_ITEM_CONDITION_TYPE_DEFERRED_REGISTER.register(modEventBus);

        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::FMLCommonSetup);
        modEventBus.addListener(CommonConfig::UpdateConfig);
        modEventBus.addListener(ClientConfig::UpdateConfig);
        modEventBus.addListener(CompatModule::onInterModEnqueue);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);

        // CompatModule.init();

        CompatModule.register(MinecraftForge.EVENT_BUS, modEventBus);

        ModAdvancements.register();

        EclipticSeasonsBundles.init();
    }

    public static String defaultConfigName(ModConfig.Type type, String modId) {
        // config file name would be "forge-client.toml" and "forge-server.toml"
        return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
    }

    public static String configName(String type) {
        // config file name would be "forge-client.toml" and "forge-server.toml"
        return String.format(Locale.ROOT, "%s/%s.toml", EclipticSeasonsApi.MODID, type);
    }

    public static ResourceLocation rl(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static ResourceLocation erl(String modid, String id) {
        return new ResourceLocation(modid, id);
    }

    public void FMLCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(SimpleNetworkHandler::init);
        event.enqueueWork(CompatModule::setup);
        event.enqueueWork(IceOrSnowCauldronBlock::init);
    }

    public void gatherData(final GatherDataEvent event) {
        start.dataGen(event);
    }

}
