package com.teamtea.eclipticseasons.common.core.crop;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.season.definition.ChangeMode;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import com.teamtea.eclipticseasons.api.data.season.definition.SeasonDefinition;
import com.teamtea.eclipticseasons.api.data.season.definition.selector.IChangeSelector;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.api.misc.BiomeHolderPredicate;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class NaturalPlantHandler {


    public static void initCache(HolderLookup.Provider lookupProvider, boolean isSever) {
        if (!isSever) return;
        HolderLookup.RegistryLookup<Block> blocks = lookupProvider.lookupOrThrow(Registries.BLOCK);
        initTag(blocks,EclipticBlockTags.NATURAL_PLANTS);
        initTag(blocks,EclipticBlockTags.VOLATILE);
    }

    private static void initTag(HolderLookup.RegistryLookup<Block> blocks, TagKey<Block> naturalPlants) {
        Optional<HolderSet.Named<Block>> n = blocks.get(naturalPlants);
        if (n.isPresent()) {
            for (Holder<Block> blockHolder : n.get()) {
                for (BlockState possibleState : blockHolder.value().getStateDefinition().getPossibleStates()) {
                    possibleState.initCache();
                }
            }
        }
    }

    public final static Map<Block, EnumMap<SolarTerm, List<Pair<BiomeHolderPredicate, ChangeMode>>>> SEASON_DEFINITIONS = new IdentityHashMap<>();

    public static void resetUpdate(HolderLookup.Provider registryAccess, boolean isServer) {
        if (isServer) {
            SEASON_DEFINITIONS.clear();
            var registry = registryAccess.lookup(ESRegistries.SEASON_DEFINITION);
            if (registry.isEmpty()) {
                SimpleUtil.warningForModWrongCalling(ESRegistries.SEASON_DEFINITION);
            } else {
                for (SeasonDefinition seasonDefinition : ESSortInfo.sorted2(registry.get())) {
                    Map<SolarTerm, List<ChangeMode>> combine = seasonDefinition.changes().combine();
                    combine.forEach(
                            (solarTerm, changeModes) -> {
                                for (ChangeMode changeMode : changeModes) {
                                    for (final Block possibleBlock : changeMode.getPossibleBlocks()) {
                                        var blockMap = SEASON_DEFINITIONS.computeIfAbsent(possibleBlock, (b) -> new EnumMap<>(SolarTerm.class));
                                        var pairList = blockMap.computeIfAbsent(solarTerm, (b) -> new ArrayList<>());
                                        if (seasonDefinition.biomes().isPresent()) {
                                            pairList.add(Pair.of(BiomeHolderPredicate.of(seasonDefinition.biomes().get()), changeMode));
                                        } else {
                                            pairList.add(Pair.of(BiomeHolderPredicate.of(), changeMode));
                                        }
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

    public static boolean shouldTick(BlockState state) {
        var map = SEASON_DEFINITIONS.getOrDefault(state.getBlock(), null);
        if (map == null || map.isEmpty()) return false;
        for (var value : map.values()) {
            for (var pair : value) {
                if (pair.getSecond().matchesState(state)) return true;
            }
        }
        return false;
    }

    private static boolean testChance(long seed, float chance) {
        long mixed = HashCommon.mix(seed);
        double rand = (mixed & ((1L << 53) - 1)) / (double) (1L << 53);
        return rand < chance;
    }

    private static final ThreadLocal<ISeasonChangeContext> SEASON_CHANGE_CONTEXT_THREAD_LOCAL =
            ThreadLocal.withInitial(ISeasonChangeContext::of);

    public static void tickBlock(ServerLevel level, BlockPos pos, BlockState state) {
        // if (CommonConfig.isSeasonDefinition())
        {
            SolarTerm nowSolarTerm = EclipticUtil.getNowSolarTerm(level);
            if (nowSolarTerm.isValid()) {
                var mapMap = SEASON_DEFINITIONS.getOrDefault(state.getBlock(), null);
                if (mapMap != null) {
                    List<Pair<BiomeHolderPredicate, ChangeMode>> pairs = mapMap.getOrDefault(nowSolarTerm, null);
                    if (pairs == null) return;
                    Holder<Biome> cropBiome = null;
                    long fixedSeedValue = -1;
                    boolean hasCheckFixedSeed = false;
                    for (int i = 0, pairsSize = pairs.size(); i < pairsSize; i++) {
                        Pair<BiomeHolderPredicate, ChangeMode> pair = pairs.get(i);
                        // if (pair.getFirst().test(cropBiome))
                        {
                            ChangeMode changeMode = pair.getSecond();
                            if (changeMode.fixedSeed()) {
                                if (!hasCheckFixedSeed) {
                                    fixedSeedValue = level.getSeed();
                                    // fixedSeedValue ^= HashCommon.mix(pos.getX());
                                    // fixedSeedValue ^= HashCommon.mix(pos.getY());
                                    // fixedSeedValue ^= HashCommon.mix(pos.getZ());
                                    fixedSeedValue ^= HashCommon.mix(state.getSeed(pos));
                                    fixedSeedValue ^= HashCommon.mix(EclipticSeasonsApi.getInstance().getSolarDays(level));
                                    fixedSeedValue ^= HashCommon.mix(EclipticSeasonsApi.getInstance().getDayInTerm(level));
                                    fixedSeedValue ^= HashCommon.mix(EclipticSeasonsApi.getInstance().getLastingDaysOfEachTerm(level));
                                    // fixedSeedValue ^= HashCommon.mix(TimePeriod.fromTimeOfDay(level.getTimeOfDay(1f)).ordinal() * 100);
                                    hasCheckFixedSeed = true;
                                }
                                if (!testChance(fixedSeedValue, changeMode.chance())) continue;
                                if (level.getRandom().nextFloat() > changeMode.fixedSeedChance()) continue;

                            } else if (level.getRandom().nextFloat() >= changeMode.chance()) continue;

                            if (changeMode.matches(state, level, pos)) {
                                cropBiome = cropBiome == null ? CropGrowthHandler.getCropBiome(level, pos) : cropBiome;
                                if (!pair.getFirst().test(cropBiome)) continue;

                                int totalWeight = 0;
                                List<IChangeSelector> selectors = changeMode.selectors();
                                ISeasonChangeContext context = SEASON_CHANGE_CONTEXT_THREAD_LOCAL.get();
                                for (int j = 0, selectorsSize = selectors.size(); j < selectorsSize; j++) {
                                    var blockStatePlaced = selectors.get(j);
                                    if (blockStatePlaced.shouldApply(level, pos, context)) {
                                        totalWeight += blockStatePlaced.getWeight();
                                    }
                                }
                                if (totalWeight <= 0) return;
                                int weightIndex = changeMode.fixedSeed()
                                        ? Math.floorMod(fixedSeedValue, totalWeight)
                                        : level.getRandom().nextInt(totalWeight);
                                IChangeSelector chosen = null;
                                List<IChangeSelector> selectorsed = changeMode.selectors();
                                for (int j = 0, selectorsedSize = selectorsed.size(); j < selectorsedSize; j++) {
                                    var blockStatePlaced = selectorsed.get(j);
                                    if (!blockStatePlaced.shouldApply(level, pos, context)) continue;
                                    weightIndex -= blockStatePlaced.getWeight();
                                    if (weightIndex <= 0) {
                                        chosen = blockStatePlaced;
                                        break;
                                    }
                                }

                                if (chosen != null) {
                                    boolean applied = chosen.place(level, pos, context);
                                    if (applied && chosen.getLoot().isPresent() && chosen.dropWhenApplied(level, pos, context)) {
                                        dropLootTable(level, pos, chosen.getLoot().get(), changeMode.fixedSeed() ? fixedSeedValue : level.getRandom().nextLong(), state);
                                    }
                                }

                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean setBlockAndSelfCheck(ServerLevel level, BlockPos pos, BlockState chosen) {
        return setBlockAndSelfCheck(level, pos, chosen, level.getBlockState(pos));
    }

    public static boolean setBlockAndSelfCheck(ServerLevel level, BlockPos pos, BlockState chosen, BlockState old) {
        if (old != chosen) {
            boolean set = level.setBlock(pos, chosen, Block.UPDATE_CLIENTS);
            if (set) {
                SoundType soundType = (chosen.is(Blocks.AIR) ? old : chosen).getSoundType();
                if (soundType != null)
                    level.playSound(null, pos, chosen.is(Blocks.AIR) ? soundType.getBreakSound() : soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                return true;
            }
        }
        return false;
    }

    public static void dropLootTable(ServerLevel level, BlockPos pos, ResourceKey<LootTable> resourcekey, long seed, BlockState state) {
        if (resourcekey != null && level != null) {
            LootTable loottable = level.getServer().reloadableRegistries().getLootTable(resourcekey);
            LootParams.Builder lootparams$builder = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withParameter(LootContextParams.BLOCK_STATE, state)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));
            for (ItemStack randomItem : loottable.getRandomItems(lootparams$builder.create(LootContextParamSets.BLOCK), seed)) {
                Block.popResource(level, pos, randomItem);
            }
        }
    }
}
