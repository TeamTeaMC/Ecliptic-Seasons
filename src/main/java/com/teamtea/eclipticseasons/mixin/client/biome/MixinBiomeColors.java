package com.teamtea.eclipticseasons.mixin.client.biome;


import com.teamtea.eclipticseasons.api.constant.solar.color.base.TemperateSolarTermColors;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.common.mixin.injector.DirectInject;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BiomeColors.class})
public abstract class MixinBiomeColors {

    @DirectInject(at = {@At("RETURN")}, method = {"getAverageGrassColor"},
            mode = DirectInject.Mode.RETURN_IF_TRUE,
            returnHandler = "eclipticseasons$getAverageGrassColor")
    private static boolean eclipticseasons$shouldReplaceGrassColor(BlockAndTintGetter level,
                                                               BlockPos pos) {
        return level != null && pos != null && BiomeColorsHandler.shouldSetFallenLeaves(level, pos);
    }

    @Unique
    private static int eclipticseasons$getAverageGrassColor(
            BlockAndTintGetter level,
            BlockPos pos
    ) {
        return TemperateSolarTermColors.AUTUMNAL_EQUINOX.getGrassColor();
    }
}
