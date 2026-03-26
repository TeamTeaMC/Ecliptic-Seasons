package com.teamtea.eclipticseasons.compat;

import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.locating.IModFile;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.List;

public class Platform {

    // VersionRange.createFromVersionSpec("[0.11.0-z,)").containsVersion(new DefaultArtifactVersion("0.11.0.1"))

    public static boolean isModLoaded(String id) {
        if (FMLLoader.getCurrentOrNull() == null) return false;
        return FMLLoader.getCurrentOrNull().getLoadingModList().getModFileById(id) != null;
    }

    public static boolean isModsLoaded(List<String> ids) {
        return ids.stream().allMatch(Platform::isModLoaded);
    }

    public static boolean isPhysicalClient() {
        return FMLEnvironment.getDist().isClient();
    }

    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static boolean isProduction() {
        return FMLEnvironment.isProduction();
    }

    public static IModFile getModFile(String s) {
        IModFileInfo modFileById = ModList.get().getModFileById(s);
        if (modFileById == null) return null;
        return modFileById.getFile();
    }

}
