package com.teamtea.eclipticseasons.api.data.season.definition.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;


@Builder
@Data
public class TimePeriodCondition implements IChangeCondition {

    public static final MapCodec<TimePeriodCondition> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            CodecUtil.listFrom(ESExtraCodec.TIME_PERIOD)
                    .xmap(HashSet::new, ArrayList::new)
                    .fieldOf("periods").forGetter(o -> o.periods)
    ).apply(ins, TimePeriodCondition::new));


    @Builder.Default
    private final HashSet<TimePeriod> periods = new HashSet<>();

    @Override
    public Identifier getType() {
        return ChangeConditions.TIME_PERIOD;
    }

    @Override
    public boolean test(Level level, BlockPos pos, ISeasonChangeContext context) {
        TimePeriod timePeriod = TimePeriod.fromTimeOfDay(SimpleUtil.getTimeOfDay(level));
        return periods.contains(timePeriod);
    }

    @Override
    public MapCodec<? extends IChangeCondition> codec() {
        return CODEC;
    }
}
