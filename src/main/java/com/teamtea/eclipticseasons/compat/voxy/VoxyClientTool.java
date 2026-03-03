package com.teamtea.eclipticseasons.compat.voxy;

import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import me.cortex.voxy.client.core.model.bakery.ModelTextureBakery;
import me.cortex.voxy.client.core.model.bakery.ReuseVertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

import java.util.List;

public class VoxyClientTool {

    public static void renderToStream(BlockState state, RenderType layer, ReuseVertexConsumer vc) {
        if (!VoxyTool.isVoxyTest()) return;

        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            int defaultBlockTypeFlag = MapChecker.getDefaultBlockTypeFlag(state);
            BakedModel model = ExtraModelManager.getSnowyModel(state, null, defaultBlockTypeFlag, MapChecker.getSnowOffset(state, defaultBlockTypeFlag));
            if (model == null) {
                return;
            }
            ExtraRendererContext context = new ExtraRendererContext();
            context.setReplace(ExtraModelManager.isModelReplaceable(state, ClientCon.getUseLevel(), BlockPos.ZERO, model))
                    .setExtraModel(model)
                    .setOriginalModel(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state))
            ;

            int meta = ModelTextureBakery.getMetaFromLayer(state.getBlock() instanceof LeavesBlock ?
                    layer :
                    ExtraModelManager.getRenderType(state));
            for (Direction direction : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null}) {

                for (BakedQuad quad :
                        ExtraModelManager.cancelTop(context, model, ClientCon.getUseLevel(),
                                state, BlockPos.ZERO, direction,
                                ClientCon.getUseLevel().getRandom(), 42L,
                                model.getQuads(state, direction, new SingleThreadedRandomSource(42L)),
                                List.of())) {
                    int quadMeta = meta | (quad.isTinted() ? 4 : 0);
                    vc.quad(quad, quadMeta);
                }
            }
        }
    }


}
