package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.compat.Platform;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.Optional;

public class EclipticSeasonsBundles {
    public static final String MODID = "eclipticseasons_bundles";

    public static void init() {
        if (!Platform.isModLoaded(MODID)) return;

        Optional<? extends ModContainer> modContainerById = ModList.get().getModContainerById(MODID);
        ModContainer modContainer = modContainerById.orElse(null);
        if (modContainer instanceof ModContainer
                && General.COMMON_CONFIG != null) {
            modContainer.registerConfig(ModConfig.Type.COMMON, General.COMMON_CONFIG);
            if (FMLLoader.getDist() == Dist.CLIENT)
                modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
            LangUtil.tryLoadLang(MODID, true);
        }
    }

    public static ResourceLocation rl(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }
}