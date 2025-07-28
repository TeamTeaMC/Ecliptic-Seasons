package com.teamtea.eclipticseasons.common.core.snow;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public class SnowChecker {

    public static final Map<Block, SnowDefinition> SNOW_DEFINITION_MAP = new IdentityHashMap<>(1024);

    public static final Map<BlockState, SnowDefinition.Info> statemap = new IdentityHashMap<>(4096);

    public static @NotNull SnowDefinition.Info getUncacheSnow(BlockState blockState) {
        SnowDefinition.Info sno = statemap.get(blockState);
        if (sno == null) {
            SnowDefinition snowDefinition = SNOW_DEFINITION_MAP.get(blockState.getBlock());
            if (snowDefinition != null) {
                boolean match = true;
                for (SnowDefinition.PropertyTester propertyTester : snowDefinition.getMap()) {
                    if (!(propertyTester.matches(blockState) && !propertyTester.isReverse())) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    sno = snowDefinition.getInfo();
                }
            }
            statemap.put(blockState, snowDefinition == null ?
                    SnowDefinition.Info.EMPTY :
                    snowDefinition.getInfo());
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
            for (SnowDefinition snowDefinition : snowDefinitions.get()) {
                snowDefinition.fillMap(SNOW_DEFINITION_MAP);
            }
            EclipticSeasons.logger("Has registered extra snow definitions with size %s.".formatted(SNOW_DEFINITION_MAP.size()));
        }
    }
}
