package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsCriterion;
import com.teamtea.eclipticseasons.common.block.CalendarBlock;
import com.teamtea.eclipticseasons.common.block.CalendarBlockItem;
import com.teamtea.eclipticseasons.common.block.blockentity.CalendarBlockEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {
    public static final DeferredRegister<Block> BLOCK_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, EclipticSeasons.MODID);
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(Registries.ITEM, EclipticSeasonsApi.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EclipticSeasonsApi.MODID);

    public static RegistryObject<Block> snowySlab = BLOCK_DEFERRED_REGISTER.register("snowy_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyStairs = BLOCK_DEFERRED_REGISTER.register("snowy_stairs", () -> new StairBlock(Blocks.OAK_PLANKS::defaultBlockState, BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyBlock = BLOCK_DEFERRED_REGISTER.register("snowy_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyLeaves = BLOCK_DEFERRED_REGISTER.register("snowy_leaves", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));

    // calendar 日历
    public static final RegistryObject<Block> calendar = BLOCK_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<BlockItem> calendar_item = ITEM_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlockItem(calendar.get(), (new Item.Properties())));
    public static final RegistryObject<BlockEntityType<CalendarBlockEntity>> calendar_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("calendar", () -> BlockEntityType.Builder.of(CalendarBlockEntity::new, ModContents.calendar.get()).build(null));

    public static SolarTermsCriterion solarTermsCriterion = new SolarTermsCriterion(EclipticSeasons.rl("solar_terms"));
    public static SolarTermsCriterion heatStrokeCriterion = new SolarTermsCriterion(EclipticSeasons.rl("heat_stroke"));

    public static void register() {
        CriteriaTriggers.register(solarTermsCriterion);
        CriteriaTriggers.register(heatStrokeCriterion);
    }

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
                                                    output.accept(itemDeferredHolder.get())
                                    );
                                })
                                .build());
            });
    }


}
