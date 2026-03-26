package com.teamtea.eclipticseasons.data.extend.example;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.data.api.provider.base.ESClientDataMapProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SeasonTextureProvider extends ESClientDataMapProvider<SeasonalTexture> {
    public SeasonTextureProvider(PackOutput output, String modid,  CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid,  registries, ClientJsonCacheListener.DIRECTORY_SEASON_TEXTURES, SeasonalTexture.CODEC);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        add(Identifier.withDefaultNamespace("oak_leaves"), new SeasonalTexture(
                List.of(), Optional.empty(), Optional.of(Either.right(ClimateTypeBiomeTags.SEASONAL)), List.of(
                SeasonalTexture.Slice.builder().season(Season.SPRING)
                        .textures(List.of((Map.of(
                                "all", Identifier.withDefaultNamespace("block/cherry_leaves")
                        ))))
                        .build()
        )
        ));

        add(Identifier.withDefaultNamespace("oak_leaves_2"), new SeasonalTexture(
                List.of(Identifier.withDefaultNamespace("block/oak_leaves")),
                Optional.empty(),
                Optional.of(Either.right(ClimateTypeBiomeTags.SEASONAL)),
                List.of(SeasonalTexture.Slice.builder().season(Season.SPRING)
                        .textures(List.of(Map.of(
                                "all", Identifier.withDefaultNamespace("block/cherry_leaves")
                        ), Map.of(
                                "all", Identifier.withDefaultNamespace("block/spruce_leaves")
                        )))
                        .tintMap(Map.of("#all", -1))
                        .build()
                )
        ));

        add(Identifier.withDefaultNamespace("oak_leaves_3"), new SeasonalTexture(
                List.of(Identifier.withDefaultNamespace("block/oak_leaves")), Optional.empty(), Optional.of(Either.right(ClimateTypeBiomeTags.SEASONAL)), List.of(
                SeasonalTexture.Slice.builder().season(Season.SPRING)
                        .transitionMaterials(List.of(
                                Pair.of(Map.of(
                                        "all", Identifier.withDefaultNamespace("block/cherry_leaves")
                                ), Map.of(
                                        "all", Identifier.withDefaultNamespace("block/spruce_leaves")
                                ))
                        ))
                        .build()
        )
        ));

        add(Identifier.withDefaultNamespace("oak_leaves_4"), new SeasonalTexture(
                List.of(Identifier.withDefaultNamespace("block/oak_leaves")), Optional.empty(), Optional.of(Either.right(ClimateTypeBiomeTags.SEASONAL)), List.of(
                SeasonalTexture.Slice.builder().season(Season.SPRING)
                        .transitionMaterials(List.of(
                                Pair.of(Map.of(
                                        "all", Identifier.withDefaultNamespace("block/cherry_leaves")
                                ), Map.of(
                                        "all", Identifier.withDefaultNamespace("block/spruce_leaves")
                                )),
                                Pair.of(Map.of(
                                        "all", Identifier.withDefaultNamespace("block/cherry_leaves")
                                ), Map.of(
                                        "all", Identifier.withDefaultNamespace("block/spruce_leaves")
                                ))
                        ))
                        .build()
        )
        ));
    }
}
