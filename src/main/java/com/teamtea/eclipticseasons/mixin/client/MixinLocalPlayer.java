package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.sound.SeasonalBiomeAmbientSoundsHandler;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.IAmbientSoundHandler;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({ClientPlayerEntity.class})
public abstract class MixinLocalPlayer {

    @Shadow
    @Final
    private List<IAmbientSoundHandler> ambientSoundHandlers;

    @Inject(at = {@At("RETURN")}, method = {"<init>"})
    private void eclipticseasons$init(CallbackInfo ci, @Local Minecraft minecraft, @Local ClientWorld clientLevel) {
        if (ClientConfig.Sound.sound.get() && clientLevel.dimensionType().natural())
            ambientSoundHandlers.add(new SeasonalBiomeAmbientSoundsHandler((ClientPlayerEntity) (Object) this, minecraft.getSoundManager(), clientLevel.getBiomeManager()));
    }
}
