package com.teamtea.eclipticseasons.client.gui.screen.config;

import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ConfigScreenSession;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ESConfigScreenSession;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModContainer;

public interface ConfigScreenDefinition {
    String modId();

    default ConfigScreenText text() {
        return new ConfigScreenText(Component.translatable("options.title"));
    }

    void initialize(ConfigScreenContext context);

    default ConfigScreenSession createSession(ConfigScreenContext context) {
        return new ESConfigScreenSession(context.configs());
    }

    default Screen create(Minecraft minecraft, Screen parent) {
        return new ESModConfigScreen(minecraft,parent,this);
    }
}
