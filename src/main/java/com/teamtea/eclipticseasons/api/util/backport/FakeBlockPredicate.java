package com.teamtea.eclipticseasons.api.util.backport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.Optional;

public record FakeBlockPredicate(Optional<HolderSet<Block>> blocks, Optional<FakeStatePropertiesPredicate> properties) {
    public static final Codec<FakeBlockPredicate> CODEC = RecordCodecBuilder.create(
            p_337342_ -> p_337342_.group(
                            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(FakeBlockPredicate::blocks),
                            FakeStatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(FakeBlockPredicate::properties)
                    )
                    .apply(p_337342_, FakeBlockPredicate::new)
    );

    public FakeBlockPredicate(HolderSet<Block> blockHolderSet) {
        this(Optional.of(blockHolderSet), Optional.empty());
    }

    public boolean matches(BlockState state) {
        return (this.blocks.isEmpty() || state.is(this.blocks.get()))
                && (this.properties.isEmpty() || this.properties.get().matches(state));
    }

    public boolean matches(Level level, BlockPos pos) {
        if (!level.isLoaded(pos))
            return false;
        return matches(level.getBlockState(pos));
    }


    public HolderSet<Block> getBlocks() {
        return blocks.orElseGet(HolderSet::direct);
    }

    public static class Builder {
        private Optional<HolderSet<Block>> blocks = Optional.empty();
        private Optional<FakeStatePropertiesPredicate> properties = Optional.empty();

        private Builder() {
        }

        public static FakeBlockPredicate.Builder block() {
            return new FakeBlockPredicate.Builder();
        }

        public FakeBlockPredicate.Builder of(Block... blocks) {
            this.blocks = Optional.of(HolderSet.direct(Block::builtInRegistryHolder, blocks));
            return this;
        }

        public FakeBlockPredicate.Builder of(TagKey<Block> tag) {
            this.blocks = Optional.of(BuiltInRegistries.BLOCK.getOrCreateTag(tag));
            return this;
        }


        public FakeBlockPredicate.Builder setProperties(FakeStatePropertiesPredicate.Builder properties) {
            this.properties = properties.build();
            return this;
        }

        public FakeBlockPredicate build() {
            return new FakeBlockPredicate(this.blocks, this.properties);
        }
    }
}
