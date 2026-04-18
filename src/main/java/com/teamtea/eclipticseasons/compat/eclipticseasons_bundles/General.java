package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.Platform;
import net.minecraft.util.GsonHelper;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        // COMMON_BUILDER.comment("Compat settings");

        JarContents jarContents = Platform.getModFile(EclipticSeasonsBundles.MODID).getContents();

        Set<String> topDirs = new HashSet<>();
        jarContents.visitContent("resourcepacks", (relativePath, resource) -> {
            String path = relativePath.replace("\\", "/");
            if (path.endsWith("/bundle.cfg")) {
                String[] parts= path.substring("resourcepacks/".length()).split("/");
                if (parts.length >= 2) {
                    topDirs.add(parts[0]);
                }
            }
        });

        {
            bsp:
            for (String packageName : topDirs) {
                String configPath = "resourcepacks/"+packageName + "/bundle.cfg";
                if (!jarContents.containsFile(configPath)) continue;
                try {
                    InputStream inputStream = jarContents.openFile(configPath);
                    if (inputStream == null) continue;
                    String json = new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    inputStream.close();
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
                    if (config == null) continue;

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
                            continue;
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
                        if (!matched) continue;
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
        }


        // If any skip, then null
        try {
            COMMON_CONFIG = COMMON_BUILDER.build();
        } catch (IllegalStateException e) {

        }


    }

}
