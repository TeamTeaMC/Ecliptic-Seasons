package com.teamtea.eclipticseasons.common.network.message;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MessageCodec {

    public static final StreamCodec<ByteBuf, List<Long>> longlistStreamCodec = new StreamCodec<>() {
        @Override
        public void encode(ByteBuf pBuffer, List<Long> pValue) {
            pBuffer.writeInt(pValue.size());
            for (Long i : pValue) {
                pBuffer.writeLong(i);
            }
        }

        @Override
        public @NonNull List<Long> decode(ByteBuf pBuffer) {
            int size = pBuffer.readInt();
            ArrayList<Long> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(pBuffer.readLong());
            }
            return list;
        }
    };

    public static final StreamCodec<ByteBuf, List<Integer>> intlistStreamCodec = new StreamCodec<>() {
        @Override
        public void encode(ByteBuf pBuffer, List<Integer> pValue) {
            pBuffer.writeInt(pValue.size());
            for (Integer i : pValue) {
                pBuffer.writeInt(i);
            }
        }

        @Override
        public @NonNull List<Integer> decode(ByteBuf pBuffer) {
            int size = pBuffer.readInt();
            ArrayList<Integer> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(pBuffer.readInt());
            }
            return list;
        }
    };

    public static final StreamCodec<ByteBuf, List<BlockPos>> poslistStreamCodec = new StreamCodec<>() {
        @Override
        public void encode(ByteBuf pBuffer, List<BlockPos> pValue) {
            pBuffer.writeInt(pValue.size());
            for (BlockPos i : pValue) {
                BlockPos.STREAM_CODEC.encode(pBuffer, i);
            }
        }

        @Override
        public @NonNull List<BlockPos> decode(ByteBuf pBuffer) {
            int size = pBuffer.readInt();
            ArrayList<BlockPos> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(BlockPos.STREAM_CODEC.decode(pBuffer));
            }
            return list;
        }
    };

    public static final StreamCodec<ByteBuf, List<ResourceKey<Level>>> dimensionKeysStreamCodec = new StreamCodec<>() {
        @Override
        public void encode(ByteBuf pBuffer, List<ResourceKey<Level>> pValue) {
            pBuffer.writeInt(pValue.size());
            for (ResourceKey<Level> i : pValue) {
                ResourceKey.streamCodec(Registries.DIMENSION).encode(pBuffer, i);
            }
        }

        @Override
        public @NonNull List<ResourceKey<Level>> decode(ByteBuf pBuffer) {
            int size = pBuffer.readInt();
            ArrayList<ResourceKey<Level>> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(ResourceKey.streamCodec(Registries.DIMENSION).decode(pBuffer));
            }
            return list;
        }
    };

    public static final StreamCodec<ByteBuf, int[]> intArrayStreamCodec = new StreamCodec<>() {
        @Override
        public void encode(ByteBuf pBuffer, int[] pValue) {
            pBuffer.writeInt(pValue.length);
            for (int i : pValue) {
                ByteBufCodecs.VAR_INT.encode(pBuffer, i);
            }
        }

        @Override
        public int @NonNull [] decode(ByteBuf pBuffer) {
            int size = pBuffer.readInt();
            int[] list = new int[size];
            for (int i = 0; i < size; i++) {
                // ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list())
                list[i] = ByteBufCodecs.VAR_INT.decode(pBuffer);
            }
            return list;
        }
    };

    static {

    }
}
