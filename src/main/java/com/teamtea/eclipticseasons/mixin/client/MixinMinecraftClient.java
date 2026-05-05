package com.teamtea.eclipticseasons.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Window;
import com.teamtea.eclipticseasons.client.render.FogRenderer;
import com.teamtea.eclipticseasons.client.sound.SeasonalBackgroundMusicSelectManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.biome.Biome;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Shadow
    @Final
    private Window window;

    @Shadow
    @Nullable
    public LocalPlayer player;

    /*
    * 因为没有 resize 事件
    * 所以先 mixin 手动调用一下 FogRenderer 的 resize
    * */
    @Inject(
            method = "resizeDisplay",
            at = @At("RETURN")
    )
    private void eclipticseasons$resize(CallbackInfo ci) {
        FogRenderer.INSTANCE.resize(window.getWidth(), window.getHeight());
    }

    @ModifyExpressionValue(
            method = "getSituationalMusic",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/sounds/Musics;CREATIVE:Lnet/minecraft/sounds/Music;",
                    opcode = Opcodes.GETSTATIC)
    )
    private Music eclipticseasons$getSituationalMusic_replaceCreativeMusic(Music original, @Local Holder<Biome> biomeHolder) {
        Music music = SeasonalBackgroundMusicSelectManager.getMusic(original, player.blockPosition(), biomeHolder, true, false);
        if (music != original) {
            original = music;
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "getSituationalMusic",
            at = {@At(value = "INVOKE", target = "Ljava/util/Optional;orElse(Ljava/lang/Object;)Ljava/lang/Object;")})
    private <T> T eclipticseasons$getSituationalMusic_survive(T returnValue, @Local Holder<Biome> biomeHolder) {
        if (returnValue instanceof Music original) {
            Music music = SeasonalBackgroundMusicSelectManager.getMusic(original, player.blockPosition(), biomeHolder, false, false);
            if (music != returnValue) {
                returnValue = (T) music;
            }
        }
        return returnValue;
    }

    @Inject(
            method = "getSituationalMusic",
            at = {@At(value = "RETURN", ordinal = 3)},
            cancellable = true)
    private void eclipticseasons$getSituationalMusic_water(CallbackInfoReturnable<Music> cir,
                                                           @Local Holder<Biome> biomeHolder) {
        Music returnValue = cir.getReturnValue();
        Music music = SeasonalBackgroundMusicSelectManager.getMusic(returnValue, player.blockPosition(), biomeHolder, false, true);
        if (music != returnValue) cir.setReturnValue(music);
    }
}
