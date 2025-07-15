package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.ISolarTerm;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.solar.SolarTermHelper;
import com.teamtea.eclipticseasons.common.core.solar.SolarTermHumidityChart;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;


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
        strings.removeIf(String::isEmpty);
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

    public static MutableComponent addSolarIconBefore(ISolarTerm solarTerm, MutableComponent mutableComponent) {

        // Style noBitstyle = mutableComponent.getStyle()
        //         .withFont(mutableComponent.getStyle().getFont());
        Style aDefault = Style.EMPTY.withFont(new ResourceLocation("default"));
        Style style = Style.EMPTY.withFont(solarTerm.getIconFont());

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
                        CommonConfig.Season.enableInformIcon.get() ?
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
                double[] humidities = new double[24];
                for (int i = 0; i < 24; i++) {
                    SolarTerm solarTerm = SolarTerm.collectValues()[i];
                    humidities[i] = EclipticUtil.getHumidityConstantFloat(solarTerm, biomeHolder, !level.isClientSide());
                }
                String biomeName = Component.translatable(Util.makeDescriptionId("biome", e.getKey().location())).getString();
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
}
