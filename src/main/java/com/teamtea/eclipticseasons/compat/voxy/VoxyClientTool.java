package com.teamtea.eclipticseasons.compat.voxy;

import com.teamtea.eclipticseasons.client.core.SeasonModelManager;
import com.teamtea.eclipticseasons.client.core.context.SeasonRendererContext;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import me.cortex.voxy.client.core.model.bakery.ReuseVertexConsumer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

import java.util.ArrayList;
import java.util.List;

public class VoxyClientTool {

    public static void renderToStream(BlockState state, ReuseVertexConsumer translucentVC, ReuseVertexConsumer opaqueVC) {
        if (!VoxyTool.isVoxyTest()) return;

        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            int defaultBlockTypeFlag = MapChecker.getDefaultBlockTypeFlag(state);
            BlockStateModel model = SeasonModelManager.getSnowyModel(state, null, defaultBlockTypeFlag, MapChecker.getSnowOffset(state, defaultBlockTypeFlag));
            if (model == null) {
                return;
            }
            SeasonRendererContext context = new SeasonRendererContext();
            context.setReplace(SeasonModelManager.isModelReplaceable(model, defaultBlockTypeFlag))
                    .add(model)
                    .setOriginalModel(SeasonModelManager.models.getBlockStateModel(state))
            ;

            List<BlockStateModelPart> out = new ArrayList<>();
            model.collectParts(new SingleThreadedRandomSource(42L), out);

            for (BlockStateModelPart part : out) {
                for (Direction direction : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null}) {
                    for (BakedQuad quad : part.getQuads(direction)) {
                        (quad.materialInfo().layer() == ChunkSectionLayer.TRANSLUCENT ? translucentVC : opaqueVC).quad(quad, state.is(BlockTags.LEAVES));
                    }
                }
            }

        }
    }


}
