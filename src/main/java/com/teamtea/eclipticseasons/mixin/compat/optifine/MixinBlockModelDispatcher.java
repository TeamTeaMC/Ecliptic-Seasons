package com.teamtea.eclipticseasons.mixin.compat.optifine;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockRenderDispatcher.class)
public class MixinBlockModelDispatcher {

    @WrapOperation(
            method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    // ordinal = 1,
                    // remap = false,
                    target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;")
    )
    private BakedModel eclipticseasons$renderBatched_replaceModel(
            BlockRenderDispatcher instance,
            BlockState pState,
            Operation<BakedModel> original,
            @Local(argsOnly = true) BlockAndTintGetter blockAndTintGetter
    ) {
        ExtraRendererContext rendererHolder = IExtraRendererContextOwner.of(blockAndTintGetter);
        BakedModel bakedModel = rendererHolder.getExtraModel();
        if (bakedModel != null) return bakedModel;
        return original.call(instance, pState);
    }

    // @Inject(
    //         remap = false,
    //         method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
    //         at = @At(value = "RETURN"
    //                 // shift = At.Shift.AFTER,
    //                 // ordinal = 1,
    //                 // remap = false,
    //         )
    // )
    // private void eclipticseasons$renderBatched_end_clean(
    //         BlockState pState, BlockPos pPos, BlockAndTintGetter pLevel, PoseStack pPoseStack, VertexConsumer pConsumer, boolean pCheckSides, RandomSource pRandom, ModelData modelData, RenderType renderType, CallbackInfo ci
    // ) {
    //     if (pLevel instanceof IOFModelTaker iofModelTaker) {
    //         iofModelTaker.eclipticseasons$setSnowModel(null);
    //     }
    // }


}
