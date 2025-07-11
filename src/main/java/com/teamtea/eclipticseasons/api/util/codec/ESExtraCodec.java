package com.teamtea.eclipticseasons.api.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import java.util.Optional;


public class ESExtraCodec {

    public static final StringRepresentable.EnumCodec<SolarTerm> SOLAR_TERM = StringRepresentable.fromEnum(SolarTerm::collectValues);

    public static final StringRepresentable.EnumCodec<Season> SEASON = StringRepresentable.fromEnum(Season::collectValues);

    public static final StringRepresentable.EnumCodec<Humidity> HUMIDITY = StringRepresentable.fromEnum(Humidity::collectValues);


    public static final StringRepresentable.EnumCodec<TimePeriod> TIME_PERIOD = StringRepresentable.fromEnum(TimePeriod::collectValues);


    public static final Codec<HolderSet<Block>> BLOCK_HOLDER_SET_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(b -> Optional.ofNullable(b))
            ).apply(instance, b -> b.orElseGet(HolderSet::direct))
    );

    public static final Codec<HolderSet<Biome>> BIOME_HOLDER_SET_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(b -> Optional.ofNullable(b))
            ).apply(instance, b -> b.orElseGet(HolderSet::direct))
    );


}
