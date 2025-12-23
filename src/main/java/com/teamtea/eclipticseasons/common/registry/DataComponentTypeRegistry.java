package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.item.attachment.ClickPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponentTypeRegistry {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EclipticSeasonsApi.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ClickPos>> CLICK_POS = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(
            "click_pos", () -> DataComponentType.<ClickPos>builder().persistent(ClickPos.CODEC).build()
    );

}
