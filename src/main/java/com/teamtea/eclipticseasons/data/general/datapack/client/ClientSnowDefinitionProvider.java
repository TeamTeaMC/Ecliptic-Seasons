package com.teamtea.eclipticseasons.data.general.datapack.client;

import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.common.core.snow.ClientModelDefinitions;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientDataMapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ClientSnowDefinitionProvider extends ESClientDataMapProvider<SnowDefinition> {
    public ClientSnowDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_SNOW_DEFINITION, SnowDefinition.CODEC);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<Block> blockRegistryLookup = provider.lookupOrThrow(Registries.BLOCK);
        add("stone", SnowDefinition.builder()
                .blocks(HolderSet.direct(Blocks.COBBLESTONE.builtInRegistryHolder()))
                .info(SnowDefinition.Info.builder().mid(ClientModelDefinitions.OVERLAY_TINY).build())
                .build());
    }
}
