package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.common.resource.conditions.SeasonalSimulationLevelCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ResourceConditionRegistry {
    @SubscribeEvent
    public static void registerSerializers(FMLCommonSetupEvent event) {
        CraftingHelper.register(SeasonalSimulationLevelCondition.Serializer.INSTANCE);
    }
}
