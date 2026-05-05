package com.teamtea.eclipticseasons.api.data.client;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.season.SpecialDays;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

@Data
@Builder
@Accessors(fluent = true)
// @AllArgsConstructor
// @RequiredArgsConstructor
public class SeasonalBackgroundMusic {

    public static final Codec<SeasonalBackgroundMusic> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ESExtraCodec.SOLAR_TERM.optionalFieldOf("start", SolarTerm.NONE).forGetter(o -> o.start),
            ESExtraCodec.SOLAR_TERM.optionalFieldOf("end", SolarTerm.NONE).forGetter(o -> o.end),
            ESExtraCodec.SEASON.optionalFieldOf("season", Season.NONE).forGetter(o -> o.season),
            // Codec.BOOL.optionalFieldOf("indoor", false).forGetter(o -> o.indoor),
            CodecUtil.holderSetCodec(ESRegistries.SPECIAL_DAYS).optionalFieldOf("special_days", HolderSet.empty()).forGetter(o -> o.specialDays),
            Codec.BOOL.optionalFieldOf("ignore_time", true).forGetter(o -> o.ignore_time),
            Codec.BOOL.optionalFieldOf("day", true).forGetter(o -> o.day),
            ESExtraCodec.TIME_PERIOD.optionalFieldOf("time_period", TimePeriod.NONE).forGetter(o -> o.timePeriod),
            // Codec.BOOL.optionalFieldOf("inwater", false).forGetter(o -> o.inwater),
            Codec.BOOL.optionalFieldOf("rain", false).forGetter(o -> o.rain),
            CodecUtil.holderCodec(ESRegistries.AGRO_CLIMATE).optionalFieldOf("climate").forGetter(o -> o.climate),
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("biomes", HolderSet.empty()).forGetter(o -> o.biomes),
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("ignored_biomes", HolderSet.empty()).forGetter(o -> o.ignored_biomes),
            BackgroundMusicBuilder.CODEC.fieldOf("music").forGetter(o -> o.music),
            // Codec.BOOL.optionalFieldOf("loop", true).forGetter(o -> o.loop),
            Codec.INT.optionalFieldOf("weight", 10).forGetter(o -> o.weight),
            Codec.INT.optionalFieldOf("priority", 1000).forGetter(o -> o.priority)
    ).apply(ins, (start1, end1, season1, specialDays1, ignore_time1, day1, timePeriod1, rain1, climate1, biomes1, ignored_biomes1, music1, weight1, priority1) ->
            new SeasonalBackgroundMusic(start1, end1, season1, specialDays1, ignore_time1, day1, timePeriod1, rain1, climate1, biomes1, ignored_biomes1, music1, music1.to(), weight1, priority1)));


    @Builder.Default
    private final SolarTerm start = SolarTerm.NONE;
    @Builder.Default
    private final SolarTerm end = SolarTerm.NONE;
    @Builder.Default
    private final Season season = Season.NONE;
    @Builder.Default
    private final HolderSet<SpecialDays> specialDays = HolderSet.empty();
    @Builder.Default
    private final boolean ignore_time = true;
    @Builder.Default
    private final boolean day = true;
    @Builder.Default
    private final TimePeriod timePeriod = TimePeriod.NONE;
    // @Builder.Default
    // private final boolean inwater = false;
    @Builder.Default
    private final boolean rain = false;
    @Builder.Default
    private final Optional<Holder<AgroClimaticZone>> climate = Optional.empty();
    @Builder.Default
    private final HolderSet<Biome> biomes = HolderSet.empty();
    @Builder.Default
    private final HolderSet<Biome> ignored_biomes = HolderSet.empty();
    @NonNull
    private final BackgroundMusicBuilder music;

    // @NonNull
    private BackgroundMusic musicHolder;

    // @Builder.Default
    // private final boolean loop = true;
    @Builder.Default
    private final int weight = 10;
    @Builder.Default
    private final int priority = 1000;


    public record BackgroundMusicBuilder(Optional<MusicBuilder> defaultMusic, Optional<MusicBuilder> creativeMusic,
                                         Optional<MusicBuilder> underwaterMusic) {
        public BackgroundMusicBuilder(MusicBuilder music) {
            this(Optional.of(music), Optional.empty(), Optional.empty());
        }

        public static final Codec<BackgroundMusicBuilder> CODEC = RecordCodecBuilder.create(
                i -> i.group(
                                MusicBuilder.CODEC.optionalFieldOf("default").forGetter(BackgroundMusicBuilder::defaultMusic),
                                MusicBuilder.CODEC.optionalFieldOf("creative").forGetter(BackgroundMusicBuilder::creativeMusic),
                                MusicBuilder.CODEC.optionalFieldOf("underwater").forGetter(BackgroundMusicBuilder::underwaterMusic)
                        )
                        .apply(i, BackgroundMusicBuilder::new)
        );

        public BackgroundMusic to() {
            return new BackgroundMusic(defaultMusic.map(MusicBuilder::to),
                    creativeMusic.map(MusicBuilder::to),
                    underwaterMusic.map(MusicBuilder::to));
        }
    }

    public record MusicBuilder(Identifier sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        public static final Codec<MusicBuilder> CODEC = RecordCodecBuilder.create(
                i -> i.group(
                                Identifier.CODEC.fieldOf("sound").forGetter(MusicBuilder::sound),
                                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_delay").forGetter(MusicBuilder::minDelay),
                                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_delay").forGetter(MusicBuilder::maxDelay),
                                Codec.BOOL.optionalFieldOf("replace_current_music", false).forGetter(MusicBuilder::replaceCurrentMusic)
                        )
                        .apply(i, MusicBuilder::new)
        );

        public Music to() {
            Holder<SoundEvent> s = Holder.direct(SoundEvent.createVariableRangeEvent(sound));
            return new Music(s, minDelay, maxDelay, replaceCurrentMusic);
        }
    }

}
