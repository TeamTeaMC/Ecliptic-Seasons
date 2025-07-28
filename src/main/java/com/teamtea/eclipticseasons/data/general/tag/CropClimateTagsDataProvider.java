package com.teamtea.eclipticseasons.data.general.tag;

import com.teamtea.eclipticseasons.api.constant.tag.CropClimateTags;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CropClimateTagsDataProvider extends TagsProvider<AgroClimaticZone> {


    public CropClimateTagsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, ESRegistries.AGRO_CLIMATE, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<AgroClimaticZone> lookup = provider.lookupOrThrow(ESRegistries.AGRO_CLIMATE);
        for (Holder.Reference<AgroClimaticZone> cropClimateTypeReference : lookup.listElements().sorted(Comparator.comparing(
              r->r.key().location()
        )).toList()) {
            tag(CropClimateTags.ALL).add(cropClimateTypeReference.key());
        }
        tag(CropClimateTags.OVERWORLD).addAll(List.of(AgroClimateRegistry.TEMPERATE, AgroClimateRegistry.COLD, AgroClimateRegistry.HOT
                // AgroClimateRegistry.DESERT
        ));
    }
}
