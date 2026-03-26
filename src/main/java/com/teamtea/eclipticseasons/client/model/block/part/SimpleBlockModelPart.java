package com.teamtea.eclipticseasons.client.model.block.part;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record SimpleBlockModelPart(Map<Direction, List<BakedQuad>> map) implements BlockStateModelPart {

    @Override
    public @NonNull List<BakedQuad> getQuads(@Nullable Direction direction) {
        return map.getOrDefault(direction, List.of());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public Material.@NonNull Baked particleMaterial() {
        return new Material.Baked(null, true);
    }


    @Override
    public int materialFlags() {
        return -1;
    }
}
