package com.teamtea.eclipticseasons.common.core.crop;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.season.SeasonDefinition;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class NaturalPlantHandler {

    private final static Map<Block, Map<Holder<?>, EnumMap<SolarTerm, SeasonDefinition.ChangeMode>>> SEASON_DEFINITIONS =
            new IdentityHashMap<>();

    private static void splitSet(SolarTerm solarTerm, SeasonDefinition.ChangeMode changeMode, Block possibleBlock, HolderSet<?> holderSet) {
        for (var holder : holderSet) {
            Map<Holder<?>, EnumMap<SolarTerm, SeasonDefinition.ChangeMode>> holderEnumMapMap =
                    SEASON_DEFINITIONS.computeIfAbsent(possibleBlock, (b) -> new HashMap<>());
            EnumMap<SolarTerm, SeasonDefinition.ChangeMode> solarTermChangeModeEnumMap =
                    holderEnumMapMap.computeIfAbsent(holder, (b) -> new EnumMap<>(SolarTerm.class));
            solarTermChangeModeEnumMap.put(solarTerm, changeMode);
        }
    }

    public static void resetUpdate(RegistryAccess registryAccess, boolean isServer) {
        if (isServer) {
            SEASON_DEFINITIONS.clear();
            var registry = registryAccess.registry(ESRegistries.SEASON_DEFINITION);
            if (registry.isEmpty()) {
                SimpleUtil.warningForModWrongCalling(ESRegistries.SEASON_DEFINITION);
                return;
            } else {
                for (SeasonDefinition seasonDefinition : registry.get()) {
                    EnumMap<SolarTerm, List<SeasonDefinition.ChangeMode>> combine = seasonDefinition.changes().combine();
                    combine.forEach(
                            (solarTerm, changeModes) -> {
                                for (SeasonDefinition.ChangeMode changeMode : changeModes) {
                                    for (final Block possibleBlock : changeMode.getPossibleBlocks()) {
                                        seasonDefinition.climate().ifPresent(
                                                holderSet -> {
                                                    splitSet(solarTerm, changeMode, possibleBlock, holderSet);
                                                }
                                        );
                                        seasonDefinition.biomes().ifPresent(holderSet -> {
                                            splitSet(solarTerm, changeMode, possibleBlock, holderSet);
                                        });
                                    }
                                }
                            }
                    );

                }
            }
        }
    }


    public static void clearOnClientExitOrServerClose() {
        SEASON_DEFINITIONS.clear();
    }

    public static void tickBlock(ServerLevel level, BlockPos pos, BlockState blockState) {
        if (MapChecker.isValidDimension(level)) {
            var mapMap = SEASON_DEFINITIONS.getOrDefault(blockState.getBlock(), null);
            if (mapMap != null) {
                Holder<Biome> cropBiome = CropGrowthHandler.getCropBiome(level, pos);
                EnumMap<SolarTerm, SeasonDefinition.ChangeMode> enumMap = mapMap.getOrDefault(cropBiome, null);
                if (enumMap == null) {
                    enumMap = mapMap.getOrDefault(CropGrowthHandler.getclimateTypeHolder(cropBiome), null);
                }
                if (enumMap != null) {
                    SolarTerm nowSolarTerm = EclipticUtil.getNowSolarTerm(level);
                    SeasonDefinition.ChangeMode changeMode = enumMap.getOrDefault(nowSolarTerm, null);
                    if (changeMode != null) {
                        if (level.getRandom().nextFloat() < changeMode.chance()
                                && changeMode.original().matches(level, pos)
                        ) {
                            for (SeasonDefinition.PlaceContent placeContent : changeMode.place()) {
                                BlockState blockStatePlaced = placeContent.block().get(level.getRandom().nextInt(placeContent.block().size()));
                                if (blockStatePlaced.isEmpty()) {
                                    level.removeBlock(pos.offset(placeContent.offset().orElse(Vec3i.ZERO)), false);
                                } else if (placeContent.offset().isEmpty()) {
                                    level.setBlock(pos, blockStatePlaced, Block.UPDATE_CLIENTS);
                                } else {
                                    BlockPos offset = pos.offset(placeContent.offset().get());
                                    if (placeContent.replace() || level.getBlockState(offset).canBeReplaced()) {
                                        level.setBlock(offset, blockStatePlaced, Block.UPDATE_CLIENTS);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
