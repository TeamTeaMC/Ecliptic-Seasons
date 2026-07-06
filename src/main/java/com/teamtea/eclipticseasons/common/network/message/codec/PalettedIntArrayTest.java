package com.teamtea.eclipticseasons.common.network.message.codec;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Random;

public final class PalettedIntArrayTest {

    public static void main(String[] args) {
        // 1. Completely silence Log4j2 to prevent local .log file generation
        System.setProperty("log4j2.statusLoggerLevel", "OFF");
        System.setProperty("log4j2.level", "OFF");
        System.setProperty("log4j2.provider", "org.apache.logging.log4j.simple.SimpleLoggerContextFactory");

        // 2. Start Benchmark and Verification Pipeline
        System.out.println("=== 1.20 Biome Compression Test Suite ===\n");
        System.out.printf("%-25s | %-16s | %-16s | %-16s%n", "Scenario", "Standard (Bytes)", "Paletted (Bytes)", "Bandwidth Saved");
        System.out.println("--------------------------------------------------------------------------------");

        // Scenario A: Single uniform biome (Flat worlds or chunk interiors)
        int[] singleBiome = new int[256];
        java.util.Arrays.fill(singleBiome, 1);
        runComparison("Single Biome (Flat)", singleBiome);

        // Scenario B: Two biomes (Chunk cutting straight through a border)
        int[] twoBiomes = new int[256];
        for (int i = 0; i < 256; i++) {
            twoBiomes[i] = (i < 128) ? 1 : 2;
        }
        runComparison("Two Biomes (Boundary)", twoBiomes);

        // Scenario C: 8 diverse biomes (Typical chunk in standard modded terrain)
        int[] medBiomes = new int[256];
        for (int i = 0; i < 256; i++) {
            medBiomes[i] = (i / 32) + 1;
        }
        runComparison("8 Biomes (Standard)", medBiomes);

        // Scenario D: Absolute worst case (64 fragmented biomes)
        int[] worstCase = new int[256];
        Random random = new Random(42);
        for (int i = 0; i < 256; i++) {
            worstCase[i] = random.nextInt(64) + 1;
        }
        runComparison("64 Biomes (Worst)", worstCase);
    }

    private static void runComparison(String scenarioName, int[] data) {
        // 1.20 network buffers wrap standard Netty ByteBufs via FriendlyByteBuf
        FriendlyByteBuf standardBuf = new FriendlyByteBuf(Unpooled.buffer());
        FriendlyByteBuf palettedBuf = new FriendlyByteBuf(Unpooled.buffer());

        try {
            // Benchmark old/standard VarInt array loop (Control Group)
            standardBuf.writeInt(data.length);
            for (int val : data) {
                standardBuf.writeVarInt(val);
            }
            int standardSize = standardBuf.readableBytes();

            // Benchmark new paletted bit-packing format
            PalettedIntArrayCodecs.encodeBiome256(palettedBuf, data);
            int palettedSize = palettedBuf.readableBytes();

            // Calculate percentage saved
            double savedRate = (1.0 - ((double) palettedSize / standardSize)) * 100.0;

            // Output structural row metrics
            System.out.printf("%-25s | %-16d | %-16d | %-15.2f%%%n",
                    scenarioName, standardSize, palettedSize, savedRate);

            // Data Integrity Verification
            // Decode standard buffer
            int stdLen = standardBuf.readInt();
            int[] decodedStd = new int[stdLen];
            for (int i = 0; i < stdLen; i++) {
                decodedStd[i] = standardBuf.readVarInt();
            }

            // Decode paletted buffer
            int[] decodedPal = PalettedIntArrayCodecs.decodeBiome256(palettedBuf);

            // Assertion Logic: Ensure byte-perfect precision
            if (!java.util.Arrays.equals(data, decodedStd) || !java.util.Arrays.equals(data, decodedPal)) {
                System.err.println("ALERT: Data corruption detected in scenario: " + scenarioName);
            }

        } finally {
            // Explicitly release memory allocations to prevent off-heap leakage in CI loops
            standardBuf.release();
            palettedBuf.release();
        }
    }
}