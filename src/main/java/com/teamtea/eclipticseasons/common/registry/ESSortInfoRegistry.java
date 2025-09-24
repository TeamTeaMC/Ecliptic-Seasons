package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ESSortInfoRegistry {
    private static ResourceKey<ESSortInfo> createKey(ResourceKey<? extends Registry<?>> name) {
        return ResourceKey.create(ESRegistries.EXTRA_INFO, name.location());
    }

    public static List<ResourceLocation> getAllKeys(Class<?> classType) {
        List<ResourceLocation> keys = new ArrayList<>();
        try {
            for (Field field : classType.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
                        && ResourceKey.class.isAssignableFrom(field.getType())) {
                    Object value = field.get(null);
                    if (value instanceof ResourceKey<?> key) {
                        keys.add(key.location());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            EclipticSeasons.logger(e);
        }
        return keys;
    }

    public static void bootstrap(BootstapContext<ESSortInfo> context) {
        add(context, ESRegistries.AGRO_CLIMATE, AgroClimateRegistry.class);
        add(context, ESRegistries.CROP, CropRegistry.class);
        add(context, ESRegistries.BIOME_CLIMATE_SETTING, BiomeClimateSettingsRegistry.class);
        add(context, ESRegistries.SEASON_CYCLE, SeasonCycleRegistry.class);
        add(context, ESRegistries.SEASON_QUEST, SeasonQuestRegistry.class);
        add(context, ESRegistries.SNOW_DEFINITIONS, SnowDefinitionsRegistry.class);
    }

    private static void add(BootstapContext<ESSortInfo> context, ResourceKey<? extends Registry<?>> name, Class<?> classType) {
        context.register(createKey(name),
                new ESSortInfo(
                        name.location(),
                        getAllKeys(classType),
                        800,
                        false
                ));
    }
}
