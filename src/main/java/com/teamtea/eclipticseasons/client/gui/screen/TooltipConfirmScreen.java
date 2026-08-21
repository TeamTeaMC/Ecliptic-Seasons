package com.teamtea.eclipticseasons.client.gui.screen;

import com.mojang.realmsclient.RealmsMainScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;

public class TooltipConfirmScreen extends ConfirmScreen {
    public static final Component GAME_RESTART_YES = Component.translatable("menu.quit");
    private static final String LANG_PREFIX = "eclipticseasons.configuration.uitext.";
    public static final Component RESTART_NO_TOOLTIP = Component.translatable(LANG_PREFIX + "restart.return.tooltip").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
    public static final Component RESTART_NO = Component.translatable(LANG_PREFIX + "restart.return");
    public static final Component GAME_RESTART_MESSAGE = Component.translatable(LANG_PREFIX + "restart.game.text");
    public static final Component SERVER_RESTART_MESSAGE = Component.translatable(LANG_PREFIX + "restart.server.text");
    public static final Component SERVER_RESTART_TITLE = Component.translatable(LANG_PREFIX + "restart.server.title");
    public static final Component GAME_RESTART_TITLE = Component.translatable(LANG_PREFIX + "restart.game.title");
    static final Component RETURN_TO_MENU = Component.translatable("menu.returnToMenu");

    TooltipConfirmScreen(BooleanConsumer callback, Component title, Component message, Component yesButton, Component noButton) {
        super(callback, title, message, yesButton, noButton);
    }


    @Override
    protected void addButtons(int y) {
        super.addButtons(y);
        this.noButton = (RESTART_NO_TOOLTIP);
    }

    public static void onDisconnect() {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.isLocalServer();
        boolean flag1 = minecraft.isConnectedToRealms();
        minecraft.level.disconnect();
        if (flag) {
            minecraft.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
        } else {
            minecraft.clearLevel();
        }

        TitleScreen titlescreen = new TitleScreen();
        if (flag) {
            minecraft.setScreen(titlescreen);
        } else if (flag1) {
            minecraft.setScreen(new RealmsMainScreen(titlescreen));
        } else {
            minecraft.setScreen(new JoinMultiplayerScreen(titlescreen));
        }
    }
}
