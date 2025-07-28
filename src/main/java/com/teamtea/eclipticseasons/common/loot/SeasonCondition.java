package com.teamtea.eclipticseasons.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.common.registry.LootItemConditionRegistry;
import lombok.Data;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public record SeasonCondition(Slice require) implements LootItemCondition {
    public static final MapCodec<SeasonCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Slice.CODEC.fieldOf("require").forGetter(SeasonCondition::require))
                    .apply(instance, SeasonCondition::new)
    );

    @Override
    public @NotNull LootItemConditionType getType() {
        // return LootItemConditions.RANDOM_CHANCE;
        return LootItemConditionRegistry.SEASON.get();
    }

    @Override
    public boolean test(LootContext context) {
        ServerLevel level = context.getLevel();
        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
        if (!solarTerm.isValid()) return false;
        SolarTerm start = require.start.isValid() ? require.start :
                require.solarTerm.isValid() ? require.solarTerm :
                        require.season.isValid() ? require.season.getFirstSolarTerm() :
                                require.startSeason.isValid() ? require.endSeason.getFirstSolarTerm() : SolarTerm.NONE;

        SolarTerm end = require.end.isValid() ? require.end :
                require.solarTerm.isValid() ? require.solarTerm :
                        require.season.isValid() ? require.season.getEndSolarTerm() :
                                require.endSeason.isValid() ? require.endSeason.getEndSolarTerm() : SolarTerm.NONE;

        if (start.isValid() && end.isValid()) {
            return solarTerm.isInTerms(start, end);
        }
        return false;
    }

    @lombok.Builder
    @Data
    public static class Slice {
        public static final Codec<SeasonCondition.Slice> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("start", SolarTerm.NONE).forGetter(o -> o.start),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("end", SolarTerm.NONE).forGetter(o -> o.end),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("solar_term", SolarTerm.NONE).forGetter(o -> o.solarTerm),
                ESExtraCodec.SEASON.optionalFieldOf("start_season", Season.NONE).forGetter(o -> o.startSeason),
                ESExtraCodec.SEASON.optionalFieldOf("end_season", Season.NONE).forGetter(o -> o.endSeason),
                ESExtraCodec.SEASON.optionalFieldOf("season", Season.NONE).forGetter(o -> o.season)
        ).apply(ins, SeasonCondition.Slice::new));

        @lombok.Builder.Default
        private final SolarTerm start = SolarTerm.NONE;
        @lombok.Builder.Default
        private final SolarTerm end = SolarTerm.NONE;
        @lombok.Builder.Default
        private final SolarTerm solarTerm = SolarTerm.NONE;
        @lombok.Builder.Default
        private final Season startSeason = Season.NONE;
        @lombok.Builder.Default
        private final Season endSeason = Season.NONE;
        @lombok.Builder.Default
        private final Season season = Season.NONE;
    }

    public static SeasonCondition.Builder builder(final Slice require) {
        return new SeasonCondition.Builder(require);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final Slice require;

        public Builder(Slice require) {
            if (require == null) throw new IllegalArgumentException("Require must not be null");
            this.require = require;
        }

        @Override
        public @NotNull LootItemCondition build() {
            return new SeasonCondition(this.require);
        }
    }
}