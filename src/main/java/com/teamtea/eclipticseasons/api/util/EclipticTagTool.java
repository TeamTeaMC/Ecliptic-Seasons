package com.teamtea.eclipticseasons.api.util;

import com.teamtea.eclipticseasons.api.constant.tag.SeasonTypeBiomeTags;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public class EclipticTagTool {
    public static final Map<Biome, BiomeDictionary.Type> BIOME_TAG_KEY_MAP = new IdentityHashMap<>(128);

    public static BiomeDictionary.Type getTag(Biome biome) {
        return getTag(WeatherManager.fetchWorldIfNull(null), biome);
    }

    public static BiomeDictionary.Type getTag(World level, Biome biome) {
        BiomeDictionary.Type bt = BIOME_TAG_KEY_MAP.getOrDefault(biome, null);

        if (bt == null && level != null) {
            Optional<MutableRegistry<Biome>> biomes = level.registryAccess().registry(Registry.BIOME_REGISTRY);
            if (biomes.isPresent()) {
                for (Map.Entry<RegistryKey<Biome>, Biome> resourceKeyBiomeEntry : biomes.get().entrySet()) {
                    if (resourceKeyBiomeEntry.getValue() == biome) {
                        Optional<RegistryKey<Biome>> holder = biomes.get().getResourceKey(resourceKeyBiomeEntry.getValue());

                        if (holder.isPresent()) {

                            // BiomeDictionary.getTypes(holder.getBiomeCategory());
                            Optional<BiomeDictionary.Type> tag = BiomeDictionary.getTypes(holder.get()).stream()
                                    .filter(SeasonTypeBiomeTags.BIOMES::contains)
                                    .findFirst();
                            if (tag.isPresent()) {
                                bt = tag.get();
                            }
                        }
                    }
                }
            }
        }
        if (bt == null)
            bt = SeasonTypeBiomeTags.RAINLESS;
        BIOME_TAG_KEY_MAP.put(biome, bt);
        return bt;
    }
}
