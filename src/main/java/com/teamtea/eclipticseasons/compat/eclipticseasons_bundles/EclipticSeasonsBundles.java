package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;


import com.teamtea.eclipticseasons.compat.Platform;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Optional;

public class EclipticSeasonsBundles {
    public static final String MODID = "eclipticseasons_bundles";

    public static void init() {
        if (!Platform.isModLoaded(MODID)) return;

        Optional<? extends ModContainer> modContainerById = ModList.get().getModContainerById(MODID);
        ModContainer modContainer = modContainerById.orElse(null);
        if (modContainer instanceof ModContainer
                && General.COMMON_CONFIG != null) {
            modContainer.addConfig(new ModConfig(ModConfig.Type.COMMON, General.COMMON_CONFIG, modContainer));
        }
    }
}