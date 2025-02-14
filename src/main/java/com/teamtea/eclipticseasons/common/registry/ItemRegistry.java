package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.CalendarBlockItem;
import com.teamtea.eclipticseasons.common.item.BroomItem;
import com.teamtea.eclipticseasons.common.item.MeterItem;
import com.teamtea.eclipticseasons.common.item.SnowyMakerItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(Registries.ITEM, EclipticSeasonsApi.MODID);

    public static final DeferredHolder<Item, Item> broom = ITEM_DEFERRED_REGISTER.register("broom", () -> new BroomItem(new Item.Properties().durability(256)));
    public static final DeferredHolder<Item, Item> ice_wand = ITEM_DEFERRED_REGISTER.register("ice_wand", () -> new SnowyMakerItem(new Item.Properties()));

    public static final DeferredHolder<Item, Item> hyetometer = ITEM_DEFERRED_REGISTER.register("hyetometer", () -> new MeterItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> thermometer = ITEM_DEFERRED_REGISTER.register("thermometer", () -> new MeterItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> hygrometer = ITEM_DEFERRED_REGISTER.register("hygrometer", () -> new MeterItem(new Item.Properties()));


    public static final DeferredHolder<Item, BlockItem> bamboo_wind_chimes_item = ITEM_DEFERRED_REGISTER.register("bamboo_wind_chimes", () -> new BlockItem(BlockRegistry.bamboo_wind_chimes.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> paper_wind_chimes_item = ITEM_DEFERRED_REGISTER.register("paper_wind_chimes", () -> new BlockItem(BlockRegistry.paper_wind_chimes.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> wind_chimes_item = ITEM_DEFERRED_REGISTER.register("wind_chimes", () -> new BlockItem(BlockRegistry.wind_chimes.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> pinwheel_orange_item = ITEM_DEFERRED_REGISTER.register("pinwheel_orange", () -> new BlockItem(BlockRegistry.pinwheel_orange.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> pinwheel_lime_item = ITEM_DEFERRED_REGISTER.register("pinwheel_lime", () -> new BlockItem(BlockRegistry.pinwheel_lime.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> pinwheel_blue_item = ITEM_DEFERRED_REGISTER.register("pinwheel_blue", () -> new BlockItem(BlockRegistry.pinwheel_blue.get(), (new Item.Properties())));
    public static final DeferredHolder<Item, BlockItem> calendar_item = ITEM_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlockItem(BlockRegistry.calendar.get(), (new Item.Properties())));


}
