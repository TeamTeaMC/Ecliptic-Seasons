package com.teamtea.eclipticseasons.client.model.bakequad;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuadFixer {
    public static int posIndex = 0;
    public static int vertice_Space = DefaultVertexFormat.BLOCK.getVertexSize() / 4;

    public static List<Object> getBakeQuadInfo(BakedQuad bakedQuad) {
        ArrayList<Object> stringLike = new ArrayList<>();
        stringLike.add(bakedQuad.getSprite().contents().name());
        stringLike.add(",");
        stringLike.add(bakedQuad.getDirection());
        stringLike.add(",");
        stringLike.addAll(List.of(
                getMinX(bakedQuad), ",", getMinY(bakedQuad), ",", getMinZ(bakedQuad), ",",
                getMaxX(bakedQuad), ",", getMaxY(bakedQuad), ",", getMaxZ(bakedQuad)
        ));
        return stringLike;
    }

    public static float getMinValue(int[] vertices, int index) {
        float minV = 1;
        for (int i = 0; i < 4; i++) {
            int j = vertice_Space * i;
            float v = Float.intBitsToFloat(vertices[j + posIndex + index]);
            if (v < minV) minV = v;
        }
        float epsilon = 1e-7f;
        return Math.abs(minV) < epsilon ? 0.0f : minV;
    }

    public static float getMaxValue(int[] vertices, int index) {
        float maxV = -1;
        for (int i = 0; i < 4; i++) {
            int j = vertice_Space * i;
            float v = Float.intBitsToFloat(vertices[j + posIndex + index]);
            if (v > maxV) maxV = v;
        }
        return maxV;
    }

    public static float getMaxX(BakedQuad bakedQuad) {
        return getMaxValue(bakedQuad.getVertices(), 0);
    }

    public static float getMaxY(BakedQuad bakedQuad) {
        return getMaxValue(bakedQuad.getVertices(), 1);
    }

    public static float getMaxZ(BakedQuad bakedQuad) {
        return getMaxValue(bakedQuad.getVertices(), 2);
    }

    public static float getMinX(BakedQuad bakedQuad) {
        return getMinValue(bakedQuad.getVertices(), 0);
    }

    public static float getMinY(BakedQuad bakedQuad) {
        return getMinValue(bakedQuad.getVertices(), 1);
    }

    public static float getMinZ(BakedQuad bakedQuad) {
        return getMinValue(bakedQuad.getVertices(), 2);
    }

    public static boolean cover(BakedQuad bakedQuad, BakedQuad testQuad) {


        float x0 = getMinX(bakedQuad);
        float x1 = getMaxX(bakedQuad);
        float x2 = getMinX(testQuad);
        float x3 = getMaxX(testQuad);

        float y0 = getMinY(bakedQuad);
        float y1 = getMaxY(bakedQuad);
        float y2 = getMinY(testQuad);
        float y3 = getMaxY(testQuad);

        float z0 = getMinZ(bakedQuad);
        float z1 = getMaxZ(bakedQuad);
        float z2 = getMinZ(testQuad);
        float z3 = getMaxZ(testQuad);

        // TODO: CTM would bring some invalid quad
        boolean result = (x0 == x1 ? 1 : 0) + (y0 == y1 ? 1 : 0) + (z0 == z1 ? 1 : 0) >= 2;
        if (result) return false;

        if (bakedQuad.getDirection() == Direction.UP) {
            if (y0 > y3)
                if ((x1 >= x3 && x0 <= x2)
                        && z1 >= z3 && z0 <= z2)
                    return true;
        } else if (bakedQuad.getDirection() == testQuad.getDirection()) {
            // 平行x轴

            if (x0 == x1 && x2 == x3 && x0 == x2) {
                if (z0 <= z2 && z1 >= z3)
                    return y1 > y3;

            } else if (z0 == z1 && z2 == z3 && z0 == z2) {
                if (x0 <= x2 && x1 >= x3)
                    return y1 > y3;
            }


        }


        return false;
    }

    public static ArrayList<BakedQuad> fixQuadCTM(ArrayList<BakedQuad> quadsCTM) {
        quadsCTM.removeIf(bakedQuad -> bakedQuad.getDirection() == Direction.DOWN);
        quadsCTM.removeIf(bakedQuad -> bakedQuad.getSprite().contents().name().getPath().contains("grape_small_leaves"));
        quadsCTM.removeIf(bakedQuad -> bakedQuad.getSprite().contents().name().getPath().contains("grape_stage"));

        quadsCTM.sort(Comparator.comparingDouble(b -> getMaxY(((BakedQuad) b))).reversed());

        ArrayList<BakedQuad> visibleFaces = new ArrayList<>();
        for (int i = 0; i < quadsCTM.size(); i++) {
            BakedQuad faceA = quadsCTM.get(i);
            boolean isCovered = false;

            for (int j = 0; j < i; j++) {
                BakedQuad faceB = quadsCTM.get(j);
                if (cover(faceB, faceA)) {
                    isCovered = true;
                    break;
                }
            }
            if (!isCovered) {
                visibleFaces.add(faceA);
            }
        }
        return visibleFaces;
    }


     public static Direction getDirectionQuickly
             (BakedQuad instance) {
         int[] vertices = instance.getVertices();

         // 基准顶点
         float x0 = Float.intBitsToFloat(vertices[0]);
         float y0 = Float.intBitsToFloat(vertices[1]);
         float z0 = Float.intBitsToFloat(vertices[2]);

         float x1 = Float.intBitsToFloat(vertices[8]);
         float y1 = Float.intBitsToFloat(vertices[9]);
         float z1 = Float.intBitsToFloat(vertices[10]);

         float x3 = Float.intBitsToFloat(vertices[24]);
         float y3 = Float.intBitsToFloat(vertices[25]);
         float z3 = Float.intBitsToFloat(vertices[26]);

         // 边向量
         float edge1X = x1 - x0;
         float edge1Y = y1 - y0;
         float edge1Z = z1 - z0;

         float edge2X = x3 - x0;
         float edge2Y = y3 - y0;
         float edge2Z = z3 - z0;

         // 法线
         float nx = edge1Y * edge2Z - edge1Z * edge2Y;
         float ny = edge1Z * edge2X - edge1X * edge2Z;
         float nz = edge1X * edge2Y - edge1Y * edge2X;

         float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
         if (len > 1e-6f) {
             nx /= len;
             ny /= len;
             nz /= len;
         } else {
             // 退化，交给原逻辑
             return instance.getDirection();
         }

         // 角度阈值：cos(75°) ≈ 0.2588
         if (Math.abs(ny) >= 0.2588f) {
             // 小于75° → 算成UP
             return Direction.UP;
         } else {
             // 大于等于75° → 基本竖直，按XZ选择方向
             return Math.abs(nx) > Math.abs(nz)
                     ? (nx > 0 ? Direction.EAST : Direction.WEST)
                     : (nz > 0 ? Direction.SOUTH : Direction.NORTH);
         }
     }
}
