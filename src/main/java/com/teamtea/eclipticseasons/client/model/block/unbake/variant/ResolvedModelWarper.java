package com.teamtea.eclipticseasons.client.model.block.unbake.variant;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Data
@Accessors(chain = true)
public class ResolvedModelWarper implements ResolvedModel {
    private final ResolvedModel base;
    private TextureSlots newT;
    private UnbakedGeometry newG;

    @Override
    public @NonNull TextureSlots getTopTextureSlots() {
        return newT != null ? newT : ResolvedModel.super.getTopTextureSlots();
    }

    @Override
    public @NonNull UnbakedGeometry getTopGeometry() {
        return newG != null ? newG : ResolvedModel.super.getTopGeometry();
    }


    @Override
    public @NonNull UnbakedModel wrapped() {
        return base.wrapped();
    }

    @Override
    public @Nullable ResolvedModel parent() {
        return base.parent();
    }

    @Override
    public @NonNull String debugName() {
        return base.debugName();
    }
}
