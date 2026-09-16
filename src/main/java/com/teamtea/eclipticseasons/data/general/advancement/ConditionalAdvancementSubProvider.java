package com.teamtea.eclipticseasons.data.general.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public abstract class ConditionalAdvancementSubProvider
        extends AdvancementSubProvider {

    protected ConditionalAdvancementSubProvider(BootstrapContext<Advancement> output) {
        super(output);
    }

    abstract void generate(
            HolderLookup.Provider registries,
            BootstrapContext<Advancement> output
    );

    @Override
    public void generate() {
        throw new UnsupportedOperationException();
    }
}