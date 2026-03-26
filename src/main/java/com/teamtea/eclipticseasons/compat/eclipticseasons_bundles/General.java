package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.Platform;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class General {
    public record PackController(
            ModConfigSpec.BooleanValue enable,
            ModConfigSpec.BooleanValue priorityLoading,
            BundleConfig config
    ) {
        public static PackController of(ModConfigSpec.BooleanValue enable,
                                        ModConfigSpec.BooleanValue priorityLoading,
                                        BundleConfig config) {
            return new PackController(enable, priorityLoading, config);
        }
    }

    public static ModConfigSpec COMMON_CONFIG;

    // public static ModConfigSpec.ConfigValue<String> order;
    public static Map<String, PackController> enableList = new HashMap<>();

    static {
        DynamicOps<JsonElement> dynamicops = JsonOps.INSTANCE;
        ArtifactVersion minecraft = Platform.getModFile("minecraft").getModFileInfo().getMods().get(0).getVersion();
        LangUtil.tryLoadLang(EclipticSeasonsBundles.MODID, false);

        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

        //COMMON_BUILDER.comment("Compat settings");
        var basePath = Platform.getModFile(EclipticSeasonsBundles.MODID).getContents().getPrimaryPath().getFileName().resolve("resourcepacks");
        try (var fileList = Files.list(basePath)) {
            bsp:
            for (Path path : fileList.toList()) {
                String packageName = path.getFileName().toString();


                Path configPath = path.resolve("bundle.cfg");
                if (Files.notExists(configPath)) continue;

                try {
                    String json = Files.readString(configPath);
                    if (json.isEmpty()) continue;

                    JsonElement jsonElement = GsonHelper.parse(json);
                    BundleConfig config = BundleConfig.CODEC
                            .parse(dynamicops, jsonElement)
                            .resultOrPartial(x ->
                                    {
                                        String formatted = ("Invalid JSON in " + configPath);
                                        EclipticSeasons.LOGGER.warn(formatted);
                                    }
                            ).orElse(null);
                    if (config == null) continue bsp;

                    System.out.println("Loaded config for " + config.getId());


                    if (!config.getRequire().isEmpty()) {
                        boolean anyLoaded = false;

                        for (String require : config.getRequire()) {
                            if (Platform.isModLoaded(require)) {
                                anyLoaded = true;
                                if (!config.isRequireAll()) break;
                            } else {
                                if (config.isRequireAll()) {
                                    continue bsp;
                                }
                            }
                        }

                        if (!config.isRequireAll() && !anyLoaded) {
                            continue bsp;
                        }
                    }


                    if (!config.getMcVersion().isEmpty()) {
                        boolean matched = config.getMcVersion().stream().anyMatch(spec -> {
                            try {
                                if (!spec.contains(")") && !spec.contains("]") && !spec.contains("[")) {
                                    spec = "[%s,%s]".formatted(spec, spec);
                                }
                                return VersionRange.createFromVersionSpec(spec)
                                        .containsVersion(minecraft);
                            } catch (Exception ignored) {
                                return false;
                            }
                        });
                        if (!matched) continue bsp;
                    }


                    COMMON_BUILDER.comment(LangUtil.parseI18n(config.getDescription().isEmpty() ?
                            EclipticSeasons.erl(EclipticSeasonsBundles.MODID, config.getId()).toLanguageKey("pack_description") :
                            config.getDescription()
                    ));
                    COMMON_BUILDER.translation(
                            EclipticSeasons.erl(EclipticSeasonsBundles.MODID, config.getId()).toLanguageKey("pack")
                    ).push(config.getId());
                    var enable = COMMON_BUILDER
                            .comment(String.format("Enable compat package %s", config.getId()))
                            .translation(packageName)
                            .define("Enable", config.isEnable());

                    var priorityLoading = COMMON_BUILDER
                            .comment("This package will be loaded first.")
                            //.translation(packageName)
                            .define("PriorityLoading", config.isTop());
                    enableList.put(packageName, PackController.of(enable, priorityLoading, config));
                    COMMON_BUILDER.pop();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (com.google.gson.JsonParseException e) {
                    System.err.println("Invalid JSON in " + configPath + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // If any skip, then null
        try {
            COMMON_CONFIG = COMMON_BUILDER.build();
        } catch (IllegalStateException e) {

        }


    }

}
