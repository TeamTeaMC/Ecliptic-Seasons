package com.teamtea.eclipticseasons.api.data.client.model;

import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Predicate;

public record ModelTester(
        ResourceLocation modelResourceLocation,
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
