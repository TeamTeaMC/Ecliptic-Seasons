package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.client.particle.ParticleUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class MixinClientClientLevel {


    @Shadow
    public abstract void addParticle(ParticleOptions p_104706_, double p_104707_, double p_104708_, double p_104709_, double p_104710_, double p_104711_, double p_104712_);


    @Shadow
    public abstract void addDestroyBlockEffect(BlockPos p_171667_, BlockState p_171668_);

    @Inject(at = {@At("RETURN")}, method = {"animateTick"})
    private void eclipticseasons$animateTick(int x, int y, int z, CallbackInfo ci) {
        ParticleUtil.createParticle((ClientLevel)(Object)this,x,y,z);

    }
}
