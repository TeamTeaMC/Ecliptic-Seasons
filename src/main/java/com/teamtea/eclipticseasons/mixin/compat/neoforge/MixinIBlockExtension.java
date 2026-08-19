package com.teamtea.eclipticseasons.mixin.compat.neoforge;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IBlockExtension.class)
public interface MixinIBlockExtension {

    @ModifyReturnValue(
            method = "getMapColor",
            at = @At("RETURN")
    )
    private MapColor eclipticseasons$getColor(
            MapColor original,
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            MapColor defaultColor
    ) {
        if (CommonConfig.Map.changeMapColor.get()) {
            var ii = MapColorReplacer.getBlockIfSnowColorAndCareLoad(level, state, pos);
            if (ii != null)
                original = ii;
        }
        return original;
    }
}
