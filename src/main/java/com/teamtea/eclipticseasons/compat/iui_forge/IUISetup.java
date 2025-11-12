package com.teamtea.eclipticseasons.compat.iui_forge;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.anningui.iui_forge.ImproperUI;
import org.anningui.iui_forge.ImproperUIAPI;
import org.anningui.iui_forge.script.ScriptParser;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;

public class IUISetup {
    public static final IUISetup INSTANCE = new IUISetup();

    @SubscribeEvent
    public void onClientEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            addTagSupplier("blockview",SnowyBlockView::new);

            ImproperUIAPI.init(EclipticSeasonsApi.MODID, ImproperUI.class,
                    "assets/%s/improperui/snow.ui".formatted(EclipticSeasonsApi.MODID)
            );
        });
    }

    private static void addTagSupplier(String tag, Supplier<?> supplier) {
        try {
            Field field = ScriptParser.class.getDeclaredField("tagSuppliers");
            field.setAccessible(true);

            Map<String, Supplier<?>> tagSuppliers = (Map<String, Supplier<?>>) field.get(null);
            tagSuppliers.put(tag, supplier);

            field.setAccessible(false);

        } catch (Exception e) {
            throw new RuntimeException("Failed to add tag supplier: " + tag, e);
        }
    }
}
