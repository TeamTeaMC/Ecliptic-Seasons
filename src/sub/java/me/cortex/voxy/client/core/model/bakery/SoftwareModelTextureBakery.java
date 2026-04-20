package me.cortex.voxy.client.core.model.bakery;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

public class SoftwareModelTextureBakery {
    private final ReuseVertexConsumer vc = new ReuseVertexConsumer();

    public static int getMetaFromLayer(RenderType layer) {
        return 0;
    }

    private void bakeBlockModel(BlockState state, RenderType layer) {
        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
            int meta = getMetaFromLayer(layer);

            for(Direction direction : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null}) {
                for(BakedQuad quad : model.getQuads(state, direction, new SingleThreadedRandomSource(42L))) {
                    this.vc.quad(quad, meta | (quad.isTinted() ? 4 : 0));
                }
            }

        }
    }

    public int renderToStream(BlockState state, int streamBuffer, int streamOffset) {
        return 0;
    }
}
