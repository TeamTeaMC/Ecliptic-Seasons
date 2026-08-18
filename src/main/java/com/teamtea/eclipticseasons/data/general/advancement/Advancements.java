package com.teamtea.eclipticseasons.data.general.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Advancements extends AdvancementProvider {
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final List<ConditionalAdvancementSubProvider> subProviders;

    public Advancements(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries
    ) {
        this(
                output,
                registries,
                List.of(
                        new ESAdvancementGenerator()
                )
        );
    }

    private Advancements(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            List<ConditionalAdvancementSubProvider> subProviders
    ) {
        super(output, registries, asVanillaSubProviders(subProviders));

        this.pathProvider =
                output.createRegistryElementsPathProvider(
                        Registries.ADVANCEMENT
                );
        this.registries = registries;
        this.subProviders = List.copyOf(subProviders);
    }

    private static List<AdvancementSubProvider> asVanillaSubProviders(
            List<ConditionalAdvancementSubProvider> subProviders
    ) {
        return new ArrayList<>(subProviders);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.registries.thenCompose(lookup -> {
            Set<Identifier> generatedAdvancements = new HashSet<>();
            List<CompletableFuture<?>> tasks = new ArrayList<>();

            ConditionalAdvancementOutput output =
                    (holder, conditions) -> {
                        if (!generatedAdvancements.add(holder.id())) {
                            throw new IllegalStateException(
                                    "Duplicate advancement " + holder.id()
                            );
                        }

                        tasks.add(DataProvider.saveStable(
                                cache,
                                lookup,
                                Advancement.CONDITIONAL_CODEC,
                                Optional.of(new WithConditions<>(
                                        holder.value(),
                                        conditions
                                )),
                                this.pathProvider.json(holder.id())
                        ));
                    };

            for (ConditionalAdvancementSubProvider subProvider
                    : this.subProviders) {
                subProvider.generate(lookup, output);
            }

            return CompletableFuture.allOf(
                    tasks.toArray(CompletableFuture[]::new)
            );
        });
    }
}