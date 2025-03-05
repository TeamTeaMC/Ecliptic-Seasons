package com.teamtea.eclipticseasons.mixin;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.*;
import java.util.stream.Collectors;


public class EclipticSeasonsMixinPlugin implements IMixinConfigPlugin {

    public static final String MIXIN_COMPAT_PACKAGE = "mixin.compat.";

    private static int isOptLoad = 0;

    public static boolean isOptLoad(){
        return isOptLoad==1;
    }
    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
        CompatModule.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

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
            String sub = Arrays.stream(mixinClassName.split(MIXIN_COMPAT_PACKAGE)).collect(Collectors.toCollection(ArrayList::new)).get(1);
            String modid = Arrays.stream(sub.split("\\.")).collect(Collectors.toCollection(ArrayList::new)).get(0);
            return FMLLoader.getLoadingModList().getModFileById(modid) != null
                    || (Objects.equals(modid, "optifine") && isOptLoad == 1);
        }
        if (targetClassName.endsWith("ModelBlockRenderer")
                && isOptLoad == 1
        ) {

            return false;
        }
        return true;
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
}
