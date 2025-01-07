package com.teamtea.eclipticseasons.mixin.compat.legendarysurvivaloverhaul.client;


import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_SeasonalCalendarSeasonTypeProperty;
import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_SeasonalCalendarTimeProperty;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sfiomn.legendarysurvivaloverhaul.LegendarySurvivalOverhaul;
import sfiomn.legendarysurvivaloverhaul.registry.ItemRegistry;

@Mixin({LegendarySurvivalOverhaul.ClientModEvents.class})
public abstract class MixinModClientCore {

    // @Inject(
    //         remap = false,
    //         method = "onClientSetup",
    //         at = @At(value = "HEAD")
    // )
    // private static void ecliptic$registerGuiOverlays(FMLClientSetupEvent event, CallbackInfo ci) {
    //     ItemProperties.register(ItemRegistry.SEASONAL_CALENDAR.get(), new ResourceLocation("legendarysurvivaloverhaul", "time"), new LSO_SeasonalCalendarTimeProperty());
    //     ItemProperties.register(ItemRegistry.SEASONAL_CALENDAR.get(), new ResourceLocation("legendarysurvivaloverhaul", "seasontype"), new LSO_SeasonalCalendarSeasonTypeProperty());
    // }


}
