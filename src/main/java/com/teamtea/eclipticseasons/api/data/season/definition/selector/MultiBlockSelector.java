package com.teamtea.eclipticseasons.api.data.season.definition.selector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import com.teamtea.eclipticseasons.api.data.season.definition.condition.IChangeCondition;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.core.crop.NaturalPlantHandler;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@Builder
public class MultiBlockSelector implements IChangeSelector {

    public static final MapCodec<MultiBlockSelector> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Part.CODEC.listOf().fieldOf("parts").forGetter(o -> o.multiBlocks),
            Codec.INT.optionalFieldOf("weight", IChangeSelector.DEFAULT_WEIGHT).forGetter(o -> o.weight),
            CodecUtil.listFrom(IChangeCondition.CODEC).optionalFieldOf("conditions", List.of()).forGetter(o -> o.conditions),
            Vec3i.CODEC.optionalFieldOf("offset").forGetter(o -> o.offset),
            ResourceKey.codec(Registries.LOOT_TABLE).optionalFieldOf("loot").forGetter(o -> o.loot)
    ).apply(ins, MultiBlockSelector::new));

    @Singular
    private final List<Part> multiBlocks;

    @Builder.Default
    private final int weight = IChangeSelector.DEFAULT_WEIGHT;
    @Singular
    private final List<IChangeCondition> conditions;
    @Builder.Default
    private final Optional<Vec3i> offset = Optional.empty();
    @Builder.Default
    private final Optional<ResourceKey<LootTable>> loot = Optional.empty();

    @Override
    public Identifier getType() {
        return ChangeSelectors.MULTI_BLOCKS;
    }

    @Override
    public MapCodec<? extends IChangeSelector> codec() {
        return CODEC;
    }

    @Override
    public boolean place(ServerLevel level, BlockPos origin, ISeasonChangeContext context) {
        List<Part> get = multiBlocks;
        int count = 0;
        for (int j = 0, getSize = get.size(); j < getSize; j++) {
            Part part = get.get(j);
            BlockState newState = part.state;
            BlockPos newpos = part.offset.map(origin::offset).orElse(origin);
            if (part.replace.isEmpty()) {
                BlockState oldState = level.getBlockState(newpos);
                if (oldState.canBeReplaced(Fluids.EMPTY)) {
                    boolean set = NaturalPlantHandler.setBlockAndSelfCheck(level, newpos, newState, oldState);
                    count += set ? 1 : 0;
                }
            } else if (part.replace.get() || level.isEmptyBlock(newpos)) {
                boolean set = NaturalPlantHandler.setBlockAndSelfCheck(level, newpos, newState);
                count += set ? 1 : 0;
            }
        }
        return count > 0;
    }

    @Data
    @Builder
    public static final class Part {
        public static final Codec<Part> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                BlockState.CODEC.fieldOf("block").forGetter(o -> o.state),
                Vec3i.CODEC.optionalFieldOf("offset").forGetter(o -> o.offset),
                Codec.BOOL.optionalFieldOf("replace").forGetter(o -> o.replace)
        ).apply(ins, Part::new));

        private final BlockState state;
        @Builder.Default
        private final Optional<Vec3i> offset = Optional.empty();
        @Builder.Default
        private final Optional<Boolean> replace = Optional.empty();
    }
}
