package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.CodecUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.TestOnly;

import java.util.Optional;


// TODO:季节性波动，比如随机的旱涝，以及大雪
@TestOnly
public record CropSeasonControl(
        SolarTerm solarTerm,
        float grow_chance,
        float death_chance,
        float fertile_chance,
        Optional<BlockState> deadState
) {

    public static final Codec<CropSeasonControl> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("index").forGetter(c -> c.solarTerm.ordinal()),
            Codec.FLOAT.fieldOf("grow_chance").forGetter(CropSeasonControl::grow_chance),
            Codec.FLOAT.fieldOf("death_chance").forGetter(CropSeasonControl::death_chance),
            Codec.FLOAT.fieldOf("fertile_chance").forGetter(CropSeasonControl::fertile_chance),
            BlockState.CODEC.optionalFieldOf("deadState").forGetter(CropSeasonControl::deadState)
    ).apply(ins, ((i, rate, canSurvive, canFertile, deadState) ->
            new CropSeasonControl(SolarTerm.collectValues()[i], rate, canSurvive, canFertile, deadState)
    )));

    public static final Codec<SolarTerm> SOLAR_TERM_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("index").forGetter(Enum::ordinal)
    ).apply(ins, i -> SolarTerm.collectValues()[i]));

    public static final Codec<SolarTerm> SOLAR_TERM_CODEC_STRING = Codec.STRING
            .comapFlatMap(s -> {
                try {
                    return DataResult.success(SolarTerm.valueOf(s.toUpperCase()));
                } catch (ResourceLocationException resourcelocationexception) {
                    return DataResult.error(() -> "Not a valid solar term: " + s + " " + resourcelocationexception.getMessage());
                }
            }, SolarTerm::getName)
            .stable();


    // TODO:EnumMap is faster
    public static final CodecUtil.IdentityHashCodec<SolarTerm, CropSeasonControl> IDENTITY_HASH_CODEC = new CodecUtil.IdentityHashCodec<>(SOLAR_TERM_CODEC_STRING, CODEC);


}
