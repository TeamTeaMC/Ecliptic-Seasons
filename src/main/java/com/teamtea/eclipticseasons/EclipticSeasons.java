package com.teamtea.eclipticseasons;


import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.misc.BasicWeather;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsRecord;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsCriterion;
import com.teamtea.eclipticseasons.common.block.CalendarBlock;
import com.teamtea.eclipticseasons.common.block.CalendarBlockItem;
import com.teamtea.eclipticseasons.common.block.PaperWindmillBlock;
import com.teamtea.eclipticseasons.common.block.WindChimesBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.CalendarBlockEntity;
import com.teamtea.eclipticseasons.common.block.blockentity.PaperWindmillBlockEntity;
import com.teamtea.eclipticseasons.common.block.blockentity.WindChimesBlockEntity;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.item.BroomItem;
import com.teamtea.eclipticseasons.common.item.SnowyMakerItem;
import com.teamtea.eclipticseasons.common.misc.HeatStrokeEffect;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.ServerConfig;
import com.teamtea.eclipticseasons.data.start;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
// import xueluoanping.fluiddrawerslegacy.handler.ControllerFluidCapabilityHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EclipticSeasonsApi.MODID)
public class EclipticSeasons {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(EclipticSeasonsApi.MODID);
    public static final String NETWORK_VERSION = "1.0";

    public EclipticSeasons(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::FMLCommonSetup);
        modEventBus.addListener(this::FMLCommonSetup);
        modEventBus.addListener(this::gatherData);

        ModContents.BLOCK_DEFERRED_REGISTER.register(modEventBus);
        ModContents.ITEM_DEFERRED_REGISTER.register(modEventBus);
        ModContents.BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register(modEventBus);
        ModContents.TRIGGER_DEFERRED_REGISTER.register(modEventBus);
        ModContents.ATTACHMENT_TYPES.register(modEventBus);

        TestContents.weathers.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, ServerConfig.SERVER_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        if (FMLLoader.getDist() == Dist.CLIENT)
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }


    public static ResourceLocation rl(String id) {
        return ResourceLocation.fromNamespaceAndPath(EclipticSeasonsApi.MODID, id);
    }


    public void FMLCommonSetup(final FMLCommonSetupEvent event) {
        // SimpleNetworkHandler.init();
        CompatModule.register();
    }


    public void gatherData(final GatherDataEvent event) {
        start.dataGen(event);
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

        // if (!FMLEnvironment.production||General.bool.get())
        {
            StringBuilder output = new StringBuilder();

            for (Object i : x) {
                if (i == null) output.append(", ").append("null");
                else if (i.getClass().isArray()) {
                    ;
                    output.append(", [");
                    for (Object c : Arrays.stream((Object[]) i).toList()) {
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

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
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

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static final class ParticleRegistry {
        public static final SimpleParticleType FIREFLY = new SimpleParticleType(false);
        public static final SimpleParticleType WILD_GOOSE = new SimpleParticleType(false);
        public static final SimpleParticleType BUTTERFLY = new SimpleParticleType(false);
        public static final ParticleType<ColorParticleOption> FALLEN_LEAVES = create(false, ColorParticleOption::codec, ColorParticleOption::streamCodec);

        @SubscribeEvent
        public static void blockRegister(RegisterEvent event) {
            event.register(Registries.PARTICLE_TYPE, particleTypeRegisterHelper -> {
                particleTypeRegisterHelper.register(rl("firefly"), FIREFLY);
                particleTypeRegisterHelper.register(rl("wild_goose"), WILD_GOOSE);
                particleTypeRegisterHelper.register(rl("butterfly"), BUTTERFLY);
                particleTypeRegisterHelper.register(rl("fallen_leaves"), FALLEN_LEAVES);
            });
        }

        private static <T extends ParticleOptions> ParticleType<T> create(
                boolean pOverrideLimitter,
                final Function<ParticleType<T>, MapCodec<T>> pCodecGetter,
                final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> pStreamCodecGetter
        ) {
            return new ParticleType<T>(pOverrideLimitter) {
                @Override
                public MapCodec<T> codec() {
                    return pCodecGetter.apply(this);
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                    return pStreamCodecGetter.apply(this);
                }
            };
        }


    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static final class EffectRegistry {
        public static class Effects {
            public static final ResourceKey<MobEffect> HEAT_STROKE = ResourceKey.create(Registries.MOB_EFFECT, rl("heat_stroke"));
        }

        public static final MobEffect HEAT_STROKE = new HeatStrokeEffect(MobEffectCategory.NEUTRAL, 0xf9d27d);


        @SubscribeEvent
        public static void blockRegister(RegisterEvent event) {
            event.register(Registries.MOB_EFFECT, helper -> {
                helper.register(Effects.HEAT_STROKE, HEAT_STROKE);
            });
        }

    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class ModContents {
        public static final DeferredRegister<Block> BLOCK_DEFERRED_REGISTER = DeferredRegister.create(Registries.BLOCK, EclipticSeasonsApi.MODID);
        public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(Registries.ITEM, EclipticSeasonsApi.MODID);
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EclipticSeasonsApi.MODID);


        public static DeferredHolder<Block, Block> snowySlab = BLOCK_DEFERRED_REGISTER.register("snowy_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));
        public static DeferredHolder<Block, Block> snowyStairs = BLOCK_DEFERRED_REGISTER.register("snowy_stairs", () -> new StairBlock(Blocks.OAK_PLANKS.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
        public static DeferredHolder<Block, Block> snowyBlock = BLOCK_DEFERRED_REGISTER.register("snowy_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
        public static DeferredHolder<Block, Block> snowyLeaves = BLOCK_DEFERRED_REGISTER.register("snowy_leaves", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));

        // calendar 日历
        public static final DeferredHolder<Block, Block> calendar = BLOCK_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
        public static final DeferredHolder<Item, BlockItem> calendar_item = ITEM_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlockItem(calendar.get(), (new Item.Properties())));
        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CalendarBlockEntity>> calendar_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("calendar", () -> BlockEntityType.Builder.of(CalendarBlockEntity::new, ModContents.calendar.get()).build(null));

        // paper_wind_mill 纸风车
        public static final DeferredHolder<Block, Block> paper_wind_mill = BLOCK_DEFERRED_REGISTER.register("paper_wind_mill", () -> new PaperWindmillBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
        public static final DeferredHolder<Item, BlockItem> paper_wind_mill_item = ITEM_DEFERRED_REGISTER.register("paper_wind_mill", () -> new BlockItem(paper_wind_mill.get(), (new Item.Properties())));
        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PaperWindmillBlockEntity>> paper_wind_mill_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("paper_wind_mill", () -> BlockEntityType.Builder.of(PaperWindmillBlockEntity::new, ModContents.paper_wind_mill.get()).build(null));

        // wind_chimes 风铃
        public static final DeferredHolder<Block, Block> wind_chimes = BLOCK_DEFERRED_REGISTER.register("wind_chimes", () -> new WindChimesBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
        public static final DeferredHolder<Item, BlockItem> wind_chimes_item = ITEM_DEFERRED_REGISTER.register("wind_chimes", () -> new BlockItem(wind_chimes.get(), (new Item.Properties())));
        public static final DeferredHolder<Block, Block> paper_wind_chimes = BLOCK_DEFERRED_REGISTER.register("paper_wind_chimes", () -> new WindChimesBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
        public static final DeferredHolder<Item, BlockItem> paper_wind_chimes_item = ITEM_DEFERRED_REGISTER.register("paper_wind_chimes", () -> new BlockItem(paper_wind_chimes.get(), (new Item.Properties())));
        public static final DeferredHolder<Block, Block> bamboo_wind_chimes = BLOCK_DEFERRED_REGISTER.register("bamboo_wind_chimes", () -> new WindChimesBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.BAMBOO).noOcclusion().pushReaction(PushReaction.DESTROY)));
        public static final DeferredHolder<Item, BlockItem> bamboo_wind_chimes_item = ITEM_DEFERRED_REGISTER.register("bamboo_wind_chimes", () -> new BlockItem(bamboo_wind_chimes.get(), (new Item.Properties())));
        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindChimesBlockEntity>> wind_chimes_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("wind_chimes", () -> BlockEntityType.Builder.of(WindChimesBlockEntity::new, ModContents.wind_chimes.get(), ModContents.paper_wind_chimes.get(), ModContents.bamboo_wind_chimes.get()).build(null));

        public static final DeferredHolder<Item, Item> snowy_maker_item = ITEM_DEFERRED_REGISTER.register("snowy_maker", () -> new SnowyMakerItem(new Item.Properties()));
        public static final DeferredHolder<Item, Item> broom_item = ITEM_DEFERRED_REGISTER.register("broom", () -> new BroomItem(new Item.Properties().durability(256)));


        // advancement
        public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_DEFERRED_REGISTER = DeferredRegister.create(Registries.TRIGGER_TYPE, EclipticSeasonsApi.MODID);
        public static final Supplier<SolarTermsCriterion> SOLAR_TERMS = TRIGGER_DEFERRED_REGISTER.register("solar_terms", SolarTermsCriterion::new);
        public static final Supplier<SolarTermsCriterion> heatStroke = TRIGGER_DEFERRED_REGISTER.register("heat_stroke", SolarTermsCriterion::new);

        // DataComponent
        public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EclipticSeasonsApi.MODID);
        public static final Supplier<AttachmentType<SolarTermsRecord>> SOLAR_TERMS_RECORD = ATTACHMENT_TYPES.register(
                "solar_terms_record",
                () -> AttachmentType.builder(() -> new SolarTermsRecord(new ArrayList<>())).serialize(SolarTermsRecord.CODEC).build());
        public static final Supplier<AttachmentType<SnowyRemover>> SNOWY_REMOVER = ATTACHMENT_TYPES.register(
                "snowy_remover",
                () -> AttachmentType.builder(() -> new SnowyRemover(new int[16][16])).serialize(SnowyRemover.CODEC).build());

        @SubscribeEvent
        public static void blockRegister(RegisterEvent event) {
            if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB)
                event.register(Registries.CREATIVE_MODE_TAB, helper -> {
                    helper.register(EclipticSeasons.rl(EclipticSeasonsApi.MODID),
                            CreativeModeTab.builder().icon(() -> new ItemStack(ModContents.calendar_item.get()))
                                    .title(Component.translatable("itemGroup." + EclipticSeasonsApi.MODID + ".core"))
                                    .displayItems((params, output) -> {
                                        ITEM_DEFERRED_REGISTER.getEntries().forEach(
                                                itemDeferredHolder ->
                                                        output.accept(itemDeferredHolder.value())
                                        );
                                    })
                                    .build());
                });
        }


    }


    private static <T> ResourceKey<Registry<T>> createRegistryKey(String pName) {
        return ResourceKey.createRegistryKey(rl(pName));
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class TestContents {
        public static final ResourceKey<Registry<BasicWeather>> WEATHER = createRegistryKey("weather");
        public static final Registry<BasicWeather> BASIC_WEATHERS = new RegistryBuilder<>(WEATHER).sync(true).create();
        public static final DeferredRegister<BasicWeather> weathers = DeferredRegister.create(WEATHER, EclipticSeasonsApi.MODID);

        static {
            // ByteBufCodecs.registry(Registries.ENTITY_TYPE).encode(p_320192_, this.type);
            if (FMLLoader.getDist() == Dist.CLIENT) {
                weathers.register("test", () -> new BasicWeather() {
                    @Override
                    protected Object clone() {
                        return this;
                    }
                });
                weathers.register("ss", () -> new BasicWeather() {
                    @Override
                    protected Object clone() {
                        return this;
                    }
                });
            } else {
                weathers.register("ss", () -> new BasicWeather() {
                    @Override
                    protected Object clone() {
                        return this;
                    }
                });
                weathers.register("test", () -> new BasicWeather() {
                    @Override
                    protected Object clone() {
                        return this;
                    }
                });
            }


        }

        @SubscribeEvent
        public static void newRegistryEvent(NewRegistryEvent event) {
            event.register(BASIC_WEATHERS);
        }

    }
}
