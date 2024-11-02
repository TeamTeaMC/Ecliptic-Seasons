package com.teamtea.eclipticseasons.mixin;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Non-critical mixin config plugin, just disables mixins if Distant Horizons isn't present,
 * since otherwise the log gets spammed with warnings.
 */
public class EclipticSeasonsMixinPlugin implements IMixinConfigPlugin {

    public static final String MIXIN_COMPAT_PACKAGE = "mixin.compat.";

    public static boolean isOptLoad() {
        return false;
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        int st = mixinClassName.indexOf(MIXIN_COMPAT_PACKAGE);
        if (st > -1) {
            String sub = Arrays.stream(mixinClassName.split(MIXIN_COMPAT_PACKAGE)).toList().get(1);
            String modid = Arrays.stream(sub.split("\\.")).toList().get(0);
            return FMLLoader.getLoadingModList().getModFileById(modid) != null;
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
