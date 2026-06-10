package com.teamtea.eclipticseasons.common.block.blockentity;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.base.SyncBlockEntity;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.GreenHouseAir;
import com.teamtea.eclipticseasons.common.core.crop.GreenHouseCoreProvider;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public class GreenHouseCoreBlockEntity extends SyncBlockEntity {

    public static final Pair<Season, Integer> EMPTY_SEASON = Pair.of(Season.NONE, 0);

    public GreenHouseCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.greenhouse_core_entity_type.get(), pos, state);
        greenHouseHolder = new Long2ObjectOpenHashMap<>();
    }

    @Getter
    private int progress;

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("progress");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
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

    private Long2ObjectOpenHashMap<GreenHouseAir> greenHouseHolder;
    private int checkSleepTime = -1;

    @Override
    public void setRemoved() {
        SolarDataManager manager = SolarHolders.getSaveData(level);
        if (manager != null) {
            manager.removeGreenHouseProvider(getBlockPos());
        }
        greenHouseHolder.clear();
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
                        level.setBlockAndUpdate(blockPos, blockState.setValue(GreenHouseCoreBlock.SEASON_ON, currentSeason.getSecond()));
                    } else level.setBlockAndUpdate(blockPos, blockState.setValue(GreenHouseCoreBlock.SEASON_ON, 0));
                }

                if (blockEntity.checkSleepTime <= 0) {
                    blockEntity.greenHouseHolder.clear();
                    GreenHouseAir.GreenHouseScanResult greenHouseScanResult;


                    // long time = System.nanoTime();
                    // blockEntity.greenHouseHolder.clear();
                    // greenHouseScanResult = GreenHouseAir.startBFS(new GreenHouseAir.LevelReaderGetter(level), blockPos, blockState, blockEntity);
                    // long l = (System.nanoTime() - time);
                    // long l1 = l / 1000000;
                    // blockEntity.checkSleepTime = (int) (l1 * 100);
                    // EclipticSeasons.logger(l1 + " ms 1", l);

                    // blockEntity.greenHouseHolder.putAll(greenHouseScanResult.airMap());

                    if (false) {
                        GreenHouseAir.scanAsync(level, blockPos, blockEntity,
                                greenHouseScanResult1 -> {
                                    // blockEntity.greenHouseHolder.clear();
                                    blockEntity.checkSleepTime = 20 * 50;
                                    blockEntity.greenHouseHolder = greenHouseScanResult1.airMap();
                                });
                    }
                    // return l1;
                } else {
                    blockEntity.checkSleepTime--;
                    if (blockEntity.checkSleepTime == 9900) {
                        // LongIterator it = blockEntity.greenHouseHolder.keySet().iterator();
                        // blockEntity.greenHouseHolder.forEach(
                        //         (aLong, greenHouseAir) -> {
                        //             // long posLong = entry.getLongKey();
                        //
                        //             BlockPos blockPos1 = greenHouseAir.blockPos;
                        //             level.setBlockAndUpdate(blockPos1, Blocks.PINK_STAINED_GLASS.defaultBlockState());
                        //
                        //         }
                        // );

                        // for (long l : blockEntity.greenHouseHolder.keySet().toLongArray()) {
                        //     BlockPos blockPos1=BlockPos.of(l);
                        //     level.setBlockAndUpdate(blockPos1, Blocks.RED_STAINED_GLASS.defaultBlockState());
                        // }

                        // for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-64, -10, -64), blockPos.offset(64, 10, 64))) {
                        //     if (blockEntity.greenHouseHolder.containsKey(pos.asLong()))
                        //         level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        // }
                    }
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
                    if (level.getRandom().nextInt(CommonConfig.Crop.seasonalPrayerRitualCropBonusReduction.get() / 5 + 1) < extra || level.getRandom().nextDouble() < 100.0 / (
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
