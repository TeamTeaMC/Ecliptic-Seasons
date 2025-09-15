package com.teamtea.eclipticseasons.common.core.map;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public class ChunkInfoMap {
    public static final int TYPE_BIOME = 1;
    public static final int TYPE_HEIGHT = 0;

    private final short[][] matrix = new short[MapChecker.ChunkSize][MapChecker.ChunkSize];
    private final short[][] biomes = new short[MapChecker.ChunkSize][MapChecker.ChunkSize];
    // private final Object[][] lockArray = new Object[MapChecker.ChunkSize][MapChecker.ChunkSize];
    final int x;
    final int z;
    final short minY;
    final boolean isClient;

    public ChunkInfoMap(int x, int z, int minY, boolean isClient) {
        this.minY = (short) minY;
        this.isClient = isClient;
        this.x = x;
        this.z = z;
        EclipticSeasons.extraLogger(true,String.format("Create new Height Map with [%s, %s]", x, z));
        for (int i = 0; i < MapChecker.ChunkSize; i++) {
            for (int j = 0; j < MapChecker.ChunkSize; j++) {
                biomes[i][j] = -1;
                matrix[i][j] = this.minY;
                // lockArray[i][j] = new Object();
            }
        }
        EclipticSeasons.extraLogger(true,String.format("End create [%s, %s]", x, z));
    }

    // 获取chunk内部位置
    public static int getChunkValue(int i) {
        return i & (MapChecker.ChunkSizeLoc);
    }

    public int getHeight(int x, int z) {
        x = getChunkValue(x);
        z = getChunkValue(z);
        return matrix[x][z];
    }

    public int getBiome(int x, int z) {
        x = getChunkValue(x);
        z = getChunkValue(z);
        return biomes[x][z];
    }

    public int getHeight(BlockPos pos) {
        return getHeight(pos.getX(), pos.getZ());
    }

    public int getBiome(BlockPos pos) {
        return getBiome(pos.getX(), pos.getZ());
    }

    public int updateHeight(int x, int z, int y) {
        x = getChunkValue(x);
        z = getChunkValue(z);
        int old;
        // synchronized (lockArray[x][z])
        {
            old = matrix[x][z];
            matrix[x][z] = (short) y;
        }

        return old;
    }

    public int updateBiome(int x, int z, int id) {
        x = getChunkValue(x);
        z = getChunkValue(z);
        int old;
        // synchronized (lockArray[x][z])
        {
            old = biomes[x][z];
            biomes[x][z] = (short) id;
        }
        return old;
    }

    public int updateBiome(BlockPos pos, int id) {
        return updateBiome(pos.getX(), pos.getZ(), id);
    }

    public int updateHeight(BlockPos pos, int y) {
        return updateHeight(pos.getX(), pos.getZ(), y);
    }


    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public short getMinY() {
        return minY;
    }
}
