package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.util.ColorHelper;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import com.teamtea.eclipticseasons.compat.yuushya.YuushyaChecker;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.render.texture.SpriteFinderCache;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mixin({BlockRenderer.class})
public abstract class MixinBlockRenderer extends AbstractBlockRenderContext implements SodiumStatus {

    @Unique
    public SodiumBoard eclipticSeasons$chunkBuilderMeshingTask;

    @Unique
    private List<BakedQuad> eclipticSeasons$bakedQuads = new ArrayList<>();

    @Unique
    private BakedModel eclipticSeasons$snowModel = null;

    @Unique
    private boolean eclipticSeasons$shouldCollectBakeQuads = false;

    @Unique
    private boolean eclipticSeasons$shouldReplaceOriginalGrassModel = false;

    @Unique
    private BlockPos.MutableBlockPos eclipticSeasons$mutableBlockPos = new BlockPos.MutableBlockPos();

    @ModifyExpressionValue(
            remap = false,
            method = "renderModel",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    target = "Ljava/util/Iterator;hasNext()Z")
    )
    private boolean eclipticseasons$renderModel(
            boolean original
    ) {
        // BakedModel snowModel = null;
        // if (!original) {
        //     snowModel = ModelManager.findModel(slice, pos, state, random, randomSeed);
        //     if (eclipticSeasons$chunkBuilderMeshingTask != null)
        //         eclipticSeasons$chunkBuilderMeshingTask.eclipticSeasons$addCount();
        // } else {
        //     // if (ModelManager.isModelReplaceable(state))
        //     if (ModelManager.isModelReplaceable(((IBlockStateFlagger) state).getBlockTypeFlag(level, pos))) {
        //
        //         snowModel = ModelManager.findModel(slice, pos, state, random, randomSeed);
        //         if (eclipticSeasons$chunkBuilderMeshingTask != null)
        //             eclipticSeasons$chunkBuilderMeshingTask.eclipticSeasons$addCount();
        //     }
        // }

        if (eclipticSeasons$shouldReplaceOriginalGrassModel && eclipticSeasons$snowModel != null) {
            original = false;
        }

        if (!original && eclipticSeasons$snowModel != null) {
            eclipticSeasons$shouldCollectBakeQuads = false;
            original = false;
            this.type = ModelManager.getRenderType(state);
            ((FabricBakedModel) eclipticSeasons$snowModel).emitBlockQuads(this.level, state, pos, this.randomSupplier, this);
        }
        return original;
    }

    // TODO: 如果以后使用shouldCollectBakeQuads去渲染的话，可能需要
    // 不要写太前面了，这里还得初始化
    @Inject(
            method = "renderModel",
            at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;")
    )
    private void eclipticseasons$renderModel_start(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        eclipticSeasons$snowModel = ModelManager.findModel(slice, pos, state, random, randomSeed, eclipticSeasons$mutableBlockPos);
        if (eclipticSeasons$snowModel != null) {
            eclipticSeasons$shouldReplaceOriginalGrassModel = ModelManager.isModelReplaceable(state,level, pos);
            if (!eclipticSeasons$shouldReplaceOriginalGrassModel) {
                boolean yuushyaBlock = YuushyaChecker.isyuushyaContinuityBlock(state);
                if (yuushyaBlock) {
                    eclipticSeasons$shouldCollectBakeQuads = true;
                }
            }
        } else {
            eclipticSeasons$shouldReplaceOriginalGrassModel = false;
            eclipticSeasons$shouldCollectBakeQuads = false;
        }


        if (eclipticSeasons$chunkBuilderMeshingTask != null)
            eclipticSeasons$chunkBuilderMeshingTask.eclipticSeasons$addCount();
    }

    @Inject(
            method = "renderModel",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$renderModel_end(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (!eclipticSeasons$bakedQuads.isEmpty()) {
            eclipticSeasons$bakedQuads.clear();
        }
        eclipticSeasons$shouldReplaceOriginalGrassModel = false;
        eclipticSeasons$shouldCollectBakeQuads = false;
        eclipticSeasons$snowModel = null;
    }

    // TODO:这里缓存xyz顶点和光照信息，然后交给下一级构建，或者想办法还原bakequad。
    @Inject(
            method = "processQuad",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$processQuad_cacheQuad(MutableQuadViewImpl quad, CallbackInfo ci) {
        if (eclipticSeasons$shouldCollectBakeQuads) {
            eclipticSeasons$bakedQuads.add(quad.toBakedQuad(quad.sprite(SpriteFinderCache.forBlockAtlas())));
            // EclipticSeasons.logger(eclipticSeasons$bakedQuads.stream().map(BakedQuad::getDirection).toList());
        }
    }

    @Override
    public BakedModel getSnowModel() {
        return eclipticSeasons$snowModel;
    }

    @Override
    public List<BakedQuad> getCacheBakeQuad() {
        return eclipticSeasons$bakedQuads;
    }

    @Override
    public void eclipticSeasons$bindCounter(SodiumBoard sodiumBoard) {
        this.eclipticSeasons$chunkBuilderMeshingTask = sodiumBoard;
    }
}
