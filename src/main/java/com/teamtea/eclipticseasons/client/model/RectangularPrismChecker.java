package com.teamtea.eclipticseasons.client.model;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.client.renderer.block.model.BakedQuad;

public class RectangularPrismChecker {

    public static int xyzIndex = 0;
    public static int verticeSpace = DefaultVertexFormat.BLOCK.getVertexSize() / 4;

    public static boolean isRectangularPrism(BakedQuad bakedQuad) {
        if (bakedQuad == null) return false;
        IntArraySet projections = new IntArraySet(4);
        for (int i = 0; i < 4; i++) {
            int j = verticeSpace * i;
            projections.add(bakedQuad.getVertices()[j + xyzIndex + 1]);
        }

        // 斜面的y小于情况
        if (projections.size() > 2)
            return false;


        IntArraySet projectionsx = new IntArraySet(4);
        for (int i = 0; i < 4; i++) {
            int j = verticeSpace * i;
            projectionsx.add(bakedQuad.getVertices()[j + xyzIndex]);
        }
        IntArraySet projectionsz = new IntArraySet(4);
        for (int i = 0; i < 4; i++) {
            int j = verticeSpace * i;
            projectionsz.add(bakedQuad.getVertices()[j + xyzIndex+2]);
        }

        // 没有平行任何一个轴
        return projections.size() != 2 || projectionsz.size() != 2 || projectionsx.size() != 2;
    }


}
