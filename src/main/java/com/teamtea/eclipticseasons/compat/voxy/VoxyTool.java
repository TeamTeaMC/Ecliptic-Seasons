package com.teamtea.eclipticseasons.compat.voxy;

import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import me.cortex.voxy.common.world.WorldEngine;
import me.cortex.voxy.common.world.WorldSection;
import me.cortex.voxy.common.world.other.Mapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.function.IntConsumer;

public final class VoxyTool {
    public static final int MAX_VOXY_BLOCK_ID = 0xFFFFF;
    public static final int VIRTUAL_ICE_BLOCK_ID = MAX_VOXY_BLOCK_ID;
    private static final RandomSource RANDOM_SOURCE_THREAD_LOCAL = RandomSource.createThreadLocalInstance(42L);

    private VoxyTool() {
    }

    public static boolean isVoxyTest() {
        return CompatModule.CommonConfig.voxyTest.get();
    }

    /** Creates a render-only seasonal view without mutating Voxy sections or Mapper storage. */
    public static long[] createSeasonalRenderData(WorldEngine world, WorldSection section, long[] source) {
        if (!isVoxyTest()) return source;
        Mapper mapper = world.getMapper();
        long[] output = Arrays.copyOf(source, source.length);
        int scale = 1 << section.lvl;
        int centerOffset = section.lvl == 0 ? 0 : scale >> 1;

        for (int index = 0; index < source.length; index++) {
            long mappingId = source[index];
            if (Mapper.isAir(mappingId)) continue;
            int localX = index & 31;
            int localZ = index >> 5 & 31;
            int localY = index >> 10 & 31;
            int storedBlockId = Mapper.getBlockId(mappingId);
            int originalBlockId = fixId(mapper, storedBlockId);
            BlockState state = mapper.getBlockStateFromBlockId(originalBlockId);
            Long aboveMappingId = getAboveMapping(world, section, source, localX, localY, localZ);
            int renderBlockId = originalBlockId;

            if (aboveMappingId != null) {
                BlockPos pos = new BlockPos((((section.x << 5) + localX) << section.lvl) + centerOffset,
                        (((section.y << 5) + localY) << section.lvl) + centerOffset,
                        (((section.z << 5) + localZ) << section.lvl) + centerOffset);
                renderBlockId = getSeasonalRenderBlockId(mapper, mappingId, aboveMappingId, originalBlockId, state, pos);
            }
            if (renderBlockId != storedBlockId) {
                output[index] = Mapper.withBlockBiome(mappingId, renderBlockId, Mapper.getBiomeId(mappingId));
            }
        }
        return output;
    }

    private static @Nullable Long getAboveMapping(WorldEngine world, WorldSection section, long[] currentData, int localX, int localY, int localZ) {
        if (localY < 31) return currentData[WorldSection.getIndex(localX, localY + 1, localZ)];
        WorldSection above = world.acquireIfExists(section.lvl, section.x, section.y + 1, section.z);
        if (above == null) return null;
        try {
            return above._unsafeGetRawDataArray()[WorldSection.getIndex(localX, 0, localZ)];
        } finally {
            above.release(WorldSection.RELEASE_HINT_POSSIBLE_REUSE);
        }
    }

    private static int getSeasonalRenderBlockId(Mapper mapper, long mappingId, long aboveMappingId, int originalBlockId, BlockState state, BlockPos pos) {
        Level level = ClientCon.getUseLevel();
        if (level == null) return originalBlockId;
        int aboveBlockId = fixId(mapper, Mapper.getBlockId(aboveMappingId));
        BlockState stateAbove = mapper.getBlockStateFromBlockId(aboveBlockId);
        int light = Mapper.getLightId(aboveMappingId);
        int skyLight = light & 0x0F;
        int blockLight = light >> 4 & 0x0F;
        if (skyLight <= 9) return originalBlockId;
        if (CommonConfig.Snow.notSnowyNearGlowingBlock.get()
                && blockLight >= CommonConfig.Snow.notSnowyNearGlowingBlockLevel.getAsInt()) return originalBlockId;
        Biome biome = getBiome(level, mapper, Mapper.getBiomeId(mappingId));
        if (biome == null) return originalBlockId;
        if (isFreezableWater(state)) {
            return shouldFreeze(level, biome, state, stateAbove, pos) ? VIRTUAL_ICE_BLOCK_ID : originalBlockId;
        }
        int flag = MapChecker.getDefaultBlockTypeFlag(state);
        if (flag <= MapChecker.FLAG_NONE || !canRenderSnow(state, stateAbove, flag)) return originalBlockId;
        return MapChecker.shouldSnowAtBiome(level, biome, state, RANDOM_SOURCE_THREAD_LOCAL, state.getSeed(pos), pos)
                ? MAX_VOXY_BLOCK_ID - originalBlockId : originalBlockId;
    }

    private static boolean canRenderSnow(BlockState state, BlockState stateAbove, int flag) {
        if (MapChecker.leaveLike(flag)) {
            boolean specialLeaves = stateAbove.is(state.getBlock())
                    && (Heightmap.Types.MOTION_BLOCKING_NO_LEAVES.isOpaque().test(stateAbove)
                    || MapChecker.extraSnowPassable(stateAbove));
            return !specialLeaves || CommonConfig.Snow.snowyTree.get();
        }
        return !MapChecker.extraSnowPassable(state) || !MapChecker.extraSnowPassable(stateAbove);
    }

    private static boolean shouldFreeze(Level level, Biome biome, BlockState water, BlockState stateAbove, BlockPos pos) {
        return ClientConfig.Debug.frozenWater.get() && stateAbove.isAir()
                && MapChecker.shouldSnowAtBiome(level, biome, water, RANDOM_SOURCE_THREAD_LOCAL, water.getSeed(pos), pos);
    }

    private static @Nullable Biome getBiome(Level level, Mapper mapper, int biomeId) {
        if (biomeId < 0 || biomeId >= mapper.getBiomeEntries().length) return null;
        ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME,
                Identifier.parse(mapper.getBiomeEntries()[biomeId].biome));
        return level.registryAccess().lookupOrThrow(Registries.BIOME).get(key)
                .map(Holder.Reference::value).orElse(null);
    }

    public static boolean isFreezableWater(BlockState state) {
        return state.is(Blocks.WATER) && state.getFluidState().isSourceOfType(Fluids.WATER);
    }

    public static boolean isVirtualIceId(int blockId) {
        return blockId == VIRTUAL_ICE_BLOCK_ID;
    }

    public static int fixId(Mapper mapper, int blockId) {
        return fixId(mapper, blockId, ignored -> {
        });
    }

    public static int fixId(Mapper mapper, int blockId, IntConsumer snowyStateConsumer) {
        if (isVirtualIceId(blockId)) return blockId;
        int blockStateCount = mapper.getBlockStateCount();
        if (blockId < blockStateCount) return blockId;
        int decoded = MAX_VOXY_BLOCK_ID - blockId;
        if (decoded < blockStateCount) {
            snowyStateConsumer.accept(decoded);
            return decoded;
        }
        return blockId;
    }
}
