package com.teamtea.eclipticseasons.client.model.block.unbake.variant;

import lombok.Data;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;

@Data
public class VariantHolder implements BlockStateModelPart.Unbaked {
    private final Variant variant;

    @Override
    public BlockStateModelPart bake(ModelBaker modelBakery) {
        return SimpleModelWrapper.bake(modelBakery, variant.modelLocation(), variant.modelState().asModelState());
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        variant.resolveDependencies(resolver);
    }


}
