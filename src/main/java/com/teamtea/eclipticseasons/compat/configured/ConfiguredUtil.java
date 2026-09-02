package com.teamtea.eclipticseasons.compat.configured;

import com.mrcrayfish.configured.api.ConfigType;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.api.ModContext;
import com.mrcrayfish.configured.api.util.ConfigScreenHelper;
import com.mrcrayfish.configured.client.ClientHandler;
import com.mrcrayfish.configured.platform.Services;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConfiguredUtil {

    public static Screen getSafe(Screen parent) {
        return getSafe(EclipticSeasonsApi.MODID, parent);
    }

    public static Screen getSafe(String modId, Screen parent) {
        BiFunction<String, Screen, Screen> screenFactory =
                ConfiguredUtil::get;

        return CompatModule.isConfigured()
                ? screenFactory.apply(modId, parent)
                : null;
    }

    public static Screen get(String modId, Screen parent) {
        return ModList.get()
                .getModContainerById(modId)
                .map(container -> newConfigScreen(parent, container))
                .orElse(null);
    }

    public static Screen get(Screen p) {
        Optional<? extends ModContainer> modFile = ModList.get().getModContainerById((EclipticSeasonsApi.MODID));
        return modFile.map(modContainer -> newConfigScreen(p, modContainer)).orElse(null);
    }

    static Screen newConfigScreen(Screen currentScreen, ModContainer container) {
        String modId = container.getModId();
        Map<ConfigType, Set<IModConfig>> modConfigMap = ClientHandler.createConfigMap(new ModContext(modId));
        ResourceLocation backgroundTexture = Services.CONFIG.getBackgroundTexture(modId);
        return modConfigMap.isEmpty() ? null : ConfigScreenHelper.createSelectionScreen(currentScreen, Component.literal(container.getModInfo().getDisplayName()), modConfigMap,backgroundTexture);
    }
}
