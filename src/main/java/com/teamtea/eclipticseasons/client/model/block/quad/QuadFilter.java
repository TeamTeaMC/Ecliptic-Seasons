package com.teamtea.eclipticseasons.client.model.block.quad;

import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuadFilter {

    public static float getMinValue(BakedQuad quad, int index) {
        float minV = 1;
        for (int i = 0; i < 4; i++) {
            Vector3fc position = quad.position(i);
            float v = switch (index) {
                case 0 -> position.x();
                case 1 -> position.y();
                default -> position.z();
            };
            if (v < minV) minV = v;
        }
        float epsilon = 1e-7f;
        return Math.abs(minV) < epsilon ? 0.0f : minV;
    }

    public static float getMaxValue(BakedQuad quad, int index) {
        float maxV = -1;
        for (int i = 0; i < 4; i++) {
            Vector3fc position = quad.position(i);
            float v = switch (index) {
                case 0 -> position.x();
                case 1 -> position.y();
                default -> position.z();
            };
            if (v > maxV) maxV = v;
        }
        return maxV;
    }

    public static float getMaxX(BakedQuad bakedQuad) {
        return getMaxValue(bakedQuad, 0);
    }

    public static float getMaxY(BakedQuad bakedQuad) {
        return getMaxValue(bakedQuad, 1);
    }

    public static float getMaxZ(BakedQuad bakedQuad) {
        return getMaxValue(bakedQuad, 2);
    }

    public static float getMinX(BakedQuad bakedQuad) {
        return getMinValue(bakedQuad, 0);
    }

    public static float getMinY(BakedQuad bakedQuad) {
        return getMinValue(bakedQuad, 1);
    }

    public static float getMinZ(BakedQuad bakedQuad) {
        return getMinValue(bakedQuad, 2);
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

        if (bakedQuad.direction() == Direction.UP) {
            if (y0 > y3)
                if ((x1 >= x3 && x0 <= x2)
                        && z1 >= z3 && z0 <= z2)
                    return true;
        } else if (bakedQuad.direction() == testQuad.direction()) {
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

    public static ArrayList<BakedQuad> fixQuadCTM(List<BakedQuad> quadsCTM) {

        quadsCTM.removeIf(bakedQuad -> bakedQuad.direction() == Direction.DOWN);

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
}
