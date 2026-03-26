package com.teamtea.eclipticseasons.api.data.season.definition.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;


@Builder
@Data
public class EmptyAboveCondition implements IChangeCondition {

    public static final MapCodec<EmptyAboveCondition> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Codec.BOOL.fieldOf("above").forGetter(o -> o.above)
    ).apply(ins, EmptyAboveCondition::new));


    private final boolean above;

    @Override
    public Identifier getType() {
        return ChangeConditions.EMPTY_ABOVE;
    }

    @Override
    public boolean test(Level level, BlockPos pos, ISeasonChangeContext context) {
        return above ? level.isEmptyBlock(pos.above()) :
                level.isEmptyBlock(pos.below());
    }

    @Override
    public MapCodec<? extends IChangeCondition> codec() {
        return CODEC;
    }
}
