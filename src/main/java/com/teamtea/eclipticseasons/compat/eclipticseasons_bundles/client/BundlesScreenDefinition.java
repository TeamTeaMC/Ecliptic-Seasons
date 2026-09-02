package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles.client;

import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigScreenDefinition;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigScreenText;
import com.teamtea.eclipticseasons.client.gui.screen.config.builtin.ESConfigScreenDefinition;
import com.teamtea.eclipticseasons.client.gui.screen.config.source.ModConfigEntrySource;
import com.teamtea.eclipticseasons.compat.eclipticseasons_bundles.EclipticSeasonsBundles;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.config.ModConfig;

public class BundlesScreenDefinition implements ConfigScreenDefinition {

    public static final BundlesScreenDefinition INSTANCE =
            new BundlesScreenDefinition();

    protected ConfigCategory all = ConfigCategory.create(
            modId(),
            "ALL",
            0,
            false
    );

    @Override
    public String modId() {
        return EclipticSeasonsBundles.MODID;
    }

    @Override
    public ConfigScreenText text() {
        return new ConfigScreenText(Component.translatable(modId() + ".options.title"));
    }

    @Override
    public void initialize(ConfigScreenContext context) {
        context.registerCategory(all);
        context.registerConfigs(ESConfigScreenDefinition.getModConfigs(modId()));

        context.addSource(new ModConfigEntrySource(
                all,
                Component.translatable(modId() + ".options.all"),
                context.configs(),
                true
        ) {
            @Override
            protected Component resolveSection(ModConfig owner, String name) {
                String key = "pack." + owner.getModId() + "." + name;
                return Component.translatable(key);
            }
        });
    }
}
