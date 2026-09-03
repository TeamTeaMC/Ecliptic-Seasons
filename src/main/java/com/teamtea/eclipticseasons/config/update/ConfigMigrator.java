package com.teamtea.eclipticseasons.config.update;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.config.update.worker.ConfigMigration;
import com.teamtea.eclipticseasons.mixin.EclipticSeasonsMixinPlugin;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigMigrator {
    public static final int CURRENT_CONFIG_VERSION = 2;

    public static void init() {
        CommentedFileConfig mixinRecord = EclipticSeasonsMixinPlugin.PreloadedConfig.getConfig();
        int oldVersion;
        if (mixinRecord != null) {
            oldVersion = mixinRecord.getIntOrElse("version", 0);
            if (oldVersion != CURRENT_CONFIG_VERSION) {
                mixinRecord.set("version", CURRENT_CONFIG_VERSION);
            }
        } else {
            oldVersion = CURRENT_CONFIG_VERSION;
        }
        // SeasonalSimulationLevel level = SeasonalSimulationLevel.CUSTOM;
        // try (CommentedFileConfig config = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get()
        //                 .resolve(SyncType.COMMON.configName(EclipticSeasonsApi.MODID)))
        //         .preserveInsertionOrder()
        //         .build()) {
        //     config.load();
        //     Object o = config.get(CommonConfig.Season.seasonalSimulationLevel.getPath());
        //     if (o != null)
        //         level = SeasonalSimulationLevel.valueOf(o.toString().toUpperCase(Locale.ROOT));
        // } catch (Exception _) {
        // }
        //
        // SeasonalSimulationLevel finalLevel = level;
        ConfigMigrationsHolder.CONFIG_MIGRATIONS.forEach((syncType, migrations) -> {
            Path fileName = FMLPaths.CONFIGDIR.get()
                    .resolve(syncType.configName(EclipticSeasonsApi.MODID));
            if (!Files.isRegularFile(fileName)) return;

            try (CommentedFileConfig config = CommentedFileConfig.builder(fileName)
                    .preserveInsertionOrder()
                    .build()) {

                config.load();

                boolean changed = false;

                for (ConfigMigrationsHolder holder : migrations) {
                    if (oldVersion >= holder.minVersion()) continue;
                    for (ConfigMigration migration : holder.configMigrations()) {
                        boolean apply = migration.apply(config);
                        changed |= apply;
                        if (apply) {
                            EclipticSeasons.LOGGER.info("Applied config migration: {}", migration);
                        }
                    }
                }

                // changed |= SimulationUpdater.initialCheck(finalLevel, syncType, config);

                if (changed) {
                    config.save();
                }
            }
        });
    }
}