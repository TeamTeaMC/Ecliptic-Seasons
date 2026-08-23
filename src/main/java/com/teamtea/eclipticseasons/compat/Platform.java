package com.teamtea.eclipticseasons.compat;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IModFile;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.List;
import java.util.Optional;

public class Platform {

    // VersionRange.createFromVersionSpec("[0.11.0-z,)").containsVersion(new DefaultArtifactVersion("0.11.0.1"))

    public static boolean isModLoaded(String id) {
        if (FMLLoader.getCurrentOrNull() == null) return false;
        return FMLLoader.getCurrentOrNull().getLoadingModList().getModFileById(id) != null;
    }

    public static String getModName(String id, int index) {
        return FMLLoader.getLoadingModList().getModFileById(id).getMods().get(index).getDisplayName();
    }

    public static boolean isModsLoaded(List<String> ids) {
        return ids.stream().allMatch(Platform::isModLoaded);
    }

    public static boolean isPhysicalClient() {
        return FMLEnvironment.getDist().isClient();
    }

    // public static MinecraftServer getServer() {
    //     return ServerLifecycleHooks.getCurrentServer();
    // }

    public static boolean isProduction() {
        return FMLEnvironment.isProduction();
    }

    public static IModFile getModFile(String s) {
        IModFileInfo modFileById = ModList.get().getModFileById(s);
        if (modFileById == null) return null;
        return modFileById.getFile();
    }

    public static boolean isVersionSatisfied(String modId, String require) {
        return Optional.ofNullable(FMLLoader.getCurrentOrNull())
                .map(FMLLoader::getLoadingModList)
                .map(c -> c.getModFileById(modId))
                .map(ModFileInfo::getMods)
                .filter(modInfoList -> !modInfoList.isEmpty())
                .map(List::getFirst)
                .map(IModInfo::getVersion)
                .map(currentVersion -> {
                    ArtifactVersion required = new DefaultArtifactVersion(require);
                    return currentVersion.compareTo(required) >= 0;
                })
                .orElse(false);
    }

}
