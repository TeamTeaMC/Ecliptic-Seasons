/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.teamtea.eclipticseasons.client.model.block.unbake.standalone;

import java.util.function.BiFunction;

import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;
import org.jspecify.annotations.NonNull;

public class CustomUnbakeModel<T> implements UnbakedStandaloneModel<T> {

    private final ResolvableModel unbaked;
    private final BiFunction<ResolvableModel, ModelBaker, T> bake;

    public CustomUnbakeModel(ResolvableModel unbaked, BiFunction<ResolvableModel, ModelBaker, T> bake) {
        this.unbaked = unbaked;
        this.bake = bake;
    }

    @Override
    public T bake(@NonNull ModelBaker baker) {
        return bake.apply(unbaked, baker);
    }

    @Override
    public void resolveDependencies(@NonNull Resolver resolver) {
        // resolver.markDependency(modelId);
        unbaked.resolveDependencies(resolver);
    }
}
