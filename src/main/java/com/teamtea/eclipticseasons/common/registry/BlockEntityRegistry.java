package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.blockentity.CalendarBlockEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class BlockEntityRegistry {
    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, EclipticSeasonsApi.MODID);
    public static final RegistryObject<TileEntityType<CalendarBlockEntity>> calendar_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("calendar", () -> TileEntityType.Builder.of(CalendarBlockEntity::new, BlockRegistry.calendar.get()).build(null));
}
