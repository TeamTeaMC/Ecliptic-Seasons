package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {

    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(EclipticSeasonsApi.MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return ItemRegistry.calendar_item.get().getDefaultInstance();
        }
    };




}
