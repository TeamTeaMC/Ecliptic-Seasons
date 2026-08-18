package com.teamtea.eclipticseasons.client.gui.screen;

import com.mojang.realmsclient.RealmsMainScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.jspecify.annotations.NonNull;

public class TooltipConfirmScreen extends ConfirmScreen {
    TooltipConfirmScreen(BooleanConsumer callback, Component title, Component message, Component yesButton, Component noButton) {
        super(callback, title, message, yesButton, noButton);
    }

    @Override
    protected void addButtons(@NonNull LinearLayout layout) {
        super.addButtons(layout);
        this.noButton.setTooltip(Tooltip.create(ConfigurationScreen.RESTART_NO_TOOLTIP));
    }

    public static void onDisconnect() {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.isLocalServer();
        ServerData serverdata = minecraft.getCurrentServer();
        minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
        if (flag) {
            minecraft.disconnectWithSavingScreen();
        } else {
            minecraft.disconnectWithProgressScreen();
        }

        TitleScreen titlescreen = new TitleScreen();
        if (flag) {
            minecraft.setScreenAndShow(titlescreen);
        } else if (serverdata != null && serverdata.isRealm()) {
            minecraft.setScreenAndShow(new RealmsMainScreen(titlescreen));
        } else {
            minecraft.setScreenAndShow(new JoinMultiplayerScreen(titlescreen));
        }
    }
}
