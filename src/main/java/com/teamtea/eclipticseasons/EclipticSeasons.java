package com.teamtea.eclipticseasons;


import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.misc.HeatStrokeEffect;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.data.start;
import net.minecraft.block.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
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

        // if (!FMLEnvironment.production||General.bool.get())
        {
            StringBuilder output = new StringBuilder();

            for (Object i : x) {
                if (i == null) output.append(", ").append("null");
                else if (i.getClass().isArray()) {
                    output.append(", [");
                    for (Object c : (int[]) i) {
                        output.append(c).append(",");
                    }
                    output.append("]");
                } else if (i instanceof List) {
                    output.append(", [");
                    for (Object c : (List) i) {
                        output.append(c);
                    }
                    output.append("]");
                } else
                    output.append(", ").append(i);
            }
            LOGGER.info(output.substring(1));
        }

    }


    public EclipticSeasons() {

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModContents.ModBlocks.register(FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::FMLCommonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);


    }


    public static ResourceLocation rl(String id) {
        return new ResourceLocation(MODID, id);
    }

    public void FMLCommonSetup(final FMLCommonSetupEvent event) {
        SimpleNetworkHandler.init();
        CompatModule.register();
    }

    public void gatherData(final GatherDataEvent event) {
        start.dataGen(event);
    }


    public static class ModContents {
        public static final DeferredRegister<Block> ModBlocks = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
        public static RegistryObject<Block> snowySlab = ModBlocks.register("snowy_slab", () -> new SlabBlock(AbstractBlock.Properties.copy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));
        public static RegistryObject<Block> snowyStairs = ModBlocks.register("snowy_stairs", () -> new StairsBlock(Blocks.OAK_PLANKS::defaultBlockState, AbstractBlock.Properties.copy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
        public static RegistryObject<Block> snowyBlock = ModBlocks.register("snowy_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
        public static RegistryObject<Block> snowyLeaves = ModBlocks.register("snowy_leaves", () -> new Block(AbstractBlock.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class SoundEventsRegistry {
        public final static SoundEvent spring_forest = new SoundEvent(rl("ambient.spring_forest"));
        public final static SoundEvent garden_wind = new SoundEvent(rl("ambient.garden_wind"));
        public final static SoundEvent night_river = new SoundEvent(rl("ambient.night_river"));
        public final static SoundEvent windy_leave = new SoundEvent(rl("ambient.windy_leave"));
        public final static SoundEvent winter_forest = new SoundEvent(rl("ambient.winter_forest"));
        public final static SoundEvent winter_cold = new SoundEvent(rl("ambient.winter_cold"));

        @SubscribeEvent
        public static void blockRegister(RegistryEvent.Register<SoundEvent> event) {
            // MultiPackResourceManager
            // event.register(Registry.SOUND_EVENT.key(), soundEventRegisterHelper -> {
            //     soundEventRegisterHelper.register(spring_forest.getLocation(), spring_forest);
            //     soundEventRegisterHelper.register(garden_wind.getLocation(), garden_wind);
            //     soundEventRegisterHelper.register(night_river.getLocation(), night_river);
            //     soundEventRegisterHelper.register(windy_leave.getLocation(), windy_leave);
            //     soundEventRegisterHelper.register(winter_forest.getLocation(), winter_forest);
            //     soundEventRegisterHelper.register(winter_cold.getLocation(), winter_cold);
            // });
            spring_forest.setRegistryName(rl("spring_forest"));
            garden_wind.setRegistryName(rl("garden_wind"));
            night_river.setRegistryName(rl("night_river"));
            windy_leave.setRegistryName(rl("windy_leave"));
            winter_forest.setRegistryName(rl("winter_forest"));
            winter_cold.setRegistryName(rl("winter_cold"));
            event.getRegistry().registerAll(spring_forest,
                    garden_wind,
                    night_river,
                    windy_leave,
                    winter_forest,
                    winter_cold
            );
        }


        @SubscribeEvent
        public static void onServerAboutToStartEvent(FMLCommonSetupEvent event) {
            WeatherManager.BIOME_WEATHER_LIST.clear();
            WeatherManager.NEXT_CHECK_BIOME_MAP.clear();
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class EffectRegistry {
        public static final Effect HEAT_STROKE = new HeatStrokeEffect(EffectType.NEUTRAL, 0xf9d27d);

        @SubscribeEvent
        public static void blockRegister(RegistryEvent.Register<Effect> event) {
            // event.register(Registry.MOB_EFFECT.key(), soundEventRegisterHelper -> {
            //     soundEventRegisterHelper.register(rl("heat_stroke"), HEAT_STROKE);
            // });
            HEAT_STROKE.setRegistryName(rl("heat_stroke"));
            event.getRegistry().register(HEAT_STROKE);
        }


    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ParticleRegistry {
        public static final BasicParticleType FIREFLY = new BasicParticleType(false);
        public static final BasicParticleType WILD_GOOSE = new BasicParticleType(false);

        @SubscribeEvent
        public static void blockRegister(RegistryEvent.Register<ParticleType<?>> event) {

            // event.getRegistry().register(Registry.PARTICLE_TYPE.key(), particleTypeRegisterHelper -> {
            //     particleTypeRegisterHelper.register(rl("firefly"), FIREFLY);
            //     particleTypeRegisterHelper.register(rl("wild_goose"), WILD_GOOSE);
            // });
            FIREFLY.setRegistryName(rl("firefly"));
            WILD_GOOSE.setRegistryName(rl("wild_goose"));
            event.getRegistry().registerAll(
                    FIREFLY,WILD_GOOSE
            );
        }


    }
}
