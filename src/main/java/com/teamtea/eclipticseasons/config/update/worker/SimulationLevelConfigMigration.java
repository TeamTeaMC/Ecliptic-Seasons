package com.teamtea.eclipticseasons.config.update.worker;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import lombok.Builder;

import java.util.Locale;

@Builder
public record SimulationLevelConfigMigration(
        String path,
        SeasonalSimulationLevel requiredLevel
) implements ConfigMigration {

    @Override
    public boolean apply(CommentedFileConfig config) {
        Object value = config.get("Core.SeasonalSimulationLevel");
        if (value == null) return false;

        SeasonalSimulationLevel level;
        try {
            level = SeasonalSimulationLevel.valueOf(value.toString().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return false;
        }

        if (level == SeasonalSimulationLevel.CUSTOM) return false;

        boolean enabled = level.enable(requiredLevel);
        Object current = config.get(path);
        if (current instanceof Boolean bool && bool == enabled) return false;

        config.set(path, enabled);
        return true;
    }
}
