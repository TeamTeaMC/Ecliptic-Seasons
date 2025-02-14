package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.blockentity.CalendarBlockEntity;
import com.teamtea.eclipticseasons.common.block.blockentity.PinWheelBlockEntity;
import com.teamtea.eclipticseasons.common.block.blockentity.WindChimesBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EclipticSeasonsApi.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindChimesBlockEntity>> wind_chimes_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("wind_chimes", () -> BlockEntityType.Builder.of(WindChimesBlockEntity::new, BlockRegistry.wind_chimes.get(), BlockRegistry.paper_wind_chimes.get(), BlockRegistry.bamboo_wind_chimes.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PinWheelBlockEntity>> pinwheel_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("paper_wind_mill", () -> BlockEntityType.Builder.of(PinWheelBlockEntity::new, BlockRegistry.pinwheel_blue.get(), BlockRegistry.pinwheel_lime.get(), BlockRegistry.pinwheel_orange.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CalendarBlockEntity>> calendar_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("calendar", () -> BlockEntityType.Builder.of(CalendarBlockEntity::new, BlockRegistry.calendar.get()).build(null));
}
