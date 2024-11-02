package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;

public class EclipticTagTool {


    public static final Map<Biome, TagKey<Biome>> BIOME_TAG_KEY_MAP = new HashMap<>(128);


    public static TagKey<Biome> getTag(Biome biome) {
        return getTag(WeatherManager.getMainServerLevel(), biome);
    }

    public static TagKey<Biome> getTag(Level level, Biome biome) {
        var bt = BIOME_TAG_KEY_MAP.getOrDefault(biome, null);

        if (bt == null && level != null) {
            var biomes = level.registryAccess().registry(Registries.BIOME);
            if (biomes.isPresent()) {
                for (Map.Entry<ResourceKey<Biome>, Biome> resourceKeyBiomeEntry : biomes.get().entrySet()) {
                    if (resourceKeyBiomeEntry.getValue() == biome) {
                        var holder = biomes.get().getHolder(resourceKeyBiomeEntry.getKey());
                        if (holder.isPresent()) {
                            var tag = holder.get().tags().filter(ClimateTypeBiomeTags.BIOME_TYPES::contains).findFirst();
                            if (tag.isPresent()) {
                                bt = tag.get();
                            }
                        }
                    }
                }
            }
        }
        if (bt == null)
            bt = ClimateTypeBiomeTags.RAINLESS;
        BIOME_TAG_KEY_MAP.put(biome, bt);
        return bt;
    }
}
