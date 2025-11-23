package com.teamtea.eclipticseasons.mixin.client.biome;


import com.teamtea.eclipticseasons.api.constant.solar.color.base.TemperateSolarTermColors;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BiomeColors.class})
public abstract class MixinBiomeColors {

    @Inject(at = {@At("RETURN")}, method = {"getAverageGrassColor"}, cancellable = true)
    private static void eclipticseasons$getAverageGrassColor(BlockAndTintGetter pLevel,
                                                             BlockPos pBlockPos,
                                                             CallbackInfoReturnable<Integer> cir) {
        if (pLevel != null && pBlockPos != null) {
            if (BiomeColorsHandler.shouldSetFallenLeaves(pLevel, pBlockPos)) {
                cir.setReturnValue(TemperateSolarTermColors.AUTUMNAL_EQUINOX.getGrassColor());
            }
        }
    }

}
