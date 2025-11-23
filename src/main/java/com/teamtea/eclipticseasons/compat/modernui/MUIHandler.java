package com.teamtea.eclipticseasons.compat.modernui;

import com.mojang.brigadier.CommandDispatcher;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import icyllis.modernui.mc.MuiModApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

public class MUIHandler {
    public static final MUIHandler INSTANCE = new MUIHandler();

    @SubscribeEvent
    public  void onRegisterClientCommandsEvent(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(EclipticSeasonsApi.SMODID)
                .then(Commands.literal("ui")
                        .requires((source) -> source.hasPermission(2))
                        .executes((stackCommandContext) ->
                        {
                            Screen screen = MuiModApi.get().createScreen(new DataFragment());
                            //Minecraft.getInstance().execute(()->{
                            //    Minecraft.getInstance().setScreen(screen);
                            //});
                            Minecraft.getInstance().setScreen(screen);
                            return 0;
                        })
                )
        );
    }

}
