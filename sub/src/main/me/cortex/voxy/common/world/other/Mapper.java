package me.cortex.voxy.common.world.other;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.block.state.BlockState;

public class Mapper {
    public static long composeMappingId(byte light, int blockId, int biomeId) {
        return 0;
    }

    public BlockState getBlockStateFromBlockId(int blockId) {
        return null;
    }

    public int getBlockStateCount() {
        return 0;
    }

    private final ObjectArrayList<StateEntry> blockId2stateEntry = new ObjectArrayList<>();

    public int getBlockStateOpacity(int blockId) {
        return this.blockId2stateEntry.get(blockId).opacity;
    }

    public static final class StateEntry {
        int opacity;
    }
}
