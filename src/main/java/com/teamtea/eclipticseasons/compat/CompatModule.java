package com.teamtea.eclipticseasons.compat;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.momosoftworks.coldsweat.ColdSweat;
import com.teamtea.eclipticseasons.compat.cold_sweat.Cold_Sweat;
import com.teamtea.eclipticseasons.compat.dynamictrees.DynamicTreeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class CompatModule {

    public static void register() {

        if (ModList.get().isLoaded(DynamicTrees.MOD_ID)) {
            DynamicTreeMod.init();

        }

        if (ModList.get().isLoaded(ColdSweat.MOD_ID)) {
            MinecraftForge.EVENT_BUS.register(Cold_Sweat.INSTANCE);
        }


    }
}
