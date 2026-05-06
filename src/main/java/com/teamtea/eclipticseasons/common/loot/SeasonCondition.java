package com.teamtea.eclipticseasons.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.common.registry.LootItemConditionRegistry;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public record SeasonCondition(Slice require) implements LootItemCondition {
    public static final MapCodec<SeasonCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Slice.CODEC.fieldOf("require").forGetter(SeasonCondition::require))
                    .apply(instance, SeasonCondition::new)
    );

    public static final Codec<SeasonCondition> CONDITION_CODEC = SeasonCondition.CODEC.codec();


    @Override
    public @NotNull LootItemConditionType getType() {
        // return LootItemConditions.RANDOM_CHANCE;
        return LootItemConditionRegistry.SEASON.get();
    }

    @Override
    public boolean test(LootContext context) {
        ServerLevel level = context.getLevel();
        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);

        AgroClimaticZone climate = require.climate.map(Holder::value).orElse(null);

        SolarTerm start = require.start.isValid() ? require.start :
                require.solarTerm.isValid() ? require.solarTerm :
                        climate != null && require.season.isValid() ? require.season.getFirstSolarTerm(climate) :
                                climate != null && require.startSeason.isValid() ? require.endSeason.getFirstSolarTerm(climate) : SolarTerm.NONE;

        SolarTerm end = require.end.isValid() ? require.end :
                require.solarTerm.isValid() ? require.solarTerm :
                        climate != null && require.season.isValid() ? require.season.getEndSolarTerm(climate) :
                                climate != null && require.endSeason.isValid() ? require.endSeason.getEndSolarTerm(climate) : SolarTerm.NONE;

        if (start.isValid() && end.isValid()) {
            return solarTerm.isInTerms(start, end);
        }

        Season startSeason = require.season.isValid() ? require.season : require.startSeason;
        Season endSeason = require.season.isValid() ? require.season : require.endSeason;
        if (startSeason.isValid() && endSeason.isValid()) {
            var vec3 = context.getParamOrNull(LootContextParams.ORIGIN);
            BlockPos pos = vec3 == null ? null : BlockPos.containing(vec3);
            if (pos != null) {
                Season agroSeason = EclipticSeasonsApi.getInstance().getSeasonSignal(level, pos);
                return agroSeason.isInTerms(startSeason, endSeason);
            }
        }
        return solarTerm == SolarTerm.NONE;
    }

    @lombok.Builder
    @Data
    public static class Slice {
        public static final Codec<Slice> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("start", SolarTerm.NONE).forGetter(o -> o.start),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("end", SolarTerm.NONE).forGetter(o -> o.end),
                ESExtraCodec.SOLAR_TERM.optionalFieldOf("solar_term", SolarTerm.NONE).forGetter(o -> o.solarTerm),
                CodecUtil.holderCodec(ESRegistries.AGRO_CLIMATE).optionalFieldOf("climate").forGetter(o -> o.climate),
                ESExtraCodec.SEASON.optionalFieldOf("start_season", Season.NONE).forGetter(o -> o.startSeason),
                ESExtraCodec.SEASON.optionalFieldOf("end_season", Season.NONE).forGetter(o -> o.endSeason),
                ESExtraCodec.SEASON.optionalFieldOf("season", Season.NONE).forGetter(o -> o.season)
        ).apply(ins, Slice::new));

        @lombok.Builder.Default
        private final SolarTerm start = SolarTerm.NONE;
        @lombok.Builder.Default
        private final SolarTerm end = SolarTerm.NONE;
        @lombok.Builder.Default
        private final SolarTerm solarTerm = SolarTerm.NONE;
        @lombok.Builder.Default
        private final Optional<Holder<AgroClimaticZone>> climate = Optional.empty();
        @lombok.Builder.Default
        private final Season startSeason = Season.NONE;
        @lombok.Builder.Default
        private final Season endSeason = Season.NONE;
        @lombok.Builder.Default
        private final Season season = Season.NONE;
    }

    public static Builder builder(final Slice require) {
        return new Builder(require);
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

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<SeasonCondition> {
        public static final Serializer INSTANCE = new Serializer();

        public void serialize(@NotNull JsonObject jsonObject, @NotNull SeasonCondition value, @NotNull JsonSerializationContext context) {
            JsonElement jsonelement = CONDITION_CODEC.encodeStart(JsonOps.INSTANCE, value).getOrThrow(false, EclipticSeasons::logger);
            if (jsonelement.isJsonObject()) {
                JsonObject obj = jsonelement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    jsonObject.add(entry.getKey(), entry.getValue());
                }
            }
        }

        public @NotNull SeasonCondition deserialize(@NotNull JsonObject value, @NotNull JsonDeserializationContext context) {
            return CONDITION_CODEC.decode(JsonOps.INSTANCE, value).getOrThrow(false, EclipticSeasons::logger).getFirst();
        }
    }
}