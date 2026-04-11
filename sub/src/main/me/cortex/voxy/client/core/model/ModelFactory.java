package me.cortex.voxy.client.core.model;

import me.cortex.voxy.client.core.model.bakery.SoftwareModelTextureBakery;
import me.cortex.voxy.common.world.other.Mapper;

public class ModelFactory {
    private final Mapper mapper = null;
    public final SoftwareModelTextureBakery bakery2 = null;

    public  boolean addEntry(int blockId) {
        var blockState = this.mapper.getBlockStateFromBlockId(blockId);
        int flags = this.bakery2.renderToStream(blockState, 0, 0);
        return true;
    }
}
