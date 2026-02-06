package me.cortex.voxy.client.core.model;

import me.cortex.voxy.client.core.model.bakery.ModelTextureBakery;
import me.cortex.voxy.common.world.other.Mapper;

public class ModelFactory {
    private final Mapper mapper = null;
    public final ModelTextureBakery bakery = null;

    public  boolean addEntry(int blockId) {
        var blockState = this.mapper.getBlockStateFromBlockId(blockId);
        int flags = this.bakery.renderToStream(blockState, 0, 0);
        return true;
    }
}
