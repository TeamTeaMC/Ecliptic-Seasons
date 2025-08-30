package com.teamtea.eclipticseasons.api.data.season.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public record MultiBlockPart(BlockState state, Optional<Vec3i> offset) {
    public static final Codec<MultiBlockPart> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            BlockState.CODEC.fieldOf("state").forGetter(MultiBlockPart::state),
            Vec3i.CODEC.optionalFieldOf("offset").forGetter(MultiBlockPart::offset)
    ).apply(ins, MultiBlockPart::new));

    public static MultiBlockPart of(BlockState state, Vec3i vec3i) {
        return new MultiBlockPart(state, Optional.of(vec3i));
    }

    public static MultiBlockPart of(BlockState state) {
        return new MultiBlockPart(state, Optional.empty());
    }

    public static List<MultiBlockPart> ofList(BlockState upper, BlockState lower) {
        return List.of(MultiBlockPart.of(upper, new Vec3i(0, 1, 0)),
                MultiBlockPart.of(lower));
    }
}
