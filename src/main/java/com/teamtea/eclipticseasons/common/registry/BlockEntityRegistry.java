package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.blockentity.CalendarBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registry.BLOCK_ENTITY_TYPE_REGISTRY, EclipticSeasonsApi.MODID);
    public static final RegistryObject<BlockEntityType<CalendarBlockEntity>> calendar_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("calendar", () -> BlockEntityType.Builder.of(CalendarBlockEntity::new, BlockRegistry.calendar.get()).build(null));
}
