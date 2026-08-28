package com.teamtea.eclipticseasons.config.update.worker;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import lombok.Builder;

@Builder
public record ConfigPathRenamer(
        String oldPath,
        String newPath
) implements ConfigMigration {

    @Override
    public boolean apply(CommentedFileConfig config) {
        Object value = config.get(oldPath);

        if (value == null) {
            return false;
        }

        String comment = config.getComment(oldPath);

        config.set(newPath, value);

        if (comment != null) {
            config.setComment(newPath, comment);
        }

        config.remove(oldPath);

        return true;
    }
}
