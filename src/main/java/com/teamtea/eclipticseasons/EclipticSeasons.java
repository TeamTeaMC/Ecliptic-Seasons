package com.teamtea.eclipticseasons;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.client.particle.ColorParticleOptions;
import com.teamtea.eclipticseasons.client.particle.FireflyParticle;
import com.teamtea.eclipticseasons.common.misc.HeatStrokeEffect;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.config.ServerConfig;
import com.teamtea.eclipticseasons.data.start;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Function;
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

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ServerConfig.SERVER_CONFIG);
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
        public static RegistryObject<Block> snowySlab = ModBlocks.register("snowy_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));
        public static RegistryObject<Block> snowyStairs = ModBlocks.register("snowy_stairs", () -> new StairBlock(Blocks.OAK_PLANKS::defaultBlockState, BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
        public static RegistryObject<Block> snowyBlock = ModBlocks.register("snowy_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
        public static RegistryObject<Block> snowyLeaves = ModBlocks.register("snowy_leaves", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class SoundEventsRegistry {
        public final static SoundEvent spring_forest = SoundEvent.createVariableRangeEvent(rl("ambient.spring_forest"));
        public final static SoundEvent garden_wind = SoundEvent.createVariableRangeEvent(rl("ambient.garden_wind"));
        public final static SoundEvent night_river = SoundEvent.createVariableRangeEvent(rl("ambient.night_river"));
        public final static SoundEvent windy_leave = SoundEvent.createVariableRangeEvent(rl("ambient.windy_leave"));
        public final static SoundEvent winter_forest = SoundEvent.createVariableRangeEvent(rl("ambient.winter_forest"));
        public final static SoundEvent winter_cold = SoundEvent.createVariableRangeEvent(rl("ambient.winter_cold"));

        @SubscribeEvent
        public static void blockRegister(RegisterEvent event) {
            // MultiPackResourceManager
            event.register(Registries.SOUND_EVENT, soundEventRegisterHelper -> {
                soundEventRegisterHelper.register(spring_forest.getLocation(), spring_forest);
                soundEventRegisterHelper.register(garden_wind.getLocation(), garden_wind);
                soundEventRegisterHelper.register(night_river.getLocation(), night_river);
                soundEventRegisterHelper.register(windy_leave.getLocation(), windy_leave);
                soundEventRegisterHelper.register(winter_forest.getLocation(), winter_forest);
                soundEventRegisterHelper.register(winter_cold.getLocation(), winter_cold);
            });
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class EffectRegistry {
        public static final MobEffect HEAT_STROKE = new HeatStrokeEffect(MobEffectCategory.NEUTRAL, 0xf9d27d);

        @SubscribeEvent
        public static void blockRegister(RegisterEvent event) {
            event.register(Registries.MOB_EFFECT, soundEventRegisterHelper -> {
                soundEventRegisterHelper.register(rl("heat_stroke"), HEAT_STROKE);
            });


        }


    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ParticleRegistry {
        public static final SimpleParticleType FIREFLY = new SimpleParticleType(false);
        public static final SimpleParticleType WILD_GOOSE = new SimpleParticleType(false);
        public static final SimpleParticleType BUTTERFLY = new SimpleParticleType(false);
        public static final ParticleType<ColorParticleOptions> FALLEN_LEAVES = create(false, ColorParticleOptions.DESERIALIZER, (p_123819_) -> ColorParticleOptions.CODEC);

        @SubscribeEvent
        public static void blockRegister(RegisterEvent event) {
            event.register(Registries.PARTICLE_TYPE, particleTypeRegisterHelper -> {
                particleTypeRegisterHelper.register(rl("firefly"), FIREFLY);
                particleTypeRegisterHelper.register(rl("wild_goose"), WILD_GOOSE);
                particleTypeRegisterHelper.register(rl("butterfly"), BUTTERFLY);
                particleTypeRegisterHelper.register(rl("fallen_leaves"), FALLEN_LEAVES);
            });
        }

        private static <T extends ParticleOptions> ParticleType<T> create(boolean pOverrideLimiter, ParticleOptions.Deserializer<T> pDeserializer, final Function<ParticleType<T>, Codec<T>> pCodecFactory) {
            return new ParticleType<T>(pOverrideLimiter, pDeserializer) {
                public Codec<T> codec() {
                    return pCodecFactory.apply(this);
                }
            };
        }
    }
}
