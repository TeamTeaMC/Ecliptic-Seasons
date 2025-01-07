package com.teamtea.eclipticseasons.mixin.compat.legendarysurvivaloverhaul.client;


import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_RenderSeasonCards;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sfiomn.legendarysurvivaloverhaul.client.events.ClientModBusEvents;

@Mixin({ClientModBusEvents.class})
public abstract class MixinClientModBusEvents {

    // @Inject(
    //         remap = false,
    //         method = "registerGuiOverlays",
    //         at = @At(value = "HEAD")
    // )
    // private static void ecliptic$registerGuiOverlays(RegisterGuiOverlaysEvent event, CallbackInfo ci) {
    //     event.registerAbove(VanillaGuiOverlay.ITEM_NAME.id(), "season_card_patch", LSO_RenderSeasonCards.SEASON_CARD_GUI);
    // }
}
