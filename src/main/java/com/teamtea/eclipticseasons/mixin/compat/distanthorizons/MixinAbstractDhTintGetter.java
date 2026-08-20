package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.seibel.distanthorizons.common.wrappers.block.AbstractDhTintGetter_neoforge;
import com.seibel.distanthorizons.core.dataObjects.BlockBiomeWrapperPair;
import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ConcurrentHashMap;

@ConditionalMixin(value = "distanthorizons", version = "3.0.0-b")
@Mixin(AbstractDhTintGetter_neoforge.class)
public interface MixinAbstractDhTintGetter {

    @Accessor(value = "COLOR_BY_BLOCK_BIOME_PAIR", remap = false)
    static ConcurrentHashMap<BlockBiomeWrapperPair, Integer> getBiomeColorCache() {
        return new ConcurrentHashMap<>();
    }

}
