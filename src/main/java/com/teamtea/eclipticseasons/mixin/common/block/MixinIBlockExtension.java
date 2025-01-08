package com.teamtea.eclipticseasons.mixin.common.block;


import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinIBlockExtension {


    @Shadow protected abstract BlockState asState();

    @Inject(at = {@At("HEAD")},
            method = {"getMapColor"},
            cancellable = true)
    public void ecliptic$getColor(BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<MapColor> cir) {
        var ii = MapColorReplacer.getTopSnowColor(pLevel, asState(), pPos);
        if (ii != null)
            cir.setReturnValue(ii);
    }
}
