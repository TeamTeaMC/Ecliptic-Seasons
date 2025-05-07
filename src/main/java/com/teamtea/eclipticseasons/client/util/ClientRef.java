package com.teamtea.eclipticseasons.client.util;

import com.teamtea.eclipticseasons.api.data.client.SeasonalBiomeAmbient;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import net.minecraft.core.RegistryAccess;

import java.util.*;

public class ClientRef {


    public static final List<SeasonalBiomeAmbient> sounds=new ArrayList<>();

    public static void updateClientSide(RegistryAccess registryAccess) {
        sounds.clear();
        buildSeasonalSounds(registryAccess);
    }

    private static void buildSeasonalSounds(RegistryAccess registryAccess) {
        sounds.addAll(ClientJsonCacheListener.ambientCache
                .build(SeasonalBiomeAmbient.CODEC, registryAccess).values());
    }



    public static void clearOnClientExitOrServerClose() {
        sounds.clear();
    }
}
