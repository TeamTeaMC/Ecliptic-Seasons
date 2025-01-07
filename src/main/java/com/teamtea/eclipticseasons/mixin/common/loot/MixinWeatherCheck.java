package com.teamtea.eclipticseasons.mixin.common.loot;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.WeatherCheck;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(WeatherCheck.class)
public class MixinWeatherCheck {
    @Shadow
    @Final
    @Nullable
    Boolean isRaining;

    @Shadow
    @Final
    @Nullable
    Boolean isThundering;

    //     TODO:检查一下谁用过这个
    @Inject(at = {@At("HEAD")}, method = {"test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z"}, cancellable = true)
    private void ecliptic$Client_isRaining(LootContext pContext, CallbackInfoReturnable<Boolean> cir) {
        if (EclipticUtil.useSolarWeather()) {
            WeatherManager.WeatherCheck weatherCheck = new WeatherManager.WeatherCheck(
                    Optional.ofNullable(isRaining), Optional.ofNullable(isThundering));
            cir.setReturnValue(WeatherManager.testWeatherCheck(pContext, weatherCheck));
        }
    }
}
