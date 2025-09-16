package com.teamtea.eclipticseasons.mixin.compat.optifine;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.client.core.ExtraRendererContext;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.model.MulBakeModel;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
import com.teamtea.eclipticseasons.compat.optfine.IOFModelTaker;
import com.teamtea.eclipticseasons.compat.vanilla.IExtendBlockView;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public abstract class MixinRebuildTask {

    @WrapOperation(
            method = "compile",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    // ordinal = 1,
                    remap = false,
                    target = "Lnet/minecraft/client/resources/model/BakedModel;getModelData(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraftforge/client/model/data/ModelData;)Lnet/minecraftforge/client/model/data/ModelData;")
    )
    private ModelData eclipticseasons$compile_replaceModel(
            BakedModel instance,
            BlockAndTintGetter blockAndTintGetter,
            BlockPos pos,
            BlockState blockState,
            ModelData modelData,
            Operation<ModelData> original,
            @Local RandomSource randomsource,
            @Local LocalRef<BakedModel> modelLocalRef
    ) {
        if (blockAndTintGetter instanceof IOFModelTaker iofModelTaker) {
            ExtraRendererContext rendererHolder = IExtraRendererContextOwner.of(blockAndTintGetter);
            boolean replaceable = false;
            long seed = blockState.getSeed(pos);
            randomsource.setSeed(seed);
            BakedModel model = ExtraModelManager.findModel(blockAndTintGetter, pos, blockState, randomsource, seed,
                    blockAndTintGetter instanceof IExtendBlockView view ? view.getModelCheckPos() : null);
            if (model != null) {
                BakedModel oldModel = modelLocalRef.get();
                BakedModel newModel;
                boolean snowy = model instanceof SnowyBakedModelWrapper<?>;
                boolean special = model instanceof SnowyBakedModelWrapper<?> snowyBakedModelWrapper && snowyBakedModelWrapper.isLowLayer();
                BakedModel cacheModel = snowy ? null : iofModelTaker.eclipticseasons$hasCache(oldModel, special);
                if (cacheModel == null) {
                    RenderType renderType = ExtraModelManager.getRenderType(blockState);
                    replaceable = ExtraModelManager.isModelReplaceable(blockState, blockAndTintGetter, pos, model);
                    // todo maybe we can cache it in future
                    newModel = new MulBakeModel<>(oldModel, model, replaceable, renderType);
                    if (snowy) {
                        iofModelTaker.eclipticseasons$setCache(oldModel, newModel, special);
                    }
                } else {
                    newModel = cacheModel;
                }
                // replace the model with ours
                modelLocalRef.set(newModel);

                rendererHolder.setReplace(replaceable)
                        .setModelData(modelData)
                        .setOriginalModel(oldModel)
                        .setExtraModel(newModel);
            } else {
                rendererHolder.resetAll();
            }
        }
        return original.call(instance, blockAndTintGetter, pos, blockState, modelData);
    }

    // @Inject(
    //         // remap = false,
    //         method = "compile",
    //         at = @At(value = "INVOKE",
    //                 // shift = At.Shift.AFTER,
    //                 // ordinal = 1,
    //                 // remap = false,
    //                 target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;")
    // )
    // private void eclipticseasons$compile_pre_clean(
    //         BlockState pState, BlockPos pPos, BlockAndTintGetter pLevel, PoseStack pPoseStack, VertexConsumer pConsumer, boolean pCheckSides, RandomSource pRandom, ModelData modelData, RenderType renderType, CallbackInfo ci
    // ) {
    //     if (pLevel instanceof IOFModelTaker iofModelTaker) {
    //         iofModelTaker.eclipticseasons$setSnowModel(null);
    //     }
    // }
    @ModifyExpressionValue(
            method = "compile",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    // ordinal = 1,
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;")
    )
    private RenderShape eclipticseasons$compile_replaceModel(
            RenderShape original,
            @Local RandomSource randomsource,
            @Local RenderChunkRegion blockAndTintGetter,
            @Local(ordinal = 2) BlockPos pos,
            @Local BlockState blockState,
            @Share("frozen_water") LocalBooleanRef ref
    ) {
        ref.set(false);
        if (!IceKeeper.notFrozen(blockAndTintGetter, pos, blockState, blockState.getFluidState())) {
            ref.set(true);
            return RenderShape.MODEL;
        }
        return original;
    }

    @WrapOperation(
            method = "compile",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    // ordinal = 1,
                    target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;")
    )
    private BakedModel eclipticseasons$compile_replaceModel(
            BlockRenderDispatcher instance,
            BlockState pState,
            Operation<BakedModel> original,
            @Local LocalRef<BlockState> blockState,
            @Share("frozen_water") LocalBooleanRef ref
    ) {
        if (ref.get()) {
            blockState.set(IceKeeper.getFakeState(pState, pState.getFluidState()));
            BakedModel iceModel = IceKeeper.getIceModel(pState, pState.getFluidState());
            if (iceModel != null) return iceModel;
        }
        return original.call(instance, pState);
    }
}
