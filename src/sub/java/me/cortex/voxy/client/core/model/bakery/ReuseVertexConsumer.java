package me.cortex.voxy.client.core.model.bakery;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;

public class ReuseVertexConsumer {
    public ReuseVertexConsumer quad(BakedQuad quad, int metadata) {
        return this;
    }

    public ReuseVertexConsumer quad(BakedQuad quad, boolean b, RenderType layer) {
        return null;
    }
}
