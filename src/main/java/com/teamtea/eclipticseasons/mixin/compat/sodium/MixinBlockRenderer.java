package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.helper.ColorHelper;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.renderer.RenderType;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Mixin({BlockRenderer.class})
public abstract class MixinBlockRenderer extends AbstractBlockRenderContext implements SodiumStatus {

    @Unique
    public SodiumBoard eclipticSeasons$chunkBuilderMeshingTask;

    @Unique
    private static final Iterator<Object> EMPTY_ITER= Collections.emptyIterator();

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
        BakedModel snowModel = null;
        if (!original) {
            snowModel = ModelManager.findModel(slice, pos, state, random,randomSeed);
            if (eclipticSeasons$chunkBuilderMeshingTask != null)
                eclipticSeasons$chunkBuilderMeshingTask.eclipticSeasons$addCount();
        } else {
            if (ModelManager.isModelReplaced(state)) {

                snowModel = ModelManager.findModel(slice, pos, state, random,randomSeed);
                if (eclipticSeasons$chunkBuilderMeshingTask != null)
                    eclipticSeasons$chunkBuilderMeshingTask.eclipticSeasons$addCount();
            }
        }

        if (snowModel != null) {
            original = false;
            this.type = ModelManager.getRenderType(state);
            ((FabricBakedModel) snowModel).emitBlockQuads(this.level, state, pos, this.randomSupplier, this);
        }
        return original;
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
