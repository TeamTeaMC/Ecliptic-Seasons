package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.CalendarBlockItem;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class ItemRegistry {
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, EclipticSeasonsApi.MODID);
    public static final RegistryObject<BlockItem> calendar_item = ITEM_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlockItem(BlockRegistry.calendar.get(), (new Item.Properties().tab(ModContents.CREATIVE_TAB))));
}
