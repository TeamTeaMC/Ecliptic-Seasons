package com.teamtea.eclipticseasons.api.data.season;


import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.misc.util.HolderMappable;
import com.teamtea.eclipticseasons.api.misc.util.MapFiller;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.ClientModelDefinitions;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class SnowDefinition implements MapFiller<Block, SnowDefinition>, HolderMappable<HolderSet<Block>, SnowDefinition> {
    public static final Codec<SnowDefinition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BLOCK).fieldOf("blocks").forGetter(o -> o.blocks),
            PropertyTester.CODEC.listOf().optionalFieldOf("properties", List.of()).forGetter(o -> o.map),
            Info.MAP_CODEC.forGetter(o -> o.info)
    ).apply(ins, SnowDefinition::new));
    @NonNull
    private final HolderSet<Block> blocks;
    @Builder.Default
    private final List<PropertyTester> map = new ArrayList<>();
    @Builder.Default
    private final Info info = Info.builder().build();

    @Override
    public void fillMap(Map<Block, List<SnowDefinition>> map) {
        for (Holder<Block> block : blocks) {
            List<SnowDefinition> definitionList = map.computeIfAbsent(block.value(), (b) -> new ArrayList<>());
            definitionList.add(this);
        }
    }

    @Override
    public Pair<HolderSet<Block>, SnowDefinition> asHolderMapping() {
        return Pair.of(blocks, this);
    }

    @Builder
    @Data
    public static class Info {
        public static final MapCodec<Info> MAP_CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
                Codec.INT.optionalFieldOf("flag", MapChecker.FLAG_CUSTOM_JSON).forGetter(o -> o.flag),
                Codec.BOOL.optionalFieldOf("snow_passable", false).forGetter(o -> o.snowPassable),
                Codec.BOOL.optionalFieldOf("ignore_offset", false).forGetter(o -> o.ignoreOffset),
                Codec.INT.optionalFieldOf("offset", 0).forGetter(o -> o.offset),
                Identifier.CODEC.optionalFieldOf("mid", ClientModelDefinitions.EMPTY).forGetter(o -> o.mid),
                Identifier.CODEC.optionalFieldOf("mid2", ClientModelDefinitions.EMPTY).forGetter(o -> o.mid2)
        ).apply(ins, Info::new));
        public static final Codec<Info> CODEC = MAP_CODEC.codec();

        public static final Info EMPTY = Info.builder().flag(MapChecker.FLAG_IGNORE).build();

        @Builder.Default
        private final int flag = MapChecker.FLAG_CUSTOM_JSON;
        @Builder.Default
        private final boolean snowPassable = false;
        @Builder.Default
        private final boolean ignoreOffset = false;
        @Builder.Default
        private final int offset = 0;
        @Builder.Default
        private final Identifier mid = ClientModelDefinitions.OVERLAY;
        @Builder.Default
        private final Identifier mid2 = ClientModelDefinitions.EMPTY;

        public boolean isValid() {
            return this.flag != MapChecker.FLAG_IGNORE;
        }
    }

    @Builder
    @Data
    public static class PropertyTester {
        public static final Codec<PropertyTester> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Codec.STRING.fieldOf("name").forGetter(o -> o.name),
                Codec.BOOL.optionalFieldOf("reverse", false).forGetter(o -> o.reverse),
                CodecUtil.either(ExactMatcher.CODEC, RangeMatcher.CODEC,
                        ExactMatcher.class, RangeMatcher.class).fieldOf("matcher").forGetter(o -> o.matcher)
        ).apply(ins, PropertyTester::new));


        @NonNull
        private final String name;
        @Builder.Default
        private final boolean reverse = false;
        @NonNull
        private final PropertyMatcher matcher;

        public boolean matches(BlockState blockState) {
            Property<?> test = null;
            for (Property<?> property : blockState.getProperties()) {
                if (property.getName().equals(name)) {
                    test = property;
                }
            }
            return matcher.matches(test, blockState);
        }
    }


    @Builder
    @Data
    public static class ExactMatcher implements PropertyMatcher {
        public static final Codec<ExactMatcher> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Codec.STRING.fieldOf("value").forGetter(o -> o.value)
        ).apply(ins, ExactMatcher::new));

        @NonNull
        private final String value;

        @Override
        public boolean matches(@Nullable Property<?> property, BlockState blockState) {
            return property != null && blockState.getValue(property).toString().equals(value);
        }
    }

    @Builder
    @Data
    public static class RangeMatcher implements PropertyMatcher {
        public static final Codec<RangeMatcher> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Codec.BOOL.optionalFieldOf("ignore_min", false).forGetter(o -> o.ignoreMin),
                Codec.INT.fieldOf("minTime").forGetter(o -> o.min),
                Codec.BOOL.optionalFieldOf("ignore_max", false).forGetter(o -> o.ignoreMax),
                Codec.INT.fieldOf("maxTime").forGetter(o -> o.max)
        ).apply(ins, RangeMatcher::new));

        @Builder.Default
        private final boolean ignoreMin = false;
        private final int min;
        @Builder.Default
        private final boolean ignoreMax = false;
        private final int max;

        @Override
        public boolean matches(@org.jetbrains.annotations.Nullable Property<?> property, BlockState blockState) {
            if (property != null && blockState.getValue(property) instanceof Integer integer) {
                return (ignoreMin || integer >= min) && (ignoreMax || integer <= max);
            }
            return false;
        }
    }


    public interface PropertyMatcher {
        boolean matches(@Nullable Property<?> property, BlockState blockState);
    }
}
