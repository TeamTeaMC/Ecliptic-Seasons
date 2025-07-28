package com.teamtea.eclipticseasons.mixin.compat.cold_sweat;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.momosoftworks.coldsweat.util.world.WorldHelper;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({WorldHelper.class})
public class MixinWorldHelper {

    @WrapOperation(
            method = {"isRainingAt"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isRaining()Z")
    )
    private static boolean eclipticseasons$isRainingAt_isRaining(Level instance,
                                                                 Operation<Boolean> original,
                                                                 @Local(argsOnly = true) BlockPos pos) {
        return EclipticSeasonsApi.getInstance().isRainingOrSnowing(instance, pos);
    }


    @WrapOperation(
            method = {"isRainingAt"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private static Biome.Precipitation eclipticseasons$isRainingAt_getPrecipitationAt(Biome instance, BlockPos pos, Operation<Biome.Precipitation> original, @Local(argsOnly = true) Level level) {
        return EclipticSeasonsApi.getInstance().getPrecipitationAt(level, pos);
    }

}
