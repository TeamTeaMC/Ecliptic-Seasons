package com.teamtea.eclipticseasons.client.model;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BakedQuadRetexturedAndOffset extends BakedQuad {
    public static int uvIndex = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.POSITION) / 4;
    public static int verticeSpace = DefaultVertexFormat.BLOCK.getVertexSize() / 4;

    private final float y1;
    private final float y2;

    public BakedQuadRetexturedAndOffset(BakedQuad quad,  float y1, float y2) {
        super(Arrays.copyOf(quad.getVertices(), quad.getVertices().length), quad.getTintIndex(), FaceBakery.calculateFacing(quad.getVertices()), quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion());
        this.y1 = y1;
        this.y2 = y2;

        this.remapQuad();
    }

    private void remapQuad() {
        for (int i = 0; i < 4; ++i) {
            int j = verticeSpace * i;
            float y = Float.intBitsToFloat(this.vertices[j + uvIndex + 1]);
            y = y > 0.5f ? y1 : y2;
            this.vertices[j + uvIndex + 1] = Float.floatToRawIntBits(y);
        }
    }

    @Override
    public @NotNull TextureAtlasSprite getSprite() {
        return sprite;
    }

    @Override
    public int getTintIndex() {
        return -1;
    }

    @Override
    public boolean isTinted() {
        return false;
    }
}
