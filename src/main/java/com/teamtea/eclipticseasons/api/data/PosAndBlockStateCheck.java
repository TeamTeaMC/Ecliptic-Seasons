package com.teamtea.eclipticseasons.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.Optional;

@TestOnly
public record PosAndBlockStateCheck(
        Vec3i offset, BlockStatePropertyCondition block
        // ,
        // Optional<List<BlockStatePropertyCondition>> blocks
) {

    public static final Codec<PosAndBlockStateCheck> DIRECT_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Vec3i.CODEC.fieldOf("offset").forGetter(PosAndBlockStateCheck::offset),
            BlockStatePropertyCondition.CODEC.codec().fieldOf("block").forGetter(PosAndBlockStateCheck::block)
            // TagKey.codec(Registries.BLOCK).optionalFieldOf("tag").forGetter(PosAndBlockStateCheck::tag),
            // BlockStatePropertyCondition.CODEC.codec().listOf().optionalFieldOf("blocks").forGetter(PosAndBlockStateCheck::blocks)
    ).apply(builder, PosAndBlockStateCheck::new));

    //  基于Builder
    static {

    }

}
