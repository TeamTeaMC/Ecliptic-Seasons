package com.teamtea.eclipticseasons.data.datapack.client;

import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.datapack.client.base.ESClientDataMapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BiomeColorProvider extends ESClientDataMapProvider<BiomeColor> {
    public BiomeColorProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider>  registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_BIOME, BiomeColor.CODEC);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<Biome> biomeRegistryLookup = provider.lookupOrThrow(Registries.BIOME);
        // add("plains",new BiomeColor(
        //         biomeRegistryLookup.getOrThrow(Tags.Biomes.IS_PLAINS),
        //         SolarTermValueMap.<ColorMode>builder()
        //                 .putSeason(Season.SPRING,new ColorMode(Optional.of(Color.PINK.getRGB()),Optional.empty(),Optional.empty()))
        //                 .putSeason(Season.SUMMER,new ColorMode(Optional.of(Color.GREEN.getRGB()),Optional.empty(),Optional.empty()))
        //                 .putSeason(Season.AUTUMN,new ColorMode(Optional.of(Color.ORANGE.getRGB()),Optional.empty(),Optional.empty()))
        //                 .putSeason(Season.WINTER,new ColorMode(Optional.of(Color.BLUE.getRGB()),Optional.empty(),Optional.empty()))
        //                 .putSeason(Season.NONE,new ColorMode(Optional.of(Color.WHITE.getRGB()),Optional.empty(),Optional.empty()))
        //                 .ofBuild(),Optional.empty()
        // ));
    }
}
