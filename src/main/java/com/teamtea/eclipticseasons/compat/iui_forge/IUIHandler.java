package com.teamtea.eclipticseasons.compat.iui_forge;

import com.mojang.brigadier.CommandDispatcher;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.anningui.iui_forge.config.Paths;
import org.anningui.iui_forge.render.ImproperUIPanel;
import org.anningui.iui_forge.script.callbacks.BuiltInCallbacks;

import java.io.File;

public class IUIHandler {
    public static final IUIHandler INSTANCE = new IUIHandler();

    @SubscribeEvent
    public  void onRegisterClientCommandsEvent(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(EclipticSeasonsApi.SMODID)
                .then(Commands.literal("ui")
                        .requires((source) -> source.hasPermission(2))
                        .executes((stackCommandContext) ->
                        {
                            //ImproperUIAPI.parseAndRunFile(EclipticSeasonsApi.MODID, "snow.ui", new MenuCallbacks());

                            String var10002 = Paths.getScripts(EclipticSeasonsApi.MODID);
                            File script = new File(var10002 + "snow.ui");
                            (new ImproperUIPanel(script, new MenuCallbacks(),new BuiltInCallbacks()))
                                    .open();
                            return 0;
                        })
                )
        );
    }

}
