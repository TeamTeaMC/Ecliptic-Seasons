package com.teamtea.eclipticseasons.compat.voxy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamtea.eclipticseasons.compat.voxy.helper.IVoxyAboveLightingSupplier;
import com.teamtea.eclipticseasons.compat.voxy.helper.VoxyPairCompoundTag;
import me.cortex.voxy.common.voxelization.ILightingSupplier;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;

public class VoxyImporterTool {

    private static final byte[] OTHER = new byte[0];

    public static ILightingSupplier getILightingSupplier(
            Codec<PalettedContainer<BlockState>> blockStateCodec,
            Codec<PalettedContainerRO<Holder<Biome>>> biomeCodec,
            PalettedContainerRO<Holder<Biome>> defaultBiomeProvider,
            PalettedContainer<BlockState> blockContainer, PalettedContainerRO<Holder<Biome>> biomeContainer, ILightingSupplier lightSupplier, CompoundTag above) {
        if (!VoxyTool.isVoxyTest()) return lightSupplier;
        if (above == null) return lightSupplier;

        byte[] blockLightData = above.getByteArray("BlockLight").orElse(OTHER);
        byte[] skyLightData = above.getByteArray("SkyLight").orElse(OTHER);
        DataLayer blockLight;
        if (blockLightData.length != 0) {
            blockLight = new DataLayer(blockLightData);
        } else {
            blockLight = null;
        }
        DataLayer skyLight;
        if (skyLightData.length != 0) {
            skyLight = new DataLayer(skyLightData);
        } else {
            skyLight = null;
        }
        LevelChunkSection levelChunkSection = null;
        if (above.getCompound("block_states").isPresent()) {
            DataResult<PalettedContainer<BlockState>> blockStatesRes = blockStateCodec.parse(NbtOps.INSTANCE, above.getCompoundOrEmpty("block_states"));
            if (blockStatesRes.hasResultOrPartial()) {
                PalettedContainer<BlockState> blockStates = blockStatesRes.getPartialOrThrow();
                PalettedContainerRO<Holder<Biome>> biomes = defaultBiomeProvider;
                CompoundTag optBiomes = above.getCompoundOrEmpty("biomes");
                if (!optBiomes.isEmpty()) {
                    biomes = biomeCodec.parse(NbtOps.INSTANCE, optBiomes).result().orElse(defaultBiomeProvider);
                }
                levelChunkSection = new LevelChunkSection(blockStates, biomes);
            }
        }
        lightSupplier = new IVoxyAboveLightingSupplier(lightSupplier, blockLight, skyLight,
                new LevelChunkSection(blockContainer, biomeContainer), levelChunkSection);
        return lightSupplier;
    }

    public static CompoundTag getVoxyPairCompoundTag(int y, CompoundTag compoundTag, CompoundTag chunk) {
        if (!VoxyTool.isVoxyTest()) return compoundTag;

        ListTag sections = chunk.getListOrEmpty("sections");
        if (sections.isEmpty()) return compoundTag;
        int index = y - sections.getCompoundOrEmpty(0).getIntOr("Y", 0) + 1;
        return new VoxyPairCompoundTag()
                .setOriginal(compoundTag)
                .setAbove(index < sections.size() ?
                        sections.getCompoundOrEmpty(index) : null)
                .pack();
    }
}
