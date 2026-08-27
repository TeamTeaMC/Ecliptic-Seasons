package com.teamtea.eclipticseasons.compat.voxy;

import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.stub.PlainsStubHolder;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.function.IntConsumer;

public final class VoxyTool {
    public static final int MAX_VOXY_BLOCK_ID = 0xFFFFF;
    public static final int VIRTUAL_ICE_BLOCK_ID = MAX_VOXY_BLOCK_ID;
    private static final RandomSource RANDOM_SOURCE_THREAD_LOCAL = RandomSource.createThreadLocalInstance(42L);
    private static final ThreadLocal<BlockPos.MutableBlockPos> MUTABLE_BLOCK_POS_THREAD_LOCAL = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

    private VoxyTool() {
    }

    public static boolean isVoxyTest() {
        return CompatModule.CommonConfig.voxyCompatibility.get();
    }

    /**
     * Creates a render-only seasonal view without mutating
     * Voxy sections or Mapper storage.
     */
    public static long[] createSeasonalRenderData(WorldEngine world, WorldSection section, long[] source) {
        if (!isVoxyTest()) return source;

        Mapper mapper = world.getMapper();
        long[] output = source;

        int scale = 1 << section.lvl;
        int centerOffset = section.lvl == 0 ? 0 : scale >> 1;
        int sectionBaseX = ((section.x << 5) << section.lvl) + centerOffset;
        int sectionBaseY = ((section.y << 5) << section.lvl) + centerOffset;
        int sectionBaseZ = ((section.z << 5) << section.lvl) + centerOffset;

        BlockPos.MutableBlockPos pos = MUTABLE_BLOCK_POS_THREAD_LOCAL.get();

        WorldSection aboveSection = null;
        long[] aboveData = null;
        boolean aboveSectionChecked = false;

        try {
            for (int index = 0; index < source.length; index++) {
                long mappingId = source[index];
                if (Mapper.isAir(mappingId)) continue;

                int storedBlockId = Mapper.getBlockId(mappingId);
                int originalBlockId = fixId(mapper, storedBlockId);
                BlockState state = mapper.getBlockStateFromBlockId(originalBlockId);

                // 先排除不支持积雪且不是可冻结水源的方块。
                boolean freezableWater = isFreezableWater(state);
                int blockFlag = MapChecker.getDefaultBlockTypeFlag(state);
                if (!freezableWater && blockFlag <= MapChecker.FLAG_NONE) continue;

                int localX = index & 31;
                int localZ = index >> 5 & 31;
                int localY = index >> 10 & 31;

                long aboveMappingId;

                if (localY < 31) {
                    // 上方方块仍然位于当前 section。
                    aboveMappingId = source[index + 1024];
                } else {
                    // 只有 localY == 31 时，上方方块才跨 section。
                    if (!aboveSectionChecked) {
                        aboveSectionChecked = true;
                        aboveSection = world.acquireIfExists(
                                section.lvl,
                                section.x,
                                section.y + 1,
                                section.z
                        );

                        if (aboveSection != null) {
                            aboveData = aboveSection._unsafeGetRawDataArray();
                        }
                    }

                    if (aboveData == null) continue;

                    // 读取上方 section 的 localY == 0 平面。
                    aboveMappingId = aboveData[index & 0x3FF];
                }

                pos.set(
                        sectionBaseX + localX * scale,
                        sectionBaseY + localY * scale,
                        sectionBaseZ + localZ * scale
                );

                int renderBlockId = getSeasonalRenderBlockId(
                        mapper,
                        mappingId,
                        aboveMappingId,
                        originalBlockId,
                        state,
                        pos
                );

                if (renderBlockId == storedBlockId) continue;

                if (output == source) {
                    output = Arrays.copyOf(source, source.length);
                }

                output[index] = Mapper.withBlockBiome(
                        mappingId,
                        renderBlockId,
                        Mapper.getBiomeId(mappingId)
                );
            }

            return output;
        } finally {
            if (aboveSection != null) {
                aboveSection.release(
                        WorldSection.RELEASE_HINT_POSSIBLE_REUSE
                );
            }
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
        Holder<Biome> biome = getBiome(level, mapper, Mapper.getBiomeId(mappingId));
        if (biome == null) return originalBlockId;
        if (isFreezableWater(state)) {
            return shouldFreeze(level, biome.value(), state, stateAbove, pos) ? VIRTUAL_ICE_BLOCK_ID : originalBlockId;
        }
        int flag = MapChecker.getDefaultBlockTypeFlag(state);
        if (flag <= MapChecker.FLAG_NONE || !canRenderSnow(state, stateAbove, flag)) return originalBlockId;
        return MapChecker.shouldSnowAtBiome(level, biome.value(), state, RANDOM_SOURCE_THREAD_LOCAL, state.getSeed(pos), pos)
                ? MAX_VOXY_BLOCK_ID - originalBlockId : originalBlockId;
    }

    private static boolean canRenderSnow(BlockState state, BlockState stateAbove, int flag) {
        if (MapChecker.leaveLike(flag)) {
            boolean specialLeaves = stateAbove.is(state.getBlock())
                    && (Heightmap.Types.MOTION_BLOCKING_NO_LEAVES.isOpaque().test(stateAbove)
                    || MapChecker.extraSnowPassable(stateAbove));
            return !specialLeaves || CommonConfig.Snow.snowyTree.get();
        }
        return !MapChecker.solidTest(stateAbove);
    }

    private static boolean shouldFreeze(Level level, Biome biome, BlockState water, BlockState stateAbove, BlockPos pos) {
        return ClientConfig.Debug.frozenWater.get() && stateAbove.isAir()
                && MapChecker.shouldSnowAtBiome(level, biome, water, RANDOM_SOURCE_THREAD_LOCAL, water.getSeed(pos), pos);
    }

    private static final Object BIOME_CACHE_LOCK = new Object();
    private static final VarHandle BIOME_ARRAY_HANDLE = MethodHandles.arrayElementVarHandle(Holder[].class);
    private static volatile Holder<Biome>[] BIOME_ID_CACHE = new Holder[512];

    private static @Nullable Holder<Biome> getBiome(Level level, Mapper mapper, int biomeId) {
        if (biomeId < 0) return null;
        Holder<Biome>[] cache = BIOME_ID_CACHE;
        if (biomeId < cache.length) {
            Holder<Biome> biome = (Holder<Biome>) BIOME_ARRAY_HANDLE.getAcquire(cache, biomeId);
            if (biome != null) return biome;
        }

        Mapper.BiomeEntry entry = ((IVoxyMapper) mapper).eclipticseasons$getBiomeEntry(biomeId);
        if (entry == null) return null;
        ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, Identifier.parse(entry.biome));
        Holder<Biome> resolved = level.registryAccess().lookupOrThrow(Registries.BIOME).get(key).orElse(PlainsStubHolder.PLAINS);

        synchronized (BIOME_CACHE_LOCK) {
            cache = BIOME_ID_CACHE;
            if (biomeId < cache.length) {
                Holder<Biome> cached = (Holder<Biome>) BIOME_ARRAY_HANDLE.getAcquire(cache, biomeId);
                if (cached != null) return cached;
            } else {
                cache = Arrays.copyOf(cache, Math.max(biomeId + 1, cache.length << 1));
                BIOME_ID_CACHE = cache;
            }
            BIOME_ARRAY_HANDLE.setRelease(cache, biomeId, resolved);
            return resolved;
        }
    }

    public static void clearBiomeCache() {
        synchronized (BIOME_CACHE_LOCK) {
            BIOME_ID_CACHE = new Holder[256];
        }
    }

    public static boolean isFreezableWater(BlockState state) {
        return state.is(Blocks.WATER) && state.getFluidState().isSourceOfType(Fluids.WATER);
    }

    public static boolean isVirtualIceId(int blockId) {
        return blockId == VIRTUAL_ICE_BLOCK_ID;
    }

    public static int fixId(Mapper mapper, int blockId) {
        return fixId(mapper, blockId, null);
    }

    public static int fixId(Mapper mapper, int blockId, @Nullable IntConsumer snowyStateConsumer) {
        if (isVirtualIceId(blockId)) return blockId;
        int blockStateCount = mapper.getBlockStateCount();
        if (blockId < blockStateCount) return blockId;
        int decoded = MAX_VOXY_BLOCK_ID - blockId;
        if (decoded < blockStateCount) {
            if (snowyStateConsumer != null)
                snowyStateConsumer.accept(decoded);
            return decoded;
        }
        return blockId;
    }
}
