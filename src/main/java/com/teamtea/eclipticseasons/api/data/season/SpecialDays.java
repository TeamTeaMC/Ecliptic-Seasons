package com.teamtea.eclipticseasons.api.data.season;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import lombok.Data;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

@Data
public class SpecialDays {

    public static final Codec<SpecialDays> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.FLOAT.optionalFieldOf("start",0f).forGetter(o -> o.start),
            Codec.FLOAT.optionalFieldOf("end", 0f).forGetter(o -> o.end),
            Codec.INT.optionalFieldOf("lasting_days", 0).forGetter(o -> o.lastingDays),
            ESExtraCodec.SOLAR_TERM.fieldOf("term").forGetter(o -> o.term),
            Identifier.CODEC.fieldOf("icon").forGetter(o -> o.icon),
            ComponentSerialization.CODEC.fieldOf("tittle").forGetter(o -> o.tittle),
            ComponentSerialization.CODEC.optionalFieldOf("alternation", Component.empty()).forGetter(o -> o.alternation)
    ).apply(ins, SpecialDays::new));

    public final float start;
    public final float end;
    public final int lastingDays;
    public final SolarTerm term;
    public final Identifier icon;
    public final Component tittle;
    public final Component alternation;

}
