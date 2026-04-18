package com.teamtea.eclipticseasons.compat.voxy.helper;

import lombok.experimental.Accessors;
import me.cortex.voxy.common.voxelization.ILightingSupplier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;

@Accessors(chain = true)
public record IVoxyAboveLightingSupplier(
        ILightingSupplier original,
        DataLayer blockLight, DataLayer skyLight,
        LevelChunkSection originalSection,
        LevelChunkSection aboveSection) implements ILightingSupplier {


    @Override
    public byte supply(int bx, int by, int bz) {
        if (by < 16) return original.supply(bx, by, bz);
        by -= 16;
        int block = 0;
        int sky = 0;
        if (blockLight != null) {
            block = blockLight.get(bx, by, bz);
        }

        if (skyLight != null) {
            sky = skyLight.get(bx, by, bz);
        }

        return (byte) (sky | block << 4);
    }


    public BlockState getBlockState(int bx, int by, int bz) {
        if (by < 16)
            return originalSection == null ? Blocks.AIR.defaultBlockState() :
                    originalSection.getBlockState(bx, by, bz);
        return aboveSection == null ? Blocks.AIR.defaultBlockState() :
                aboveSection.getBlockState(bx, by - 16, bz);
    }
}
