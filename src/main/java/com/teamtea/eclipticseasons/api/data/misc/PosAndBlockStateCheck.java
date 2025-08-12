package com.teamtea.eclipticseasons.api.data.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.backport.FakeBlockPredicate;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record PosAndBlockStateCheck(
        Vec3i offset, FakeBlockPredicate block
) {

    public static final Codec<PosAndBlockStateCheck> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Vec3i.CODEC.fieldOf("offset").forGetter(PosAndBlockStateCheck::offset),
            FakeBlockPredicate.CODEC.fieldOf("block").forGetter(PosAndBlockStateCheck::block)
    ).apply(builder, PosAndBlockStateCheck::new));

    private boolean matchesState(BlockState state) {
        return block().matches(state);
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
