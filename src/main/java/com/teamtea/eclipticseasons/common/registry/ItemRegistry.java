package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.CalendarBlockItem;
import com.teamtea.eclipticseasons.common.item.MeterItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, EclipticSeasonsApi.MODID);
    public static final RegistryObject<BlockItem> calendar_item = ITEM_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlockItem(BlockRegistry.calendar.get(), (new Item.Properties().tab(ModContents.CREATIVE_TAB))));



    public static final RegistryObject<MeterItem> hyetometer = ITEM_DEFERRED_REGISTER.register("hyetometer", () -> new MeterItem(new Item.Properties().tab(ModContents.CREATIVE_TAB)));
    public static final RegistryObject<MeterItem> thermometer = ITEM_DEFERRED_REGISTER.register("thermometer", () -> new MeterItem(new Item.Properties().tab(ModContents.CREATIVE_TAB)));
    public static final RegistryObject<MeterItem> hygrometer = ITEM_DEFERRED_REGISTER.register("hygrometer", () -> new MeterItem(new Item.Properties().tab(ModContents.CREATIVE_TAB)));
}
