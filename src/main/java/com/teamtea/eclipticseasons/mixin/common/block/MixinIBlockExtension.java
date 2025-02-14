package com.teamtea.eclipticseasons.mixin.common.block;


import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
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


    // TODO:允许关闭，这个要在服务器端比较麻烦
    @Inject(at = {@At("HEAD")},
            method = {"getMapColor"},
            cancellable = true)
    public default void ecliptic$getColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor, CallbackInfoReturnable<MapColor> cir) {
        var ii = MapColorReplacer.getTopSnowColor(level, state, pos);
        if (ii != null)
            cir.setReturnValue(ii);
    }
}
