package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.Arrays;

public record BiomeHolder(
        int[] biomes, boolean hasUpdated
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
                                    )
                                    .apply(snowyRemoverInstance, (biomes, hasUpdated) ->
                                            new BiomeHolder(biomes.stream().mapToInt(s -> s).toArray(), hasUpdated)
                                    )
            )
    );

    public SimplePair<int[], Boolean> prepareBiomes(ServerLevel serverLevel, ChunkPos chunkPos) {
        int[] newBiomes = new int[256];
        boolean near = true;
        if (!hasUpdated) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    int xm = chunkPos.getBlockX(i);
                    int zm = chunkPos.getBlockZ(j);
                    mutableBlockPos.set(xm, 0, zm);
                    newBiomes[i * 16 + j] =
                            MapChecker.biomeToId(serverLevel, MapChecker.getSurfaceBiome(serverLevel, mutableBlockPos).value());
                    near &= MapChecker.isLoadNearBy(serverLevel, mutableBlockPos);
                }
            }
            if (!near) {
                EclipticSeasons.logger("warning", chunkPos, near);
            }
        } else {
            System.arraycopy(biomes, 0, newBiomes, 0, biomes.length);
        }
        return SimplePair.of(newBiomes, near);
    }

    public boolean fillArray(int[] ints, ServerLevel serverLevel, ChunkPos chunkPos) {
        SimplePair<int[], Boolean> pair = prepareBiomes(serverLevel, chunkPos);
        System.arraycopy(pair.getKey(), 0, ints, 0, biomes.length);
        return pair.getValue();
    }
}
