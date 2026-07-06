package com.teamtea.eclipticseasons.common.network.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class PalettedIntArrayCodecs {
    private static final int FIXED_SIZE = 256;

    private static final byte MODE_SINGLE = 0;
    private static final byte MODE_PALETTED = 1;

    public static final StreamCodec<ByteBuf, int[]> BIOME_256 = new StreamCodec<>() {
        @Override
        public void encode(ByteBuf buf, int[] values) {
            if (values.length != FIXED_SIZE) {
                throw new EncoderException("Expected int array length " + FIXED_SIZE + ", got " + values.length);
            }

            Map<Integer, Integer> paletteIndex = new HashMap<>();
            int[] palette = new int[FIXED_SIZE];
            int[] indices = new int[FIXED_SIZE];

            int paletteSize = 0;

            for (int i = 0; i < FIXED_SIZE; i++) {
                int value = values[i];
                Integer index = paletteIndex.get(value);

                if (index == null) {
                    index = paletteSize;
                    paletteIndex.put(value, index);
                    palette[paletteSize++] = value;
                }

                indices[i] = index;
            }

            if (paletteSize == 1) {
                buf.writeByte(MODE_SINGLE);
                ByteBufCodecs.VAR_INT.encode(buf, palette[0]);
                return;
            }

            buf.writeByte(MODE_PALETTED);

            ByteBufCodecs.VAR_INT.encode(buf, paletteSize);
            for (int i = 0; i < paletteSize; i++) {
                ByteBufCodecs.VAR_INT.encode(buf, palette[i]);
            }

            int bits = Math.max(1, Mth.ceillog2(paletteSize));
            ByteBufCodecs.VAR_INT.encode(buf, bits);

            SimpleBitStorage storage = new SimpleBitStorage(bits, FIXED_SIZE, indices);
            long[] raw = storage.getRaw();

            ByteBufCodecs.VAR_INT.encode(buf, raw.length);
            for (long l : raw) {
                buf.writeLong(l);
            }
        }

        @Override
        public int[] decode(ByteBuf buf) {
            byte mode = buf.readByte();

            if (mode == MODE_SINGLE) {
                int value = ByteBufCodecs.VAR_INT.decode(buf);
                int[] values = new int[FIXED_SIZE];
                Arrays.fill(values, value);
                return values;
            }

            if (mode != MODE_PALETTED) {
                throw new DecoderException("Unknown paletted int array mode: " + mode);
            }

            int paletteSize = ByteBufCodecs.VAR_INT.decode(buf);
            if (paletteSize <= 1 || paletteSize > FIXED_SIZE) {
                throw new DecoderException("Invalid palette size: " + paletteSize);
            }

            int[] palette = new int[paletteSize];
            for (int i = 0; i < paletteSize; i++) {
                palette[i] = ByteBufCodecs.VAR_INT.decode(buf);
            }

            int bits = ByteBufCodecs.VAR_INT.decode(buf);
            int expectedBits = Math.max(1, Mth.ceillog2(paletteSize));

            if (bits != expectedBits) {
                throw new DecoderException("Invalid bit width: " + bits + ", expected " + expectedBits);
            }

            int longCount = ByteBufCodecs.VAR_INT.decode(buf);

            // Query the storage engine directly to prevent edge-case mathematical calculation mismatches
            SimpleBitStorage dummyStorage = new SimpleBitStorage(bits, FIXED_SIZE);
            int expectedLongCount = dummyStorage.getRaw().length;

            if (longCount != expectedLongCount) {
                throw new DecoderException("Invalid packed long count: " + longCount + ", expected " + expectedLongCount);
            }

            long[] raw = new long[longCount];
            for (int i = 0; i < longCount; i++) {
                raw[i] = buf.readLong();
            }

            SimpleBitStorage storage = new SimpleBitStorage(bits, FIXED_SIZE, raw);

            // Allocation Optimization: Unpack indices directly into the final values array to avoid a temporary int[256] allocation
            int[] values = new int[FIXED_SIZE];
            storage.unpack(values);

            for (int i = 0; i < FIXED_SIZE; i++) {
                int index = values[i];

                if (index < 0 || index >= paletteSize) {
                    throw new DecoderException("Palette index out of bounds: " + index + " / " + paletteSize);
                }

                values[i] = palette[index];
            }

            return values;
        }
    };

    private PalettedIntArrayCodecs() {
    }
}
