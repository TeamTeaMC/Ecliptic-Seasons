package com.teamtea.eclipticseasons.common.network.message.codec;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public final class PalettedIntArrayCodecs {

    private static final int MAX_SIZE = 256;


    public static void encodeBiome256(FriendlyByteBuf buf, int[] values) {
        if (values.length != MAX_SIZE) {
            throw new IllegalArgumentException("Array length must be exactly " + MAX_SIZE);
        }

        // 1. 提取群系调色板（去重）
        IntList palette = new IntArrayList();
        for (int value : values) {
            if (!palette.contains(value)) {
                palette.add(value);
            }
        }

        int paletteSize = palette.size();

        if (paletteSize == 1) {
            buf.writeByte(0); // Mode 0
            buf.writeVarInt(palette.getInt(0));
            return;
        }

        buf.writeByte(1); // Mode 1
        buf.writeVarInt(paletteSize);
        for (int i = 0; i < paletteSize; i++) {
            buf.writeVarInt(palette.getInt(i));
        }

        int bits = Mth.ceillog2(paletteSize);
        buf.writeByte(bits);

        int valuesPerLong = 64 / bits;
        int longCount = (MAX_SIZE + valuesPerLong - 1) / valuesPerLong;
        buf.writeVarInt(longCount);

        long currentLong = 0L;
        int bitOffset = 0;

        for (int value : values) {
            int paletteIndex = palette.indexOf(value);
            currentLong |= ((long) paletteIndex << bitOffset);
            bitOffset += bits;

            if (bitOffset + bits > 64) {
                buf.writeLong(currentLong);
                currentLong = 0L;
                bitOffset = 0;
            }
        }
        if (bitOffset > 0) {
            buf.writeLong(currentLong);
        }
    }


    public static int[] decodeBiome256(FriendlyByteBuf buf) {
        int[] values = new int[MAX_SIZE];
        int mode = buf.readByte();

        if (mode == 0) {
            int singleValue = buf.readVarInt();
            java.util.Arrays.fill(values, singleValue);
            return values;
        }

        int paletteSize = buf.readVarInt();
        int[] palette = new int[paletteSize];
        for (int i = 0; i < paletteSize; i++) {
            palette[i] = buf.readVarInt();
        }

        int bits = buf.readByte();
        int longCount = buf.readVarInt();

        int valuesPerLong = 64 / bits;
        long mask = (1L << bits) - 1L;
        int valueIndex = 0;

        for (int i = 0; i < longCount; i++) {
            long currentLong = buf.readLong();
            for (int j = 0; j < valuesPerLong && valueIndex < MAX_SIZE; j++) {
                int paletteIndex = (int) ((currentLong >> (j * bits)) & mask);
                values[valueIndex++] = palette[paletteIndex];
            }
        }

        return values;
    }
}