package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.CalendarBlockItem;
import com.teamtea.eclipticseasons.common.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(Registries.ITEM, EclipticSeasonsApi.MODID);
    public static final RegistryObject<BlockItem> calendar_item = ITEM_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlockItem(BlockRegistry.calendar.get(), (new Item.Properties())));
    
    public static final RegistryObject<MeterItem> hyetometer = ITEM_DEFERRED_REGISTER.register("hyetometer", () -> new MeterItem(new Item.Properties()));
    public static final RegistryObject<MeterItem> thermometer = ITEM_DEFERRED_REGISTER.register("thermometer", () -> new MeterItem(new Item.Properties()));
    public static final RegistryObject<MeterItem> hygrometer = ITEM_DEFERRED_REGISTER.register("hygrometer", () -> new MeterItem(new Item.Properties()));

    public static final RegistryObject<Item>  broom = ITEM_DEFERRED_REGISTER.register("broom", () -> new BroomItem(new Item.Properties().durability(256)));

    public static final RegistryObject<Item> growth_detector = ITEM_DEFERRED_REGISTER.register("growth_detector", () -> new GrowthDetectorItem(new Item.Properties()));

    public static final RegistryObject<Item> greenhouse_core_container_item = ITEM_DEFERRED_REGISTER.register("greenhouse_core_container", () -> new GreenHouseFrameItem(BlockRegistry.greenhouse_core_container.get(), (new Item.Properties())));

    public static final RegistryObject<Item> spring_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("spring_greenhouse_core", () -> new GreenHouseCoreItem(BlockRegistry.spring_greenhouse_core.get(), (new Item.Properties())));
    public static final RegistryObject<Item> summer_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("summer_greenhouse_core", () -> new GreenHouseCoreItem(BlockRegistry.summer_greenhouse_core.get(), (new Item.Properties())));
    public static final RegistryObject<Item> autumn_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("autumn_greenhouse_core", () -> new GreenHouseCoreItem(BlockRegistry.autumn_greenhouse_core.get(), (new Item.Properties())));
    public static final RegistryObject<Item> winter_greenhouse_core_item = ITEM_DEFERRED_REGISTER.register("winter_greenhouse_core", () -> new GreenHouseCoreItem(BlockRegistry.winter_greenhouse_core.get(), (new Item.Properties())));

    public static final RegistryObject<Item> spring_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("spring_greenhouse_essence", () -> new GreenHouseCoreCoreItem((new Item.Properties())));
    public static final RegistryObject<Item> summer_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("summer_greenhouse_essence", () -> new GreenHouseCoreCoreItem((new Item.Properties())));
    public static final RegistryObject<Item> autumn_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("autumn_greenhouse_essence", () -> new GreenHouseCoreCoreItem((new Item.Properties())));
    public static final RegistryObject<Item> winter_greenhouse_essence_item = ITEM_DEFERRED_REGISTER.register("winter_greenhouse_essence", () -> new GreenHouseCoreCoreItem((new Item.Properties())));

    public static final RegistryObject<Item> seasonal_prayer_scroll_item = ITEM_DEFERRED_REGISTER.register("seasonal_prayer_scroll", () -> new QuestSignChangeItem((new Item.Properties())));

}
