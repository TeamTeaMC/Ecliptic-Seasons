package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.serializer.SerializerHolder;
import com.teamtea.eclipticseasons.common.network.message.MessageCodec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import lombok.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Data
public class NoneSnowArea implements SerializerHolder {

    public static final Codec<NoneSnowArea> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.LONG.listOf().fieldOf("pos_list").forGetter(o -> o.posMap.longStream().boxed().toList())
    ).apply(ins, longs -> new NoneSnowArea(new LongLinkedOpenHashSet(longs))));

    public static final StreamCodec<ByteBuf, NoneSnowArea> STREAM_CODEC = StreamCodec.composite(
            MessageCodec.longlistStreamCodec,
            o -> o.posMap.longStream().boxed().toList(),
            longs -> new NoneSnowArea(new LongLinkedOpenHashSet(longs))
    );


    private final LongLinkedOpenHashSet posMap;

    public boolean neverSnowyAt(BlockPos blockPos) {
        return !EclipticUtil.canSnowyBlockInteract() && posMap.contains(blockPos.asLong());
    }

    public boolean canSkip() {
        return posMap.isEmpty();
    }

    public boolean add(BlockPos blockPos) {
        boolean add = posMap.add(blockPos.asLong());
        if (add) setChanged();
        return add;
    }

    public boolean remove(BlockPos blockPos) {
        boolean remove = posMap.remove(blockPos.asLong());
        if (remove) setChanged();
        return remove;
    }

    public static NoneSnowArea empty() {
        return new NoneSnowArea(new LongLinkedOpenHashSet());
    }


    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private transient CompoundTag cacheTag = null;

}
