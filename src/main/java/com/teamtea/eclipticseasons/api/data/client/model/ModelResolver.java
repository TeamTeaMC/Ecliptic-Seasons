package com.teamtea.eclipticseasons.api.data.client.model;

import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;
import java.util.List;

public record ModelResolver(List<ModelTester> modelTesters) {

    public @Nullable ModelTester tryFind(BlockState blockState) {
        for (ModelTester modelTester : modelTesters) {
            if (modelTester.test(blockState)) {
                return modelTester;
            }
        }
        return null;
    }
}
