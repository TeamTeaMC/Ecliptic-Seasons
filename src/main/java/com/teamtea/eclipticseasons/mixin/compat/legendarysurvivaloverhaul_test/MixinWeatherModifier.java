package com.teamtea.eclipticseasons.mixin.compat.legendarysurvivaloverhaul_test;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_ESUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sfiomn.legendarysurvivaloverhaul.api.temperature.ModifierBase;
import sfiomn.legendarysurvivaloverhaul.common.temperature.WeatherModifier;

@Mixin({WeatherModifier.class})
public abstract class MixinWeatherModifier {

    @WrapOperation(
            remap = false,
            method = "getWorldInfluence",
            at = @At(value = "INVOKE",
                    ordinal = 1,
                    target = "Lsfiomn/legendarysurvivaloverhaul/api/temperature/ModifierBase;getWorldInfluence(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)F")
    )
    private float eclipticseasons$getWorldInfluence(ModifierBase instance, Player player, Level world, BlockPos pos, Operation<Float> original) {
        return LSO_ESUtil.eclipticseasons$EclipticSeasons.get().getWorldInfluence(player, world, pos);
    }

    @WrapOperation(
            remap = false,
            method = "getWorldInfluence",
            at = @At(value = "INVOKE",
                    target = "Lsfiomn/legendarysurvivaloverhaul/util/WorldUtil;isRainingOrSnowingAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$getWorldInfluence_isRainingOrSnowingAt(Level world, BlockPos pos, Operation<Boolean> original) {
        return EclipticSeasonsApi.getInstance().isRainOrSnowAt(world, pos);
    }

    @WrapOperation(
            method = "getWorldInfluence",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;isRaining()Z"
            )
    )
    private boolean eclipticseasons$getWorldInfluence_isRaining(Level world, Operation<Boolean> original, @Local(argsOnly = true) BlockPos blockPos) {
        return world.isRainingAt(blockPos);
    }
}
