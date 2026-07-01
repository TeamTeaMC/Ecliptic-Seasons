package com.teamtea.eclipticseasons.client.core;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public final class SnowQuadSplitter {
    private SnowQuadSplitter() {
    }

    private static final int STRIDE = 8;
    private static final int VERTEX_COUNT = 4;

    public static void split(List<BakedQuad> result,BakedQuad quad, int split) {
        if (quad == null) return ;
        if (split <= 1) {
            return ;
        }

        int[] data = quad.getVertices();
        float[][] pos = readPositions(data);
        float[][] uv = readUvs(data);


        for (int y = 0; y < split; y++) {
            for (int x = 0; x < split; x++) {
                float u0 = x / (float) split;
                float u1 = (x + 1) / (float) split;
                float v0 = y / (float) split;
                float v1 = (y + 1) / (float) split;

                result.add(buildSubQuad(quad, pos, uv, u0, v0, u1, v1));
            }
        }

    }

    private static BakedQuad buildSubQuad(
            BakedQuad source,
            float[][] pos,
            float[][] uv,
            float u0,
            float v0,
            float u1,
            float v1
    ) {
        int[] out = new int[VERTEX_COUNT * STRIDE];

        putVertex(out, 0, sample(pos, u0, v0), sample(uv, u0, v0), source);
        putVertex(out, 1, sample(pos, u0, v1), sample(uv, u0, v1), source);
        putVertex(out, 2, sample(pos, u1, v1), sample(uv, u1, v1), source);
        putVertex(out, 3, sample(pos, u1, v0), sample(uv, u1, v0), source);

        return new BakedQuad(
                out,
                source.getTintIndex(),
                source.getDirection(),
                source.getSprite(),
                source.isShade()
        );
    }

    private static float[][] readPositions(int[] data) {
        float[][] pos = new float[4][3];

        for (int i = 0; i < 4; i++) {
            int base = i * STRIDE;
            pos[i][0] = Float.intBitsToFloat(data[base]);
            pos[i][1] = Float.intBitsToFloat(data[base + 1]);
            pos[i][2] = Float.intBitsToFloat(data[base + 2]);
        }

        return pos;
    }

    private static float[][] readUvs(int[] data) {
        float[][] uv = new float[4][2];

        for (int i = 0; i < 4; i++) {
            int base = i * STRIDE;
            uv[i][0] = Float.intBitsToFloat(data[base + 4]);
            uv[i][1] = Float.intBitsToFloat(data[base + 5]);
        }

        return uv;
    }

    private static float[] sample(float[][] p, float u, float v) {
        float[] left = lerp(p[0], p[1], v);
        float[] right = lerp(p[3], p[2], v);
        return lerp(left, right, u);
    }

    private static float[] lerp(float[] a, float[] b, float t) {
        float[] out = new float[a.length];

        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] + (b[i] - a[i]) * t;
        }

        return out;
    }

    private static void putVertex(
            int[] out,
            int index,
            float[] pos,
            float[] uv,
            BakedQuad source
    ) {
        int base = index * STRIDE;
        int sourceBase = index * STRIDE;
        int[] sourceData = source.getVertices();

        out[base] = Float.floatToRawIntBits(pos[0]);
        out[base + 1] = Float.floatToRawIntBits(pos[1]);
        out[base + 2] = Float.floatToRawIntBits(pos[2]);

        out[base + 3] = sourceData[sourceBase + 3];

        out[base + 4] = Float.floatToRawIntBits(uv[0]);
        out[base + 5] = Float.floatToRawIntBits(uv[1]);

        out[base + 6] = sourceData[sourceBase + 6];
        out[base + 7] = sourceData[sourceBase + 7];
    }
}