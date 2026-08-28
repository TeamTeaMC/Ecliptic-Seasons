package com.teamtea.eclipticseasons.config.update.worker;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import lombok.Builder;

import java.util.function.Function;

@Builder
public record ConfigValueMover(
        String oldPath,
        String newPath,
        Function<Object, Object> transformer
) implements ConfigMigration {

    @Override
    public boolean apply(CommentedFileConfig config) {
        Object value = config.get(oldPath);

        if (value == null) {
            return false;
        }

        Object result = transformer.apply(value);

        if (result == null) {
            return false;
        }

        String comment = config.getComment(oldPath);

        config.set(newPath, result);

        if (comment != null) {
            config.setComment(newPath, comment);
        }

        config.remove(oldPath);

        return true;
    }
}
