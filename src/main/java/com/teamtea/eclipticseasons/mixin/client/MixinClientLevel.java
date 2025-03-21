package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinClientLevel {


    @Inject(at = {@At("HEAD")}, method = {"isRaining"}, cancellable = true)
    private void eclipticseasons$isRaining(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ClientLevel clientLevel) {
            if (EclipticUtil.hasLocalWeather(clientLevel))
                cir.setReturnValue(ClientWeatherChecker.isRain(clientLevel));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getRainLevel"}, cancellable = true)
    private void eclipticseasons$getRainLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientLevel clientLevel) {
            if (EclipticUtil.hasLocalWeather(clientLevel))
                cir.setReturnValue(ClientWeatherChecker.getRainLevel(clientLevel, p_46723_));
        }
    }

    // @Inject(at = {@At("HEAD")}, method = {"isRainingAt"}, cancellable = true)
    // private void eclipticseasons$isRainingAt(BlockPos p_46759_, CallbackInfoReturnable<Boolean> cir) {
    //     if ((Object) this instanceof ClientLevel clientLevel) {
    //         if (EclipticUtil.useSolarWeather())
    //             cir.setReturnValue(ClientWeatherChecker.isRainingAt(clientLevel, p_46759_));
    //     }
    // }

    @Inject(at = {@At("HEAD")}, method = {"isThundering"}, cancellable = true)
    private void eclipticseasons$isThundering(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ClientLevel clientLevel) {
            if (EclipticUtil.hasLocalWeather(clientLevel))
                cir.setReturnValue(ClientWeatherChecker.isThundering(clientLevel));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getThunderLevel"}, cancellable = true)
    private void eclipticseasons$getThunderLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientLevel clientLevel) {
            if (EclipticUtil.hasLocalWeather(clientLevel))
                cir.setReturnValue(ClientWeatherChecker.getThunderLevel(clientLevel, p_46723_));
        }
    }
}
