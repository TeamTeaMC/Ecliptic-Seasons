package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinClientLevel {


    @Inject(at = {@At("HEAD")}, method = {"isRaining"}, cancellable = true)
    private void eclipticseasons$Client_isRaining(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ClientLevel clientLevel) {
            if (EclipticUtil.hasLocalWeather(clientLevel)) {
                cir.setReturnValue(ClientWeatherChecker.isRain(clientLevel));
            }
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getRainLevel"}, cancellable = true)
    private void eclipticseasons$Client_getRainLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientLevel clientLevel) {
            if (EclipticUtil.hasLocalWeather(clientLevel)) {
                cir.setReturnValue(ClientWeatherChecker.getRainLevel(clientLevel, p_46723_));
            }
        }
    }

    @Inject(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")}, method = {"isRainingAt"}, cancellable = true)
    private void eclipticseasons$client$isRainingAt_endBiomeCheck(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ClientLevel level) {
            if (EclipticUtil.hasLocalWeather(level)) {
                Biome biome = MapChecker.getSurfaceBiome(level, pos).value();
                Biome.Precipitation precipitation = EclipticUtil.getRainOrSnow(level,biome,pos);
                cir.setReturnValue(precipitation == Biome.Precipitation.RAIN);
            }
        }
    }

    // @Inject(at = {@At("HEAD")}, method = {"isRainingAt"}, cancellable = true)
    // private void eclipticseasons$Client_isRainingAt(BlockPos p_46759_, CallbackInfoReturnable<Boolean> cir) {
    //     if (EclipticUtil.useSolarWeather())
    //         if ((Object) this instanceof ClientLevel clientLevel) {
    //             cir.setReturnValue(ClientWeatherChecker.isRainingAt(clientLevel, p_46759_));
    //         }
    // }

    @Inject(at = {@At("HEAD")}, method = {"isThundering"}, cancellable = true)
    private void eclipticseasons$isThundering(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ClientLevel clientLevel) {
            if (EclipticUtil.hasLocalWeather(clientLevel)) {
                cir.setReturnValue(ClientWeatherChecker.isThundering(clientLevel));
            }
        }
    }


    @Inject(at = {@At("HEAD")}, method = {"getThunderLevel"}, cancellable = true)
    private void eclipticseasons$Client_getThunderLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientLevel clientLevel) {
            if (EclipticUtil.hasLocalWeather(clientLevel)) {
                cir.setReturnValue(ClientWeatherChecker.getThunderLevel(clientLevel, p_46723_));
            }
        }
    }
}
