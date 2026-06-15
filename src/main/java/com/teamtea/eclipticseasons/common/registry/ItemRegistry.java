package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.CalendarBlockItem;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(Registries.ITEM, EclipticSeasonsApi.MODID);

    public static final RegistryObject<BlockItem> bamboo_wind_chimes_item = ITEM_DEFERRED_REGISTER.register("bamboo_wind_chimes", () -> new BlockItem(BlockRegistry.bamboo_wind_chimes.get(), (new Item.Properties())));
    public static final RegistryObject<BlockItem> paper_wind_chimes_item = ITEM_DEFERRED_REGISTER.register("paper_wind_chimes", () -> new BlockItem(BlockRegistry.paper_wind_chimes.get(), (new Item.Properties())));
    public static final RegistryObject<BlockItem> wind_chimes_item = ITEM_DEFERRED_REGISTER.register("wind_chimes", () -> new BlockItem(BlockRegistry.wind_chimes.get(), (new Item.Properties())));
    public static final RegistryObject<BlockItem> pinwheel_orange_item = ITEM_DEFERRED_REGISTER.register("pinwheel_orange", () -> new BlockItem(BlockRegistry.pinwheel_orange.get(), (new Item.Properties())));
    public static final RegistryObject<BlockItem> pinwheel_lime_item = ITEM_DEFERRED_REGISTER.register("pinwheel_lime", () -> new BlockItem(BlockRegistry.pinwheel_lime.get(), (new Item.Properties())));
    public static final RegistryObject<BlockItem> pinwheel_blue_item = ITEM_DEFERRED_REGISTER.register("pinwheel_blue", () -> new BlockItem(BlockRegistry.pinwheel_blue.get(), (new Item.Properties())));

    public static final RegistryObject<BlockItem> calendar_item = ITEM_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlockItem(BlockRegistry.calendar.get(), (new Item.Properties())));

    public static final RegistryObject<MeterItem> hyetometer = ITEM_DEFERRED_REGISTER.register("hyetometer", () -> new MeterItem(new Item.Properties()));
    public static final RegistryObject<MeterItem> thermometer = ITEM_DEFERRED_REGISTER.register("thermometer", () -> new MeterItem(new Item.Properties()));
    public static final RegistryObject<MeterBlockItem> hygrometer = ITEM_DEFERRED_REGISTER.register("hygrometer", () -> new MeterBlockItem(BlockRegistry.hygrometer.get(), new Item.Properties()));

    public static final RegistryObject<Item> broom = ITEM_DEFERRED_REGISTER.register("broom", () -> new BroomItem(new Item.Properties().durability(256)));

    public static final RegistryObject<Item> growth_detector = ITEM_DEFERRED_REGISTER.register("growth_detector", () -> new GrowthDetectorItem(new Item.Properties()));

    public static final RegistryObject<Item> greenhouse_core_container_item = ITEM_DEFERRED_REGISTER.register("greenhouse_core_container", () -> new GreenHouseFrameItem(BlockRegistry.greenhouse_core_container.get(), (new Item.Properties())));

    public static final RegistryObject<Item> spring_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("spring_greenhouse_core", () -> new GreenhouseCoreBlockItem((GreenHouseCoreBlock) BlockRegistry.spring_greenhouse_core.get(), (new Item.Properties())));
    public static final RegistryObject<Item> summer_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("summer_greenhouse_core", () -> new GreenhouseCoreBlockItem((GreenHouseCoreBlock) BlockRegistry.summer_greenhouse_core.get(), (new Item.Properties())));
    public static final RegistryObject<Item> autumn_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("autumn_greenhouse_core", () -> new GreenhouseCoreBlockItem((GreenHouseCoreBlock) BlockRegistry.autumn_greenhouse_core.get(), (new Item.Properties())));
    public static final RegistryObject<Item> winter_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("winter_greenhouse_core", () -> new GreenhouseCoreBlockItem((GreenHouseCoreBlock) BlockRegistry.winter_greenhouse_core.get(), (new Item.Properties())));

    public static final RegistryObject<Item> spring_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("spring_greenhouse_essence", () -> new GreenhouseEssenceItem((new Item.Properties())));
    public static final RegistryObject<Item> summer_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("summer_greenhouse_essence", () -> new GreenhouseEssenceItem((new Item.Properties())));
    public static final RegistryObject<Item> autumn_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("autumn_greenhouse_essence", () -> new GreenhouseEssenceItem((new Item.Properties())));
    public static final RegistryObject<Item> winter_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("winter_greenhouse_essence", () -> new GreenhouseEssenceItem((new Item.Properties())));

    public static final RegistryObject<Item> seasonal_prayer_scroll_item = ITEM_DEFERRED_REGISTER.register("seasonal_prayer_scroll", () -> new QuestSignChangeItem((new Item.Properties())));

    public static final RegistryObject<Item> block_in_wooden_grate_block_item = ITEM_DEFERRED_REGISTER.register("block_in_wooden_grate_block", () -> new BlockItem(BlockRegistry.block_in_wooden_grate_block.get(), (new Item.Properties())));

    public static final RegistryObject<RecordItem> snowless_hometown = ITEM_DEFERRED_REGISTER.register("snowless_hometown", () -> new RecordItem(14, () -> SoundEventsRegistry.snowless_hometown, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 103*20));

    public static final RegistryObject<BlockItem> humidity_tank_item = ITEM_DEFERRED_REGISTER.register("humidity_tank",() -> new HumidityModifierBlockItem(BlockRegistry.humidity_tank.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> dehumidifier_item = ITEM_DEFERRED_REGISTER.register("dehumidifier", () -> new HumidityModifierBlockItem(BlockRegistry.dehumidifier.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> season_sensor_item = ITEM_DEFERRED_REGISTER.register("season_sensor", () -> new BlockItem(BlockRegistry.season_sensor.get(), (new Item.Properties())));

}
