package com.teamtea.eclipticseasons.api.data.client;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

@Data
@Builder
public class SeasonalBiomeAmbient {
    public static final Codec<SeasonalBiomeAmbient> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ESExtraCodec.SOLAR_TERM.optionalFieldOf("start", SolarTerm.NONE).forGetter(o -> o.start),
            ESExtraCodec.SOLAR_TERM.optionalFieldOf("end", SolarTerm.NONE).forGetter(o -> o.end),
            ESExtraCodec.SEASON.optionalFieldOf("season", Season.NONE).forGetter(o -> o.season),
            Codec.BOOL.optionalFieldOf("indoor", false).forGetter(o -> o.indoor),
            Codec.BOOL.optionalFieldOf("ignore_time", true).forGetter(o -> o.ignore_time),
            Codec.BOOL.optionalFieldOf("day", true).forGetter(o -> o.day),
            ESExtraCodec.TIME_PERIOD.optionalFieldOf("time_period", TimePeriod.NONE).forGetter(o -> o.timePeriod),
            Codec.BOOL.optionalFieldOf("inwater", false).forGetter(o -> o.inwater),
            Codec.BOOL.optionalFieldOf("rain", false).forGetter(o -> o.rain),
            CodecUtil.holderCodec(ESRegistries.AGRO_CLIMATE).optionalFieldOf("climate").forGetter(o -> o.climate),
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("biomes", HolderSet.empty()).forGetter(o -> o.biomes),
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("ignored_biomes", HolderSet.empty()).forGetter(o -> o.ignored_biomes),
            CodecUtil.holderCodec(Registries.SOUND_EVENT).fieldOf("sound").forGetter(o -> o.sound),
            Codec.BOOL.optionalFieldOf("loop", true).forGetter(o -> o.loop),
            Codec.INT.optionalFieldOf("seed", -1).forGetter(o -> o.seed),
            Codec.INT.optionalFieldOf("priority", 1000).forGetter(o -> o.priority)
    ).apply(ins, SeasonalBiomeAmbient::new));


    @Builder.Default
    private final SolarTerm start = SolarTerm.NONE;
    @Builder.Default
    private final SolarTerm end = SolarTerm.NONE;
    @Builder.Default
    private final Season season = Season.NONE;
    @Builder.Default
    private final boolean indoor = false;
    @Builder.Default
    private final boolean ignore_time = true;
    @Builder.Default
    private final boolean day = true;
    @Builder.Default
    private final TimePeriod timePeriod = TimePeriod.NONE;
    @Builder.Default
    private final boolean inwater = false;
    @Builder.Default
    private final boolean rain = false;
    @Builder.Default
    private final Optional<Holder<AgroClimaticZone>> climate = Optional.empty();
    @Builder.Default
    private final HolderSet<Biome> biomes = HolderSet.empty();
    @Builder.Default
    private final HolderSet<Biome> ignored_biomes = HolderSet.empty();
    @NonNull
    private final Holder<SoundEvent> sound;
    @Builder.Default
    private final boolean loop = true;
    @Builder.Default
    private final int seed = -1;
    @Builder.Default
    private final int priority = 1000;
}
