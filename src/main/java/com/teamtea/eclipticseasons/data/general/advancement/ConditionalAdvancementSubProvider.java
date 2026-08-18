package com.teamtea.eclipticseasons.data.general.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public interface ConditionalAdvancementSubProvider
        extends AdvancementSubProvider {

    void generate(
            HolderLookup.Provider registries,
            ConditionalAdvancementOutput output
    );

    @Override
    default void generate(
            HolderLookup.@NonNull Provider registries,
            @NonNull Consumer<AdvancementHolder> output
    ) {
        generate(
                registries,
                (advancement, conditions) -> output.accept(advancement)
        );
    }
}