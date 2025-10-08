package com.teamtea.eclipticseasons.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.misc.BasicWeather;
import com.teamtea.eclipticseasons.client.sound.SeasonalBiomeAmbientSoundsHandler;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({LocalPlayer.class})
public abstract class MixinLocalPlayer implements BasicWeather {

    @Shadow
    @Final
    private List<AmbientSoundHandler> ambientSoundHandlers;

    @Inject(at = {@At("RETURN")}, method = {"<init>"})
    private void eclipticseasons$init(CallbackInfo ci, @Local(argsOnly = true) Minecraft minecraft,
                                      @Local(argsOnly = true) ClientLevel clientLevel) {
        if (ClientConfig.Sound.naturalSound.get() && MapChecker.isValidDimension(clientLevel))
            ambientSoundHandlers.add(new SeasonalBiomeAmbientSoundsHandler((LocalPlayer) (Object) this, minecraft.getSoundManager(), clientLevel.getBiomeManager()));
    }
}
