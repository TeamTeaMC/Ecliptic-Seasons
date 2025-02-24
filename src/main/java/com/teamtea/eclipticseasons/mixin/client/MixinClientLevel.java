package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinClientLevel {


    @Inject(at = {@At("HEAD")}, method = {"isRaining"}, cancellable = true)
    private void eclipticseasons$isRaining(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ClientWorld ) {
            cir.setReturnValue(ClientWeatherChecker.isRain((ClientWorld) (Object) this));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getRainLevel"}, cancellable = true)
    private void eclipticseasons$getRainLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientWorld ) {
            cir.setReturnValue(ClientWeatherChecker.getRainLevel((ClientWorld) (Object) this, p_46723_));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"isRainingAt"}, cancellable = true)
    private void eclipticseasons$isRainingAt(BlockPos p_46759_, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ClientWorld ) {
            cir.setReturnValue(ClientWeatherChecker.isRainingAt((ClientWorld) (Object) this, p_46759_));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"isThundering"}, cancellable = true)
    private void eclipticseasons$isThundering(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ClientWorld ) {
            cir.setReturnValue(ClientWeatherChecker.isThundering((ClientWorld) (Object) this));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getThunderLevel"}, cancellable = true)
    private void eclipticseasons$getThunderLevel(float p_46723_, CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof ClientWorld ) {
            cir.setReturnValue(ClientWeatherChecker.getThunderLevel( (ClientWorld) (Object) this,p_46723_));
        }
    }
}
