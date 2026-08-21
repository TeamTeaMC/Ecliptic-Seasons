package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.common.resource.conditions.SeasonalSimulationLevelCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;


public class ResourceConditionRegistry {

    public static void init() {
        CraftingHelper.register(SeasonalSimulationLevelCondition.Serializer.INSTANCE);
    }
}
