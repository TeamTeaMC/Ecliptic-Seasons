package com.teamtea.eclipticseasons.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.HashSet;

public class RectangularPrismChecker {

    public static int xyzIndex = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.POSITION) / 4;
    public static int verticeSpace = DefaultVertexFormat.BLOCK.getVertexSize() / 4;

    public static boolean isRectangularPrism(BakedQuad bakedQuad) {
        if (bakedQuad == null) return false;
        HashSet<Integer> projections = new HashSet<>();

        for (int i = 0; i < 4; i++) {
            int j = verticeSpace * i;
            projections.add(bakedQuad.getVertices()[j + xyzIndex + 1]);
        }

        // 斜面的y小于情况
        if (projections.size() > 2)
            return false;


        HashSet<Integer> projectionsx = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            int j = verticeSpace * i;
            projectionsx.add(bakedQuad.getVertices()[j + xyzIndex]);
        }
        HashSet<Integer> projectionsz = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            int j = verticeSpace * i;
            projectionsz.add(bakedQuad.getVertices()[j + xyzIndex+2]);
        }

        // 没有平行任何一个轴
        return projections.size() != 2 || projectionsz.size() != 2 || projectionsx.size() != 2;
    }


}
