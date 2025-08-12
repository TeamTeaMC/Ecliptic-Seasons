package com.teamtea.eclipticseasons.api.util.backport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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
}
