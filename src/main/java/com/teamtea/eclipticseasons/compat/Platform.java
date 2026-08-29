package com.teamtea.eclipticseasons.compat;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;


import java.util.List;
import java.util.Optional;

public class Platform {

    public static boolean isModLoaded(String id) {
        return FMLLoader.getLoadingModList().getModFileById(id) != null;
    }

    public static String getModName(String id, final int index) {
        return Optional.of(FMLLoader.getLoadingModList())
                .map(c -> c.getModFileById(id))
                .map(ModFileInfo::getMods)
                .filter(list -> index >= 0 && index < list.size())
                .map(list -> list.get(index))
                .map(IModInfo::getDisplayName)
                .orElse("");
    }

    public static boolean isModsLoaded(List<String> ids) {
        return ids.stream().allMatch(Platform::isModLoaded);
    }

    public static boolean isPhysicalClient() {
        return FMLEnvironment.dist.isClient();
    }

    // public static MinecraftServer getServer() {
    //     return ServerLifecycleHooks.getCurrentServer();
    // }

    public static boolean isProduction() {
        return FMLEnvironment.production;
    }

    public static IModFile getModFile(String s) {
        IModFileInfo modFileById = ModList.get().getModFileById(s);
        if (modFileById == null) return null;
        return modFileById.getFile();
    }

    public static boolean isVersionSatisfied(String modId, String require) {
        return Optional.of(FMLLoader.getLoadingModList())
                .map(c -> c.getModFileById(modId))
                .map(ModFileInfo::getMods)
                .filter(modInfoList -> !modInfoList.isEmpty())
                .map(modInfo -> modInfo.get(0))
                .map(IModInfo::getVersion)
                .map(currentVersion -> {
                    if (!require.startsWith("[") && !require.startsWith("(")) {
                        return currentVersion.compareTo(new DefaultArtifactVersion(require)) >= 0;
                    }
                    try {
                        return VersionRange.createFromVersionSpec(require).containsVersion(currentVersion);
                    } catch (InvalidVersionSpecificationException exception) {
                        throw new IllegalArgumentException("Invalid version requirement: " + require, exception);
                    }
                })
                .orElse(false);
    }
}
