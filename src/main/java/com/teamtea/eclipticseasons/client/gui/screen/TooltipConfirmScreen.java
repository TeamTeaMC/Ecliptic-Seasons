package com.teamtea.eclipticseasons.client.gui.screen;

import com.mojang.realmsclient.RealmsMainScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public class TooltipConfirmScreen extends ConfirmScreen {
    TooltipConfirmScreen(BooleanConsumer callback, Component title, Component message, Component yesButton, Component noButton) {
        super(callback, title, message, yesButton, noButton);
    }

    @Override
    protected void addButtons(int y) {
        super.addButtons(y);
        this.noButton = (ConfigurationScreen.RESTART_NO_TOOLTIP);
    }

    public static void onDisconnect() {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.isLocalServer();
        ServerData serverdata = minecraft.getCurrentServer();
        minecraft.level.disconnect();
        if (flag) {
            minecraft.disconnect(new GenericMessageScreen(Component.translatable("menu.savingLevel")), false);
        } else {
            minecraft.disconnect();
        }

        TitleScreen titlescreen = new TitleScreen();
        if (flag) {
            minecraft.setScreen(titlescreen);
        } else if (serverdata != null && serverdata.isRealm()) {
            minecraft.setScreen(new RealmsMainScreen(titlescreen));
        } else {
            minecraft.setScreen(new JoinMultiplayerScreen(titlescreen));
        }
    }
}
