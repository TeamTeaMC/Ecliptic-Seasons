package com.teamtea.eclipticseasons.data.tag;

import com.teamtea.eclipticseasons.api.constant.tag.CropClimateTags;
import com.teamtea.eclipticseasons.api.data.climate.CropClimateType;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CropClimateTagsDataProvider extends TagsProvider<CropClimateType> {


    public CropClimateTagsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, ESRegistries.CROP_CLIMATE, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (Holder.Reference<CropClimateType> cropClimateTypeReference : provider.lookupOrThrow(ESRegistries.CROP_CLIMATE).listElements().toList()) {
            tag(CropClimateTags.ALL).add(cropClimateTypeReference.key());
        }
    }
}
