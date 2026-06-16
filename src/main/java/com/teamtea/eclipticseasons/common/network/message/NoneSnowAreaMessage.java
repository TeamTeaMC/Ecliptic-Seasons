package com.teamtea.eclipticseasons.common.network.message;

import com.teamtea.eclipticseasons.common.core.map.NoneSnowArea;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;

public class NoneSnowAreaMessage {
    public final ChunkPos chunkPos;
    public final NoneSnowArea value;


    public NoneSnowAreaMessage(ChunkPos chunkPos, NoneSnowArea noneSnowArea) {
        this.chunkPos = chunkPos;
        this.value = noneSnowArea;
    }

    public NoneSnowAreaMessage(FriendlyByteBuf buf) {
        chunkPos = buf.readChunkPos();
        value = new NoneSnowArea(new LongLinkedOpenHashSet(buf.readLongArray()));
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeChunkPos(chunkPos);
        buf.writeLongArray(this.value.getPosMap().toArray(new long[0]));
    }
}
