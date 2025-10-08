package com.teamtea.eclipticseasons.mixin.common.block;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.block.IceOrSnowCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LayeredCauldronBlock.class)
public class MixinLayeredCauldronBlock {

    @Shadow
    @Final
    public static int MAX_FILL_LEVEL;

    @ModifyExpressionValue(at = {@At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;")},
            method = {"handlePrecipitation"})
    public Comparable<?> eclipticseasons$handlePrecipitation(Comparable<?> original,
                                                             @Local(argsOnly = true) BlockState state,
                                                             @Local(argsOnly = true) Level level,
                                                             @Local(argsOnly = true) BlockPos pos,
                                                             @Local(argsOnly = true) Biome.Precipitation precipitation) {
        if (original.equals(MAX_FILL_LEVEL)) {
            IceOrSnowCauldronBlock.handleChange(state, level, pos, precipitation);
        }
        return original;
    }
}
