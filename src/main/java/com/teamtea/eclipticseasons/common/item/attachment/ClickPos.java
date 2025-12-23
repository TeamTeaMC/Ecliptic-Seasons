package com.teamtea.eclipticseasons.common.item.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public record ClickPos(BlockPos last) {

    public static final Codec<ClickPos> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            BlockPos.CODEC.fieldOf("last").forGetter(ClickPos::last)
    ).apply(ins, ClickPos::new));
}
