package com.teamtea.eclipticseasons.mixin.common.loot;


import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.WeatherCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WeatherCheck.class)
public class MixinWeatherCheck {
    //     TODO:检查一下谁用过这个
    @Inject(at = {@At("HEAD")}, method = {"test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z"}, cancellable = true)
    private void ecliptic$Client_isRaining(LootContext pContext,CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfig.Weather.useSolarWeather.get()) {
            cir.setReturnValue(WeatherManager.testWeatherCheck(pContext,(WeatherCheck)(Object)this));
        }
    }

}
