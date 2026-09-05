package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;

import java.util.List;

public class EmptyStateModel implements BlockStateModel {
    public static final EmptyStateModel EMPTY = new EmptyStateModel();

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
    }

    @Override
    public Material.Baked particleMaterial() {
        return null;
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return 0;
    }
}
