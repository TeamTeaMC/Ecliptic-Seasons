package com.teamtea.eclipticseasons.common.core.snow;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SnowChecker {

    public static final Map<Block, List<SnowDefinition>> SNOW_DEFINITION_MAP = new IdentityHashMap<>(1024);

    public static final Map<BlockState, SnowDefinition.Info> statemap = new IdentityHashMap<>(4096);

    public static @NotNull SnowDefinition.Info getUncacheSnow(BlockState blockState) {
        SnowDefinition.Info sno = statemap.get(blockState);
        if (sno == null) {
            SnowDefinition snowDefinition = null;
            List<SnowDefinition> snowDefinitions = SNOW_DEFINITION_MAP.get(blockState.getBlock());
            if (snowDefinitions != null) {
                snowFind:
                for (SnowDefinition definition : snowDefinitions) {
                    for (SnowDefinition.PropertyTester propertyTester : definition.getMap()) {
                        boolean matches = propertyTester.matches(blockState);
                        if (matches ^ propertyTester.isReverse()) {
                            snowDefinition = definition;
                            break snowFind;
                        }
                    }
                }
            }
            sno = snowDefinition == null ? SnowDefinition.Info.EMPTY : snowDefinition.getInfo();
            statemap.put(blockState, sno);
        }
        return sno == null ? SnowDefinition.Info.EMPTY : sno;
    }

    public static void clearOnClientExitOrServerClose() {
        SNOW_DEFINITION_MAP.clear();
        statemap.clear();
    }

    // we don't care if we are in a server or client mode now, because block is not syncable
    // there keeps only one copy of block registry in the process
    public static void resetUpdate(RegistryAccess registryAccess, boolean isServer) {
        statemap.clear();
        Optional<Registry<SnowDefinition>> snowDefinitions = registryAccess.registry(ESRegistries.SNOW_DEFINITIONS);
        if (snowDefinitions.isEmpty()) {
            SimpleUtil.warningForModWrongCalling(ESRegistries.SNOW_DEFINITIONS);
        } else {
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
            EclipticSeasons.logger("Has registered extra snow definitions with size %s.".formatted(SNOW_DEFINITION_MAP.size()));
        }
    }
}
