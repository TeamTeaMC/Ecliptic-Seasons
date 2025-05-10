package com.teamtea.eclipticseasons.common.core.snow;

import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public class SnowChecker {

    public static final Map<Block, SnowDefinition> SNOW_DEFINITION_MAP = new IdentityHashMap<>(1024);

    public static final Map<BlockState, SnowDefinition.Info> statemap = new IdentityHashMap<>(4096);

    public static @NotNull SnowDefinition.Info getUncacheSnow(BlockState blockState) {
        SnowDefinition.Info sno = statemap.get(blockState);
        if (sno == null) {
            SnowDefinition snowDefinition = SNOW_DEFINITION_MAP.get(blockState.getBlock());
            if (snowDefinition != null) {
                sno = snowDefinition.getInfo();
            }
        }
        return sno == null ? SnowDefinition.Info.EMPTY : sno;
    }

    public static void clearOnClientExitOrServerClose() {
        SNOW_DEFINITION_MAP.clear();
    }

    // we don't care if we are in a server or client mode now, because block is not syncable
    // there keeps only one copy of block registry in the process
    public static void resetUpdate(RegistryAccess registryAccess, boolean isServer) {
        SNOW_DEFINITION_MAP.clear();
        if (isServer) {
            for (SnowDefinition snowDefinition : registryAccess.registryOrThrow(ESRegistries.SNOW_DEFINITIONS)) {
                snowDefinition.fillMap(SNOW_DEFINITION_MAP);
            }
        } else {
            if (ClientCon.snowDefCache != null)
                for (SnowDefinition snowDefinition : ClientCon.snowDefCache.build(registryAccess, SnowDefinition.class)) {
                    snowDefinition.fillMap(SNOW_DEFINITION_MAP);
                }
        }
    }
}
