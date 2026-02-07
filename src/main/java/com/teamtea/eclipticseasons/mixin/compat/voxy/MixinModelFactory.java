package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.compat.voxy.IVoxyModelController;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.client.core.model.ModelFactory;
import me.cortex.voxy.client.core.model.bakery.ModelTextureBakery;
import me.cortex.voxy.common.world.other.Mapper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ModelFactory.class})
public abstract class MixinModelFactory {


    @Shadow(remap = false)
    @Final
    public ModelTextureBakery bakery;

    @WrapOperation(
            remap = false,
            method = "addEntry",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/world/other/Mapper;getBlockStateFromBlockId(I)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState eclipticseasons$addEntry_setBS(Mapper instance, int blockId, Operation<BlockState> original) {

        return original.call(instance, VoxyTool.fixId(instance, blockId, (i) -> {
            if (bakery instanceof IVoxyModelController modelController) {
                modelController.setSnowyBlock(true);
            }
        }));
    }

    @Inject(
            remap = false,
            method = "addEntry",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$addEntry_clean(
            int blockId, CallbackInfoReturnable<Boolean> cir) {
        if (bakery instanceof IVoxyModelController modelController) {
            modelController.setSnowyBlock(false);
        }
    }
}
