package me.cortex.voxy.common.world.service;

import me.cortex.voxy.common.voxelization.ILightingSupplier;
import me.cortex.voxy.common.voxelization.VoxelizedSection;
import me.cortex.voxy.common.voxelization.WorldConversionFactory;
import me.cortex.voxy.common.world.other.Mapper;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;

import java.util.concurrent.ConcurrentLinkedDeque;

public class VoxelIngestService {
    private static final ThreadLocal<VoxelizedSection> SECTION_CACHE = null;

    private record IngestSection(int cx, int cy, int cz) {
    }

    private final ConcurrentLinkedDeque<IngestSection> ingestQueue = new ConcurrentLinkedDeque<>();

    private void processJob() {
        var task = this.ingestQueue.pop();
        if (false) {
        } else {
            VoxelizedSection section = null;
            Mapper stateMapper = null;
            PalettedContainer<BlockState> blockContainer = null;
            PalettedContainerRO<Holder<Biome>> biomeContainer = null;
            ILightingSupplier lightSupplier = null;
            VoxelizedSection csec = WorldConversionFactory.convert(
                    section,
                    stateMapper,
                    blockContainer,
                    biomeContainer,
                    lightSupplier
            );
        }
    }

    public boolean enqueueIngest(LevelChunk chunk) {
        this.ingestQueue.add(new IngestSection(0, 0, 0));
        return false;
    }
}
