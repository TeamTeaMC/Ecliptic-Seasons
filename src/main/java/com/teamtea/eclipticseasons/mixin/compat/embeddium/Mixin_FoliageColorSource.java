package com.teamtea.eclipticseasons.mixin.compat.embeddium;

import com.teamtea.eclipticseasons.client.color.season.FoliageColorSource;
import com.teamtea.eclipticseasons.compat.embeddium.EmbeddiumBlenderColorProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({FoliageColorSource.class, FoliageColorSource.Impl.class})
public abstract class Mixin_FoliageColorSource implements EmbeddiumBlenderColorProvider {
}
