package com.teamtea.eclipticseasons.data.general;

import com.mojang.datafixers.util.Either;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientDataMapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SeasonTextureProvider extends ESClientDataMapProvider<SeasonalTexture> {
    public SeasonTextureProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries, ClientJsonCacheListener.DIRECTORY_SEASON_TEXTURES, SeasonalTexture.CODEC);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        add(new ResourceLocation("oak_leaves"), new SeasonalTexture(
                Optional.empty(), Optional.of(Either.right(ClimateTypeBiomeTags.SEASONAL)), List.of(
                SeasonalTexture.Slice.builder().season(Season.SPRING)
                        .textures((Map.of(
                                "all", new ResourceLocation("block/cherry_leaves")
                        )))
                        .build()
        )
        ));
    }
}
