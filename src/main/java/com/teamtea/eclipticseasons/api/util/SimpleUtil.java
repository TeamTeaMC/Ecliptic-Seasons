package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;

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
    public static long testTime(Runnable runnable) {
        long time = System.nanoTime();
        for (int zzz = 0; zzz < 100000 * 100; zzz++) {
            runnable.run();
        }
        long l = (System.nanoTime() - time)/1000000;
        EclipticSeasons.logger(l);
        return l;
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
        for (int i = 2; i < 10; i++) {
            strings.add(getModUse(i));
        }
        return new ArrayList<>(new HashSet<>(strings));
    }


    public static MutableComponent addSolarIconBefore(SolarTerm solarTerm, MutableComponent mutableComponent) {
        // we need do a backup
        if(FMLEnvironment.production)
            return mutableComponent;

        Style noBitstyle = mutableComponent.getStyle()
                .withFont(mutableComponent.getStyle().getFont());
        return Component.literal("\uE010")
                .withStyle(Style.EMPTY.withFont(EclipticSeasons.rl("test")))
                .append(Component.literal(" ")
                        .withStyle(noBitstyle)
                        .append(mutableComponent))
                // .append(mutableComponent.withStyle(noBitstyle))
                ;

    }

}
