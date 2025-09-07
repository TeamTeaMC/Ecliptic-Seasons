package com.teamtea.eclipticseasons.common.network.message;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;

public class ChunkBiomeUpdateMessage{
    public int[] biomes;
    public ChunkPos chunkPos;
    public int version;

    // public ChunkBiomeUpdateMessage() {
    //     this(new int[256], 0, 0, ChunkBiomeUpdateMessage.FLAG_EMPTY);
    // }

    public ChunkBiomeUpdateMessage(int[] biomes, ChunkPos chunkPos, int version) {
        this.biomes = biomes;
        this.chunkPos = chunkPos;
        this.version = version;
    }


    public ChunkBiomeUpdateMessage(FriendlyByteBuf buf) {
        int size = buf.readInt();
        int[] list = new int[size];
        for (int i = 0; i < size; i++) {
            list[i] = buf.readVarInt();
        }
        this.biomes = list;
        this.chunkPos = buf.readChunkPos();
        this.version = buf.readVarInt();
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(biomes.length);
        for (int i : biomes) {
            buf.writeVarInt(i);
        }
        buf.writeChunkPos(this.chunkPos);
        buf.writeVarInt(this.version);
    }

    // public static final Capability<ChunkBiomeUpdateMessage> CHUNK_BIOME_UPDATE_MESSAGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    // });
    // public static final int FLAG_NEED_VERSION = -1;
    // public static final int FLAG_FILL_SMALL = -2;
    // public static final int FLAG_EMPTY = -3;
    // @Override
    // public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    //     if (cap == CHUNK_BIOME_UPDATE_MESSAGE_CAPABILITY) {
    //         return LazyOptional.of(() -> this).cast();
    //     }
    //     return LazyOptional.empty();
    // }
    //
    // @Override
    // public CompoundTag serializeNBT() {
    //     CompoundTag tag = new CompoundTag();
    //     tag.putIntArray("biomes", this.biomes);
    //     tag.putInt("x", this.x);
    //     tag.putInt("z", this.z);
    //     tag.putInt("version", this.version);
    //     return tag;
    // }
    //
    // @Override
    // public void deserializeNBT(CompoundTag nbt) {
    //     this.biomes = nbt.getIntArray("biomes");
    //     this.x = nbt.getInt("x");
    //     this.z = nbt.getInt("z");
    //     this.version = nbt.getInt("version");
    // }
}
