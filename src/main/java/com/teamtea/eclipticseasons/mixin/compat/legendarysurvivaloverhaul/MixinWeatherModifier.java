package com.teamtea.eclipticseasons.mixin.compat.legendarysurvivaloverhaul;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_ESUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
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
                    target = "Lsfiomn/legendarysurvivaloverhaul/api/temperature/ModifierBase;getWorldInfluence(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)F")
    )
    private float eclipticseasons$getWorldInfluence(ModifierBase instance, PlayerEntity player, World world, BlockPos pos, Operation<Float> original) {
        return LSO_ESUtil.eclipticseasons$EclipticSeasons.get().getWorldInfluence(player, world, pos);
    }

    @WrapOperation(
            remap = false,
            method = "getWorldInfluence",
            at = @At(value = "INVOKE",
                    target = "Lsfiomn/legendarysurvivaloverhaul/util/WorldUtil;isRainingOrSnowingAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private boolean eclipticseasons$getWorldInfluence_isRainingOrSnowingAt(World world, BlockPos pos, Operation<Boolean> original) {
        return WeatherManager.getRainOrSnow(world,world.getBiome(pos), pos)!= Biome.RainType.NONE;
    }

    @WrapOperation(
            method = "getWorldInfluence",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;isRaining()Z"
            )
    )
    private boolean eclipticseasons$getWorldInfluence_isRaining(World world, Operation<Boolean> original, @Local(argsOnly = true) BlockPos blockPos) {
        return world.isRainingAt(blockPos);
    }
}
