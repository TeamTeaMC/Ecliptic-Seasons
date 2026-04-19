package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.seibel.distanthorizons.common.wrappers.block.AbstractDhTintGetter;
import com.seibel.distanthorizons.core.dataObjects.BlockBiomeWrapperPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ConcurrentHashMap;

@Mixin(AbstractDhTintGetter.class)
public interface MixinAbstractDhTintGetter {

    @Accessor(value = "COLOR_BY_BLOCK_BIOME_PAIR", remap = false)
    static ConcurrentHashMap<BlockBiomeWrapperPair, Integer> getBiomeColorCache() {
        return new ConcurrentHashMap<>();
    }

}
