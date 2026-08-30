package com.teamtea.eclipticseasons.client.lod;

import net.minecraft.resources.ResourceLocation;

public record SeasonalModelEntry(
        int originalBlockId,
        ResourceLocation modelIdentifier,
        boolean snowy
) {
}
