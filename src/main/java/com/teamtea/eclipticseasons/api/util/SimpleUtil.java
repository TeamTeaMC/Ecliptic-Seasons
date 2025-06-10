package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


// for other mod use
public class SimpleUtil {
    public static void testTime(Runnable runnable) {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100000 * 100; i++) {
            runnable.run();
        }
        EclipticSeasons.logger(System.currentTimeMillis() - time);
    }


    public static String getModUse(int offset) {
        try {
            return Optional.of(Class.forName(Thread.currentThread().getStackTrace()[offset].getClassName()))
                    .map(Class::getProtectionDomain)
                    .map(ProtectionDomain::getCodeSource)
                    .map(CodeSource::getLocation)
                    .map(URL::getFile)
                    .map(it -> new File(it.split("%23")[0]).getAbsolutePath())
                    .map(i -> FMLLoader.getLoadingModList().getModFiles()
                            .stream()
                            .filter(modFileInfo ->
                                    new File(modFileInfo.getFile().getFilePath().toString()).getAbsolutePath().equals(i)).findFirst().get())
                    .map(modFileInfo -> modFileInfo.getFile().getModFileInfo().moduleName())
                    .get();
        } catch (Exception e) {
        }
        return "";
    }

    public static List<String> getModsUse(int offset) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 2; i < 15; i++) {
            strings.add(getModUse(i));
        }
        return new ArrayList<>(new HashSet<>(strings));
    }

    public static <E> void warningForModWrongCalling(ResourceKey<? extends Registry<? extends E>> registryKey) {
        SimpleUtil.warningForModWrongCalling("Warning for call " + registryKey + " at wrong time.");
    }

    public static void warningForModWrongCalling(String message) {
        HashSet<String> strings = new HashSet<>(getModsUse(0));
        strings.removeIf(s -> s.equals(EclipticSeasonsApi.MODID));
        strings.removeIf(s -> s.equals("neoforge"));
        strings.removeIf(s -> s.equals("minecraft"));
        strings.removeIf(s -> s.isEmpty());
        EclipticSeasons.logger(message);
        EclipticSeasons.logger("Suspected mod: " + String.join(",", strings));
    }

    public static MutableComponent addSolarIconBefore(SolarTerm solarTerm, MutableComponent mutableComponent) {

        Style noBitstyle = mutableComponent.getStyle()
                .withFont(mutableComponent.getStyle().getFont());
        return Component.literal(solarTerm.getFontLabel())
                .withStyle(Style.EMPTY.withFont(SolarTerm.getFont()))
                .append(Component.literal(" ")
                        .withStyle(noBitstyle)
                        .append(mutableComponent))

                // .append(mutableComponent.withStyle(noBitstyle))
                ;

    }

    public static MutableComponent getSolarTermMessage(SolarTerm solarTerm) {
        return Component
                .empty()
                // .literal("\n")
                .append(Component.translatable("info.eclipticseasons.environment.solar_term.message",
                        CommonConfig.Season.enableInformIcon.get() ?
                                SimpleUtil.addSolarIconBefore(solarTerm, solarTerm.getAlternationText()) :
                                solarTerm.getAlternationText()
                ));
    }

    public static RegistryAccess getRegistryAccess(BlockEntity blockEntity) {
        RegistryAccess registryAccess = null;
        if (blockEntity.getLevel() != null) {
            registryAccess = blockEntity.getLevel().registryAccess();
        } else if (ClientCon.getUseLevel() != null) {
            registryAccess = ClientCon.getUseLevel().registryAccess();
        } else {
            if (ServerLifecycleHooks.getCurrentServer() != null)
                registryAccess = ServerLifecycleHooks.getCurrentServer().registryAccess();
        }
        return registryAccess;
    }
}
