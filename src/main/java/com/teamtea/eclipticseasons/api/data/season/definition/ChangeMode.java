package com.teamtea.eclipticseasons.api.data.season.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.season.definition.selector.IChangeSelector;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import lombok.Builder;
import lombok.Singular;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 这里我有一个特殊的想法，根据关卡种子、位置和天数进展、年份固定生成种子
 **/
@Builder
public record ChangeMode(BlockPredicate original,
                         @Singular
                         List<IChangeSelector> selectors,
                         float chance,
                         boolean fixedSeed) {

    public static final Codec<ChangeMode> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            BlockPredicate.CODEC.fieldOf("target").forGetter(ChangeMode::original),
            CodecUtil.listFrom(IChangeSelector.CCODEC).fieldOf("place").forGetter(ChangeMode::selectors),
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
        return blockEntity != null && nbtPredicate.matches(blockEntity.saveWithFullMetadata(level.registryAccess()));
    }

    public boolean matches(BlockState state, Level level, BlockPos pos) {
        if (!original.requiresNbt()) {
            return matchesState(state);
        } else {
            if (!level.isLoaded(pos)) {
                return false;
            } else {
                return this.matchesState(level.getBlockState(pos))
                        && (original.nbt().isEmpty() || matchesBlockEntity(level, level.getBlockEntity(pos), original.nbt().get()));
            }
        }
    }
}
