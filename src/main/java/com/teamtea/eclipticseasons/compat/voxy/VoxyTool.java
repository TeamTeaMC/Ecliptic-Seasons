package com.teamtea.eclipticseasons.compat.voxy;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import me.cortex.voxy.common.voxelization.VoxelizedSection;
import me.cortex.voxy.common.world.other.Mapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.IntConsumer;

public class VoxyTool {
    public static boolean isVoxyTest() {
        return CompatModule.CommonConfig.voxyTest.get();
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

    private static final int maxBlockId = 0xFFFFF;

    public static int fixId(Mapper mapper, int blockId) {
        return fixId(mapper, blockId, VoxyTool::emptyConsumer);
    }

    private static void emptyConsumer(int i) {
    }

    public static int fixId(Mapper mapper, int blockId, IntConsumer consumer) {
        int blockStateCount = mapper.getBlockStateCount();
        if (blockId < blockStateCount) return blockId;
        blockId = maxBlockId - blockId;
        if (blockId < blockStateCount) {
            consumer.accept(blockId);
            return blockId;
        }
        return maxBlockId - blockId;
    }
}
