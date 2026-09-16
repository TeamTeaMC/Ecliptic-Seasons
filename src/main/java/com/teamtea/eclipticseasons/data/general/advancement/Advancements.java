package com.teamtea.eclipticseasons.data.general.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.packs.*;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.neoforged.neoforge.common.data.internal.NeoForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Advancements extends AdvancementProvider {

    // public static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder()
    //         .add(Registries.ADVANCEMENT, new Advancements());

    public Advancements(

    ) {
        super(List.of(
                ESAdvancementGenerator::new
        ));
    }
}
