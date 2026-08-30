package com.teamtea.eclipticseasons.client.gui.screen.config.source;

import com.electronwill.nightconfig.core.Config;
import com.teamtea.eclipticseasons.client.gui.screen.ConfigScreenContext;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.entry.callback.SimpleBoolEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.callback.CallbackBooleanEntry;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import com.teamtea.eclipticseasons.config.util.RestartTypeUtil;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public class MixinConfigEntrySource implements ConfigEntrySource {
    protected ConfigCategory category;
    protected Config config;

    public MixinConfigEntrySource(ConfigCategory category, Config config) {
        this.category = category;
        this.config = config;
    }

    @Override
    public void load(ConfigScreenContext context) {
        if (context.containsCategory(category)) collect(config, "", context);
    }

    protected void collect(Config config, String path, ConfigScreenContext context) {
        for (Config.Entry entry : config.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String fullPath = path.isEmpty() ? key : path + "." + key;

            if (value instanceof Config child) {
                // if ("compat".equals(path)) addCompatToggle(key, child, fullPath, context);
                collect(child, fullPath, context);
            } else if (value instanceof Boolean) {
                context.add(category, moduleTitle(fullPath),
                        new SimpleBoolEntry(key, entry::getValue, changed -> config.set(key, changed)));
            }
        }
    }

    protected void addCompatToggle(
            String moduleId,
            Config module,
            String fullPath,
            ConfigScreenContext context
    ) {
        context.addFirst(category, moduleTitle(fullPath + ".enabled"),
                new CallbackBooleanEntry(
                        compatName(moduleId),
                        () -> anyEnabled(module),
                        enabled -> setAll(module, enabled),
                        () -> true)
                        .setSyncType(SyncType.MIXINS)
                        .setRestartType(RestartTypeUtil.RestartType.GAME));
    }

    protected boolean anyEnabled(Config config) {
        for (Config.Entry entry : config.entrySet()) {
            if (entry.getValue() instanceof Boolean enabled && enabled) return true;
            if (entry.getValue() instanceof Config child && anyEnabled(child)) return true;
        }
        return false;
    }

    protected void setAll(Config config, boolean enabled) {
        for (Config.Entry entry : config.entrySet()) {
            if (entry.getValue() instanceof Boolean) config.set(entry.getKey(), enabled);
            else if (entry.getValue() instanceof Config child) setAll(child, enabled);
        }
    }

    protected Component moduleTitle(String fullPath) {
        String[] parts = fullPath.split("\\.");
        String[] module = Arrays.copyOf(parts, Math.max(0, parts.length - 1));
        if (module.length == 0) return Component.literal("Mixin");
        if (module.length == 1) return Component.literal("Mixin - " + capitalize(module[0]));
        if ("compat".equals(module[0])) {
            return Component.literal("Mixin - Compat: " + compatName(module[1]));
        }
        return Component.literal("Mixin - " + capitalize(module[0]) + ": " + capitalize(module[1]));
    }

    protected String compatName(String moduleId) {
        return switch (moduleId) {
            case "neoforge" -> "NeoForge";
            case "sodium" -> "Sodium";
            case "iris" -> "Iris";
            case "distanthorizons" -> "Distant Horizons";
            case "fabric_renderer_indigo" -> "Fabric Renderer Indigo";
            case "voxy" -> "Voxy";
            case "optfine" -> "Optfine";
            default -> moduleId;
        };
    }

    protected String capitalize(String value) {
        if (value == null || value.isEmpty()) return "Mixin";
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
