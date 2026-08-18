package com.teamtea.eclipticseasons.config.update;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import lombok.Builder;
import net.minecraft.Util;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class ConfigMigrator {

    public static final Map<SyncType, List<ConfigMigration>> CONFIG_MIGRATIONS =
            Util.make(new HashMap<>(), map -> {
                map.put(SyncType.COMMON, List.of(
                        ConfigValueMover.builder()
                                .oldPath("Crop.ComplexGreenHouseCheck")
                                .newPath("Crop.GreenHouseCheckMode")
                                .transformer(value -> {
                                    if (value instanceof Boolean boolValue) {
                                        return boolValue ? "FULL" : "BASIC";
                                    }
                                    return null;
                                })
                                .build()
                ));
            });


    public interface ConfigMigration {
        boolean apply(CommentedFileConfig config);
    }


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


    public static void init() {
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
        CONFIG_MIGRATIONS.forEach((syncType, migrations) -> {
            Path fileName = FMLPaths.CONFIGDIR.get()
                    .resolve(syncType.configName(EclipticSeasonsApi.MODID));

            try (CommentedFileConfig config = CommentedFileConfig.builder(fileName)
                    .preserveInsertionOrder()
                    .build()) {

                config.load();

                boolean changed = false;

                for (ConfigMigration migration : migrations) {
                    boolean apply = migration.apply(config);
                    changed |= apply;
                    if (apply) {
                        EclipticSeasons.LOGGER.info("Applied config migration: {}", migration);
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