/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.teamtea.eclipticseasons.client.model.block.unbake.standalone;

import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.client.model.block.unbake.SolarBlockModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;
import org.jspecify.annotations.NonNull;

import java.util.List;


public record SeasonalUnbakeModel<T>(
        Identifier baseModel, List<SeasonalTexture> seasonalTextures
) implements UnbakedStandaloneModel<T> {


    @Override
    public T bake(@NonNull ModelBaker baker) {
        BlockStateModel bake = SolarBlockModel.bake(baker, baker.getModel(baseModel), seasonalTextures);
        T t = (T) bake;
        return t;
    }

    @Override
    public void resolveDependencies(@NonNull Resolver resolver) {
        for (SeasonalTexture seasonalTexture : seasonalTextures) {
            for (Identifier identifier : seasonalTexture.getParent()) {
                resolver.markDependency(identifier);
            }
        }
    }
}
