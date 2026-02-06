package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.teamtea.eclipticseasons.compat.voxy.IVoxyModelController;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.client.core.model.ModelFactory;
import me.cortex.voxy.client.core.model.bakery.ModelTextureBakery;
import me.cortex.voxy.common.world.other.Mapper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ModelFactory.class})
public abstract class MixinModelFactory {


    @WrapOperation(
            remap = false,
            method = "addEntry",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/world/other/Mapper;getBlockStateFromBlockId(I)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState eclipticseasons$addEntry_setBS(Mapper instance, int blockId, Operation<BlockState> original,
                                                      @Share("is_snowy_block") LocalBooleanRef localBooleanRef) {
        try {
            return original.call(instance, blockId);
        } catch (Exception e) {
            int maxBlockId = 0xFFFFF;
            localBooleanRef.set(true);
            return original.call(instance, VoxyTool.isVoxyTest() ?
                    maxBlockId - blockId : blockId);
        }
    }

    @WrapOperation(
            remap = false,
            method = "addEntry",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/client/core/model/bakery/ModelTextureBakery;renderToStream(Lnet/minecraft/world/level/block/state/BlockState;II)V")
    )
    private void eclipticseasons$addEntry_render(
            ModelTextureBakery instance,
            BlockState layer,
            int i, int mat,
            Operation<Void> original,
            @Share("is_snowy_block") LocalBooleanRef localBooleanRef) {
        if (VoxyTool.isVoxyTest() && instance instanceof IVoxyModelController modelController) {
            modelController.setSnowyBlock(localBooleanRef.get());
        }
        original.call(instance, layer, i, mat);
        if (VoxyTool.isVoxyTest() && instance instanceof IVoxyModelController modelController) {
            modelController.setSnowyBlock(false);
        }
    }
}
