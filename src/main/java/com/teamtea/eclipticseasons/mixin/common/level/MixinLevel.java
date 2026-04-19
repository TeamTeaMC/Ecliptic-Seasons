package com.teamtea.eclipticseasons.mixin.common.level;


import com.teamtea.eclipticseasons.api.misc.IBiomeWeatherProvider;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(Level.class)
public class MixinLevel implements IBiomeWeatherProvider {

    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")}, method = {"isRainingAt"}, cancellable = true)
    private void eclipticseasons$isRainingAt_endBiomeCheck(BlockPos p_46759_, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Level level) {
            cir.setReturnValue(WeatherManager.isRainingUnderSky(level, p_46759_));
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
}
