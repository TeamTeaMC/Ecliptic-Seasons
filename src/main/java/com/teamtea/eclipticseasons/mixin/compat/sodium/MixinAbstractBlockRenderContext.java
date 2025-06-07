package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.model.ISnowyReplaceModel;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.compat.ctm.CTMSpriteChecker;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Supplier;

@Mixin({AbstractBlockRenderContext.class})
public abstract class MixinAbstractBlockRenderContext {
    @Shadow
    protected long randomSeed;
    @Shadow
    @Final
    protected Supplier<RandomSource> randomSupplier;
    @Shadow
    protected RandomSource random;

    @Shadow
    protected BlockAndTintGetter level;

    @Shadow
    protected BlockPos pos;

    @Shadow
    protected BlockState state;

    @ModifyExpressionValue(
            remap = false,
            method = "bufferDefaultModel",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelAccess;getQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraft/client/renderer/RenderType;Lnet/caffeinemc/mods/sodium/client/services/SodiumModelData;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$bufferDefaultModel_getQuads(
            List<BakedQuad> original,
            @Local(argsOnly = true) BakedModel bakedModel,
            @Local(argsOnly = true) BlockState state,
            @Local Direction side,
            @Local RandomSource rand) {
        if (this instanceof SodiumStatus sodiumStatus && sodiumStatus.getSnowModel() != null)
            return ModelManager.cancelTop(bakedModel, level, state, pos, side, rand, randomSeed, original, sodiumStatus.getCacheBakeQuad(), sodiumStatus.getSnowModel());
        return original;
    }

    @Inject(
            remap = false,
            method = "bufferDefaultModel",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/frapi/mesh/MutableQuadViewImpl;fromVanilla(Lnet/minecraft/client/renderer/block/model/BakedQuad;Lnet/fabricmc/fabric/api/renderer/v1/material/RenderMaterial;Lnet/minecraft/core/Direction;)Lnet/caffeinemc/mods/sodium/client/render/frapi/mesh/MutableQuadViewImpl;")
    )
    private void eclipticseasons$bufferDefaultModel_cache(
            BakedModel model, BlockState state, CallbackInfo ci,
            @Local(argsOnly = true) BakedModel bakedModel,
            @Local BakedQuad bakedQuad,
            @Local Direction side,
            @Local RandomSource rand) {
        if (this instanceof SodiumStatus sodiumStatus
                && sodiumStatus.getSnowModel() != null
                && !(ISnowyReplaceModel.isInvalid(bakedModel))
                && !sodiumStatus.shouldCollect()) {
            try {
                if (bakedQuad.getSprite() != null
                        && bakedQuad.getSprite() instanceof CTMSpriteChecker ctmSpriteChecker
                        && ctmSpriteChecker.isCTMSprite()) {
                    sodiumStatus.setShouldCollect(true);
                }
            } catch (Exception exception) {
                EclipticSeasons.logger(exception);
            }
        }
    }

    // @ModifyExpressionValue(
    //         remap = false,
    //         method = "renderQuad",
    //         at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/frapi/render/AbstractBlockRenderContext;transform(Lnet/fabricmc/fabric/api/renderer/v1/mesh/MutableQuadView;)Z")
    // )
    // private boolean eclipticseasons$renderQuad(boolean original,@Local(argsOnly = true) MutableQuadViewImpl quad){
    //     if (YuushyaChecker.isyuushyaBlock(state)) {
    //         EclipticSeasons.logger(original,ModelManager.getBakeQuadInfo(
    //                 quad.toBakedQuad(quad.sprite(SpriteFinderCache.forBlockAtlas()))));
    //     }
    //     return original;
    // }


    // @WrapOperation(
    //         remap = false,
    //         method = "renderModel",
    //         at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;isFaceVisible(Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;Lnet/minecraft/core/Direction;)Z")
    // )
    // private boolean mixin$renderModel_isFaceVisible(BlockRenderer blockRenderer, BlockRenderContext ctx, Direction face, Operation<Boolean> original) {
    //     return ModelManager.shouldisFaceVisible(blockRenderer,ctx,face,original);
    // }


}
