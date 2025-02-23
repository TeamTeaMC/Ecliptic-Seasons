package com.teamtea.eclipticseasons.compat;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.forgespi.locating.IModFile;

import java.util.List;

public class Platform {

    public static boolean isModLoaded(String id) {
        return FMLLoader.getLoadingModList().getModFileById(id) != null;
    }

    public static boolean isModsLoaded(List<String> ids) {
        return ids.stream().allMatch(Platform::isModLoaded);
    }

    public static boolean isPhysicalClient() {
        return FMLEnvironment.dist.isClient();
    }

    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static boolean isProduction() {
        return FMLEnvironment.production;
    }

    public static IModFile getModFile(String s) {
        return ModList.get().getModFileById(s).getFile();
    }

}
