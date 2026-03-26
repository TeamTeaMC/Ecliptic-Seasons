package com.teamtea.eclipticseasons.client.itemproperties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientExtraUtil;
import lombok.Getter;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public class CounterState extends NeedleDirectionHelper {
    public static final MapCodec<CounterState> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            Query.CODEC.fieldOf("query").forGetter(CounterState::getQuery),
                            Codec.FLOAT.fieldOf("max_length").forGetter(CounterState::getMaxLength)
                    )
                    .apply(i, CounterState::new)
    );

    @Getter
    private final Query query;
    @Getter
    private final float maxLength;

    public CounterState(Query query, float maxLength) {
        super(false);
        this.query = query;
        this.maxLength = maxLength;
    }


    @Override
    protected float calculate(ItemStack itemStack, ClientLevel level, int seed, @NonNull ItemOwner owner) {
        BlockPos blockPosition = BlockPos.containing(owner.position());
        return query.function.apply(level, blockPosition).ordinal() * (1f / (maxLength - 1));
    }

    @FunctionalInterface
    public static interface BiomePosFunction<B, P, I> {
        I apply(B b, P p);
    }

    public enum Query implements StringRepresentable {
        hyetometer(EclipticUtil::getRainfallAt),
        hygrometer((level, pos) -> {
            float humidityAt = EclipticUtil.getHumidityLevelAt(level, pos);
            humidityAt = ClientExtraUtil.modifyHumidity(level, pos, humidityAt);
            return Humidity.getHumid(humidityAt);
        }),
        thermometer(EclipticUtil::getTemperatureAt);

        public static final Codec<Query> CODEC = StringRepresentable.fromEnum(Query::values);

        private final BiomePosFunction<Level, BlockPos, ITranslatable> function;

        Query(BiomePosFunction<Level, BlockPos, ITranslatable> biomeIntegerFunction) {
            this.function = biomeIntegerFunction;
        }

        @Override
        public @NonNull String getSerializedName() {
            return toString().toLowerCase(Locale.ROOT);
        }
    }
}
