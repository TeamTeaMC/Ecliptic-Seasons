package com.teamtea.eclipticseasons.mixin.compat.sodium;

import com.teamtea.eclipticseasons.client.color.season.FoliageColorSource;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBlenderColorProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({FoliageColorSource.class, FoliageColorSource.Impl.class})
public abstract class Mixin_FoliageColorSource implements SodiumBlenderColorProvider {
}
