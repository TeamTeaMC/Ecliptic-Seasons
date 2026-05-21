package com.teamtea.eclipticseasons.common.block.blockentity;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
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
import com.teamtea.eclipticseasons.config.CommonConfig;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public class GreenHouseCoreBlockEntity extends SyncBlockEntity {
    public static final Pair<Season, Integer> EMPTY_SEASON = Pair.of(Season.NONE, 0);

    public GreenHouseCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.greenhouse_core_entity_type.get(), pos, state);
    }

    @Getter
    private int progress;

    @Override
    public void load(CompoundTag tag) {
        progress = tag.getInt("progress");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("progress", progress);
    }


    public void updateProgress(int addition) {
        this.progress += addition;
        this.progress = Mth.clamp(this.progress, 0, 100);
        inventoryChanged();
    }

    public static Component getProgressComponent(BlockEntity blockEntity, BlockState state) {
        if (blockEntity instanceof GreenHouseCoreBlockEntity entity) {
            int progress = entity.getProgress();
            int stage = state.getValue(GreenHouseCoreBlock.AGE);
            if (stage != GreenHouseCoreBlock.MAX_STAGE) {
                int a_progress = (stage * 100 + progress) / GreenHouseCoreBlock.MAX_STAGE;
                return Component.translatable("info.eclipticseasons.greenhouse_core.prayer_progress", a_progress);
            }
        }
        return Component.empty();
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
            if (GreenHouseCoreBlock.isPowered(blockState)) {
                SolarDataManager manager = SolarHolders.getSaveData(level);
                if (manager != null) {
                    GreenHouseCoreProvider nearGreenHouseProvider = manager.queryGreenHouseProvider(blockPos);
                    if (nearGreenHouseProvider != null) {
                        if (nearGreenHouseProvider.getAvailCost() < 10) {
                            nearGreenHouseProvider.addAvailCost(999);
                        }
                    } else {
                        manager.addGreenHouseCoreProvider(blockPos, new GreenHouseCoreProvider(greenHouseCoreBlock.getSeason(), 999));
                    }
                }

                // set redstone signal
                if (!level.isClientSide() && level.getGameTime() % 100 == 0) {
                    Pair<Season, Integer> currentSeason = getCurrentSeason(level, blockPos);
                    if (currentSeason.getFirst() == greenHouseCoreBlock.getSeason()) {
                        level.setBlockAndUpdate(blockPos, blockState.setValue(GreenHouseCoreBlock.POWER, currentSeason.getSecond()));
                    } else level.setBlockAndUpdate(blockPos, blockState.setValue(GreenHouseCoreBlock.POWER, 0));
                }

            } else {
                if (level instanceof ServerLevel) {
                    SolarDataManager manager = SolarHolders.getSaveData(level);
                    int extra = 0;
                    if (manager != null) {
                        GreenHouseCoreProvider nearGreenHouseProvider = manager.queryGreenHouseProvider(blockPos);
                        if (nearGreenHouseProvider != null) {
                            if (nearGreenHouseProvider instanceof Consumer consumer)
                                extra = consumer.getAndCostEnergy();
                        } else {
                            manager.addGreenHouseCoreProvider(blockPos, new Consumer(greenHouseCoreBlock.getSeason(), 0));
                        }
                    }
                    if (level.getRandom().nextInt(CommonConfig.Crop.seasonalPrayerRitualCropBonusReduction.get()/ 5 + 1) < extra || level.getRandom().nextDouble() < 100.0 / (
                            CommonConfig.Crop.seasonalPrayerRitualTimeCost.get() * EclipticUtil.getDayLengthInMinecraft(level))) {
                        extra = extra > 0 ? extra - 1 : 0;
                        Pair<Season, Integer> currentSeason = getCurrentSeason(level, blockPos);
                        if (currentSeason.getFirst() == greenHouseCoreBlock.getSeason()
                                && !level.getBlockState(blockPos.below()).isSolidRender(level, blockPos)
                                && !CropGrowthHandler.isInRoom(level, blockPos, blockState, Optional.empty())) {
                            boolean nextStage = blockEntity.progress == 100;
                            blockEntity.updateProgress(nextStage ? -100 : 1 + extra);
                            if (nextStage) {
                                level.setBlockAndUpdate(blockPos, blockState
                                        .setValue(GreenHouseCoreBlock.AGE, blockState.getValue(GreenHouseCoreBlock.AGE) + 1));
                            }
                        }

                    }
                }
            }
        }
    }

    public static class Consumer extends GreenHouseCoreProvider {
        int energy = 0;

        public Consumer(Season season, int availCost) {
            super(season, availCost);
        }

        public void addEnergy(int attach) {
            this.energy += attach;
        }

        public int getAndCostEnergy() {
            int old = this.energy;
            this.energy = old > 0 ? old - 1 : 0;
            return old;
        }
    }

    public static Pair<Season, Integer> getCurrentSeason(Level level, BlockPos blockPos) {
        Holder<Biome> cropBiome = CropGrowthHandler.getCropBiome(level, blockPos);
        Holder<AgroClimaticZone> agroClimaticZoneHolder = CropGrowthHandler.getclimateTypeHolder(cropBiome);
        if (agroClimaticZoneHolder != null) {
            AgroClimaticZone agroClimaticZone = agroClimaticZoneHolder.value();
            List<Pair<Season, Integer>> pairs = agroClimaticZone.seasonalSignalDurations();
            return findCurrentSeason(pairs, EclipticUtil.getNowSolarTerm(level).ordinal());
        }
        return EMPTY_SEASON;
    }

    public static Pair<Season, Integer> findCurrentSeason(List<Pair<Season, Integer>> localSeason, int index) {
        if (localSeason.isEmpty()) return EMPTY_SEASON;
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
                    power = Math.min(index, totalMergedLength - localIndex) * 30 / totalMergedLength;
                } else {
                    power = Math.min((accumulatedLength + seasonLength) - index, index - accumulatedLength) * 30 / (seasonLength);
                }

                return Pair.of(season, Mth.clamp(power, 1, 15));
            }

            accumulatedLength += seasonLength;
        }

        return Pair.of(Season.NONE, 0);
    }
}
