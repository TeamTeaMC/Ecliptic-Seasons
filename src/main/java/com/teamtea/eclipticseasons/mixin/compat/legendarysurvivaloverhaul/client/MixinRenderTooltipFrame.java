package com.teamtea.eclipticseasons.mixin.compat.legendarysurvivaloverhaul.client;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_ESUtil;
import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_SeasonalCalendarSeasonTypeProperty;
import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_SeasonalCalendarTimeProperty;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sfiomn.legendarysurvivaloverhaul.LegendarySurvivalOverhaul;
import sfiomn.legendarysurvivaloverhaul.client.render.RenderTooltipFrame;
import sfiomn.legendarysurvivaloverhaul.registry.ItemRegistry;

@Mixin({RenderTooltipFrame.class})
public abstract class MixinRenderTooltipFrame {
    //
    // @Shadow(remap = false)
    // public static void render(ForgeGui forgeGui, GuiGraphics guiGraphics, int width, int height, Component text) {
    // }
    //
    // @Shadow(remap = false)
    // private static Entity ENTITY_LOOKED_AT;
    //
    // @Inject(
    //         remap = false,
    //         method = "lambda$static$0",
    //         at = @At(value = "FIELD",
    //                 target="Lsfiomn/legendarysurvivaloverhaul/LegendarySurvivalOverhaul;sereneSeasonsLoaded:Z"
    //         )
    // )
    // private static void ecliptic$lambda$static$0(ForgeGui forgeGui,
    //                                              GuiGraphics guiGraphics,
    //                                              float partialTicks,
    //                                              int width,
    //                                              int height,
    //                                              CallbackInfo ci,
    //                                              @Local Item itemInFrame
    // ) {
    //     if ((itemInFrame == ItemRegistry.SEASONAL_CALENDAR.get())) {
    //         render(forgeGui, guiGraphics, width, height, LSO_ESUtil.seasonTooltip(ENTITY_LOOKED_AT.blockPosition(), ENTITY_LOOKED_AT.level()));
    //     }
    // }

}
