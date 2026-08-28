package com.teamtea.eclipticseasons.config.update.worker;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import lombok.Builder;

import java.util.function.Function;

@Builder
public record ConfigValueTransformer(
        String path,
        Function<Object, Object> transformer
) implements ConfigMigration {

    @Override
    public boolean apply(CommentedFileConfig config) {
        Object value = config.get(path);

        if (value == null) {
            return false;
        }

        Object result = transformer.apply(value);

        if (result == null || result.equals(value)) {
            return false;
        }

        config.set(path, result);

        return true;
    }
}
