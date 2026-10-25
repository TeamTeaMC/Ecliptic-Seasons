
package com.teamtea.eclipticseasons.config.update.worker.special;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.config.update.worker.ConfigMigration;
import lombok.Builder;

@Builder
public record SnowfallTimingConfigMigration(
        String unifiedPath,
        String regionalPath,
        String newPath
) implements ConfigMigration {

    @Override
    public boolean apply(CommentedFileConfig config) {
        Object unifiedValue = config.get(unifiedPath);
        Object regionalValue = config.get(regionalPath);

        if (unifiedValue == null && regionalValue == null) {
            return false;
        }

        boolean unified = unifiedValue instanceof Boolean value
                && value;

        boolean regional = regionalValue instanceof Boolean value
                ? value
                : true;

        String mode = unified
                ? "UNIFIED"
                : regional ? "REGIONAL" : "CUSTOM";

        if (config.get(newPath) == null) {
            config.set(newPath, mode);
        }

        config.remove(unifiedPath);
        config.remove(regionalPath);

        return true;
    }
}
