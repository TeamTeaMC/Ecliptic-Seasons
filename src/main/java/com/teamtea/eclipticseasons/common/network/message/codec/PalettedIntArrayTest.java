package com.teamtea.eclipticseasons.common.network.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import java.util.Random;

public final class PalettedIntArrayTest {

    // Control Group: Standard uncompressed int array codec
    public static final StreamCodec<ByteBuf, int[]> INT_ARRAY_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(ByteBuf buf, int[] values) {
            ByteBufCodecs.VAR_INT.encode(buf, values.length);
            for (int value : values) {
                ByteBufCodecs.VAR_INT.encode(buf, value);
            }
        }

        @Override
        public int[] decode(ByteBuf buf) {
            int length = ByteBufCodecs.VAR_INT.decode(buf);
            int[] values = new int[length];
            for (int i = 0; i < length; i++) {
                values[i] = ByteBufCodecs.VAR_INT.decode(buf);
            }
            return values;
        }
    };

    public static void main(String[] args) {
        // 1. SILENCE LOG4J COMPLETELY: Turn off file creation and internal state logging
        System.setProperty("log4j2.statusLoggerLevel", "OFF");
        System.setProperty("log4j2.level", "OFF");
        // Force log4j2 to use a dummy provider that does not initialize file appenders
        System.setProperty("log4j2.provider", "org.apache.logging.log4j.simple.SimpleLoggerContextFactory");

        // 2. Start the benchmark
        System.out.println("=== Biome Compression Efficiency Comparison ===\n");
        System.out.printf("%-25s | %-16s | %-16s | %-16s%n", "Scenario", "Standard (Bytes)", "Paletted (Bytes)", "Bandwidth Saved");
        System.out.println("--------------------------------------------------------------------------------");

        int[] singleBiome = new int[256];
        java.util.Arrays.fill(singleBiome, 1);
        runComparison("Single Biome (Flat)", singleBiome);

        int[] twoBiomes = new int[256];
        for (int i = 0; i < 256; i++) {
            twoBiomes[i] = (i < 128) ? 1 : 2;
        }
        runComparison("Two Biomes (Boundary)", twoBiomes);

        int[] medBiomes = new int[256];
        for (int i = 0; i < 256; i++) {
            medBiomes[i] = (i / 32) + 1;
        }
        runComparison("8 Biomes (Standard)", medBiomes);

        int[] worstCase = new int[256];
        Random random = new Random(42);
        for (int i = 0; i < 256; i++) {
            worstCase[i] = random.nextInt(64) + 1;
        }
        runComparison("64 Biomes (Worst)", worstCase);
    }

    private static void runComparison(String scenarioName, int[] data) {
        ByteBuf standardBuf = Unpooled.buffer();
        ByteBuf palettedBuf = Unpooled.buffer();

        try {
            INT_ARRAY_STREAM_CODEC.encode(standardBuf, data);
            int standardSize = standardBuf.readableBytes();

            PalettedIntArrayCodecs.BIOME_256.encode(palettedBuf, data);
            int palettedSize = palettedBuf.readableBytes();

            double savedRate = (1.0 - ((double) palettedSize / standardSize)) * 100.0;

            System.out.printf("%-25s | %-16d | %-16d | %-15.2f%%%n",
                    scenarioName, standardSize, palettedSize, savedRate);

            int[] decodedStd = INT_ARRAY_STREAM_CODEC.decode(standardBuf);
            int[] decodedPal = PalettedIntArrayCodecs.BIOME_256.decode(palettedBuf);

            if (!java.util.Arrays.equals(data, decodedStd) || !java.util.Arrays.equals(data, decodedPal)) {
                System.err.println("ALERT: Integrity verification failed for scenario: " + scenarioName);
            }

        } finally {
            standardBuf.release();
            palettedBuf.release();
        }
    }
}