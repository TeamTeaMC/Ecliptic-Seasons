package com.teamtea.eclipticseasons.api.util.backport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public record FakeBlockPredicate(Optional<HolderSet<Block>> blocks, Optional<FakeStatePropertiesPredicate> properties) {
    public static final Codec<FakeBlockPredicate> CODEC = RecordCodecBuilder.create(
            p_337342_ -> p_337342_.group(
                            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(FakeBlockPredicate::blocks),
                            FakeStatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(FakeBlockPredicate::properties)
                    )
                    .apply(p_337342_, FakeBlockPredicate::new)
    );
}
