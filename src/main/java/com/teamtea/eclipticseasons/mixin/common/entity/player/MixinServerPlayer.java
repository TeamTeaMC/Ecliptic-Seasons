package com.teamtea.eclipticseasons.mixin.common.entity.player;


import com.mojang.authlib.GameProfile;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerPlayerEntity.class})
public abstract class MixinServerPlayer extends PlayerEntity {


    public MixinServerPlayer(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
        super(p_i241920_1_, p_i241920_2_, p_i241920_3_, p_i241920_4_);
    }

    @Inject(at = {@At("RETURN")}, method = {"tick"})
    private void eclipticseasons$init(CallbackInfo ci) {
        WeatherManager.tickPlayerSeasonEffecct((ServerPlayerEntity)(Object)this);
    }


}
