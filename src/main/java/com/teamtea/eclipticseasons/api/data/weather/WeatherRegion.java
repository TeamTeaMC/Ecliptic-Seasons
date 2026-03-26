package com.teamtea.eclipticseasons.api.data.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record WeatherRegion(Holder<Biome> core, HolderSet<Biome> sub,
                            int priority,
                            List<ICondition> iCondition) implements Comparable<WeatherRegion> {
    public WeatherRegion(Holder<Biome> core, HolderSet<Biome> sub) {
        this(core, sub, 1000, List.of());
    }

    public static final Codec<WeatherRegion> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderCodec(Registries.BIOME).fieldOf("core").forGetter(WeatherRegion::core),
            CodecUtil.holderSetCodec(Registries.BIOME).fieldOf("sub").forGetter(WeatherRegion::sub),
            Codec.INT.optionalFieldOf("priority", 1000).forGetter(WeatherRegion::priority),
            ICondition.LIST_CODEC.optionalFieldOf(ConditionalOps.DEFAULT_CONDITIONS_KEY, List.of()).forGetter(WeatherRegion::iCondition)
    ).apply(ins, WeatherRegion::new));

    @Override
    public int compareTo(@NotNull WeatherRegion c) {
        return Integer.compare(priority(), c.priority());
    }
}
