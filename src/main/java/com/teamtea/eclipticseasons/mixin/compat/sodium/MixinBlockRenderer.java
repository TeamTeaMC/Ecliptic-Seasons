package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.google.common.annotations.Beta;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.api.misc.client.IExtraRendererContextOwner;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.core.ExtraRenderDispatcher;
import com.teamtea.eclipticseasons.client.core.context.ExtraRendererContext;
import com.teamtea.eclipticseasons.api.misc.client.ISpriteChecker;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.DefaultTerrainRenderPasses;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.texture.SpriteFinderCache;
import net.caffeinemc.mods.sodium.client.services.PlatformModelEmitter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Beta
@Mixin({BlockRenderer.class})
public abstract class MixinBlockRenderer extends AbstractBlockRenderContext implements SodiumStatus {

    // @Shadow
    // private boolean allowDowngrade;
    @Unique
    public SodiumBoard eclipticseasons$chunkBuilderMeshingTask;

    @Unique
    private List<BakedQuad> eclipticseasons$bakedQuads = new ArrayList<>();

    // @Unique
    // private BlockStateModel eclipticseasons$snowModel = null;

    @Unique
    private boolean eclipticseasons$shouldCollectBakeQuads = false;

    @Unique
    private boolean eclipticseasons$shouldReplaceOriginalGrassModel = false;

    @Unique
    private BlockPos.MutableBlockPos eclipticseasons$mutableBlockPos = new BlockPos.MutableBlockPos();

    // @Unique
    // private ESSodiumContext eclipticseasons$esContext = null;

    @Unique
    private boolean eclipticseasons$cancelDowngradedPass = false;


    @WrapOperation(
            remap = false,
            method = "renderModel",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,

                    target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelEmitter;emitModel(Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;Ljava/util/function/Predicate;Lnet/caffeinemc/mods/sodium/client/render/model/MutableQuadViewImpl;Lnet/minecraft/util/RandomSource;Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/caffeinemc/mods/sodium/client/services/PlatformModelEmitter$Bufferer;)V")
    )
    private void eclipticseasons$renderModel_wrap_emitBlockQuads(
            PlatformModelEmitter instance,
            BlockStateModel blockStateModel,
            Predicate<Direction> directionPredicate,
            MutableQuadViewImpl mutableQuadView,
            RandomSource randomSource,
            BlockAndTintGetter blockAndTintGetter,
            BlockPos blockPos, BlockState blockState,
            PlatformModelEmitter.Bufferer bufferer,
            Operation<Void> original
    ) {

        if (IExtraRendererContextOwner.of(slice).shouldApply())
            eclipticseasons$cancelDowngradedPass = true;

        // if (!eclipticseasons$shouldReplaceOriginalGrassModel || eclipticseasons$snowModel == null)
        original.call(instance, blockStateModel, directionPredicate, mutableQuadView, randomSource, blockAndTintGetter, blockPos, blockState, bufferer);
        // PlatformModelEmitter.getInstance().emitModel(blockStateModel, this::isFaceCulled, this.getForEmitting(), this.random, this.level, pos, state, this::bufferDefaultModel);

        // if (eclipticseasons$snowModel != null) {
        //     eclipticseasons$cancelDowngradedPass = true;
        //     eclipticseasons$shouldCollectBakeQuads = false;
        //     // this.type = ExtraModelManager.getRenderType(state);
        //     // original.call(instance, eclipticseasons$snowModel, directionPredicate, mutableQuadView, randomSource, blockAndTintGetter, blockPos, blockState, bufferer);
        //     PlatformModelEmitter.getInstance().emitModel(eclipticseasons$snowModel, this::isFaceCulled, this.getForEmitting(), this.random, this.level, pos, state, this::bufferDefaultModel);
        // }

    }

    @Inject(
            method = "renderModel",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelEmitter;emitModel(Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;Ljava/util/function/Predicate;Lnet/caffeinemc/mods/sodium/client/render/model/MutableQuadViewImpl;Lnet/minecraft/util/RandomSource;Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/caffeinemc/mods/sodium/client/services/PlatformModelEmitter$Bufferer;)V")
    )
    private void eclipticseasons$renderModel_start(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        // this.allowDowngrade = false;
        ExtraRendererContext extraRendererContext = IExtraRendererContextOwner.of(slice);
        ExtraRenderDispatcher.findModel
                (extraRendererContext,
                        (IMapSlice) (Object) slice, pos, state, random, state.getSeed(pos), eclipticseasons$mutableBlockPos, null);

        if (extraRendererContext.shouldApply()) {
            eclipticseasons$shouldReplaceOriginalGrassModel = extraRendererContext.isReplace();
            if (!eclipticseasons$shouldReplaceOriginalGrassModel) {
                boolean ctmBlock = ExtraModelManager.isSpecialCTMBlock(state);
                if (ctmBlock) {
                    eclipticseasons$shouldCollectBakeQuads = true;
                }
            }
        } else {
            eclipticseasons$shouldReplaceOriginalGrassModel = false;
            eclipticseasons$shouldCollectBakeQuads = false;
        }

        extraRendererContext
                .setModelData(ModelData.EMPTY)
                .setOriginalModel(model)
                // .setExtraModels(eclipticseasons$snowModel)
                // .setReplace(eclipticseasons$shouldReplaceOriginalGrassModel)
        ;

        if (eclipticseasons$chunkBuilderMeshingTask != null)
            eclipticseasons$chunkBuilderMeshingTask.eclipticseasons$addCount();
    }


    @Inject(
            remap = false,
            method = "renderModel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;forceOpaque(ZLnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private void eclipticseasons$renderModel_useAmbientOcclusion(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        TriState modelForAmbientOcclusion =
                ExtraRendererContext.
                        getModelForAmbientOcclusion(slice, state,
                                ModelData.EMPTY, ChunkSectionLayer.CUTOUT);
        if (modelForAmbientOcclusion != null && modelForAmbientOcclusion.isTrue()) {
            prepareAoInfo(true);
        }
    }

    @Inject(
            method = "renderModel",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$renderModel_end(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (!eclipticseasons$bakedQuads.isEmpty()) {
            eclipticseasons$bakedQuads.clear();
        }
        eclipticseasons$shouldReplaceOriginalGrassModel = false;
        eclipticseasons$shouldCollectBakeQuads = false;
        // eclipticseasons$snowModel = null;
        eclipticseasons$cancelDowngradedPass = false;

        IExtraRendererContextOwner.of(slice).resetAll();
    }

    @Inject(
            method = "processQuad",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$processQuad_cacheQuad(MutableQuadViewImpl quad, CallbackInfo ci) {
        if (eclipticseasons$shouldCollectBakeQuads) {
            TextureAtlasSprite sprite = quad.sprite(SpriteFinderCache.forBlockAtlas());
            // if( SpriteUtil.INSTANCE.hasAnimation(sprite))
            // SpriteUtil.INSTANCE.markSpriteActive(sprite);
            // Todo
            // eclipticseasons$bakedQuads.add(quad.toBakedQuad (sprite));
        }
    }

    @Inject(
            method = "bufferQuad",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;isTranslucent()Z")
    )
    private void eclipticseasons$bufferQuad_cancelAttemptPassDowngrade(
            MutableQuadViewImpl quad, float[] brightnesses, Material material, CallbackInfo ci,
            @Local(name = "pass") LocalRef<TerrainRenderPass> terrainRenderPassLocalRef,
            @Local(name = "atlasSprite") TextureAtlasSprite atlasSprite) {
        if (eclipticseasons$cancelDowngradedPass
                && atlasSprite instanceof ISpriteChecker spriteChecker
                && spriteChecker.isSnowyTexture()
        ) {
            terrainRenderPassLocalRef.set(DefaultTerrainRenderPasses.CUTOUT);
        }
    }

    @Inject(
            method = "release",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$release_end(CallbackInfo ci) {
        eclipticseasons$chunkBuilderMeshingTask = null;
    }

    // =============================

    @Override
    public void eclipticseasons$bindCounter(SodiumBoard sodiumBoard) {
        this.eclipticseasons$chunkBuilderMeshingTask = sodiumBoard;
    }

    @Override
    public BlockStateModel getSnowModel() {
        return IExtraRendererContextOwner.of(slice).getSnowyModel();
    }

    @Override
    public void setShouldCollect(boolean shouldCollect) {
        this.eclipticseasons$shouldCollectBakeQuads = shouldCollect;
    }

    @Override
    public boolean shouldCollect() {
        return this.eclipticseasons$shouldCollectBakeQuads;
    }

    @Override
    public List<BakedQuad> getCacheBakeQuad() {
        return eclipticseasons$bakedQuads;
    }

}
