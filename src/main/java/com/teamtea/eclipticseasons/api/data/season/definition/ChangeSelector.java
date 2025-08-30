package com.teamtea.eclipticseasons.api.data.season.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Optional;

public record ChangeSelector(
        Optional<BlockState> state,
        Optional<List<MultiBlockPart>> multiBlocks,
        int weight,
        Optional<Vec3i> offset,
        boolean replace,
        boolean copyState,
        Optional<List<String>> copyStateProperties,
        List<ChangeCondition> conditions,
        Optional<ResourceLocation> loot,
        Optional<Holder<PlacedFeature>> feature) {

    public static final Codec<ChangeSelector> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            BlockState.CODEC.optionalFieldOf("block").forGetter(ChangeSelector::state),
            MultiBlockPart.CODEC.listOf().optionalFieldOf("multi_blocks").forGetter(ChangeSelector::multiBlocks),
            Codec.INT.optionalFieldOf("weight", 10).forGetter(ChangeSelector::weight),
            Vec3i.CODEC.optionalFieldOf("offset").forGetter(ChangeSelector::offset),
            Codec.BOOL.optionalFieldOf("replace", true).forGetter(ChangeSelector::replace),
            Codec.BOOL.optionalFieldOf("copy_state", false).forGetter(ChangeSelector::copyState),
            Codec.STRING.listOf().optionalFieldOf("copy_properties").forGetter(ChangeSelector::copyStateProperties),
            CodecUtil.listFrom(ChangeCondition.CODEC).optionalFieldOf("conditions", List.of()).forGetter(ChangeSelector::conditions),
            ResourceLocation.CODEC.optionalFieldOf("loot").forGetter(ChangeSelector::loot),
            CodecUtil.holderCodec(Registries.PLACED_FEATURE).optionalFieldOf("feature").forGetter(ChangeSelector::feature)
    ).apply(ins, ChangeSelector::new));

    public ChangeSelector(BlockState state) {
        this(state, 10);
    }

    public ChangeSelector(BlockState state, int weight) {
        this(state, weight, Optional.empty());
    }

    public ChangeSelector(BlockState state, int weight, Optional<Vec3i> offset) {
        this(Optional.of(state), Optional.empty(), weight, offset, true, offset.isEmpty(), Optional.empty(), List.of(), Optional.empty(), Optional.empty());
    }

    public ChangeSelector(Optional<List<MultiBlockPart>> multiBlockParts, Optional<Vec3i> offset) {
        this(Optional.empty(), multiBlockParts, 10, offset, true, false, Optional.empty(), List.of(), Optional.empty(), Optional.empty());
    }

    public static ChangeSelector of() {
        return new ChangeSelector(Optional.empty(), Optional.empty(), 10, Optional.empty(), true, false, Optional.empty(), List.of(), Optional.empty(), Optional.empty());
    }

    public static ChangeSelector of(BlockState state) {
        return new ChangeSelector(state);
    }

    public static ChangeSelector of(BlockState state, int weight) {
        return new ChangeSelector(state, weight);
    }

    public static ChangeSelector of(BlockState state, int weight, Vec3i vec3i, ChangeCondition condition, boolean replace) {
        return new ChangeSelector(Optional.of(state), Optional.empty(), weight, Optional.of(vec3i),
                replace, false, Optional.empty(), List.of(condition), Optional.empty(), Optional.empty());
    }

    public static ChangeSelector of(List<MultiBlockPart> multiBlockParts, Vec3i vec3i) {
        return new ChangeSelector(Optional.of(multiBlockParts), Optional.of(vec3i));
    }

    public boolean isValid(Level level, BlockPos pos) {
        if (conditions.isEmpty()) return true;
        for (ChangeCondition condition : conditions) {
            if (!condition.isValid(level, pos)) {
                return (false);
            }
        }
        return true;
    }
}
