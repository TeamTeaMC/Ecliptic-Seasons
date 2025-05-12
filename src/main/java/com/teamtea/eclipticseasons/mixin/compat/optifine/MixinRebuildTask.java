package com.teamtea.eclipticseasons.mixin.compat.optifine;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.model.MulBakeModel;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.compat.optfine.IOFModelTaker;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
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
            randomsource.setSeed(blockState.getSeed(pos));
            BakedModel model = ModelManager.findModel(blockAndTintGetter, pos, blockState, randomsource);
            if (model != null) {
                BakedModel oldModel = modelLocalRef.get();
                BakedModel newModel;
                boolean snowy = model instanceof SnowyBakedModelWrapper<?>;
                boolean special = model instanceof SnowyBakedModelWrapper<?> snowyBakedModelWrapper && snowyBakedModelWrapper.isLowLayer();
                BakedModel cacheModel = snowy ? null : iofModelTaker.eclipticseasons$hasCache(oldModel, special);
                if (cacheModel == null) {
                    RenderType renderType = ModelManager.getRenderType(blockState);
                    boolean replaceable = ModelManager.isModelReplaceable(blockState, blockAndTintGetter, pos, model);
                    // todo maybe we can cache it in future
                    newModel = new MulBakeModel<>(oldModel, model, replaceable, renderType);
                    if (snowy) {
                        iofModelTaker.eclipticseasons$setCache(oldModel, newModel, special);
                    }
                } else {
                    newModel = cacheModel;
                }
                modelLocalRef.set(newModel);
                iofModelTaker.eclipticseasons$setSnowModel(newModel);
            } else {
                iofModelTaker.eclipticseasons$setSnowModel(null);
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

}
