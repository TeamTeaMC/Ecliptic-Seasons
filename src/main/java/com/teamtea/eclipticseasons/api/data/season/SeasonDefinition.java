package com.teamtea.eclipticseasons.api.data.season;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.util.backport.FakeBlockPredicate;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public record SeasonDefinition(
        Optional<HolderSet<Biome>> biomes,
        SolarTermValueMap<List<ChangeMode>> changes
) {

    public static final Codec<SeasonDefinition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("biomes").forGetter(SeasonDefinition::biomes),
            SolarTermValueMap.codec(CodecUtil.listFrom(ChangeMode.CODEC)).fieldOf("changes").forGetter(SeasonDefinition::changes)
    ).apply(ins, SeasonDefinition::new));


    /**
     * 这里我有一个特殊的想法，根据关卡种子、位置和天数进展、年份固定生成种子
     **/
    public record ChangeMode(FakeBlockPredicate original,
                             List<Selector> selectors,
                             float chance,
                             boolean fixedSeed) {

        public static final Codec<ChangeMode> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                FakeBlockPredicate.CODEC.fieldOf("target").forGetter(ChangeMode::original),
                CodecUtil.listFrom(Selector.CODEC).fieldOf("place").forGetter(ChangeMode::selectors),
                Codec.FLOAT.optionalFieldOf("chance", 1 / 16f).forGetter(ChangeMode::chance),
                Codec.BOOL.optionalFieldOf("fixed_seed", false).forGetter(ChangeMode::fixedSeed)
        ).apply(ins, ChangeMode::new));

        public List<BlockState> getPossibleStates() {
            ArrayList<BlockState> blockStates = new ArrayList<>();
            if (original.blocks().isPresent()) {
                for (Holder<Block> blockHolder : original.blocks().get()) {
                    for (BlockState possibleState : blockHolder.value().getStateDefinition().getPossibleStates()) {
                        if (original.properties().isEmpty() || original.properties().get().matches(possibleState)) {
                            blockStates.add(possibleState);
                        }
                    }
                }
            } else if (original.properties().isPresent()) {
                for (Block block : BuiltInRegistries.BLOCK) {
                    for (BlockState possibleState : block.getStateDefinition().getPossibleStates()) {
                        if (original.properties().isEmpty() || original.properties().get().matches(possibleState)) {
                            blockStates.add(possibleState);
                        }
                    }
                }
            }
            return blockStates;
        }

        public List<Block> getPossibleBlocks() {
            ArrayList<Block> blocks = new ArrayList<>();
            if (original.blocks().isPresent()) {
                for (Holder<Block> blockHolder : original.blocks().get()) {
                    blocks.add(blockHolder.value());
                }
            } else if (original.properties().isPresent()) {
                for (Block block : BuiltInRegistries.BLOCK) {
                    blockRange:
                    for (BlockState possibleState : block.getStateDefinition().getPossibleStates()) {
                        if (original.properties().isEmpty() || original.properties().get().matches(possibleState)) {
                            blocks.add(block);
                            break blockRange;
                        }
                    }
                }
            }
            return blocks;
        }

        public boolean matchesState(BlockState state) {
            return (original.blocks().isEmpty() || state.is(original.blocks().get()))
                    && (original.properties().isEmpty() || original.properties().get().matches(state));
        }

        private static boolean matchesBlockEntity(LevelReader level, @Nullable BlockEntity blockEntity, NbtPredicate nbtPredicate) {
            return blockEntity != null && nbtPredicate.matches(blockEntity.saveWithFullMetadata());
        }

        public boolean matches(BlockState state, Level level, BlockPos pos) {
            return matchesState(state);
            // if (!original.requiresNbt()) {
            //     return matchesState(state);
            // } else {
            //     if (!level.isLoaded(pos)) {
            //         return false;
            //     } else {
            //         return this.matchesState(level.getBlockState(pos))
            //                 && (original.nbt().isEmpty() || matchesBlockEntity(level, level.getBlockEntity(pos), original.nbt().get()));
            //     }
            // }
        }
    }

    public record Selector(
            Optional<BlockState> state,
            Optional<List<MultiBlockPart>> multiBlocks,
            int weight,
            Optional<Vec3i> offset,
            boolean replace,
            List<Condition> conditions,
            Optional<ResourceLocation> loot,
            Optional<Holder<PlacedFeature>> feature) {

        public static final Codec<Selector> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                BlockState.CODEC.optionalFieldOf("block").forGetter(Selector::state),
                MultiBlockPart.CODEC.listOf().optionalFieldOf("multi_blocks").forGetter(Selector::multiBlocks),
                Codec.INT.optionalFieldOf("weight", 10).forGetter(Selector::weight),
                Vec3i.CODEC.optionalFieldOf("offset").forGetter(Selector::offset),
                Codec.BOOL.optionalFieldOf("replace", true).forGetter(Selector::replace),
                CodecUtil.listFrom(Condition.CODEC).optionalFieldOf("conditions", List.of()).forGetter(Selector::conditions),
                ResourceLocation.CODEC.optionalFieldOf("loot").forGetter(Selector::loot),
                CodecUtil.holderCodec(Registries.PLACED_FEATURE).optionalFieldOf("feature").forGetter(Selector::feature)
        ).apply(ins, Selector::new));

        public Selector(BlockState state) {
            this(state, 10);
        }

        public Selector(BlockState state, int weight) {
            this(state, weight, Optional.empty());
        }

        public Selector(BlockState state, int weight, Optional<Vec3i> offset) {
            this(Optional.of(state), Optional.empty(), weight, offset, true, List.of(), Optional.empty(), Optional.empty());
        }

        public Selector(Optional<List<MultiBlockPart>> multiBlockParts, Optional<Vec3i> offset) {
            this(Optional.empty(), multiBlockParts, 10, offset, true, List.of(), Optional.empty(), Optional.empty());
        }

        public static Selector of() {
            return new Selector(Optional.empty(), Optional.empty(), 10, Optional.empty(), true, List.of(), Optional.empty(), Optional.empty());
        }

        public static Selector of(BlockState state) {
            return new Selector(state);
        }

        public static Selector of(BlockState state, int weight) {
            return new Selector(state, weight);
        }

        public static Selector of(BlockState state, int weight, Vec3i vec3i, Condition condition, boolean replace) {
            return new Selector(Optional.of(state), Optional.empty(), weight, Optional.of(vec3i),
                    replace, List.of(condition), Optional.empty(), Optional.empty());
        }

        public static Selector of(List<MultiBlockPart> multiBlockParts, Vec3i vec3i) {
            return new Selector(Optional.of(multiBlockParts), Optional.of(vec3i));
        }

        public boolean isValid(Level level, BlockPos pos) {
            if (conditions.isEmpty()) return true;
            for (Condition condition : conditions) {
                if (!condition.isValid(level, pos)) {
                    return (false);
                }
            }
            return true;
        }
    }

    public record MultiBlockPart(BlockState state, Optional<Vec3i> offset) {
        public static final Codec<MultiBlockPart> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                BlockState.CODEC.fieldOf("state").forGetter(MultiBlockPart::state),
                Vec3i.CODEC.optionalFieldOf("offset").forGetter(MultiBlockPart::offset)
        ).apply(ins, MultiBlockPart::new));

        public static MultiBlockPart of(BlockState state, Vec3i vec3i) {
            return new MultiBlockPart(state, Optional.of(vec3i));
        }

        public static MultiBlockPart of(BlockState state) {
            return new MultiBlockPart(state, Optional.empty());
        }

        public static List<MultiBlockPart> ofList(BlockState upper, BlockState lower) {
            return List.of(MultiBlockPart.of(upper, new Vec3i(0, 1, 0)),
                    MultiBlockPart.of(lower));
        }
    }

    public record Condition(Optional<Biome.Precipitation> rain, Set<TimePeriod> periods,
                            Optional<Boolean> emptyAbove) {

        public Condition(Optional<Precipitation> rain, List<TimePeriod> periods,
                         Optional<Boolean> emptyAbove) {
            this(rain.map(precipitation -> precipitation.to()),
                    periods.isEmpty() ? Set.of() : EnumSet.copyOf(periods),
                    emptyAbove);
        }

        public static Condition of(boolean emptyAbove) {
            return new Condition(Optional.empty(), Set.of(), Optional.of(emptyAbove));
        }

        public static final Codec<Condition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Precipitation.CODEC.optionalFieldOf("rain").forGetter(c -> c.rain.map(Precipitation::from)),
                CodecUtil.listFrom(ESExtraCodec.TIME_PERIOD).optionalFieldOf("periods", List.of()).forGetter(cs -> List.copyOf(cs.periods)),
                Codec.BOOL.optionalFieldOf("empty_above").forGetter(Condition::emptyAbove)
        ).apply(ins, Condition::new));

        public boolean isValid(Level level, BlockPos pos) {
            if (emptyAbove.isPresent() && emptyAbove.get() != level.isEmptyBlock(pos.above())) return false;
            TimePeriod timePeriod = TimePeriod.fromTimeOfDay(level.getTimeOfDay(1f));
            return (rain.isEmpty() || EclipticSeasonsApi.getInstance().getCurrentPrecipitationAt(level, pos) == rain.get())
                    && (periods.isEmpty() || periods.contains(timePeriod));
        }

        public static enum Precipitation implements StringRepresentable {
            NONE("none"),
            RAIN("rain"),
            SNOW("snow");

            public static final Codec<Precipitation> CODEC = StringRepresentable.fromEnum(Precipitation::values);
            private final String name;

            private Precipitation(String name) {
                this.name = name;
            }

            @Override
            public @NotNull String getSerializedName() {
                return this.name;
            }

            public static Precipitation from(Biome.Precipitation bp) {
                return valueOf(bp.toString());
            }

            public Biome.Precipitation to() {
                return Biome.Precipitation.valueOf(this.toString());
            }
        }
    }


}
