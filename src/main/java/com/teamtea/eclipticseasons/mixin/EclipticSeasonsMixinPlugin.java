package com.teamtea.eclipticseasons.mixin;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;


public class EclipticSeasonsMixinPlugin implements IMixinConfigPlugin {

    public static final String MIXIN_COMPAT_PACKAGE = "mixin.compat.";

    private static int isOptLoad = 0;

    public static boolean isOptLoad() {
        return isOptLoad == 1;
    }

    @Override
    public void onLoad(String mixinPackage) {
        CompatModule.init();
        PreloadedConfig.onLoad(mixinPackage);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean shouldApply = true;

        if (isOptLoad == 0) {
            try {
                Class<?> ignored = Class.forName("optifine.Installer");
                isOptLoad = 1;
            } catch (Exception ignored) {
                isOptLoad = 2;
            }
        }

        int st = mixinClassName.indexOf(MIXIN_COMPAT_PACKAGE);
        if (st > -1) {
            String sub = Arrays.stream(mixinClassName.split(MIXIN_COMPAT_PACKAGE)).toList().get(1);
            List<String> strings = Arrays.stream(sub.split("\\.")).toList();
            String modid = strings.get(0);
            if (strings.size() > 2) {
                if (FMLLoader.getLoadingModList().getModFileById(strings.get(1)) == null)
                    shouldApply = false;
            } else {
                shouldApply = FMLLoader.getLoadingModList().getModFileById(modid) != null
                        || (Objects.equals(modid, "optifine") && isOptLoad == 1);
            }
        }
        if (
                (
                        //         targetClassName.endsWith("ModelBlockRenderer")
                        // ||targetClassName.endsWith("ChunkRenderDispatcher$RenderChunk$RebuildTask")
                        mixinClassName.contains("mixin.client.render.chunk")
                )
                        && isOptLoad == 1
        ) {

            shouldApply = false;
        }

        if (shouldApply && !PreloadedConfig.shouldApply(mixinClassName))
            shouldApply = false;
        return shouldApply;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    public static class PreloadedConfig {
        private static CommentedFileConfig config;

        public static void onLoad(String mixinPackage) {
            var path = FMLPaths.CONFIGDIR.get().resolve(String.format(Locale.ROOT, "%s-mixins.toml", EclipticSeasons.MODID));
            config = CommentedFileConfig.builder(path)
                    .sync()
                    .preserveInsertionOrder()
                    .autosave()
                    .build();
            config.load();
        }

        public static boolean shouldApply(String mixinClassName) {
            String[] parts = mixinClassName.split("\\.");
            if (parts.length <= 4) return true;

            List<String> pathList = Arrays.stream(parts).skip(4).toList();

            if (!config.contains(pathList)) {
                config.set(String.join(".", pathList), true);
                config.save();
                return true;
            }

            return config.get(pathList);
        }
    }
}
