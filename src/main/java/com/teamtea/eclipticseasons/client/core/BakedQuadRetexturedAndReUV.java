package com.teamtea.eclipticseasons.client.core;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BakedQuadRetexturedAndReUV extends BakedQuad {
    private final TextureAtlasSprite texture;
    public static int uvIndex = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.UV0) / 4;
    public static int verticeSpace = DefaultVertexFormat.BLOCK.getVertexSize() / 4;
    private final boolean isSlabDown;

    public BakedQuadRetexturedAndReUV(BakedQuad quad, TextureAtlasSprite textureIn,boolean isSlabDown) {
        super(Arrays.copyOf(quad.getVertices(), quad.getVertices().length), quad.getTintIndex(), FaceBakery.calculateFacing(quad.getVertices()), quad.getSprite(), quad.isShade());
        this.texture = textureIn;
        this.isSlabDown=isSlabDown;
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
                    oldU = 1-Float.intBitsToFloat(this.vertices[j + 0]);
                    oldV = 1-Float.intBitsToFloat(this.vertices[j + 1]);
                }
                case WEST -> {
                    oldU = Float.intBitsToFloat(this.vertices[j + 2]);
                    oldV = 1-Float.intBitsToFloat(this.vertices[j + 1]);
                }
                case SOUTH -> {
                    oldU =  Float.intBitsToFloat(this.vertices[j + 0]);
                    oldV = 1-Float.intBitsToFloat(this.vertices[j + 1]);
                }
                case EAST -> {
                    oldU = 1 - Float.intBitsToFloat(this.vertices[j + 2]);
                    oldV = 1-Float.intBitsToFloat(this.vertices[j + 1]);
                }
                case UP -> {
                    oldU =Float.intBitsToFloat(this.vertices[j + 2]);
                    oldV = Float.intBitsToFloat(this.vertices[j + 0]);
                }
                case DOWN -> {
                    oldU = 0;
                    oldV = 0;
                }
            }

            if(direction1.ordinal()>1&&isSlabDown){
                oldV-=0.5f;
            }

            this.vertices[j + uvIndex] = Float.floatToRawIntBits(this.texture.getU(oldU));
            this.vertices[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getV(oldV));
        }
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return super.getSprite();
    }

    // We need not to mul it to 16f because internal changes
    private static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
        float f = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / f;
    }

    private static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
        float f = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / f;
    }

}
