package com.teamtea.eclipticseasons.client.lod;

import net.minecraft.resources.ResourceLocation;

public record SeasonalModelKey(
        int originalBlockId,
        ResourceLocation modelIdentifier,
        boolean snowy
) {
}
