package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

@Data
public class SnowyRemover {
    final int[][] blockWatcher;

    public int[][] blockWatcher() {
        return blockWatcher;
    }

    public static final Codec<SnowyRemover> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    snowyRemoverInstance ->
                            snowyRemoverInstance.group(Codec.INT.sizeLimitedListOf(16 * 16)
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
        cacheTag = null;
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

        private static final SnowyFlag[] VALUES = SnowyFlag.values();

        public static SnowyFlag[] collectValues() {
            return VALUES;
        }

        public SnowyFlag cycle() {
            SnowyFlag[] snowyFlags = collectValues();
            int index = ordinal();
            index = index < snowyFlags.length - 1 ? index + 1 : 0;
            return snowyFlags[index];
        }

        public static SnowyFlag cycle(int index) {
            SnowyFlag[] snowyFlags = collectValues();
            index = index < snowyFlags.length - 1 ? index + 1 : 0;
            return snowyFlags[index];
        }
    }

    public static SnowyRemover empty() {
        return new SnowyRemover(new int[16][16]);
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    private transient CompoundTag cacheTag = null;

    public static class Serializer implements IAttachmentSerializer<SnowyRemover> {

        @Override
        public SnowyRemover read(IAttachmentHolder holder, ValueInput input) {
            var snowyStatus = input.read("snowy_remover", CODEC);
            return snowyStatus.orElseGet(SnowyRemover::empty);
        }

        @Override
        public boolean write(SnowyRemover attachment, ValueOutput output) {
            output.storeNullable("snowy_remover", CODEC, attachment);
            return false;
        }

        //@Override
        //public @NotNull SnowyRemover read(@NotNull IAttachmentHolder holder, @NotNull Tag tag, HolderLookup.@NotNull Provider provider) {
        //    Optional<SnowyRemover> result = CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result();
        //    return result.orElseGet(SnowyRemover::empty);
        //}
        //
        //@Override
        //public Tag write(@NotNull SnowyRemover attachment, HolderLookup.@NotNull Provider provider) {
        //    if (attachment.cacheTag != null) {
        //        return attachment.cacheTag;
        //    }
        //    Optional<Tag> result = CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), attachment).result();
        //    if (result.orElse(null) instanceof CompoundTag compoundTag) {
        //        attachment.cacheTag = compoundTag;
        //        return compoundTag;
        //    }
        //    return new CompoundTag();
        //}

    }
}
