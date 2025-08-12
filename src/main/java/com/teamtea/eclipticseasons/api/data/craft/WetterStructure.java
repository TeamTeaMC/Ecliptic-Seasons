package com.teamtea.eclipticseasons.api.data.craft;

import com.google.common.annotations.Beta;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import net.minecraft.advancements.critereon.BlockPredicate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.Optional;

/**
* Better for define a structure with a rare grow_chance for start check which would cost much time and depend on random tick.
**/
// @TestOnly
// @Deprecated(forRemoval = true)
// @Beta
@ApiStatus.Experimental
// @SuppressWarnings("removal")
public record WetterStructure(
        float level, float range,
        int lastingTime,
        boolean enableAirCheck,
        Optional<BlockPredicate> core,
        List<PosAndBlockStateCheck> blockStatePredicate
) {
    public static final Codec<WetterStructure> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.FLOAT.fieldOf("level").forGetter(WetterStructure::level),
            Codec.FLOAT.fieldOf("range").forGetter(WetterStructure::range),
            Codec.INT.fieldOf("lasting_time").forGetter(WetterStructure::lastingTime),
            Codec.BOOL.optionalFieldOf("air_check",true).forGetter(WetterStructure::enableAirCheck),
            BlockPredicate.CODEC.optionalFieldOf("core").forGetter(WetterStructure::core),
            PosAndBlockStateCheck.CODEC.listOf().fieldOf("require").forGetter(WetterStructure::blockStatePredicate)
    ).apply(builder, WetterStructure::new));
}
