package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.blockentity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EclipticSeasonsApi.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindChimesBlockEntity>> wind_chimes_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("wind_chimes", () -> BlockEntityType.Builder.of(WindChimesBlockEntity::new, BlockRegistry.wind_chimes.get(), BlockRegistry.paper_wind_chimes.get(), BlockRegistry.bamboo_wind_chimes.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PinWheelBlockEntity>> pinwheel_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("paper_wind_mill", () -> BlockEntityType.Builder.of(PinWheelBlockEntity::new, BlockRegistry.pinwheel_blue.get(), BlockRegistry.pinwheel_lime.get(), BlockRegistry.pinwheel_orange.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CalendarBlockEntity>> calendar_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("calendar", () -> BlockEntityType.Builder.of(CalendarBlockEntity::new, BlockRegistry.calendar.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GreenHouseCoreBlockEntity>> greenhouse_core_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("greenhouse_core", () -> BlockEntityType.Builder.of(GreenHouseCoreBlockEntity::new, BlockRegistry.spring_greenhouse_core.get(), BlockRegistry.summer_greenhouse_core.get(), BlockRegistry.autumn_greenhouse_core.get(), BlockRegistry.winter_greenhouse_core.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GreenHouseCoreFrameBlockEntity>> greenhouse_core_container_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("greenhouse_core_container", () -> BlockEntityType.Builder.of(GreenHouseCoreFrameBlockEntity::new, BlockRegistry.greenhouse_core_container.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuestHangingSignBlockEntity>> season_quest_hanging_sign_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("season_quest_hanging_sign", () -> BlockEntityType.Builder.of(QuestHangingSignBlockEntity::new, BlockRegistry.season_quest_ceiling_hanging_sign.get(), BlockRegistry.season_quest_wall_hanging_sign.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HumidityControlBlockEntity>> humidity_control_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("humidity_control", () -> BlockEntityType.Builder.of(HumidityControlBlockEntity::new).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockInCopperGrateBlockEntity>> block_in_copper_grate_block_entity_type = BLOCK_ENTITY_TYPE_DEFERRED_REGISTER.register("block_in_copper_grate_block", () -> BlockEntityType.Builder.of(BlockInCopperGrateBlockEntity::new, BlockRegistry.block_in_wooden_grate_block.get(),BlockRegistry.block_in_copper_grate_block.get(), BlockRegistry.block_in_exposed_copper_grate_block.get(), BlockRegistry.block_in_weathered_copper_grate_block.get(), BlockRegistry.block_in_oxidized_copper_grate_block.get(), BlockRegistry.block_in_waxed_copper_grate_block.get(), BlockRegistry.block_in_waxed_exposed_copper_grate_block.get(), BlockRegistry.block_in_waxed_weathered_copper_grate_block.get(), BlockRegistry.block_in_waxed_oxidized_copper_grate_block.get()).build(null));

}
