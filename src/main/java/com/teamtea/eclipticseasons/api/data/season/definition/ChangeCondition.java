package com.teamtea.eclipticseasons.api.data.season.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public record ChangeCondition(Optional<Biome.Precipitation> rain, Set<TimePeriod> periods,
                              Optional<Boolean> emptyAbove) {

    public ChangeCondition(Optional<Precipitation> rain, List<TimePeriod> periods,
                           Optional<Boolean> emptyAbove) {
        this(rain.map(precipitation -> precipitation.to()),
                periods.isEmpty() ? Set.of() : EnumSet.copyOf(periods),
                emptyAbove);
    }

    public static ChangeCondition of(boolean emptyAbove) {
        return new ChangeCondition(Optional.empty(), Set.of(), Optional.of(emptyAbove));
    }

    public static final Codec<ChangeCondition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Precipitation.CODEC.optionalFieldOf("rain").forGetter(c -> c.rain.map(Precipitation::from)),
            CodecUtil.listFrom(ESExtraCodec.TIME_PERIOD).optionalFieldOf("periods", List.of()).forGetter(cs -> List.copyOf(cs.periods)),
            Codec.BOOL.optionalFieldOf("empty_above").forGetter(ChangeCondition::emptyAbove)
    ).apply(ins, ChangeCondition::new));

    public boolean isValid(Level level, BlockPos pos) {
        if (emptyAbove.isPresent() && emptyAbove.get() != level.isEmptyBlock(pos.above())) return false;
        TimePeriod timePeriod = TimePeriod.fromTimeOfDay(level.getTimeOfDay(1f));
        return (rain.isEmpty() || EclipticSeasonsApi.getInstance().getCurrentPrecipitationAt(level, pos) == rain.get())
                && (periods.isEmpty() || periods.contains(timePeriod));
    }

    public static enum Precipitation implements StringRepresentable {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow");

        public static final Codec<Precipitation> CODEC = StringRepresentable.fromEnum(Precipitation::values);
        private final String name;

        private Precipitation(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public static Precipitation from(Biome.Precipitation bp) {
            return valueOf(bp.toString());
        }

        public Biome.Precipitation to() {
            return Biome.Precipitation.valueOf(this.toString());
        }
    }
}
