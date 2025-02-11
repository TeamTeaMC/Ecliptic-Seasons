package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {

    public static final ItemGroup CREATIVE_TAB = new ItemGroup(EclipticSeasonsApi.MODID) {
        @Override
        public ItemStack makeIcon() {
            return ItemRegistry.calendar_item.get().getDefaultInstance();
        }

    };




}
