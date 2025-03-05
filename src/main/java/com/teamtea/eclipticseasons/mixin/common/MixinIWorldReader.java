package com.teamtea.eclipticseasons.mixin.common;


import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

@Mixin({IWorldReader.class})
public interface MixinIWorldReader extends IWorldReader {

    @Override
    default int getMaxLocalRawBrightness(@Nonnull BlockPos pPos) {
        int amount =this.getSkyDarken();
        if (this instanceof World ) {
            amount += WeatherManager.getRainOrSnow((World)this, this.getBiome(pPos), pPos) != Biome.RainType.NONE ? 10 : 0;
            amount = MathHelper.clamp(amount, 0, 15);
        }
        return this.getMaxLocalRawBrightness(pPos, amount);
    }


}
