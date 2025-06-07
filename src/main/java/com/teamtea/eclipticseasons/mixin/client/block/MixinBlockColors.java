package com.teamtea.eclipticseasons.mixin.client.block;

import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockColors.class})
public class MixinBlockColors {

    @Inject(at = {@At(value = "HEAD")},
            method = {"lambda$createDefault$3"}, cancellable = true)
    private static void eclipticseasons$lambda$createDefault$3_SPRUCE_LEAVES(BlockState p_92636_, BlockAndTintGetter p_92637_, BlockPos p_92638_, int p_92639_, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(BiomeColorsHandler.getSpruceColor(p_92636_,p_92637_,p_92638_,p_92639_));
    }

    @Inject(at = {@At(value = "HEAD")},
            method = {"lambda$createDefault$4"}, cancellable = true)
    private static void eclipticseasons$lambda$createDefault$4_BIRCH_LEAVES(BlockState p_92636_, BlockAndTintGetter p_92637_, BlockPos p_92638_, int p_92639_, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(BiomeColorsHandler.getBirchColor(p_92636_,p_92637_,p_92638_,p_92639_));
    }

}
