package com.teamtea.eclipticseasons.data.general.advancement;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public interface ConditionalAdvancementSubProvider
        extends AdvancementProvider.AdvancementGenerator {

    void generate(
            HolderLookup.Provider registries,
            ConditionalAdvancementOutput output,
            ExistingFileHelper existingFileHelper
    );

    @Override
    default void generate(
            HolderLookup.Provider registries,
            Consumer<AdvancementHolder> output,
            ExistingFileHelper existingFileHelper
    ) {
        generate(
                registries,
                (advancement, conditions) -> output.accept(advancement),
                existingFileHelper
        );
    }
}