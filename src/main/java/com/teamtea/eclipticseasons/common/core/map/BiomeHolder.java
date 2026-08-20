package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

@Data
public class BiomeHolder {
    final int[] biomes;
    final boolean hasUpdated;
    final int version;

    public int[] biomes() {
        return biomes;
    }

    public boolean hasUpdated() {
        return hasUpdated;
    }

    public int version() {
        return version;
    }

    public static final int FLAG_NEED_VERSION = -1;
    public static final int FLAG_FILL_SMALL = -2;

    public static final Codec<BiomeHolder> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    snowyRemoverInstance ->
                            snowyRemoverInstance.group(
                                            Codec.INT.sizeLimitedListOf(16 * 16)
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
            )
    );

    public static BiomeHolder prepareBiomes(Level serverLevel, ChunkAccess chunk, ChunkPos chunkPos, int biomeDataVersion, boolean registryUpdate) {
        int[] newBiomes = new int[256];
        boolean near = true;
        Registry<Biome> biomeRegistry = serverLevel.registryAccess().lookupOrThrow(Registries.BIOME);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int xm = chunkPos.getBlockX(i);
                int zm = chunkPos.getBlockZ(j);
                mutableBlockPos.set(xm, chunk.getHeight(Heightmap.Types.WORLD_SURFACE, i, j) + 1, zm);
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

    public static BiomeHolder fillSmallBiomes(Level serverLevel, ChunkAccess chunk, BiomeHolder oldHolder, int biomeDataVersion) {
        int[] newBiomes = new int[256];
        boolean near = true;
        int[] oldBiomes = oldHolder.biomes;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        ChunkPos chunkPos = chunk.getPos();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Holder<Biome> biomeHolder = MapChecker.idToBiome(serverLevel, oldBiomes[i * 16 + j]);
                if (MapChecker.isSmallBiome(biomeHolder)) {
                    int xm = chunkPos.getBlockX(i);
                    int zm = chunkPos.getBlockZ(j);
                    mutableBlockPos.set(xm, chunk.getHeight(Heightmap.Types.WORLD_SURFACE, i, j) + 1, zm);

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

    public static BiomeHolder empty() {
        return new BiomeHolder(new int[256], false, 0);
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    private transient int cacheVersion = Integer.MIN_VALUE;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    private transient boolean cacheUpdate = false;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    private transient CompoundTag cacheTag = null;

    public static class Serializer implements IAttachmentSerializer<BiomeHolder> {

        @Override
        public BiomeHolder read(IAttachmentHolder holder, ValueInput input) {
            var snowyStatus = input.read("biome_holder", CODEC);
            return snowyStatus.orElseGet(BiomeHolder::empty);
        }

        @Override
        public boolean write(BiomeHolder attachment, ValueOutput output) {
            output.storeNullable("biome_holder", CODEC, attachment);
            return true;
        }

        //
        //@Override
        //public Tag write(@NonNull BiomeHolder attachment, HolderLookup.@NonNull Provider provider) {
        //    if (attachment.cacheUpdate == attachment.hasUpdated && attachment.cacheVersion == attachment.version && attachment.cacheTag != null) {
        //        return attachment.cacheTag;
        //    }
        //    Optional<Tag> result = CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), attachment).result();
        //    if (result.orElse(null) instanceof CompoundTag compoundTag) {
        //        attachment.cacheTag = compoundTag;
        //        attachment.cacheUpdate = attachment.hasUpdated;
        //        attachment.cacheVersion = attachment.version;
        //        return compoundTag;
        //    }
        //    return new CompoundTag();
        //}

    }
}
