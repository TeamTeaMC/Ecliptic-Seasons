package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

@Deprecated
@ApiStatus.Experimental
public record SnowyRemover(
        int[][] blockWatcher
) {
    public static final Codec<SnowyRemover> CODEC = RecordCodecBuilder.create(
            snowyRemoverInstance ->
                    snowyRemoverInstance.group(Codec.INT.listOf()
                                    .fieldOf("blocks").forGetter(snowyRemover -> Arrays.stream(snowyRemover
                                                    .blockWatcher())
                                            .flatMapToInt(Arrays::stream)
                                            .boxed().toList()
                                    )
                            )
                            .apply(snowyRemoverInstance, ss ->
                                    new SnowyRemover(
                                            IntStream.range(0, 16)
                                                    .mapToObj(i -> ss.subList(i * 16, (i + 1) * 16).stream().mapToInt(Integer::intValue).toArray())
                                                    .toArray(int[][]::new)
                                    )
                            )
    );

    public static final int SNOWY = 0;
    public static final int NONE_SNOWY = 1;
    public static final int SNOWY_EVEN_LIGHT = 2;

    public static int getChunkPos(int golobalPos) {
        return golobalPos & 15;
    }

    public void setChunkPos(BlockPos blockPos, int value) {
        blockWatcher[getChunkPos(blockPos.getX())][getChunkPos(blockPos.getZ())] = value;
    }

    public boolean notSnowyAt(int x, int z) {
        return blockWatcher[x][z] == NONE_SNOWY;
    }

    public boolean notSnowyAt(BlockPos blockPos) {
        return notSnowyAt(getChunkPos(blockPos.getX()), getChunkPos(blockPos.getZ()));
    }

    public SnowyFlag getSnowyFlag(BlockPos blockPos) {
        return SnowyFlag.values()[blockWatcher[getChunkPos(blockPos.getX())][getChunkPos(blockPos.getZ())]];
    }

    public boolean allSnowAble() {
        return Arrays.stream(blockWatcher())
                .flatMapToInt(Arrays::stream)
                .noneMatch(c -> c > 0);
    }

    public enum SnowyFlag {
        SNOWY(ParticleTypes.SNOWFLAKE, (r) -> null),
        NONE_SNOWY(ParticleTypes.SMOKE, (r) -> r.nextInt(3) > 0 ? ParticleTypes.SMOKE : ParticleTypes.CLOUD),
        SNOWY_EVEN_LIGHT(ParticleTypes.GLOW_SQUID_INK, (r) -> r.nextInt(3) > 0 ? ParticleTypes.GLOW_SQUID_INK : null),
        SNOWY_ALWAYS(ParticleTypes.ITEM_SNOWBALL, (r) -> r.nextInt(3) > 0 ? ParticleTypes.ITEM_SNOWBALL : null);

        final ParticleOptions particleOptions;
        final Function<RandomSource, ParticleOptions> randomSourceConsumer;

        SnowyFlag(ParticleOptions particleOptions, Function<RandomSource, ParticleOptions> randomSourceConsumer) {
            this.particleOptions = particleOptions;
            this.randomSourceConsumer = randomSourceConsumer;
        }

        public ParticleOptions getNextParticleOptions() {
            return particleOptions;
        }

        public ParticleOptions getIndicatorParticleOptions(RandomSource r) {
            return randomSourceConsumer.apply(r);
        }

        public SnowyFlag cycle() {
            SnowyFlag[] snowyFlags = values();
            int index = ordinal();
            index = index < snowyFlags.length - 1 ? index + 1 : 0;
            return snowyFlags[index];
        }

        public static SnowyFlag cycle(int index) {
            SnowyFlag[] snowyFlags = values();
            index = index < snowyFlags.length - 1 ? index + 1 : 0;
            return snowyFlags[index];
        }

    }

}
