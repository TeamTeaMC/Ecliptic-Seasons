package com.teamtea.eclipticseasons.mixin.common.level;


import com.teamtea.eclipticseasons.api.misc.IBiomeWeatherProvider;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
    Holder<Biome> es$coreBiome;

    @Override
    public Holder<Biome> es$getCoreBiome() {
        return es$coreBiome;
    }

    @Override
    public void es$setCoreBiome(Holder<Biome> biomeHolder) {
        this.es$coreBiome = biomeHolder;
    }

}
