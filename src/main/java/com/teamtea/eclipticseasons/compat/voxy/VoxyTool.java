package com.teamtea.eclipticseasons.compat.voxy;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import me.cortex.voxy.client.core.model.bakery.ModelTextureBakery;
import me.cortex.voxy.client.core.model.bakery.ReuseVertexConsumer;
import me.cortex.voxy.common.voxelization.VoxelizedSection;
import me.cortex.voxy.common.world.other.Mapper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

public class VoxyTool {
    public static boolean isVoxyTest() {
        return CompatModule.CommonConfig.voxyTest.get();
    }


    public static void renderToStream(BlockState state, RenderType layer, ReuseVertexConsumer vc) {
        if (!isVoxyTest()) return;
        //if (VoxyConstant.shouldSkipCheck(state.getBlock())) return;

        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            //if (state.is(BlockTags.LOGS)) return;
            //if (state.getValue(VoxyConstant.SNOWY)) return;
            int defaultBlockTypeFlag = MapChecker.getDefaultBlockTypeFlag(state);
            BakedModel model = ExtraModelManager.getSnowyModel(state, null, defaultBlockTypeFlag, MapChecker.getSnowOffset(state, defaultBlockTypeFlag));
            if (model == null) {
                //modelLocalRef.set(sm);
                return;
            }
            int meta = ModelTextureBakery.getMetaFromLayer(state.getBlock() instanceof LeavesBlock ?
                    layer :
                    ExtraModelManager.getRenderType(state));
            for (Direction direction : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null}) {
                //int SNOW_FLAG = 1 << 30;

                for (BakedQuad quad : model.getQuads(state, direction, new SingleThreadedRandomSource(42L))) {
                    int quadMeta = meta | (quad.isTinted() ? 4 : 0);
                    //quadMeta |= SNOW_FLAG;
                    vc.quad(quad, quadMeta);
                }
            }
        }
    }


    public static int changeBlockId(int blockId, Mapper stateMapper, int i, VoxelizedSection section) {
        if (!isVoxyTest()) return blockId;
        int maxBlockId = 0xFFFFF;
        BlockState state = stateMapper.getBlockStateFromBlockId(blockId);
        if (MapChecker.getDefaultBlockTypeFlag(state)
                > MapChecker.FLAG_NONE) {
            BlockPos offset = SectionPos.of(section.x, section.y, section.z).origin()
                    .offset(i & 15, (i >> 8 & 15), i >> 4 & 15);

            Level level = ClientCon.getUseLevel();
            if (section instanceof IVoxyLevelProvider iVoxyLevelProvider) {
                Level levelBind = iVoxyLevelProvider.getLevelBind();
                if (levelBind != null) level = levelBind;
            }
            if (level != null
                    && EclipticSeasonsApi.getInstance().isSnowyBlock(level,
                    state, offset)) {
                blockId = maxBlockId - blockId;
            }
        }
        return blockId;
    }
}
