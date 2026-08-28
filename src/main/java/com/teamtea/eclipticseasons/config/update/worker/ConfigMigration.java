package com.teamtea.eclipticseasons.config.update.worker;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public interface ConfigMigration {
    boolean apply(CommentedFileConfig config);
}
