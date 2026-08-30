package com.teamtea.eclipticseasons.client.gui.screen.config.builtin;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigRegistry;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigScreenDefinition;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigScreenText;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ConfigScreenSession;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ESConfigScreenSession;
import com.teamtea.eclipticseasons.client.gui.screen.config.source.MixinConfigEntrySource;
import com.teamtea.eclipticseasons.client.gui.screen.config.source.ModConfigEntrySource;
import com.teamtea.eclipticseasons.mixin.EclipticSeasonsMixinPlugin;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;

import java.util.List;

public class ESConfigScreenDefinition implements ConfigScreenDefinition {
    public static final ESConfigScreenDefinition INSTANCE = new ESConfigScreenDefinition();
    protected Component allSection = Component.translatable("eclipticseasons.options.all");

    protected ConfigRegistry registry = new ConfigRegistry();

    protected ESConfigScreenDefinition() {
    }

    @Override
    public String modId() {
        return EclipticSeasonsApi.MODID;
    }

    @Override
    public ConfigScreenText text() {
        return new ConfigScreenText(
                Component.translatable("eclipticseasons.options.title"),
                Component.translatable("eclipticseasons.options.search"),
                Component.translatable("eclipticseasons.options.search.no_result"),
                Component.translatable("eclipticseasons.options.configure_in_classic_screen"));
    }

    @Override
    public void initialize(ConfigScreenContext context) {
        for (ConfigCategory category : ConfigCategory.values()) {
            context.registerCategory(category);
        }

        List<ModConfig> configs = ModConfigs.getModConfigs(EclipticSeasonsApi.MODID);
        context.registerConfigs(configs);
        registry.apply(context);
        context.addSource(new ModConfigEntrySource(ConfigCategory.ALL, allSection, configs));
        context.addSource(new MixinConfigEntrySource(
                ConfigCategory.ADVANCED,
                EclipticSeasonsMixinPlugin.PreloadedConfig.getConfig()));
    }

    @Override
    public ConfigScreenSession createSession(ConfigScreenContext context) {
        return new ESConfigScreenSession(
                context.configs(),
                EclipticSeasonsMixinPlugin.PreloadedConfig.getConfig(),
                SyncType.MIXINS.configName(EclipticSeasonsApi.MODID));
    }

    public ConfigRegistry registry() {
        return registry;
    }
}
