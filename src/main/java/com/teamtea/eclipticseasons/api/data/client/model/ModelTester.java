package com.teamtea.eclipticseasons.api.data.client.model;

import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.util.List;
import java.util.function.Predicate;

public record ModelTester(
        StandaloneModelKey<BlockStateModel> modelIdentifier,
        boolean replace,
        List<SnowDefinition.PropertyTester> testers
) implements Predicate<BlockState> {
    @Override
    public boolean test(BlockState blockState) {
        for (SnowDefinition.PropertyTester c : testers) {
            if (!c.matches(blockState)) {
                return false;
            }
        }
        return true;
    }
}
