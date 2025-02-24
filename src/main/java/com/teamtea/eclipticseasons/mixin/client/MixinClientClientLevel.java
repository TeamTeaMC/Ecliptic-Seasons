package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.particle.ParticleUtil;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class MixinClientClientLevel {


    @Inject(at = {@At("RETURN")}, method = {"animateTick"})
    private void eclipticseasons$animateTick(int x, int y, int z, CallbackInfo ci) {
        ParticleUtil.createParticle((ClientWorld)(Object)this,x,y,z);

    }
}
