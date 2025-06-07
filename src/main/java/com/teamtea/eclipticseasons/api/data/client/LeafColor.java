package com.teamtea.eclipticseasons.api.data.client;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.misc.util.HolderMappable;
import com.teamtea.eclipticseasons.api.misc.util.Mergable;
import com.teamtea.eclipticseasons.api.util.fast.Enum2IntMap;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

public record LeafColor(
        ColorSource colorSource,
        BlockPredicate blockPredicate,
        Optional<LocationPredicate> locationPredicate,
        Optional<SolarTermValueMap<ColorMode>> colors,
        Optional<SolarTermValueMap<List<ResourceLocation>>> sprites,
        Optional<SolarTermValueMap<Integer>> weights,
        Optional<Boolean> replace
) implements HolderMappable<HolderSet<Block>, Pair<LeafColor.InstanceHolder, LeafColor.Instance>> {
    public static final Codec<LeafColor> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            StringRepresentable.fromEnum(ColorSource::collectValues).fieldOf("source").orElse(ColorSource.CUSTOM).forGetter(LeafColor::colorSource),
            BlockPredicate.CODEC.fieldOf("block").forGetter(LeafColor::blockPredicate),
            LocationPredicate.CODEC.optionalFieldOf("location").forGetter(LeafColor::locationPredicate),
            SolarTermValueMap.codec(ColorMode.CODEC).optionalFieldOf("colors").forGetter(LeafColor::colors),
            SolarTermValueMap.codec(ResourceLocation.CODEC.listOf()).optionalFieldOf("sprites").forGetter(LeafColor::sprites),
            SolarTermValueMap.codec(Codec.INT).optionalFieldOf("weights").forGetter(LeafColor::weights),
            Codec.BOOL.optionalFieldOf("replace").forGetter(LeafColor::replace)
    ).apply(ins, LeafColor::new));


    private static final SolarTermValueMap<ColorMode> EMPTY_MODE_MAP = new SolarTermValueMap<>(
            Optional.empty(), Optional.empty(), Optional.empty()
    );
    private static final SolarTermValueMap<List<ResourceLocation>> EMPTY_LIST_MAP = new SolarTermValueMap<>(
            Optional.empty(), Optional.empty(), Optional.empty()
    );
    private static final SolarTermValueMap<Integer> EMPTY_INTEGER_MAP = new SolarTermValueMap<>(
            Optional.empty(), Optional.empty(), Optional.empty()
    );

    @Override
    public Pair<HolderSet<Block>, Pair<InstanceHolder, Instance>> asHolderMapping() {
        return Pair.of(blockPredicate().blocks().orElse(HolderSet.empty()), toInstance());
    }

    public @NotNull Pair<InstanceHolder, Instance> toInstance() {
        EnumMap<SolarTerm, ColorMode> colorMap = colors.orElse(EMPTY_MODE_MAP).combine();
        EnumMap<SolarTerm, List<ResourceLocation>> spriteMap = sprites.orElse(EMPTY_LIST_MAP).combine();
        EnumMap<SolarTerm, Integer> weightMap = weights.orElse(EMPTY_INTEGER_MAP).combine();

        Enum2ObjectMap<SolarTerm, ColorMode.Instance> colorsE = SolarTermValueMap.convertToEnum2ObjectMap(colorMap, ColorMode::toInstance);
        Enum2ObjectMap<SolarTerm, List<ResourceLocation>> spritesE = SolarTermValueMap.convertToEnum2ObjectMap(spriteMap, Function.identity());
        Enum2IntMap<SolarTerm> weightsE = new Enum2IntMap<>(SolarTerm.class);
        weightMap.forEach(weightsE::put);

        return Pair.of(new InstanceHolder(Optional.ofNullable(blockPredicate), locationPredicate), new Instance(colorSource, colorsE, spritesE, weightsE, replace.orElse(false)));
    }

    public enum ColorSource implements StringRepresentable {
        MAP,      // 地图色
        BLOCK,    // 方块色 （部分树叶可以染色）
        TEXTURE,  // 纹理色，提取纹理
        CUSTOM;   // 自定义色

        private static final ColorSource[] VALUES = ColorSource.values();

        public static ColorSource[] collectValues() {
            return VALUES;
        }

        @Override
        public @NotNull String getSerializedName() {
            return toString().toLowerCase(Locale.ROOT);
        }
    }

    public record Instance(
            ColorSource colorSource,
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> colors,
            Enum2ObjectMap<SolarTerm, List<ResourceLocation>> sprites,
            Enum2IntMap<SolarTerm> weights,
            boolean replace
    ) implements Mergable<Instance> {
        private static final Enum2ObjectMap<SolarTerm, ColorMode.Instance> EMPTY_COLOR_MAP = new Enum2ObjectMap<>(SolarTerm.class, null);

        private static final Instance EMPTY = new Instance(ColorSource.MAP,
                new Enum2ObjectMap<>(SolarTerm.class, null),
                new Enum2ObjectMap<>(SolarTerm.class, null),
                new Enum2IntMap<>(SolarTerm.class),
                false);
        @Override
        public Instance merge(Instance next) {
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> newColors = new Enum2ObjectMap<>(SolarTerm.class);
            Enum2ObjectMap<SolarTerm, List<ResourceLocation>> newSprites = new Enum2ObjectMap<>(SolarTerm.class);
            Enum2IntMap<SolarTerm> newWeights = new Enum2IntMap<>(SolarTerm.class);
            newColors.putAll(colors);
            newColors.putAll(next.colors);
            newSprites.putAll(sprites);
            newSprites.putAll(next.sprites);
            newWeights.putAll(weights);
            newWeights.putAll(next.weights);
            return new Instance(next.colorSource, newColors, newSprites, newWeights, next.replace());
        }
    }

    public record InstanceHolder(
            Optional<BlockPredicate> blockPredicate,
            Optional<LocationPredicate> locationPredicate
    ) {
        private static final InstanceHolder EMPTY = new InstanceHolder(Optional.empty(), Optional.empty());

        public boolean matches(Level level, int i, int j, int k, RandomSource random, BlockState blockstate) {
            if (blockPredicate.isPresent()) {
                BlockPredicate predicate = blockPredicate.get();

                if (predicate.blocks().isPresent() &&
                        !predicate.blocks().get().contains(blockstate.getBlockHolder())) {
                    return false;
                }

                if (predicate.properties().isPresent() &&
                        !predicate.properties().get().matches(blockstate)) {
                    return false;
                }
            }

            if (locationPredicate.isPresent()) {
                LocationPredicate location = locationPredicate.get();

                if (location.biomes().isPresent()) {
                    Holder<Biome> biomeHolder = level.getBiome(new BlockPos(i, j, k));
                    if (!location.biomes().get().contains(biomeHolder)) {
                        return false;
                    }
                }
            }

            return true;
        }

    }
}
