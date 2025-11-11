package com.teamtea.eclipticseasons.api.data.season.definition.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;


@Builder
@Data
public class PrecipitationCondition implements IChangeCondition {

    public static final MapCodec<PrecipitationCondition> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            WarpPrecipitation.CODEC.fieldOf("rain").forGetter(c -> WarpPrecipitation.from(c.precipitation))
    ).apply(ins, precipitation1 -> new PrecipitationCondition(precipitation1.to())));


    @Builder.Default
    private final Biome.Precipitation precipitation= Biome.Precipitation.RAIN;

    @Override
    public ResourceLocation getType() {
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


    public static enum WarpPrecipitation implements StringRepresentable {
        NONE("none"),
        RAIN("rain"),
        SNOW("snow");

        public static final Codec<WarpPrecipitation> CODEC = StringRepresentable.fromEnum(WarpPrecipitation::values);
        private final String name;

        private WarpPrecipitation(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public static WarpPrecipitation from(Biome.Precipitation bp) {
            return valueOf(bp.toString());
        }

        public Biome.Precipitation to() {
            return Biome.Precipitation.valueOf(this.toString());
        }
    }
}
