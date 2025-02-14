package com.teamtea.eclipticseasons.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.Optional;

/**
* Better for define a structure with a rare grow_chance for start check which would cost much time and depend on random tick.
**/
@TestOnly
public record WetterStructure(
        int level, float range,
        boolean enableAirCheck,
        Optional<BlockStatePropertyCondition> core,
        List<PosAndBlockStateCheck> blockStatePredicate
) {
    public static final Codec<WetterStructure> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("level").forGetter(WetterStructure::level),
            Codec.FLOAT.fieldOf("range").forGetter(WetterStructure::range),
            Codec.BOOL.optionalFieldOf("air_check",true).forGetter(WetterStructure::enableAirCheck),
            BlockStatePropertyCondition.CODEC.codec().optionalFieldOf("core").forGetter(WetterStructure::core),
            PosAndBlockStateCheck.DIRECT_CODEC.listOf().fieldOf("require").forGetter(WetterStructure::blockStatePredicate)
    ).apply(builder, WetterStructure::new));
}
