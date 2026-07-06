package com.teamtea.eclipticseasons.common.network.message;


import com.teamtea.eclipticseasons.common.network.message.codec.PalettedIntArrayCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;

public class ChunkBiomeUpdateMessage{
    public int[] biomes;
    public ChunkPos chunkPos;
    public int version;

    public ChunkBiomeUpdateMessage(int[] biomes, ChunkPos chunkPos, int version) {
        this.biomes = biomes;
        this.chunkPos = chunkPos;
        this.version = version;
    }


    public ChunkBiomeUpdateMessage(FriendlyByteBuf buf) {
        this.biomes = PalettedIntArrayCodecs.decodeBiome256(buf);
        this.chunkPos = buf.readChunkPos();
        this.version = buf.readVarInt();
    }


    public void toBytes(FriendlyByteBuf buf) {
        PalettedIntArrayCodecs.encodeBiome256(buf, this.biomes);
        buf.writeChunkPos(this.chunkPos);
        buf.writeVarInt(this.version);
    }
}
