package com.teamtea.eclipticseasons.client.model;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BakedQuadRetexturedAndReUV extends BakedQuad {
    private final TextureAtlasSprite texture;
    public static int uvIndex = 4;
    public static int verticeSpace = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
    private final boolean isSlabDown;
    private final float offset;


    public BakedQuadRetexturedAndReUV(BakedQuad quad, TextureAtlasSprite textureIn, boolean isSlabDown) {
        this(quad, textureIn, isSlabDown, 0.5f);
    }

    public BakedQuadRetexturedAndReUV(BakedQuad quad, TextureAtlasSprite textureIn, boolean isSlabDown, float offset) {
        super(Arrays.copyOf(quad.getVertices(), quad.getVertices().length), -1, FaceBakery.calculateFacing(quad.getVertices()), textureIn, quad.isShade(), quad.hasAmbientOcclusion());
        this.texture = textureIn;
        this.isSlabDown = isSlabDown;
        this.offset = offset;
        this.remapQuad();
    }

    private void remapQuad() {
        Direction direction1 = getDirection();

        for (int i = 0; i < 4; ++i) {
            int j = verticeSpace * i;
            float oldU = getUnInterpolatedU(this.sprite, Float.intBitsToFloat(this.vertices[j + uvIndex]));
            float oldV = getUnInterpolatedV(this.sprite, Float.intBitsToFloat(this.vertices[j + uvIndex + 1]));

            switch (direction1) {
                case NORTH -> {
                    oldU = 1 - Float.intBitsToFloat(this.vertices[j]);
                    oldV = 1 - Float.intBitsToFloat(this.vertices[j + 1]);
                }
                case WEST -> {
                    oldU = Float.intBitsToFloat(this.vertices[j + 2]);
                    oldV = 1 - Float.intBitsToFloat(this.vertices[j + 1]);
                }
                case SOUTH -> {
                    oldU = Float.intBitsToFloat(this.vertices[j]);
                    oldV = 1 - Float.intBitsToFloat(this.vertices[j + 1]);
                }
                case EAST -> {
                    oldU = 1 - Float.intBitsToFloat(this.vertices[j + 2]);
                    oldV = 1 - Float.intBitsToFloat(this.vertices[j + 1]);
                }
                case UP -> {
                    oldU = Float.intBitsToFloat(this.vertices[j + 2]);
                    oldV = Float.intBitsToFloat(this.vertices[j]);
                }
                case DOWN -> {
                    oldU = 0;
                    oldV = 0;
                }
            }

            if (direction1.ordinal() > 1 && isSlabDown) {
                oldV -= offset;
            }

            oldU= Mth.clamp(oldU,0f,1.0f);
            oldV= Mth.clamp(oldV,0f,1.0f);

            this.vertices[j + uvIndex] = Float.floatToRawIntBits(this.texture.getU(oldU*16f));
            this.vertices[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getV(oldV*16f));
            // this.vertices[j + 3] = -1;
        }
    }

    // @Override
    // public @NotNull TextureAtlasSprite getSprite() {
    //     return texture;
    // }

    // We need not to mul it to 16f because internal changes
    private static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
        float f = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / f * 16.0F;
    }

    private static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
        float f = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / f * 16.0F;
    }


    // @Override
    // public int getTintIndex() {
    //     return -1;
    // }

    // @Override
    // public boolean isTinted() {
    //     return false;
    // }
}
