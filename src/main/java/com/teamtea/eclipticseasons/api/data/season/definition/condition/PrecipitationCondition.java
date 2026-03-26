package com.teamtea.eclipticseasons.api.data.season.definition.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;


@Builder
@Data
public class PrecipitationCondition implements IChangeCondition {

    public static final MapCodec<PrecipitationCondition> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter(o -> o.precipitation)
    ).apply(ins, PrecipitationCondition::new));


    @Builder.Default
    private final Biome.Precipitation precipitation = Biome.Precipitation.RAIN;

    @Override
    public Identifier getType() {
        return ChangeConditions.PRECIPITATION;
    }

    @Override
    public boolean test(Level level, BlockPos pos, ISeasonChangeContext context) {
        return EclipticSeasonsApi.getInstance().getCurrentPrecipitationAt(level, pos) == precipitation;
    }

    @Override
    public MapCodec<? extends IChangeCondition> codec() {
        return CODEC;
    }
}
