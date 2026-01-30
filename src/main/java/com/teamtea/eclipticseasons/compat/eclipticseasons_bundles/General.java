package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;

import com.google.gson.Gson;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.compat.Platform;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class General {
    public static ForgeConfigSpec COMMON_CONFIG;

    // public static ForgeConfigSpec.ConfigValue<String> order;
    public static Map<String, SimplePair<ForgeConfigSpec.BooleanValue, BundleConfig>> enableList = new HashMap<>();

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("Compat settings");
        var basePath = Platform.getModFile(EclipticSeasonsBundles.MODID).findResource("resourcepacks");
        try (var fileList = Files.list(basePath)) {
            bsp:
            for (Path path : fileList.toList()) {
                String packageName = path.getFileName().toString();


                Path configPath = path.resolve("bundle.cfg");
                if (Files.notExists(configPath)) continue;

                try {
                    String json = Files.readString(configPath);
                    if (json.isEmpty()) continue;

                    BundleConfig config = new Gson().fromJson(json, BundleConfig.class);

                    System.out.println("Loaded config for " + config.getId());


                    for (String require : config.getRequire()) {
                        if (!Platform.isModLoaded(require)) {
                            continue bsp;
                        }
                    }

                    ForgeConfigSpec.BooleanValue enable = COMMON_BUILDER
                            .comment(String.format("Enable compat package %s", config.getId()))
                            //.translation(packageName)
                            .define(config.getId(), config.isEnable());
                    enableList.put(packageName, SimplePair.of(enable, config));

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (com.google.gson.JsonSyntaxException e) {
                    System.err.println("Invalid JSON in " + configPath + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // If any skip, then null
        try {
            COMMON_CONFIG = COMMON_BUILDER.build();
        } catch (java.lang.IllegalStateException e) {

        }


    }

}
