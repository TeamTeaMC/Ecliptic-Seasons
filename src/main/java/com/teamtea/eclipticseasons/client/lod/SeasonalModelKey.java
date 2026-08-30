package com.teamtea.eclipticseasons.client.lod;

import net.minecraft.resources.Identifier;

public record SeasonalModelKey(
        int originalBlockId,
        Identifier modelIdentifier,
        boolean snowy
) {
}
