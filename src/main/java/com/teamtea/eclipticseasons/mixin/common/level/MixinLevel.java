package com.teamtea.eclipticseasons.mixin.common.level;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.weather.WeatherDimension;
import com.teamtea.eclipticseasons.api.misc.IBiomeWeatherProvider;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(Level.class)
public class MixinLevel implements IBiomeWeatherProvider {

    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")}, method = {"precipitationAt"}, cancellable = true)
    private void eclipticseasons$precipitationAt_endBiomeCheck(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if ((Object) this instanceof Level level) {
            cir.setReturnValue(WeatherManager.getRainOrSnow(level, MapChecker.getSurfaceBiome(level, pos).value(), pos));
        }
    }


    @Unique
    private ArrayList<WeatherManager.BiomeWeather> eclipticseasons$biomeWeathers;

    @Override
    public ArrayList<WeatherManager.BiomeWeather> es$get() {
        return this.eclipticseasons$biomeWeathers;
    }

    @Override
    public void es$set(ArrayList<WeatherManager.BiomeWeather> biomeWeathers) {
        this.eclipticseasons$biomeWeathers = biomeWeathers;
    }


    @Unique
    private int eclipticseasons$getTickSpeed = -1;

    @Override
    public int es$getWeatherTickFactor() {
        if (eclipticseasons$getTickSpeed < 1) {
            ArrayList<WeatherManager.BiomeWeather> biomeList = eclipticseasons$biomeWeathers;
            int size = biomeList == null ? 64 : biomeList.size();
            size = (int) (size * (Mth.clamp(7f / EclipticSeasonsApi.getInstance().getLastingDaysOfEachTerm((Level) (Object) this), 0.8f, 3f)));
            eclipticseasons$getTickSpeed = Math.max(1, size);
        }
        return eclipticseasons$getTickSpeed;
    }

    @Unique
    private int eclipticseasons$biomePos = -1;

    @Override
    public int es$getTickBiome() {
        if (eclipticseasons$biomeWeathers == null || eclipticseasons$biomeWeathers.isEmpty()) {
            return 0;
        }

        int size = eclipticseasons$biomeWeathers.size();
        int pos = eclipticseasons$biomePos + 1;
        if (pos >= size || pos < 0) {
            pos = 0;
        }
        eclipticseasons$biomePos = pos;
        return pos;
    }

    @Unique
    Holder<Biome> es$coreBiome;

    @Override
    public Holder<Biome> es$getCoreBiome() {
        if (es$coreBiome == null) {
            Level level = (Level) (Object) this;
            for (WeatherDimension weatherDimension : ESSortInfo.sorted2(level.registryAccess().lookupOrThrow(ESRegistries.WEATHER_DIMENSION))) {
                if (weatherDimension.dimension().equals(level.dimension())) {
                    es$coreBiome = weatherDimension.core();
                    break;
                }
            }
        }
        return es$coreBiome;
    }

    @Override
    public void es$reset() {
        es$coreBiome = null;
        eclipticseasons$biomePos = 0;
        eclipticseasons$getTickSpeed = -1;
    }
}
