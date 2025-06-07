package com.teamtea.eclipticseasons.api.data.season;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.holdersets.NotHolderSet;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * The class is to make a common set would be used often.
 **/
public final class BiomeSet {


    public static final Codec<BiomeSet> CODEC = Codec.recursive(
            "biome_set", biomeSetCodec ->
                    RecordCodecBuilder.create(ins -> ins.group(
                            CodecUtil.holderSetCodec(Registries.BIOME).fieldOf("biomes").forGetter(BiomeSet::biomes),
                            biomeSetCodec.optionalFieldOf("target").forGetter(b -> b.target),
                            Codec.INT.optionalFieldOf("priority", 1000).forGetter(b -> b.priority),
                            Codec.BOOL.optionalFieldOf("reverse", false).forGetter(b -> b.reverse),
                            Codec.STRING.optionalFieldOf("group", "").forGetter(b -> b.group)
                    ).apply(ins, BiomeSet::new))
    );

    // 也许我们需要一个child分机体系
    @NotNull
    private HolderSet<Biome> biomes;
    @NotNull
    private final Optional<BiomeSet> target;
    private final int priority;
    private final boolean reverse;
    @NotNull
    private final String group;


    public BiomeSet(
            @NotNull HolderSet<Biome> biomes,
            @NotNull Optional<BiomeSet> target,
            int priority,
            boolean reverse, @NotNull String group
    ) {
        this.biomes = biomes;
        this.target = target;
        this.priority = priority;
        this.reverse = reverse;
        this.group = group;
    }

    public BiomeSet merge(RegistryAccess registryAccess, BiomeSet attachSet) {
        this.biomes =
                attachSet.reverse ? new NotHolderSet<>(registryAccess.lookupOrThrow(
                        Registries.BIOME
                ), attachSet.biomes) : new OrHolderSet<>(this.biomes, attachSet.biomes);
        return this;
    }

    public boolean matches(Holder<Biome> biomeHolder) {
        return biomes().contains(biomeHolder) && !reverse;
    }

    public HolderSet<Biome> biomes() {
        return biomes;
    }

    public @NotNull String group() {
        return group;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BiomeSet biomeSet = (BiomeSet) object;
        return priority == biomeSet.priority && reverse == biomeSet.reverse && Objects.equals(biomes, biomeSet.biomes) && Objects.equals(target, biomeSet.target) && Objects.equals(group, biomeSet.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(biomes, target, priority, reverse, group);
    }
}
