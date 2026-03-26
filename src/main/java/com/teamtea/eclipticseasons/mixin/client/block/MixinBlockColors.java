package com.teamtea.eclipticseasons.mixin.client.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.client.color.season.FoliageColorSource;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({BlockColors.class})
public class MixinBlockColors {

    @WrapOperation(at = {@At(value = "INVOKE",
            ordinal = 0,
            target = "Lnet/minecraft/client/color/block/BlockTintSources;constant(I)Lnet/minecraft/client/color/block/BlockTintSource;")},
            method = {"createDefault"})
    private static BlockTintSource eclipticseasons$lambda$createDefault$3_SPRUCE_LEAVES(int color, Operation<BlockTintSource> original) {
        return new FoliageColorSource();
    }
    @WrapOperation(at = {@At(value = "INVOKE",
            ordinal = 1,
            target = "Lnet/minecraft/client/color/block/BlockTintSources;constant(I)Lnet/minecraft/client/color/block/BlockTintSource;")},
            method = {"createDefault"})
    private static BlockTintSource eclipticseasons$lambda$createDefault$4_BIRCH_LEAVES(int color, Operation<BlockTintSource> original) {
        return new FoliageColorSource();
    }

}
