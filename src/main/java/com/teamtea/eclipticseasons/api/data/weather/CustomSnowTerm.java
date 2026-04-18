package com.teamtea.eclipticseasons.api.data.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.climate.ISnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.config.CommonConfig;
import lombok.Builder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NonNull;

import java.util.List;


@Builder
public record CustomSnowTerm(
        HolderSet<Biome> biomes,
        SolarTerm start,
        SolarTerm end,
        List<TempEvent> tempEvents
) implements ISnowTerm {

    public static final Codec<CustomSnowTerm> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BIOME).fieldOf("biomes").forGetter(CustomSnowTerm::biomes),
            ESExtraCodec.SOLAR_TERM.fieldOf("start").forGetter(CustomSnowTerm::start),
            ESExtraCodec.SOLAR_TERM.fieldOf("end").forGetter(CustomSnowTerm::end),
            TempEvent.CODEC.listOf().optionalFieldOf("events", List.of()).forGetter(CustomSnowTerm::tempEvents)
    ).apply(ins, CustomSnowTerm::new));

    @Override
    public SolarTerm getStart() {
        return start;
    }

    @Override
    public SolarTerm getEnd() {
        return end;
    }

    @Override
    public ISnowTerm cast(float tempChange) {
        if (CommonConfig.Season.dynamicSnowTerm.get())
            for (TempEvent tempEvent : tempEvents) {
                if (tempEvent.tempOffset > tempChange) {
                    return tempEvent;
                }
            }
        return this;
    }

    @Override
    public @NonNull String toString() {
        return "CustomSnowTerm{" +
                "biomes=" + biomes +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    @Builder
    public record TempEvent(float tempOffset, SolarTerm start, SolarTerm end) implements ISnowTerm {
        public static final Codec<TempEvent> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Codec.FLOAT.fieldOf("temp_offset").forGetter(TempEvent::tempOffset),
                ESExtraCodec.SOLAR_TERM.fieldOf("start").forGetter(TempEvent::start),
                ESExtraCodec.SOLAR_TERM.fieldOf("end").forGetter(TempEvent::end)
        ).apply(ins, TempEvent::new));

        @Override
        public SolarTerm getStart() {
            return start();
        }

        @Override
        public SolarTerm getEnd() {
            return end();
        }
    }
}
