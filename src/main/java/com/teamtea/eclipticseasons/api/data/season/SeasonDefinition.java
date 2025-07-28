package com.teamtea.eclipticseasons.api.data.season;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SeasonDefinition(
        Optional<HolderSet<AgroClimaticZone>> climate,
        Optional<HolderSet<Biome>> biomes,
        SolarTermValueMap<List<ChangeMode>> changes

) {

    public static final Codec<SeasonDefinition> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderSetCodec(ESRegistries.AGRO_CLIMATE).optionalFieldOf("climate").forGetter(SeasonDefinition::climate),
            CodecUtil.holderSetCodec(Registries.BIOME).optionalFieldOf("biomes").forGetter(SeasonDefinition::biomes),
            SolarTermValueMap.codec(CodecUtil.listFrom(ChangeMode.CODEC)).fieldOf("changes").forGetter(SeasonDefinition::changes)
    ).apply(ins, SeasonDefinition::new));

    public record ChangeMode(BlockPredicate original,
                             List<PlaceContent> place,
                             float chance) {

        public static final Codec<ChangeMode> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                BlockPredicate.CODEC.fieldOf("original").forGetter(ChangeMode::original),
                CodecUtil.listFrom(PlaceContent.CODEC).fieldOf("place").forGetter(ChangeMode::place),
                Codec.FLOAT.optionalFieldOf("chance", 1 / 16f).forGetter(ChangeMode::chance)
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

    }

    public record PlaceContent(
            List<BlockState> block,
            Optional<Vec3i> offset,
            boolean replace
    ) {
        public PlaceContent(
                BlockState block,
                Optional<Vec3i> offset,
                boolean replace
        ) {
            this(List.of(block), offset, replace);
        }

        public static final Codec<PlaceContent> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                CodecUtil.listFrom(BlockState.CODEC).fieldOf("block").forGetter(PlaceContent::block),
                Vec3i.CODEC.optionalFieldOf("offset").forGetter(PlaceContent::offset),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(PlaceContent::replace)
        ).apply(ins, PlaceContent::new));
    }

}
