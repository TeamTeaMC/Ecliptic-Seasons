package com.teamtea.eclipticseasons.mixin.client.sound;

import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.sound.SeasonalBiomeAmbientSoundsHandler;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MusicManager.class})
public class MixinMusicManager {
    @Shadow
    private int nextSongDelay;

    @Inject(at = {@At("RETURN")}, method = {"tick"})
    private void eclipticseasons$init(CallbackInfo ci) {
        nextSongDelay = nextSongDelay;
    }
}
