package com.teamtea.eclipticseasons.common.block.blockentity;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.GreenHouseCoreProvider;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class GreenHouseCoreBlockEntity extends SyncBlockEntity {
    public GreenHouseCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.greenhouse_core_entity_type.get(), pos, state);
    }


    @Override
    public void setRemoved() {
        SolarDataManager manager = SolarHolders.getSaveData(level);
        if (manager != null) {
            manager.removeGreenHouseProvider(getBlockPos());
        }
        super.setRemoved();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, GreenHouseCoreBlockEntity blockEntity) {
        if (blockState.getBlock() instanceof GreenHouseCoreBlock greenHouseCoreBlock) {
            SolarDataManager manager = SolarHolders.getSaveData(level);
            if (manager != null) {
                GreenHouseCoreProvider nearGreenHouseProvider = manager.queryGreenHouseProvider(blockPos);
                if (nearGreenHouseProvider != null) {
                    if (nearGreenHouseProvider.getAvailCost() < 10) {
                        nearGreenHouseProvider.addAvailCost(999);
                    }
                } else {
                    manager.addMap(blockPos, new GreenHouseCoreProvider(greenHouseCoreBlock.getSeason(), 999));
                }
            }
            if (!level.isClientSide() && level.getGameTime() % 100 == 0) {
                Holder<Biome> cropBiome = CropGrowthHandler.getCropBiome(level, blockPos);
                AgroClimaticZone agroClimaticZone = CropGrowthHandler.getclimateTypeHolder(cropBiome).value();
                List<Pair<Season, Integer>> pairs = agroClimaticZone.seasonalSignalDurations();
                Pair<Season, Integer> currentSeason = findCurrentSeason(pairs, EclipticUtil.getNowSolarTerm(level).ordinal());
                if (currentSeason.getFirst() == greenHouseCoreBlock.getSeason()) {
                    level.setBlockAndUpdate(blockPos, blockState.setValue(GreenHouseCoreBlock.POWER, currentSeason.getSecond()));
                } else level.setBlockAndUpdate(blockPos, blockState.setValue(GreenHouseCoreBlock.POWER, 0));
            }
        }
    }

    public static Pair<Season, Integer> findCurrentSeason(List<Pair<Season, Integer>> localSeason, int index) {
        if (localSeason.isEmpty()) return Pair.of(Season.NONE, 0);
        if (localSeason.size() == 1) return Pair.of(localSeason.get(0).getFirst(), 15);

        int accumulatedLength = 0;


        for (int i = 0; i < localSeason.size(); i++) {
            Season season = localSeason.get(i).getFirst();
            int seasonLength = localSeason.get(i).getSecond();

            if (index < accumulatedLength + seasonLength) {
                int power;
                Season firstSeason = localSeason.get(0).getFirst();
                Season lastSeason = localSeason.get(localSeason.size() - 1).getFirst();
                if (firstSeason.equals(lastSeason) && (i == 0 || i == localSeason.size() - 1)) {
                    int localIndex = index - accumulatedLength;
                    int totalMergedLength = localSeason.get(0).getSecond() + localSeason.get(localSeason.size() - 1).getSecond();
                    power = Math.min(index, totalMergedLength - localIndex)  * 30/ totalMergedLength;
                } else {
                    power = Math.min((accumulatedLength + seasonLength) - index, index - accumulatedLength) * 30 / (seasonLength) ;
                }

                return Pair.of(season, Mth.clamp(power,1,15));
            }

            accumulatedLength += seasonLength;
        }

        return Pair.of(Season.NONE, 0);
    }
}
