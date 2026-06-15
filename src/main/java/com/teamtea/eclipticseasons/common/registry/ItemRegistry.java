package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.item.CalendarBlockItem;
import com.teamtea.eclipticseasons.common.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEM_DEFERRED_REGISTER = DeferredRegister.createItems(EclipticSeasonsApi.MODID);

    public static final DeferredHolder<Item, Item> broom = ITEM_DEFERRED_REGISTER.registerItem("broom", BroomItem::new, (p) -> (p.durability(256)));
    public static final DeferredHolder<Item, Item> ice_wand = ITEM_DEFERRED_REGISTER.registerItem("ice_wand", IceWandItem::new, (p -> p.stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> salt_wand = ITEM_DEFERRED_REGISTER.registerItem("salt_wand", SaltWandItem::new, (p -> p.stacksTo(1).rarity(Rarity.RARE).durability(4096)));

    public static final DeferredHolder<Item, Item> hyetometer = ITEM_DEFERRED_REGISTER.registerItem("hyetometer", MeterItem::new);
    public static final DeferredHolder<Item, Item> thermometer = ITEM_DEFERRED_REGISTER.registerItem("thermometer", MeterItem::new);
    public static final DeferredHolder<Item, Item> hygrometer = ITEM_DEFERRED_REGISTER.registerItem("hygrometer", (p) -> new MeterBlockItem(BlockRegistry.hygrometer.get(), p.useBlockDescriptionPrefix()));

    public static final DeferredHolder<Item, Item> growth_detector = ITEM_DEFERRED_REGISTER.registerItem("growth_detector", GrowthDetectorItem::new);


    public static final DeferredHolder<Item, BlockItem> bamboo_wind_chimes_item = ITEM_DEFERRED_REGISTER.registerSimpleBlockItem("bamboo_wind_chimes", BlockRegistry.bamboo_wind_chimes);
    public static final DeferredHolder<Item, BlockItem> paper_wind_chimes_item = ITEM_DEFERRED_REGISTER.registerSimpleBlockItem("paper_wind_chimes", BlockRegistry.paper_wind_chimes);
    public static final DeferredHolder<Item, BlockItem> wind_chimes_item = ITEM_DEFERRED_REGISTER.registerSimpleBlockItem("wind_chimes", BlockRegistry.wind_chimes);
    public static final DeferredHolder<Item, BlockItem> pinwheel_orange_item = ITEM_DEFERRED_REGISTER.registerSimpleBlockItem("pinwheel_orange", BlockRegistry.pinwheel_orange);
    public static final DeferredHolder<Item, BlockItem> pinwheel_lime_item = ITEM_DEFERRED_REGISTER.registerSimpleBlockItem("pinwheel_lime", BlockRegistry.pinwheel_lime);
    public static final DeferredHolder<Item, BlockItem> pinwheel_blue_item = ITEM_DEFERRED_REGISTER.registerSimpleBlockItem("pinwheel_blue", BlockRegistry.pinwheel_blue);

    public static final DeferredHolder<Item, BlockItem> calendar_item = ITEM_DEFERRED_REGISTER.registerItem("calendar", p -> new CalendarBlockItem(BlockRegistry.calendar.get(), p.useBlockDescriptionPrefix()), Item.Properties::new);

    public static final DeferredHolder<Item, BlockItem> greenhouse_core_container_item = ITEM_DEFERRED_REGISTER.registerSimpleBlockItem("greenhouse_core_container", BlockRegistry.greenhouse_core_container);

    public static final DeferredHolder<Item, BlockItem> spring_greenhouse_core_item = ITEM_DEFERRED_REGISTER.registerItem("spring_greenhouse_core", (p) -> new GreenhouseCoreBlockItem((GreenHouseCoreBlock) BlockRegistry.spring_greenhouse_core.get(), p.useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> summer_greenhouse_core_item = ITEM_DEFERRED_REGISTER.registerItem("summer_greenhouse_core", (p) -> new GreenhouseCoreBlockItem((GreenHouseCoreBlock) BlockRegistry.summer_greenhouse_core.get(), p.useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> autumn_greenhouse_core_item = ITEM_DEFERRED_REGISTER.registerItem("autumn_greenhouse_core", (p) -> new GreenhouseCoreBlockItem((GreenHouseCoreBlock) BlockRegistry.autumn_greenhouse_core.get(), p.useBlockDescriptionPrefix()));
    public static final DeferredHolder<Item, BlockItem> winter_greenhouse_core_item = ITEM_DEFERRED_REGISTER.registerItem("winter_greenhouse_core", (p) -> new GreenhouseCoreBlockItem((GreenHouseCoreBlock) BlockRegistry.winter_greenhouse_core.get(), p.useBlockDescriptionPrefix()));

    public static final DeferredHolder<Item, Item> spring_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.registerItem("spring_greenhouse_essence", GreenhouseEssenceItem::new);
    public static final DeferredHolder<Item, Item> summer_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.registerItem("summer_greenhouse_essence", GreenhouseEssenceItem::new);
    public static final DeferredHolder<Item, Item> autumn_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.registerItem("autumn_greenhouse_essence", GreenhouseEssenceItem::new);
    public static final DeferredHolder<Item, Item> winter_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.registerItem("winter_greenhouse_essence", GreenhouseEssenceItem::new);

    public static final DeferredHolder<Item, Item> seasonal_prayer_scroll_item = ITEM_DEFERRED_REGISTER.registerItem("seasonal_prayer_scroll", QuestSignChangeItem::new);

    public static final DeferredHolder<Item, BlockItem> block_in_wooden_grate_block_item = ITEM_DEFERRED_REGISTER.registerSimpleBlockItem("block_in_wooden_grate_block", BlockRegistry.block_in_wooden_grate_block);


    public static final DeferredHolder<Item, Item> snowless_hometown = ITEM_DEFERRED_REGISTER.registerItem("snowless_hometown", Item::new, (p -> p.stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(SongRegistry.SNOWLESS_HOMETOWN)));

    public static final DeferredHolder<Item, BlockItem> humidity_tank_item = ITEM_DEFERRED_REGISTER.registerItem("humidity_tank", p -> new HumidityModifierBlockItem(BlockRegistry.humidity_tank.get(), p.useBlockDescriptionPrefix()), Item.Properties::new);

    public static final DeferredHolder<Item, BlockItem> dehumidifier_item = ITEM_DEFERRED_REGISTER.registerItem("dehumidifier", p -> new HumidityModifierBlockItem(BlockRegistry.dehumidifier.get(), p.useBlockDescriptionPrefix()), Item.Properties::new);

    public static final DeferredItem<BlockItem> season_sensor_item = ITEM_DEFERRED_REGISTER.registerSimpleBlockItem(BlockRegistry.season_sensor);
}
