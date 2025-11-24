package com.teamtea.eclipticseasons.api.data.craft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.api.util.backport.FakeBlockPredicate;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.Vec3i;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.jetbrains.annotations.ApiStatus;

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
        Optional<FakeBlockPredicate> core,
        List<PosAndBlockStateCheck> blockStatePredicate
) {
    public static final Codec<WetterStructure> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.FLOAT.fieldOf("level").forGetter(WetterStructure::level),
            Codec.FLOAT.fieldOf("range").forGetter(WetterStructure::range),
            Codec.INT.optionalFieldOf("lasting_time",200).forGetter(WetterStructure::lastingTime),
            Codec.BOOL.optionalFieldOf("air_check", true).forGetter(WetterStructure::enableAirCheck),
            FakeBlockPredicate.CODEC.optionalFieldOf("core").forGetter(WetterStructure::core),
            PosAndBlockStateCheck.CODEC.listOf().fieldOf("require").forGetter(WetterStructure::blockStatePredicate)
    ).apply(builder, WetterStructure::new));

    public static final Codec<WetterStructure> DIRECT_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.FLOAT.fieldOf("level").forGetter(WetterStructure::level),
            Codec.FLOAT.fieldOf("range").forGetter(WetterStructure::range),
            Codec.INT.fieldOf("lasting_time").forGetter(WetterStructure::lastingTime),
            Codec.BOOL.optionalFieldOf("air_check", true).forGetter(WetterStructure::enableAirCheck)
    ).apply(builder, (aFloat, aFloat2, integer, aBoolean) -> new WetterStructure(aFloat, aFloat2, integer, aBoolean, Optional.empty(), List.of())));

    public List<PosAndBlockStateCheck> checks() {
        return ConcatenatedListView.of(core
                        .map(e -> List.of(new PosAndBlockStateCheck(Vec3i.ZERO, e)))
                        .orElse(List.of())
                , blockStatePredicate);
    }

    public long lasting_time() {
        return lastingTime;
    }
}
