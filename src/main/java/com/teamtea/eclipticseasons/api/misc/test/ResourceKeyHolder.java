package com.teamtea.eclipticseasons.api.misc.test;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public interface ResourceKeyHolder {

    ResourceKey<Level> getResourceKey();

    void setResourceKey(ResourceKey<Level> resourceKey);
}
