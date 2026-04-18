package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.ISolarTerm;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.solar.SolarTermHelper;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.misc.SolarTermHumidityChart;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.network.chat.*;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.timeline.Timelines;
import net.neoforged.fml.loading.FMLLoader;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.List;


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
                    .map(i -> FMLLoader.getCurrentOrNull().getLoadingModList().getModFiles()
                            .stream()
                            .filter(modFileInfo ->
                                    new File(modFileInfo.getFile().getFilePath().toString()).getAbsolutePath().equals(i)).findFirst().get())
                    .map(modFileInfo -> modFileInfo.getFile().getModFileInfo().getMods().getFirst().getDisplayName())
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
        strings.removeIf(String::isEmpty);
        EclipticSeasons.logger(message);
        EclipticSeasons.logger("Suspected mod: " + String.join(",", strings));
    }


    public static MutableComponent addSolarIconBefore(SolarTerm solarTerm, MutableComponent mutableComponent) {

        Style noBitstyle = mutableComponent.getStyle()
                .withFont(mutableComponent.getStyle().getFont());
        Style aDefault = DEFAULT;
        Style style = Style.EMPTY.withFont(new FontDescription.Resource(solarTerm.getIconFont()));

        return Component.literal(solarTerm.getFontLabel())
                .withStyle(style.withColor(TextColor.fromRgb(-1)))
                .append(Component.literal(" ")
                        .withStyle(aDefault)
                        .append(mutableComponent))

                // .append(mutableComponent.withStyle(noBitstyle))
                ;

    }

    private static final Style DEFAULT = Style.EMPTY.withFont(new FontDescription.Resource(Identifier.withDefaultNamespace("default")));

    public static MutableComponent addSolarIconBefore(ISolarTerm solarTerm, MutableComponent mutableComponent) {

        // Style noBitstyle = mutableComponent.getStyle()
        //         .withFont(mutableComponent.getStyle().getFont());
        Style aDefault = DEFAULT;
        Style style = Style.EMPTY.withFont(new FontDescription.Resource(solarTerm.getIconFont()));

        return Component.literal(solarTerm.getFontLabel())
                .withStyle(style.withColor(TextColor.fromRgb(-1)))
                .append(Component.literal(" ")
                        .withStyle(aDefault)
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

    public static void sendSolarTermMessage(ServerPlayer player, SolarTerm solarTerm, boolean ignoreChangeCheck) {
        ISolarTerm iSolarTerm = SolarTermHelper.isChangedAndGet(player.level(), player.blockPosition(), solarTerm, solarTerm.getLastSolarTerm(), ignoreChangeCheck);
        if (iSolarTerm != null) {
            MutableComponent translatable = Component.translatable("info.eclipticseasons.environment.solar_term.message",
                    CommonConfig.Season.enableInformIcon.get() ?
                            SimpleUtil.addSolarIconBefore(iSolarTerm, iSolarTerm.getAlternationText()) :
                            solarTerm.getAlternationText()
            );
            player.sendSystemMessage(translatable, false);
        }
    }


    public static void printHumidityTable(Level level) {
        Registry<Biome> biomes = level.registryAccess().lookupOrThrow(Registries.BIOME);
        // List<String> list = biomes.entrySet().stream().map(e -> e.getKey().identifier().toString()).sorted().toList();
        List<Map.Entry<ResourceKey<Biome>, Biome>> collect = biomes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();
        List<List<String>> ss = new ArrayList<>();
        List<SimplePair<Identifier, Humidity>> pairs = new ArrayList<>();

        for (Map.Entry<ResourceKey<Biome>, Biome> e : collect) {
            Humidity humid = Humidity.getHumid(e.getValue().getModifiedClimateSettings().downfall(),
                    e.getValue().getModifiedClimateSettings().temperature());
            if (biomes.getOrThrow(e.getKey()).is(BiomeTags.IS_OVERWORLD)
                    // biomeswevegone,biomesoplenty,natures_spirit, minecraft
                    && e.getKey().identifier().getNamespace().contains("natures_spirit")
            ) {
                List<String> s2 = new ArrayList<>();
                s2.add(Component.translatable(Util.makeDescriptionId("biome", e.getKey().identifier())).getString());
                s2.add(e.getKey().identifier().toString());
                s2.add(humid.getTranslation().getString());
                s2.add(humid.toString());
                ss.add(s2);
                pairs.add(SimplePair.of(e.getKey().identifier(), humid));
            }
        }
        pairs.sort(Comparator.comparing(SimplePair::getValue));
        pairs = pairs.reversed();
        // for (List<String> s : ss) {
        //     EclipticSeasons.logger(
        //             "|%s|%s|%s|%s|".formatted(s.get(0), s.get(1), s.get(2), s.get(3)));
        // }
        StringBuilder stringBuilder = new StringBuilder("\n");
        for (SimplePair<Identifier, Humidity> pair : pairs) {
            stringBuilder.append(
                    "\n|%s|%s|%s|%s|".formatted(Component.translatable(Util.makeDescriptionId("biome", pair.getKey())).getString(),
                            pair.getKey(), pair.getValue().getTranslation().getString(), pair.getValue().getName()));
        }
        EclipticSeasons.logger(stringBuilder);
        EclipticSeasons.logger("------------------------end-----------------------");
    }

    public static void exportHumidityChart(Level level, String namespace) {
        printHumidityTable(level);
        Registry<Biome> biomes = level.registryAccess().lookupOrThrow(Registries.BIOME);
        // List<String> list = biomes.entrySet().stream().map(e -> e.getKey().identifier().toString()).sorted().toList();
        List<Map.Entry<ResourceKey<Biome>, Biome>> collect = biomes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        for (Map.Entry<ResourceKey<Biome>, Biome> e : collect) {
            if (biomes.getOrThrow(e.getKey()).is(BiomeTags.IS_OVERWORLD)
                    && e.getKey().identifier().getNamespace().contains(namespace)
            ) {
                var biomeHolder = BiomeClimateManager.getHolder(level.registryAccess(), e.getValue());
                double[] humidities = new double[24];
                for (int i = 0; i < 24; i++) {
                    SolarTerm solarTerm = SolarTerm.collectValues()[i];
                    humidities[i] = EclipticUtil.getHumidityConstantFloat(solarTerm, biomeHolder, !level.isClientSide());
                }
                String biomeName = Component.translatable(Util.makeDescriptionId("biome", e.getKey().identifier())).getString();
                SolarTermHumidityChart chart = new SolarTermHumidityChart(biomeName, humidities);
                if (!new File(EclipticSeasonsApi.MODID).exists()) {
                    new File(EclipticSeasonsApi.MODID).mkdir();
                }
                if (!new File(EclipticSeasonsApi.MODID + "/humid").exists()) {
                    new File(EclipticSeasonsApi.MODID + "/humid").mkdir();
                }
                if (!new File(EclipticSeasonsApi.MODID + "/humid/" + namespace).exists()) {
                    new File(EclipticSeasonsApi.MODID + "/humid/" + namespace).mkdir();
                }
                chart.exportToImage("%s/humid/%s/%s.png".formatted(EclipticSeasonsApi.MODID, namespace, biomeName), "png", 800, 400);
            }
        }
    }

    public static <T> List<Holder<T>> holderSetToList(HolderSet<T> holders) {
        List<Holder<T>> holderList = new ArrayList<>();
        for (int i = 0; i < holders.size(); i++) {
            holderList.add(holders.get(i));
        }
        return holderList;
    }


    // TODO 元胞自动机
    // https://zhuanlan.zhihu.com/p/621070746
    private static int getWorldgenNoise(ServerPlayer player, ServerLevel level, BlockPos blockPos) {
        int x = QuartPos.fromBlock(blockPos.getX());
        int y = QuartPos.fromBlock(blockPos.getY());
        int z = QuartPos.fromBlock(blockPos.getZ());
        Climate.TargetPoint targetPoint = level.getChunkSource().randomState().sampler().sample(x, y, z);
        float c = Climate.unquantizeCoord(targetPoint.continentalness());
        float e = Climate.unquantizeCoord(targetPoint.erosion());
        float t = Climate.unquantizeCoord(targetPoint.temperature());
        float h = Climate.unquantizeCoord(targetPoint.humidity());
        float w = Climate.unquantizeCoord(targetPoint.weirdness());

        OverworldBiomeBuilder overworldBiomeBuilder = new OverworldBiomeBuilder();

        return 0;
    }


    public static float getTimeOfDay(Level level) {
        // if (false) {
        //     double d0 = Mth.frac((double) level.getDefaultClockTime() / 24000.0 - 0.25);
        //     double d1 = 0.5 - Math.cos(d0 * Math.PI) / 2.0;
        //     return (float) (d0 * 2.0 + d1) / 3.0F;
        // }
        Optional<Holder<@NonNull WorldClock>> worldClockHolder = level.dimensionType().defaultClock();
        if (worldClockHolder.isEmpty()) return 0;
        var timelines = level.dimensionType().timelines()
                .stream().filter((t) -> t.value().clock() == worldClockHolder.get()).findFirst();
        if (timelines.isEmpty()) return 0;
        Holder<@NonNull Timeline> timelineHolder = timelines.get();
        long defaultClockTime = level.getDefaultClockTime();
        long periodTicks = timelineHolder.value().periodTicks().orElse(0);
        if (periodTicks == 0) return 0;
        // return ((float) (defaultClockTime % periodTicks)) / (float) periodTicks;
        double d0 = Mth.frac((double) defaultClockTime / periodTicks - 0.25);
        double d1 = 0.5 - Math.cos(d0 * Math.PI) / 2.0;
        return (float) (d0 * 2.0 + d1) / 3.0F;
    }


    public static long getDayTick(Level level) {
        return level.getDefaultClockTime();
    }

    public static int getMCDay(Level level) {
        int currentDay = level
                .registryAccess()
                .get(Timelines.OVERWORLD_DAY)
                .map(timeline -> timeline.value().getPeriodCount(level.clockManager()))
                .orElse(0);
        return currentDay;
    }
}
