package com.teamtea.eclipticseasons.compat.voxy.client;

import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.core.context.ExtraRendererContext;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.client.core.model.bakery.ReuseVertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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

    public static void forceReloadAll() {
        if (!VoxyTool.isVoxyTest()
                || !ClientCon.getAgent().isChange()
                || !CompatModule.CommonConfig.voxyAutoRefresh.get()) return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || level.getGameTime() % (20 * 15) != 0) return;

        if (ClientCon.getAgent().isSnowChange()
                && VoxyGeometryRefreshManager.refreshAll()) {
            ClientCon.getAgent().setSnowChange(false);
        }
    }

    public static void renderToStream(BlockState state, ReuseVertexConsumer translucentVC, ReuseVertexConsumer opaqueVC) {
        if (!VoxyTool.isVoxyTest()) return;

        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            int defaultBlockTypeFlag = MapChecker.getDefaultBlockTypeFlag(state);
            BlockStateModel model = ExtraModelManager.getSnowyModel(state, null, defaultBlockTypeFlag, MapChecker.getSnowOffset(state, defaultBlockTypeFlag));
            if (model == null) {
                return;
            }
            ExtraRendererContext context = new ExtraRendererContext();
            context.setReplace(ExtraModelManager.isModelReplaceable(model, defaultBlockTypeFlag))
                    // .setExtraModel(model)
                    .setOriginalModel(ExtraModelManager.models.getBlockStateModel(state))
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
