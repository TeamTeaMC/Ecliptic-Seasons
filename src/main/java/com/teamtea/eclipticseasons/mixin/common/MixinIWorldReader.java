package com.teamtea.eclipticseasons.mixin.common;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nonnull;

@Mixin({IWorldReader.class})
public interface MixinIWorldReader extends IWorldReader {

    /**
     * @author jianzoushihu ( joe vettek)
     * @reason Ecliptic Seasons adjusts the weather system to be localized under Solar Weather conditions,
     * requiring brightness information to be corrected based on block positions.
     */
    @Overwrite
    @Override
    default int getMaxLocalRawBrightness(@Nonnull BlockPos pPos) {
        int amount = this.getSkyDarken();
        if (this instanceof World && EclipticUtil.hasLocalWeather((World) this)) {
            amount = WeatherManager.getSkyDarken((World) this, pPos, amount);
        }
        return this.getMaxLocalRawBrightness(pPos, amount);
    }

}
