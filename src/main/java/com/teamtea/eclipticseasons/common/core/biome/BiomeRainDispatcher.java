package com.teamtea.eclipticseasons.common.core.biome;

import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.FlatRain;
import com.teamtea.eclipticseasons.api.constant.climate.MonsoonRain;
import com.teamtea.eclipticseasons.api.constant.climate.TemperateRain;
import com.teamtea.eclipticseasons.api.constant.climate.seasonal.ColdRain;
import com.teamtea.eclipticseasons.api.constant.climate.seasonal.HotRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.weather.CustomRain;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.biome.Biome;

import org.jspecify.annotations.Nullable;
import java.util.*;

public class BiomeRainDispatcher {

    // get id
    public static final Map<BiomeRain, Integer> INDEX_BIOME_RAIN_MAP = new IdentityHashMap<>();
    public static final Map<BiomeRain, Integer> CLIENT_INDEX_BIOME_RAIN_MAP = new IdentityHashMap<>();

    // get obj
    public static final ArrayList<BiomeRain> INDEX_BIOME_RAIN_LIST = new ArrayList<>();
    public static final ArrayList<BiomeRain> CLIENT_INDEX_BIOME_RAIN_LIST = new ArrayList<>();


    public static long hash_cache = 0;

    public static void init(HolderLookup.@Nullable RegistryLookup<Biome> biomeRegistry,
                            boolean isServer) {
        if (biomeRegistry == null) return;
        clearOnClientExitOrServerClose(isServer);

        ArrayList<BiomeRain> rains = new ArrayList<>();

        rains.addAll(Arrays.stream(FlatRain.values()).toList());
        rains.addAll(Arrays.stream(MonsoonRain.values()).toList());

        rains.addAll(Arrays.stream(ColdRain.values()).toList());
        rains.addAll(Arrays.stream(HotRain.values()).toList());
        rains.addAll(Arrays.stream(TemperateRain.values()).toList());


        for (Biome biome : biomeRegistry.listElements().map(Holder::value).toList()) {
            Map<SolarTerm, CustomRain> customRains = BiomeClimateManager.getCustomRain(biome, isServer);
            customRains.forEach((solarTerm, customRain) -> {
                if (customRain != null) {
                    List<BiomeRain> biomeRains = new ArrayList<>(customRain.resolveOrderedList());
                    while (!biomeRains.isEmpty()) {
                        BiomeRain first = biomeRains.getFirst();
                        if (first.isResolvable()) {
                            biomeRains.addAll(first.resolveOrderedList());
                        } else {
                            rains.add(first);
                            biomeRains.removeFirst();
                        }
                    }
                }
            });
        }

        for (int i = 0; i < rains.size(); i++) {
            (isServer ? INDEX_BIOME_RAIN_MAP : CLIENT_INDEX_BIOME_RAIN_MAP)
                    .put(rains.get(i), i);
        }

        (isServer ? INDEX_BIOME_RAIN_LIST : CLIENT_INDEX_BIOME_RAIN_LIST).addAll(rains);
        if (isServer) hash_cache = INDEX_BIOME_RAIN_LIST.hashCode();
    }

    public static void clearOnClientExitOrServerClose(boolean serverCause) {
        if (serverCause) {
            INDEX_BIOME_RAIN_MAP.clear();
            INDEX_BIOME_RAIN_LIST.clear();
            hash_cache = 0;
        } else {
            CLIENT_INDEX_BIOME_RAIN_MAP.clear();
            CLIENT_INDEX_BIOME_RAIN_LIST.clear();
        }
    }

    public static BiomeRain getBiomeRain(boolean isServer, int index) {
        ArrayList<BiomeRain> list = isServer ? INDEX_BIOME_RAIN_LIST : CLIENT_INDEX_BIOME_RAIN_LIST;
        if (index < 0 || index >= list.size()) return FlatRain.NONE;
        return list.get(index);
    }

    public static int indexOf(boolean isServer, BiomeRain biomeRain) {
        return (isServer ? INDEX_BIOME_RAIN_MAP : CLIENT_INDEX_BIOME_RAIN_MAP)
                .getOrDefault(biomeRain, -1);
    }
}
