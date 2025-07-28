package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.compat.sodium.ESSodiumContext;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.render.texture.SpriteFinderCache;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

@Mixin({BlockRenderer.class})
public abstract class MixinBlockRenderer extends AbstractBlockRenderContext implements SodiumStatus {

    // @Shadow
    // private boolean allowDowngrade;
    @Unique
    public SodiumBoard eclipticseasons$chunkBuilderMeshingTask;

    @Unique
    private List<BakedQuad> eclipticseasons$bakedQuads = new ArrayList<>();

    @Unique
    private BakedModel eclipticseasons$snowModel = null;

    @Unique
    private boolean eclipticseasons$shouldCollectBakeQuads = false;

    @Unique
    private boolean eclipticseasons$shouldReplaceOriginalGrassModel = false;

    @Unique
    private BlockPos.MutableBlockPos eclipticseasons$mutableBlockPos = new BlockPos.MutableBlockPos();

    @Unique
    private ESSodiumContext eclipticseasons$esContext = null;

    @Unique
    private boolean eclipticseasons$cancelDowngradedPass = false;

    @ModifyExpressionValue(
            remap = false,
            method = "renderModel",
            at = @At(value = "INVOKE",
                    ordinal = 0,
                    // shift = At.Shift.AFTER,
                    target = "Ljava/util/Iterator;hasNext()Z")
    )
    private boolean eclipticseasons$renderModel(
            boolean original,
            @Local(argsOnly = true) BakedModel model
    ) {

        if (eclipticseasons$shouldReplaceOriginalGrassModel && eclipticseasons$snowModel != null) {
            original = false;
        }

        if (!original && eclipticseasons$snowModel != null && eclipticseasons$shouldReplaceOriginalGrassModel) {

            // if (ModelManager.isSpecialCTMBlock(state)) {
            //     // eclipticseasons$esContext = new ESSodiumContext();
            //     //   eclipticseasons$esContext.updateContext(level, pos, state, model, random, modelData);
            //     //   ((FabricBakedModel) model).emitBlockQuads(this.level, state, pos, this.randomSupplier, eclipticseasons$esContext);
            //     //   getCacheBakeQuad().addAll(eclipticseasons$esContext.getQuadViews());
            // }
            eclipticseasons$cancelDowngradedPass = true;
            eclipticseasons$shouldCollectBakeQuads = false;
            original = false;
            this.type = ExtraModelManager.getRenderType(state);
            ((FabricBakedModel) eclipticseasons$snowModel).emitBlockQuads(this.level, state, pos, this.randomSupplier, this);

            // eclipticseasons$esContext.reset();
        }
        return original;
    }


    @WrapOperation(
            remap = false,
            method = "renderModel",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,

                    target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V")
    )
    private void eclipticseasons$renderModel_wrap_emitBlockQuads(
            FabricBakedModel instance, BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context, Operation<Void> original,
            @Local(argsOnly = true) BakedModel model,
            @Local Iterator<RenderType> iterator
    ) {

        // if (eclipticseasons$shouldReplaceOriginalGrassModel && eclipticseasons$snowModel != null) {
        //     original = false;
        // }

        original.call(instance, this.level, state, pos, this.randomSupplier, this);

        if (!eclipticseasons$shouldReplaceOriginalGrassModel
                && !iterator.hasNext() && eclipticseasons$snowModel != null) {

            // if (ModelManager.isSpecialCTMBlock(state)) {
            //     // eclipticseasons$esContext = new ESSodiumContext();
            //     //   eclipticseasons$esContext.updateContext(level, pos, state, model, random, modelData);
            //     //   ((FabricBakedModel) model).emitBlockQuads(this.level, state, pos, this.randomSupplier, eclipticseasons$esContext);
            //     //   getCacheBakeQuad().addAll(eclipticseasons$esContext.getQuadViews());
            // }
            // disableAllowDowngrade();
            eclipticseasons$cancelDowngradedPass = true;
            eclipticseasons$shouldCollectBakeQuads = false;
            this.type = ExtraModelManager.getRenderType(state);
            ((FabricBakedModel) eclipticseasons$snowModel).emitBlockQuads(this.level, state, pos, this.randomSupplier, this);

            // eclipticseasons$esContext.reset();
        }

    }

    // TODO: 如果以后使用shouldCollectBakeQuads去渲染的话，可能需要
    // 不要写太前面了，这里还得初始化
    @Inject(
            method = "renderModel",
            at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;")
    )
    private void eclipticseasons$renderModel_start(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        // this.allowDowngrade = false;
        eclipticseasons$snowModel = ExtraModelManager.findModel(slice, pos, state, random, randomSeed, eclipticseasons$mutableBlockPos);
        if (eclipticseasons$snowModel != null) {
            eclipticseasons$shouldReplaceOriginalGrassModel =
                    ExtraModelManager.isModelReplaceable(state, level, pos, eclipticseasons$snowModel);
            if (!eclipticseasons$shouldReplaceOriginalGrassModel) {
                boolean yuushyaBlock = ExtraModelManager.isSpecialCTMBlock(state);
                if (yuushyaBlock) {
                    eclipticseasons$shouldCollectBakeQuads = true;
                }
            }
        } else {
            eclipticseasons$shouldReplaceOriginalGrassModel = false;
            eclipticseasons$shouldCollectBakeQuads = false;
        }


        if (eclipticseasons$chunkBuilderMeshingTask != null)
            eclipticseasons$chunkBuilderMeshingTask.eclipticseasons$addCount();
    }

    @Inject(
            method = "renderModel",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$renderModel_end(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (!eclipticseasons$bakedQuads.isEmpty()) {
            eclipticseasons$bakedQuads.clear();
        }
        eclipticseasons$shouldReplaceOriginalGrassModel = false;
        eclipticseasons$shouldCollectBakeQuads = false;
        eclipticseasons$snowModel = null;
        eclipticseasons$cancelDowngradedPass = false;
    }

    @Inject(
            method = "release",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$release_end(CallbackInfo ci) {
        eclipticseasons$esContext = null;
    }

    // TODO:这里缓存xyz顶点和光照信息，然后交给下一级构建，或者想办法还原bakequad。
    @Inject(
            method = "processQuad",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$processQuad_cacheQuad(MutableQuadViewImpl quad, CallbackInfo ci) {
        if (eclipticseasons$shouldCollectBakeQuads) {
            TextureAtlasSprite sprite = quad.sprite(SpriteFinderCache.forBlockAtlas());
            // if( SpriteUtil.INSTANCE.hasAnimation(sprite))
            // SpriteUtil.INSTANCE.markSpriteActive(sprite);
            eclipticseasons$bakedQuads.add(quad.toBakedQuad(sprite));
        }
    }

    @ModifyExpressionValue(
            method = "bufferQuad",
            at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;attemptPassDowngrade(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;)Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/TerrainRenderPass;")
    )
    private TerrainRenderPass eclipticseasons$bufferQuad_cancelAttemptPassDowngrade(TerrainRenderPass original) {
        if (eclipticseasons$cancelDowngradedPass) {
            original = null;
        }
        return original;
    }

    @Override
    public BakedModel getSnowModel() {
        return eclipticseasons$snowModel;
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

    @Override
    public void eclipticseasons$bindCounter(SodiumBoard sodiumBoard) {
        this.eclipticseasons$chunkBuilderMeshingTask = sodiumBoard;
    }
}
