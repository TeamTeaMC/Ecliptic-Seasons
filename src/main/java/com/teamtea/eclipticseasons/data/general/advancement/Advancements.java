package com.teamtea.eclipticseasons.data.general.advancement;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Advancements extends AdvancementProvider {
    public Advancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new ESAdvancementGenerator()));
    }

    //public Advancements(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
    //    super(output, lookupProvider, List.of(new ESAdvancementGenerator()));
    //}
}
