package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.misc.SolarTermHumidityChart;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.fml.loading.FMLLoader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


// for other mod use
public class SimpleUtil {
    public static long testTime(Runnable runnable) {
        long time = System.nanoTime();
        for (int zzz = 0; zzz < 100000 * 100; zzz++) {
            runnable.run();
        }
        long l = (System.nanoTime() - time);
        long l1 = l / 1000000;
        EclipticSeasons.logger(l1 + " ms", l);
        return l1;
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
                        CommonConfig.Season.enableInformIcon.getAsBoolean() ?
                                SimpleUtil.addSolarIconBefore(solarTerm, solarTerm.getAlternationText()) :
                                solarTerm.getAlternationText()
                ));
    }

    public static void printHumidityTable() {
        List<SimplePair<Humidity, SimplePair<Temperature, Rainfall>>> pl = new ArrayList<>();
        for (Temperature value : Temperature.values()) {
            for (Rainfall rainfall : Rainfall.collectValues()) {
                Humidity humid = Humidity.getHumid(rainfall, value);
                pl.add(SimplePair.of(humid, SimplePair.of(value, rainfall)));

            }
        }
        pl.sort(Comparator.comparing(s -> (10 - s.getKey().ordinal()) * 100 + (s.getValue().getKey().ordinal())));
        for (SimplePair<Humidity, SimplePair<Temperature, Rainfall>> p : pl) {
            EclipticSeasons.logger("|%s|%s|%s|".formatted(
                    p.getValue().getKey().getTranslation().getString(),
                    p.getValue().getValue().getTranslation().getString(),
                    p.getKey().getTranslation().getString()));
        }
        EclipticSeasons.logger("------------------------end-----------------------");
    }

    public static void printHumidityTable(Level level) {
        Registry<Biome> biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
        // List<String> list = biomes.entrySet().stream().map(e -> e.getKey().location().toString()).sorted().toList();
        List<Map.Entry<ResourceKey<Biome>, Biome>> collect = biomes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();
        List<List<String>> ss = new ArrayList<>();
        for (Map.Entry<ResourceKey<Biome>, Biome> e : collect) {
            Humidity humid = Humidity.getHumid(e.getValue().getModifiedClimateSettings().downfall(),
                    e.getValue().getModifiedClimateSettings().temperature());
            if (biomes.getHolderOrThrow(e.getKey()).is(BiomeTags.IS_OVERWORLD)
                    // biomeswevegone,biomesoplenty
                    && e.getKey().location().getNamespace().contains("minecraft")
            ) {
                List<String> s2 = new ArrayList<>();
                s2.add(Component.translatable(Util.makeDescriptionId("biome", e.getKey().location())).getString());
                s2.add(e.getKey().location().toString());
                s2.add(humid.getTranslation().getString());
                s2.add(humid.toString());
                ss.add(s2);

            }
        }
        for (List<String> s : ss) {
            EclipticSeasons.logger(
                    "|%s|%s|%s|%s|".formatted(s.get(0), s.get(1), s.get(2), s.get(3)));
        }

        EclipticSeasons.logger("------------------------end-----------------------");
    }

    public static void exportHumidityChart(Level level, String namespace) {

        Registry<Biome> biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
        // List<String> list = biomes.entrySet().stream().map(e -> e.getKey().location().toString()).sorted().toList();
        List<Map.Entry<ResourceKey<Biome>, Biome>> collect = biomes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        for (Map.Entry<ResourceKey<Biome>, Biome> e : collect) {
            if (biomes.getHolderOrThrow(e.getKey()).is(BiomeTags.IS_OVERWORLD)
                    && e.getKey().location().getNamespace().contains(namespace)
            ) {
                var biomeHolder = BiomeClimateManager.getHolder(level.registryAccess(), e.getValue());
                Humidity[] humidities = new Humidity[24];
                for (int i = 0; i < 24; i++) {
                    SolarTerm solarTerm = SolarTerm.collectValues()[i];
                    humidities[i] = Humidity.getHumid(solarTerm, biomeHolder);
                }
                String biomeName = Component.translatable(Util.makeDescriptionId("biome", e.getKey().location())).getString();
                SolarTermHumidityChart chart = new SolarTermHumidityChart(biomeName, humidities);
                if (!new File(EclipticSeasonsApi.MODID).exists()) {
                    new File(EclipticSeasonsApi.MODID).mkdir();
                }
                if (!new File(EclipticSeasonsApi.MODID + "/humid").exists()) {
                    new File(EclipticSeasonsApi.MODID + "/humid").mkdir();
                }
                if (!new File(EclipticSeasonsApi.MODID + "/humid/"+namespace).exists()) {
                    new File(EclipticSeasonsApi.MODID + "/humid/"+namespace).mkdir();
                }
                chart.exportToImage("%s/humid/%s/%s.png".formatted(EclipticSeasonsApi.MODID,namespace, biomeName), "png", 800, 400);
            }
        }
    }
}
