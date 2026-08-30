package com.teamtea.eclipticseasons.client.lod;

import net.minecraft.resources.Identifier;

public record SeasonalModelEntry(
        int originalBlockId,
        Identifier modelIdentifier,
        boolean snowy
) {
}
