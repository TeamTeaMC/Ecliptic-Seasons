package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.model.QuadFixer;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import com.teamtea.eclipticseasons.compat.yuushya.YuushyaChecker;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.render.texture.SpriteFinderCache;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Mixin({BlockRenderer.class})
public abstract class MixinBlockRenderer extends AbstractBlockRenderContext implements SodiumStatus {

    @Unique
    public SodiumBoard eclipticSeasons$chunkBuilderMeshingTask;

    @Unique
    private static final Iterator<Object> EMPTY_ITER = Collections.emptyIterator();

    @Unique
    private List<BakedQuad> eclipticSeasons$bakedQuads = new ArrayList<>();

    @Unique
    private BakedModel eclipticSeasons$snowModel = null;

    @Unique
    private boolean eclipticSeasons$shouldCollectBakeQuads = false;

    @Unique
    private boolean eclipticSeasons$shouldReplaceOriginalGrassModel = false;

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
    @Inject(
            method = "renderModel",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$renderModel_start(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        BakedModel snowModel = ModelManager.findModel(slice, pos, state, random, randomSeed);
        if (snowModel != null) {
            eclipticSeasons$snowModel = snowModel;
            eclipticSeasons$shouldReplaceOriginalGrassModel = ModelManager.isModelReplaceable(((IBlockStateFlagger) state).getBlockTypeFlag(level, pos));
            if (!eclipticSeasons$shouldReplaceOriginalGrassModel) {
                boolean yuushyaBlock = YuushyaChecker.isyuushyaContinuityBlock(state);
                if (yuushyaBlock) {
                    eclipticSeasons$shouldCollectBakeQuads = true;
                }
            }
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
    private void eclipticseasons$cache_(MutableQuadViewImpl quad, CallbackInfo ci) {
        // int posCount = 4;
        // int[] vs = new int[4 * BakedQuadRetextured.verticeSpace];
        // for (int i = 0; i < posCount; i++) {
        //     int s = i * BakedQuadRetextured.verticeSpace;
        //     vs[s + 0] = Float.floatToIntBits(quad.x(i));
        //     vs[s + 1] = Float.floatToIntBits(quad.y(i));
        //     vs[s + 2] = Float.floatToIntBits(quad.z(i));
        //
        //     vs[s + 3] = Float.floatToIntBits(quad.color(i));
        //
        //     // 我们不需要复制uv，将按模型实际位置分配
        //     Vector3f vector3f = new Vector3f(0, 0, 0);
        //     quad.copyNormal(i, vector3f);
        //     vs[s + BakedQuadRetextured.normalIndex + 0] = Float.floatToIntBits(vector3f.x);
        //     vs[s + BakedQuadRetextured.normalIndex + 1] = Float.floatToIntBits(vector3f.y);
        //     vs[s + BakedQuadRetextured.normalIndex + 2] = Float.floatToIntBits(vector3f.z);
        // }
        // boolean shade = quad.hasShade();
        // Direction direction = quad.lightFace();
        // boolean ambientOcclusion = lightMode == LightMode.SMOOTH;
        if (eclipticSeasons$shouldCollectBakeQuads) {
            eclipticSeasons$bakedQuads.add(quad.toBakedQuad(quad.sprite(SpriteFinderCache.forBlockAtlas())));
            // EclipticSeasons.logger(eclipticSeasons$bakedQuads.stream().map(BakedQuad::getDirection).toList());
        }
    }


    @Inject(
            method = "processQuad",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void eclipticseasons$cache_if(MutableQuadViewImpl quad, CallbackInfo ci) {
        // if (YuushyaChecker.isyuushyaContinuityBlock(state)) {
        //     // EclipticSeasons.logger(ModelManager.getBakeQuadInfo(quad.toBakedQuad(quad.sprite(SpriteFinderCache.forBlockAtlas()))));
        //     if (quad.lightFace() == Direction.EAST) {
        //         if (!quad.toBakedQuad(quad.sprite(SpriteFinderCache.forBlockAtlas())).getSprite().contents().name().getNamespace().equals(EclipticSeasonsApi.MODID)) {
        //             // ci.cancel();
        //             int c=0;
        //         } else {
        //             EclipticSeasons.logger(QuadFixer.getBakeQuadInfo(quad.toBakedQuad(quad.sprite(SpriteFinderCache.forBlockAtlas()))));
        //         }
        //     }
        //     // else ci.cancel();
        // }
    }

    @Override
    public List<BakedQuad> getCacheBakeQuad() {
        return eclipticSeasons$bakedQuads;
    }

    @Override
    public void eclipticSeasons$bindCounter(SodiumBoard sodiumBoard) {
        this.eclipticSeasons$chunkBuilderMeshingTask = sodiumBoard;
    }


    // @Inject(
    //         remap = false,
    //         method = "colorizeQuad",
    //         at = @At(value = "TAIL")
    // )
    // private void eclipticseasons$colorizeQuad(
    //         MutableQuadViewImpl quad, int colorIndex, CallbackInfo ci
    // ) {
    //     int[] vertexColors = this.vertexColors;
    //     for (int i = 0; i < vertexColors.length; i++) {
    //         vertexColors[i] = Color.decode("#fffef9").getRGB();
    //     }
    //
    //     for (int i = 0; i < 4; ++i) {
    //         quad.color(i, ColorHelper.multiplyColor(vertexColors[i], quad.color(i)));
    //     }
    // }
}
