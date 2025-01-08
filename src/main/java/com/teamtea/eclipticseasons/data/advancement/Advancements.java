package com.teamtea.eclipticseasons.data.advancement;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Advancements extends ForgeAdvancementProvider
{
    public Advancements(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper,List.of(new ESAdvancementGenerator()));
    }
}
