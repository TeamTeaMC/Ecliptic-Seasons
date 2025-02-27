package com.teamtea.eclipticseasons.mixin.common;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({LevelReader.class})
public interface MixinLevelReader extends LevelReader {

    @Override
    default int getMaxLocalRawBrightness(@NotNull BlockPos pPos) {
        int amount = this.getSkyDarken();
        if (EclipticUtil.useSolarWeather() && this instanceof Level world) {
            amount += WeatherManager.getRainOrSnow(world, world.getBiome(pPos).value(), pPos) != Biome.Precipitation.NONE ? 10 : 0;
            amount = Mth.clamp(amount, 0, 15);
        }
        return this.getMaxLocalRawBrightness(pPos, amount);
    }

}
