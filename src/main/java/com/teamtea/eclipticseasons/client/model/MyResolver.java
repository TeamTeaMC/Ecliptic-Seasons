package com.teamtea.eclipticseasons.client.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

@Accessors(chain = true)
public class MyResolver implements ResolvableModel.Resolver {
    public static MyResolver INSTANCE = new MyResolver();
    // @Getter
    // private final Map<BlockState, Set<Identifier>> usedModel;
    @Getter
    private final Map<Identifier, Set<BlockState>> usedModel;
    @Setter
    private BlockState blockState;

    public MyResolver() {
        usedModel = new HashMap<>();
    }

    @Override
    public void markDependency(Identifier id) {
        Set<BlockState> identifiers = usedModel.computeIfAbsent(id, bs -> new HashSet<>());
        identifiers.add(blockState);
    }

    public void clear() {
        usedModel.clear();
    }
}
