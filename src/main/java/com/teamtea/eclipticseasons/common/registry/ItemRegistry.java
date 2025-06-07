package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.item.CalendarBlockItem;
import com.teamtea.eclipticseasons.common.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(Registries.ITEM, EclipticSeasonsApi.MODID);

    public static final DeferredHolder<Item, Item> broom = ITEM_DEFERRED_REGISTER.register("broom", () -> new BroomItem(new Item.Properties().durability(256)));
    public static final DeferredHolder<Item, Item> ice_wand = ITEM_DEFERRED_REGISTER.register("ice_wand", () -> new IceWandItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> hyetometer = ITEM_DEFERRED_REGISTER.register("hyetometer", () -> new MeterItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> thermometer = ITEM_DEFERRED_REGISTER.register("thermometer", () -> new MeterItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> hygrometer = ITEM_DEFERRED_REGISTER.register("hygrometer", () -> new MeterBlockItem(BlockRegistry.hygrometer.get(),new Item.Properties()));

    public static final DeferredHolder<Item, Item> growth_detector = ITEM_DEFERRED_REGISTER.register("growth_detector", () -> new GrowthDetectorItem(new Item.Properties()));


    public static final DeferredHolder<Item, BlockItem> bamboo_wind_chimes_item = ITEM_DEFERRED_REGISTER.register("bamboo_wind_chimes", () -> new BlockItem(BlockRegistry.bamboo_wind_chimes.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> paper_wind_chimes_item = ITEM_DEFERRED_REGISTER.register("paper_wind_chimes", () -> new BlockItem(BlockRegistry.paper_wind_chimes.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> wind_chimes_item = ITEM_DEFERRED_REGISTER.register("wind_chimes", () -> new BlockItem(BlockRegistry.wind_chimes.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> pinwheel_orange_item = ITEM_DEFERRED_REGISTER.register("pinwheel_orange", () -> new BlockItem(BlockRegistry.pinwheel_orange.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> pinwheel_lime_item = ITEM_DEFERRED_REGISTER.register("pinwheel_lime", () -> new BlockItem(BlockRegistry.pinwheel_lime.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> pinwheel_blue_item = ITEM_DEFERRED_REGISTER.register("pinwheel_blue", () -> new BlockItem(BlockRegistry.pinwheel_blue.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> calendar_item = ITEM_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlockItem(BlockRegistry.calendar.get(), (new Item.Properties())));

    public static final DeferredHolder<Item, BlockItem> greenhouse_core_container_item = ITEM_DEFERRED_REGISTER.register("greenhouse_core_container", () -> new BlockItem(BlockRegistry.greenhouse_core_container.get(), (new Item.Properties())));

    public static final DeferredHolder<Item, BlockItem> spring_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("spring_greenhouse_core", () -> new BlockItem(BlockRegistry.spring_greenhouse_core.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> summer_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("summer_greenhouse_core", () -> new BlockItem(BlockRegistry.summer_greenhouse_core.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> autumn_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("autumn_greenhouse_core", () -> new BlockItem(BlockRegistry.autumn_greenhouse_core.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> winter_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("winter_greenhouse_core", () -> new BlockItem(BlockRegistry.winter_greenhouse_core.get(), (new Item.Properties())));

    public static final DeferredHolder<Item, Item> spring_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("spring_greenhouse_essence", () -> new Item((new Item.Properties())));
    public static final DeferredHolder<Item, Item> summer_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("summer_greenhouse_essence", () -> new Item((new Item.Properties())));
    public static final DeferredHolder<Item, Item> autumn_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("autumn_greenhouse_essence", () -> new Item((new Item.Properties())));
    public static final DeferredHolder<Item, Item> winter_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("winter_greenhouse_essence", () -> new Item((new Item.Properties())));

    public static final DeferredHolder<Item, Item> seasonal_prayer_scroll_item = ITEM_DEFERRED_REGISTER.register("seasonal_prayer_scroll", () -> new QuestSignChangeItem((new Item.Properties())));

    public static final DeferredHolder<Item, Item> block_in_wooden_grate_block_item = ITEM_DEFERRED_REGISTER.register("block_in_wooden_grate_block", () -> new BlockItem(BlockRegistry.block_in_wooden_grate_block.get(), (new Item.Properties())));

}
