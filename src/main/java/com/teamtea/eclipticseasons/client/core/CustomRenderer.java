package com.teamtea.eclipticseasons.client.core;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public class CustomRenderer {


    public List<BakedQuad> onRenderer(
            List<BakedQuad> orginial,
            BlockState state,
            int blockType,
            Direction face,
            RandomSource randomSource,
            BakedModel orginialModel,
            ModelData modelData
    ) {
        return orginial;
    }
}
