package me.cortex.voxy.client.core.model;

import me.cortex.voxy.common.world.other.Mapper;

public class ModelBakerySubsystem {
    private final Mapper mapper = null;

    public void requestBlockBake(int blockId) {
        if (this.mapper.getBlockStateCount() <= blockId) {
            return;
        }
    }
}
