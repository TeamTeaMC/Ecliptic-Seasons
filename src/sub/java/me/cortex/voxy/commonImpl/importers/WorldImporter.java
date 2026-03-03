package me.cortex.voxy.commonImpl.importers;

import com.mojang.serialization.Codec;
import me.cortex.voxy.common.thread.ServiceManager;
import me.cortex.voxy.common.world.WorldEngine;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;

import java.util.function.BooleanSupplier;

import java.io.File;

public class WorldImporter implements IDataImporter {

    private final PalettedContainerRO<Holder<Biome>> defaultBiomeProvider = null;
    private final Codec<PalettedContainerRO<Holder<Biome>>> biomeCodec = null;
    private final Codec<PalettedContainer<BlockState>> blockStateCodec = null;

    public WorldImporter(WorldEngine worldEngine, Level mcWorld, ServiceManager sm, BooleanSupplier runChecker) {
    }

    public void importRegionDirectoryAsync(File file) {
    }
}
