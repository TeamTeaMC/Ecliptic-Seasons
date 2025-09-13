package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.EclipticSeasons;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public final class BiomeHolder implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final int[] biomes;
    private boolean hasUpdated;
    private int version;

    public BiomeHolder(
            int[] biomes, boolean hasUpdated, int version
    ) {
        this.biomes = biomes;
        this.hasUpdated = hasUpdated;
        this.version = version;
    }

    public static final int FLAG_NEED_VERSION = -1;
    public static final int FLAG_FILL_SMALL = -2;

    public static final Codec<BiomeHolder> CODEC = RecordCodecBuilder.create(
            snowyRemoverInstance ->
                    snowyRemoverInstance.group(
                                    Codec.INT.listOf()
                                            .fieldOf("blocks").forGetter(snowyRemover ->
                                                    IntList.of(snowyRemover.biomes())
                                            )
                                    , Codec.BOOL.fieldOf("has_updated").forGetter(BiomeHolder::hasUpdated)
                                    , Codec.INT.fieldOf("version").forGetter(BiomeHolder::version)
                            )
                            .apply(snowyRemoverInstance, (biomes, hasUpdated, v) ->
                                    {
                                        int[] biomeArryas;
                                        if (biomes instanceof IntList intList) {
                                            biomeArryas = intList.toIntArray();
                                        } else {
                                            biomeArryas = new int[biomes.size()];
                                            for (int i = 0; i < biomes.size(); i++) {
                                                biomeArryas[i] = biomes.get(i);
                                            }
                                        }
                                        return new BiomeHolder(biomeArryas, hasUpdated, v);
                                    }
                            )
    );

    public static BiomeHolder prepareBiomes(Level serverLevel, ChunkPos chunkPos, int biomeDataVersion, boolean registryUpdate) {
        int[] newBiomes = new int[256];
        boolean near = true;
        Registry<Biome> biomeRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BIOME);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int xm = chunkPos.getBlockX(i);
                int zm = chunkPos.getBlockZ(j);
                mutableBlockPos.set(xm, 0, zm);
                Holder<Biome> unCachedSurfaceBiome = MapChecker.getUnCachedSurfaceBiome(serverLevel, mutableBlockPos);
                newBiomes[i * 16 + j] = registryUpdate ?
                        MapChecker.biomeToId(biomeRegistry, unCachedSurfaceBiome.value()) :
                        MapChecker.biomeToId(serverLevel, unCachedSurfaceBiome.value());
                // near &= MapChecker.isLoadNearBy(serverLevel, mutableBlockPos);

                if (!near) break;
            }
        }

        return new BiomeHolder(newBiomes, near, biomeDataVersion);
    }

    public static BiomeHolder fillSmallBiomes(Level serverLevel, ChunkPos chunkPos, BiomeHolder oldHolder, int biomeDataVersion) {
        int[] newBiomes = new int[256];
        boolean near = true;
        int[] oldBiomes = oldHolder.biomes;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Holder<Biome> biomeHolder = MapChecker.idToBiome(serverLevel, oldBiomes[i * 16 + j]);
                if (MapChecker.isSmallBiome(biomeHolder)) {
                    int xm = chunkPos.getBlockX(i);
                    int zm = chunkPos.getBlockZ(j);
                    mutableBlockPos.set(xm, 0, zm);

                    newBiomes[i * 16 + j] =
                            MapChecker.biomeToId(serverLevel, MapChecker.getUnCachedSurfaceBiome(serverLevel, mutableBlockPos).value());
                    // near &= MapChecker.isLoadNearBy(serverLevel, mutableBlockPos);

                    if (!near) break;
                } else {
                    newBiomes[i * 16 + j] = oldBiomes[i * 16 + j];
                }
            }
        }

        return new BiomeHolder(newBiomes, near, biomeDataVersion);
    }


    public int getBiomeId(BlockPos blockPos) {
        // return -1;
        return hasUpdated ? biomes[((blockPos.getX() & 15) * 16) + (blockPos.getZ() & 15)] : -1;
    }


    // ===================================================================
    // 1.20.1 use

    public static BiomeHolder empty() {
        return new BiomeHolder(new int[256], false, 0);
    }


    public static final Capability<BiomeHolder> BIOME_HOLDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });


    private LazyOptional<BiomeHolder> cast = null;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == BIOME_HOLDER_CAPABILITY) {
            if (cast == null) cast = LazyOptional.of(() -> this);
            return cast.cast();
        }
        return LazyOptional.empty();
    }

    private int cacheVersion = Integer.MIN_VALUE;
    private boolean cacheUpdate = false;
    private CompoundTag cacheTag = null;

    @Override
    public CompoundTag serializeNBT() {
        if (cacheUpdate == hasUpdated && cacheVersion == version && cacheTag != null) {
            return cacheTag;
        }
        Optional<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE, this).result();
        if (result.orElse(null) instanceof CompoundTag compoundTag) {
            this.cacheTag = compoundTag;
            this.cacheUpdate = hasUpdated;
            this.cacheVersion = version;
            return compoundTag;
        }
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (version != 0 && !hasUpdated) return;
        Optional<BiomeHolder> result = CODEC.parse(NbtOps.INSTANCE, nbt).result();
        result.ifPresent(this::copyFrom);
    }

    public void copyFrom(BiomeHolder biomeHolder) {
        System.arraycopy(biomeHolder.biomes, 0, BiomeHolder.this.biomes, 0, biomeHolder.biomes.length);
        BiomeHolder.this.version = biomeHolder.version;
        BiomeHolder.this.hasUpdated = biomeHolder.hasUpdated;
    }

    public static final Map<ChunkPos, BiomeHolder> BIOME_HOLDER_MAP = new HashMap<>();

    // ======================================================================
    // record to class

    public int[] biomes() {
        return biomes;
    }

    public boolean hasUpdated() {
        return hasUpdated;
    }

    public int version() {
        return version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BiomeHolder) obj;
        return Arrays.equals(this.biomes, that.biomes) &&
                this.hasUpdated == that.hasUpdated &&
                this.version == that.version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(biomes), hasUpdated, version);
    }

    @Override
    public String toString() {
        return "BiomeHolder[" +
                "biomes=" + Arrays.toString(biomes) + ", " +
                "hasUpdated=" + hasUpdated + ", " +
                "version=" + version + ']';
    }

}
