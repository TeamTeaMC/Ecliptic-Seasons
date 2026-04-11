package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.voxy.helper.IVoxyModelController;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.client.core.model.ModelFactory;
import me.cortex.voxy.client.core.model.bakery.SoftwareModelTextureBakery;
import me.cortex.voxy.common.world.other.Mapper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin({ModelFactory.class})
public abstract class MixinModelFactory {


    @Shadow(remap = false)
    @Final
    public SoftwareModelTextureBakery bakery2;

    @Shadow(remap = false)
    @Final
    private Mapper mapper;

    @WrapOperation(
            remap = false,
            method = "addEntry",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/world/other/Mapper;getBlockStateFromBlockId(I)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState eclipticseasons$addEntry_setBS(Mapper instance, int blockId, Operation<BlockState> original) {
        return original.call(instance, VoxyTool.fixId(instance, blockId, (i) -> {
            // if (bakery2 instanceof IVoxyModelController modelController) {
            //     modelController.setSnowyBlock(true);
            // }
        }));
    }

    @Inject(
            remap = false,
            method = "addEntry",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$addEntry_clean(
            int blockId, CallbackInfoReturnable<Boolean> cir) {
        // if (bakery2 instanceof IVoxyModelController modelController) {
        //     modelController.setSnowyBlock(false);
        // }
    }

    @Inject(
            remap = false,
            method = "processModelResult",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$processModelResult_return(CallbackInfoReturnable<Boolean> cir) {
        if (bakery2 instanceof IVoxyModelController modelController) {
            modelController.setSnowyBlock(false);
        }
    }

    @ModifyExpressionValue(
            remap = false,
            method = "processModelResult",
            at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ConcurrentLinkedDeque;poll()Ljava/lang/Object;")
    )
    private <E> E eclipticseasons$processModelResult_setBS(E original, @Share("isSnowyBlock") LocalBooleanRef ref) {
        if (original != null) {
            try {
                Method m = original.getClass().getDeclaredMethod("blockId");
                m.setAccessible(true);
                int blockId = (int) m.invoke(original);
                VoxyTool.fixId(mapper, blockId, (i) -> {
                    if (bakery2 instanceof IVoxyModelController modelController) {
                        modelController.setSnowyBlock(true);
                    }
                });
            } catch (Exception e) {
                EclipticSeasons.logger(e);
            }
        }

        return original;
    }
}
