package com.teamtea.eclipticseasons.api.util.backport;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

public record FakeBlockPredicate(Optional<HolderSet<Block>> blocks,
                                 Optional<FakeStatePropertiesPredicate> properties,
                                 Optional<NbtPredicate> nbt
) {
    public static final Codec<FakeBlockPredicate> CODEC = RecordCodecBuilder.create(
            p_337342_ -> p_337342_.group(
                            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(FakeBlockPredicate::blocks),
                            FakeStatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(FakeBlockPredicate::properties),
                            ExtraCodecs.JSON.optionalFieldOf("nbt").forGetter(c -> Optional.of(c.nbt().map(NbtPredicate::serializeToJson).orElse(new JsonObject())))
                    )
                    .apply(p_337342_, (holders, fakeStatePropertiesPredicate, jsonElement) ->
                            new FakeBlockPredicate(holders, fakeStatePropertiesPredicate, jsonElement.map(NbtPredicate::fromJson)))
    );

    public FakeBlockPredicate(HolderSet<Block> blockHolderSet) {
        this(blockHolderSet, Optional.empty());
    }

    public FakeBlockPredicate(HolderSet<Block> blockHolderSet,
                              Optional<FakeStatePropertiesPredicate> properties) {
        this(Optional.of(blockHolderSet), properties, Optional.empty());
    }

    public FakeBlockPredicate(Optional<HolderSet<Block>> blocks,
                              Optional<FakeStatePropertiesPredicate> properties) {
        this(blocks, properties, Optional.empty());
    }

    public boolean matches(BlockState state) {
        return (this.blocks.isEmpty() || state.is(this.blocks.get()))
                && (this.properties.isEmpty() || this.properties.get().matches(state));
    }

    public static boolean matchesBlockEntity(LevelReader level, @Nullable BlockEntity blockEntity, NbtPredicate nbtPredicate) {
        return blockEntity != null && nbtPredicate.matches(blockEntity.saveWithFullMetadata());
    }

    public boolean matches(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        } else {
            return this.matches(level.getBlockState(pos))
                    && (this.nbt.isEmpty()
                    || matchesBlockEntity(level, level.getBlockEntity(pos), this.nbt.get()));
        }
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
