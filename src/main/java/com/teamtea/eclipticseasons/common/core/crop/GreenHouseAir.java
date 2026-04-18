package com.teamtea.eclipticseasons.common.core.crop;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.misc.IUnpackablePalettedContainer;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.mixin.common.chunk.MixinAccessorLevelChunkSection;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;

import org.jspecify.annotations.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


public class GreenHouseAir {
    public static final int MAX_DISTANCE = 31;
    public static final LevelChunkSectionWarp EMPTY_SECTION = new LevelChunkSectionWarp(new BlockState[0], new BitSet(), null);

    public final BlockPos blockPos;
    public final BlockState blockState;
    public final int distance;
    public final GreenHouseAir parent;
    public final BlockPos origin;
    public final BlockEntity originCenter;

    public GreenHouseAir(BlockPos blockPos, BlockState blockState, int distance, GreenHouseAir parent, BlockPos origin, BlockEntity originCenter) {
        this.blockPos = blockPos;
        this.blockState = blockState;
        this.distance = distance;
        this.parent = parent;
        this.origin = origin;
        this.originCenter = originCenter;
    }

    /**
     * 从温室核心位置开始进行气体扩展，返回所有被覆盖位置的映射。
     */
    public static GreenHouseScanResult startBFS(LevelChunkSectionProvider levelChunkSectionProvider, BlockPos corePos, BlockState coreState, BlockEntity coreEntity) {
        int MAX_XZ = CommonConfig.Crop.greenHouseMaxDiameter.get() * 2;
        int MAX_Y = CommonConfig.Crop.greenHouseMaxHeight.get() * 2;
        int MAX_DISTANCE = MAX_XZ * 2
                + MAX_Y;

        Long2ObjectOpenHashMap<GreenHouseAir> greenhouseMap = new Long2ObjectOpenHashMap<>(9600);
        // LongOpenHashSet visited = new LongOpenHashSet(9600);
        Queue<GreenHouseAir> queue = new ArrayDeque<>();

        SectionQueryContext context = new SectionQueryContext();
        int leakCount = 0;
        final int MAX_LEAKS = 3;

        LongOpenHashSet outerBoundaryPositions = new LongOpenHashSet();

        GreenHouseAir root = new GreenHouseAir(corePos, coreState, 0, null, corePos, coreEntity);
        queue.add(root);


        Direction[] values = Direction.values();
        while (!queue.isEmpty()) {
            GreenHouseAir current = queue.poll();
            BlockPos pos = current.blockPos;


            if (!context.addVisited(levelChunkSectionProvider, pos)) continue;

            long longPos = pos.asLong();
            greenhouseMap.put(longPos, current);

            // boolean isOuterLayer = false;
            // for (Direction dir : Direction.values()) {
            //     BlockPos nextPos = pos.relative(dir);
            //     long nextLong = nextPos.asLong();
            //     if (!visited.contains(nextLong)) {
            //         isOuterLayer = true;
            //         break;
            //     }
            // }
            //
            // if (isOuterLayer) {
            //     outerBoundaryPositions.add(longPos);
            // }

            for (int i = 0; i < values.length; i++) {
                Direction dir = values[i];
                BlockPos nextPos = pos.relative(dir);
                // long nextLong = nextPos.asLong();
                if (context.containsVisited(levelChunkSectionProvider, nextPos)) continue;

                BlockState nextState = context.getBlockState(levelChunkSectionProvider, nextPos);
                if (!canGreenHouseAirThrough(nextState)) continue;
                int nextDistance = current.distance + 1;

                if (Mth.abs(nextPos.getX() - corePos.getX()) > MAX_XZ
                        || Mth.abs(nextPos.getY() - corePos.getY()) > MAX_XZ
                        || Mth.abs(nextPos.getZ() - corePos.getZ()) > MAX_Y) {
                    leakCount = MAX_LEAKS + 1;
                    break;
                }
                if (nextDistance > MAX_DISTANCE) {
                    leakCount++;
                    if (leakCount > MAX_LEAKS) {
                        // current = current.parent;
                        // while (current.distance > 10) {
                        //     level.setBlockAndUpdate(current.blockPos, Blocks.WATER.defaultBlockState());
                        //     BoundingBox.encapsulatingPositions(List.of(current.blockPos)).ifPresent(
                        //             boundingBox -> {
                        //                 if (level instanceof ServerLevel serverLevel)
                        //                     serverLevel.getFluidTicks().clearArea(boundingBox);
                        //             }
                        //     );
                        //     current = current.parent;
                        // }
                        break;
                    }

                    continue;
                }

                GreenHouseAir next = new GreenHouseAir(nextPos, nextState, nextDistance, current, corePos, coreEntity);
                queue.add(next);
            }

            if (leakCount > MAX_LEAKS) break;
        }
        context.release();
        levelChunkSectionProvider.close();

        boolean isClosed = leakCount <= MAX_LEAKS;
        // level.removeBlock(corePos, false);
        if (!isClosed) {
            return new GreenHouseScanResult(isClosed, new Long2ObjectOpenHashMap<>(), new LongOpenHashSet());
        }
        // outerBoundaryPositions.addAll(greenhouseMap.keySet());
        // 返回封闭状态和温室地图
        return new GreenHouseScanResult(isClosed, greenhouseMap, outerBoundaryPositions);
    }

    /**
     * 判断一个方块是否可以让温室气体扩散过去
     */
    private static boolean canGreenHouseAirThrough(BlockState state) {
        // if (true) return state.isAir();
        if (state.isSolid()) return state.getBlock() instanceof LeavesBlock
                || state.getBlock() instanceof StainedGlassBlock;
        if (state.isAir()) return true;
        return true;
    }

    public record GreenHouseScanResult(boolean isClosed, Long2ObjectOpenHashMap<GreenHouseAir> airMap,
                                       LongOpenHashSet outerBoundaryPositions) {
    }

    public static interface LevelChunkSectionProvider extends AutoCloseable {
        LevelChunkSectionWarp get(int x, int y, int z);

        @Override
        default void close() {
        }
    }

    public record LevelReaderGetter(LevelReader levelReader) implements LevelChunkSectionProvider {
        @Override
        public LevelChunkSectionWarp get(int x, int y, int z) {
            ChunkAccess chunk1 = levelReader.getChunk(x, z);
            int sectionIndex = chunk1.getSectionIndexFromSectionY(y);
            LevelChunkSection[] sections = chunk1.getSections();
            if (sectionIndex < 0 || sectionIndex >= sections.length)
                return EMPTY_SECTION;
            LevelChunkSection chunk = sections[sectionIndex];
            return LevelChunkSectionWarp.of(chunk);
        }
    }

    public record PalettedGetter(
            Long2ObjectOpenHashMap<PalettedContainer<BlockState>> levelReader) implements LevelChunkSectionProvider {
        @Override
        public LevelChunkSectionWarp get(int x, int y, int z) {
            PalettedContainer<BlockState> orDefault = levelReader.getOrDefault(
                    SectionPos.asLong(x, y, z), null
            );
            return orDefault == null ?
                    EMPTY_SECTION :
                    LevelChunkSectionWarp.of(orDefault);
        }

        @Override
        public void close() {
            levelReader.clear();
        }
    }


    public static class SectionQueryContext {
        public final List<Pair<SectionPos, LevelChunkSectionWarp>> chunkAccessList = new ArrayList<>(1);
        private SectionPos lastPos;
        private LevelChunkSectionWarp lastWarp;

        public LevelChunkSectionWarp getSectionCache(@Nullable LevelChunkSectionProvider levelAccessor, BlockPos pos) {
            int x = SectionPos.blockToSectionCoord(pos.getX());
            int z = SectionPos.blockToSectionCoord(pos.getZ());
            int y = SectionPos.blockToSectionCoord(pos.getY());

            if (lastPos != null && lastPos.x() == x
                    && lastPos.z() == z
                    && lastPos.y() == y
                    && lastWarp != null) {
                return lastWarp;
            }

            LevelChunkSectionWarp section = getSection(levelAccessor, x, y, z);
            if (section != EMPTY_SECTION) {
                lastPos = SectionPos.of(x, y, z);
                lastWarp = section;
            } else {
                lastPos = null;
                lastWarp = null;
            }
            return section;
        }

        public @Nullable LevelChunkSectionWarp getSection(int x, int y, int z) {
            for (int i = 0, size = this.chunkAccessList.size(); i < size; i++) {
                Pair<SectionPos, LevelChunkSectionWarp> chunkAccess = this.chunkAccessList.get(i);
                SectionPos first = chunkAccess.getFirst();
                if (first.x() == x
                        && first.z() == z
                        && first.y() == y) {
                    return chunkAccess.getSecond();
                }
            }
            return null;
        }

        public LevelChunkSectionWarp getSection(@Nullable LevelChunkSectionProvider levelAccessor, int x, int y, int z) {
            LevelChunkSectionWarp section = getSection(x, y, z);
            if (section != null) return section;
            if (levelAccessor == null)
                return EMPTY_SECTION;
            LevelChunkSectionWarp levelChunkSectionWarp = levelAccessor.get(x, y, z);
            this.chunkAccessList.add(Pair.of(SectionPos.of(x, y, z), levelChunkSectionWarp));
            return levelChunkSectionWarp;
        }


        public BlockState getBlockState(LevelChunkSectionProvider levelAccessor, BlockPos pos) {
            return getSectionCache(levelAccessor, pos).getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        }

        public void release() {
            this.chunkAccessList.clear();
            this.lastPos = null;
            this.lastWarp = null;
        }

        public boolean addVisited(LevelChunkSectionProvider levelAccessor, BlockPos pos) {
            return getSectionCache(levelAccessor, pos).addAndCheckAccess(
                    pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15
            );
        }

        public boolean containsVisited(LevelChunkSectionProvider levelAccessor, BlockPos pos) {
            return getSectionCache(levelAccessor, pos)
                    .contains(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        }
    }

    public static BlockState[] getLevelChunkSection(LevelChunkSection section) {
        return IUnpackablePalettedContainer.of(
                        ((MixinAccessorLevelChunkSection) section).es$getStates())
                .eclipticseasons$unpack(BlockState.class);
    }

    public record LevelChunkSectionWarp(BlockState[] states,
                                        BitSet bitSet,
                                        @Nullable LevelChunkSection section) {

        static LevelChunkSectionWarp of(LevelChunkSection section) {
            BlockState[] bsA = getLevelChunkSection(section);
            BitSet bitSet1 = new BitSet(bsA.length);
            return new LevelChunkSectionWarp(bsA, bitSet1, section);
            // return new LevelChunkSectionWarp(new BlockState[0],section);
        }

        static LevelChunkSectionWarp of(PalettedContainer<BlockState> section) {
            BlockState[] bsA = IUnpackablePalettedContainer.of(section)
                    .eclipticseasons$unpack(BlockState.class);
            BitSet bitSet1 = new BitSet(bsA.length);
            return new LevelChunkSectionWarp(bsA, bitSet1, null);
            // return new LevelChunkSectionWarp(new BlockState[0],section);
        }

        static int getLocalIndex(int x, int y, int z) {
            return y << 4 << 4 | z << 4 | x;
        }

        public BlockState getBlockState(int x, int y, int z) {
            if (states.length == 0) return Blocks.AIR.defaultBlockState();
            return this.states[getLocalIndex(x, y, z)];
            // return section.getBlockState(x, y, z);
        }

        public boolean addAndCheckAccess(int x, int y, int z) {
            if (states.length == 0) return false;
            int index = getLocalIndex(x, y, z);
            if (bitSet.get(index)) {
                return false;
            }
            bitSet.set(index);
            return true;
        }

        public boolean contains(int x, int y, int z) {
            if (states.length == 0) return false;
            int index = getLocalIndex(x, y, z);
            return bitSet.get(index);
        }

    }

    public static void scanAsync(Level level, BlockPos origin, BlockEntity coreEntity, Consumer<GreenHouseScanResult> callback) {
        int maxR = CommonConfig.Crop.greenHouseMaxDiameter.get();
        int maxH = CommonConfig.Crop.greenHouseMaxHeight.get();

        Long2ObjectOpenHashMap<PalettedContainer<BlockState>> secMap = new Long2ObjectOpenHashMap<>();

        SectionPos center = SectionPos.of(origin);
        int xzOff = (maxR + 16) / 16;
        int yOff = (maxH + 16) / 16;

        for (int i = -xzOff; i < xzOff + 1; i++) {
            int cx = center.x() + i;
            for (int j = -xzOff; j < xzOff + 1; j++) {
                int cz = center.z() + j;
                LevelChunk levelChunk = level.getChunk(cx, cz);
                for (int k = -yOff; k < yOff + 1; k++) {
                    int cy = center.y() + k;
                    int sectionIndex = levelChunk.getSectionIndexFromSectionY(cy);
                    LevelChunkSection[] sections = levelChunk.getSections();
                    if (sectionIndex < 0 || sectionIndex >= sections.length)
                        continue;
                    LevelChunkSection section = sections[sectionIndex];
                    secMap.put(
                            SectionPos.asLong(cx, cy, cz), ((MixinAccessorLevelChunkSection) section).es$getStates().copy()
                    );
                }
            }
        }

        final LevelChunkSectionProvider palettedGetter;
        // palettedGetter = new LevelReaderGetter(level);
        palettedGetter = new PalettedGetter(secMap);
        CompletableFuture.supplyAsync(() -> {
                    return startBFS(palettedGetter, origin, coreEntity.getBlockState(), coreEntity);
                })
                .thenAcceptAsync(callback, level.getServer());
    }


}
