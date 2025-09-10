package com.teamtea.eclipticseasons.client.model.bakequad;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

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
        super(Arrays.copyOf(quad.getVertices(), quad.getVertices().length), -1,quad.getDirection(), textureIn, quad.isShade(), quad.hasAmbientOcclusion());
        this.texture = textureIn;
        this.isSlabDown = isSlabDown;
        this.offset = offset;
        this.remapQuad();
    }

    private void remapQuad() {
        // Direction direction1 = getDirection();

        float x0 = Float.intBitsToFloat(this.vertices[0]);
        float y0 = Float.intBitsToFloat(this.vertices[1]);
        float z0 = Float.intBitsToFloat(this.vertices[2]);

        float x1 = Float.intBitsToFloat(this.vertices[verticeSpace]);
        float y1 = Float.intBitsToFloat(this.vertices[verticeSpace + 1]);
        float z1 = Float.intBitsToFloat(this.vertices[verticeSpace + 2]);

        float x3 = Float.intBitsToFloat(this.vertices[3 * verticeSpace]);
        float y3 = Float.intBitsToFloat(this.vertices[3 * verticeSpace + 1]);
        float z3 = Float.intBitsToFloat(this.vertices[3 * verticeSpace + 2]);

        // 边向量
        float edge1X = x1 - x0;
        float edge1Y = y1 - y0;
        float edge1Z = z1 - z0;

        float edge2X = x3 - x0;
        float edge2Y = y3 - y0;
        float edge2Z = z3 - z0;

        // 边长度
        float lenU = (float) Math.sqrt(edge2X*edge2X + edge2Y*edge2Y + edge2Z*edge2Z);
        float lenV = (float) Math.sqrt(edge1X*edge1X + edge1Y*edge1Y + edge1Z*edge1Z);

        for (int i = 0; i < 4; ++i) {
            int j = verticeSpace * i;

            float x = Float.intBitsToFloat(this.vertices[j]);
            float y = Float.intBitsToFloat(this.vertices[j + 1]);
            float z = Float.intBitsToFloat(this.vertices[j + 2]);

            // 面局部坐标投影（交换顺序避免 90° 偏转）
            float du = ((x - x0) * edge2X + (y - y0) * edge2Y + (z - z0) * edge2Z)
                    / (edge2X * edge2X + edge2Y * edge2Y + edge2Z * edge2Z);
            float dv = ((x - x0) * edge1X + (y - y0) * edge1Y + (z - z0) * edge1Z)
                    / (edge1X * edge1X + edge1Y * edge1Y + edge1Z * edge1Z);

            // if (isSlabDown) {
            //     dv -= offset;
            // }

            du *= lenU ;
            dv *= lenV ;

            du = Mth.clamp(du, 0f, 1f);
            dv = Mth.clamp(dv, 0f, 1f);

            this.vertices[j + uvIndex]     = Float.floatToRawIntBits(this.texture.getU(du * 16f));
            this.vertices[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getV(dv * 16f));
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
