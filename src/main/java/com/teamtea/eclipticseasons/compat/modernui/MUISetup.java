package com.teamtea.eclipticseasons.compat.modernui;

import icyllis.modernui.mc.MuiModApi;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.anningui.iui_forge.script.ScriptParser;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

public class MUISetup {
    public static final MUISetup INSTANCE = new MUISetup();

    @SubscribeEvent
    public void onClientEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            
        });
    }

}
