package com.teamtea.eclipticseasons.config.update;

import com.teamtea.eclipticseasons.config.sync.SyncType;
import com.teamtea.eclipticseasons.config.update.worker.ConfigMigration;
import com.teamtea.eclipticseasons.config.update.worker.ConfigPathRenamer;
import com.teamtea.eclipticseasons.config.update.worker.ConfigValueMover;
import lombok.Builder;
import lombok.Singular;
import net.minecraft.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public record ConfigMigrationsHolder(int minVersion, @Singular List<ConfigMigration> configMigrations) {


    public static final Map<SyncType, List<ConfigMigrationsHolder>> CONFIG_MIGRATIONS =
            Util.make(new HashMap<>(), map -> {
                map.put(SyncType.COMMON, List.of(
                        new ConfigMigrationsHolder(1, List.of(
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
                        )),
                        builder()
                                .minVersion(2)
                                .configMigration(ConfigPathRenamer.builder()
                                        .oldPath("Season.EnableLocalInfoAndCalendar")
                                        .newPath("Season.BiomeBasedLocalCalendar")
                                        .build())
                                .configMigration(ConfigPathRenamer.builder()
                                        .oldPath("Crop.GreenHouseMaxDiameter")
                                        .newPath("Crop.GreenHouseMaxRadius")
                                        .build())
                                .configMigration(ConfigPathRenamer.builder()
                                        .oldPath("Crop.UseBoxDistance")
                                        .newPath("Crop.UseSquareGreenHouseRange")
                                        .build())
                                .configMigration(ConfigPathRenamer.builder()
                                        .oldPath("Crop.CropLeavesPatch")
                                        .newPath("Crop.ForceCompatCropLeafWithering")
                                        .build())
                                .configMigration(ConfigPathRenamer.builder()
                                        .oldPath("Resource.SnowTogether")
                                        .newPath("Resource.SynchronizedBiomeSnowfall")
                                        .build())
                                .configMigration(ConfigPathRenamer.builder()
                                        .oldPath("Resource.RegionalSnowTime")
                                        .newPath("Resource.ClimateZoneSnowfallTiming")
                                        .build())
                                .build()
                ));



                map.put(SyncType.CLIENT, List.of(
                        builder()
                                .minVersion(2)
                                .configMigration(ConfigPathRenamer.builder()
                                        .oldPath("Particle.SeasonGreenhouse")
                                        .newPath("Particle.GreenHouseParticles")
                                        .build())
                                .build()
                ));
            });
}
