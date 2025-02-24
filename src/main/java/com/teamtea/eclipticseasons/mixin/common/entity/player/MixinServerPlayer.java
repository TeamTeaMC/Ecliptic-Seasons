package com.teamtea.eclipticseasons.mixin.common.entity.player;


import com.mojang.authlib.GameProfile;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerPlayer.class})
public abstract class MixinServerPlayer extends Player {


    public MixinServerPlayer(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }

    @Inject(at = {@At("RETURN")}, method = {"tick"})
    private void eclipticseasons$init(CallbackInfo ci) {
        WeatherManager.tickPlayerSeasonEffecct((ServerPlayer)(Object)this);
    }


}
