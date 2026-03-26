package com.teamtea.eclipticseasons.api.data.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.TestOnly;

public record PosAndBlockStateCheck(
        Vec3i offset, BlockPredicate block
) {

    public static final Codec<PosAndBlockStateCheck> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Vec3i.CODEC.fieldOf("offset").forGetter(PosAndBlockStateCheck::offset),
            BlockPredicate.CODEC.fieldOf("block").forGetter(PosAndBlockStateCheck::block)
    ).apply(builder, PosAndBlockStateCheck::new));

    public boolean matches(ServerLevel level, BlockPos pos) {
        pos = pos.offset(offset());
        return block.matches(level, pos);
    }

}
