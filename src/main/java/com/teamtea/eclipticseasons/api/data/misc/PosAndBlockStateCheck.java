package com.teamtea.eclipticseasons.api.data.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.TestOnly;

public record PosAndBlockStateCheck(
        Vec3i offset, HolderSet<Block> block
) {

    public static final Codec<PosAndBlockStateCheck> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Vec3i.CODEC.fieldOf("offset").forGetter(PosAndBlockStateCheck::offset),
            CropGrowControlBuilder.BLOCK_HOLDER_SET_CODEC.fieldOf("block").forGetter(PosAndBlockStateCheck::block)
    ).apply(builder, PosAndBlockStateCheck::new));

    private boolean matchesState(BlockState state) {
        return block().contains(state.getBlockHolder());
    }

    public boolean matches(ServerLevel level, BlockPos pos) {
        pos = pos.offset(offset());
        if (!level.isLoaded(pos)) {
            return false;
        } else {
            return this.matchesState(level.getBlockState(pos));
        }
    }

}
