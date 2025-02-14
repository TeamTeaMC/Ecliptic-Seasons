package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Arrays;

public record BiomeHolder(
        int[] biomes, boolean hasUpdated, int version
) {
    public static final Codec<BiomeHolder> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    snowyRemoverInstance ->
                            snowyRemoverInstance.group(
                                            Codec.INT.sizeLimitedListOf(16 * 16)
                                                    .fieldOf("blocks").forGetter(snowyRemover ->
                                                            Arrays.stream(snowyRemover
                                                                            .biomes())
                                                                    .boxed().toList()
                                                    )
                                            , Codec.BOOL.fieldOf("has_updated").forGetter(BiomeHolder::hasUpdated)
                                            , Codec.INT.fieldOf("version").forGetter(BiomeHolder::version)
                                    )
                                    .apply(snowyRemoverInstance, (biomes, hasUpdated, v) ->
                                            new BiomeHolder(biomes.stream().mapToInt(s -> s).toArray(), hasUpdated, v)
                                    )
            )
    );

    public static BiomeHolder prepareBiomes(Level serverLevel, ChunkPos chunkPos, int biomeDataVersion) {
        int[] newBiomes = new int[256];
        boolean near = true;

        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int xm = chunkPos.getBlockX(i);
                int zm = chunkPos.getBlockZ(j);
                mutableBlockPos.set(xm, 0, zm);

                newBiomes[i * 16 + j] =
                        MapChecker.biomeToId(serverLevel, MapChecker.getUnCachedSurfaceBiome(serverLevel, mutableBlockPos).value());
                near &= MapChecker.isLoadNearBy(serverLevel, mutableBlockPos);

                if (!near) break;
            }
        }

        return new BiomeHolder(newBiomes, near, biomeDataVersion);
    }


    public int getBiomeId(BlockPos blockPos) {
        // return -1;
        return hasUpdated ? biomes[((blockPos.getX() & 15) * 16) + (blockPos.getZ() & 15)] : -1;
    }
}
