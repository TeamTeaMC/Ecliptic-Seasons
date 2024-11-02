package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;

@Mixin({BlockRenderer.class})
public abstract class MixinBlockRenderer extends AbstractBlockRenderContext {

    @Unique
    protected RandomSource eclipticSeasons$random = new SingleThreadedRandomSource(42L);

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
        BakedModel snowModel=null;
        if (!original) {
            snowModel = ModelManager.findModel(slice, pos, state, random);
        } else {
            if (ModelManager.isModelReplaced(state)) {
                snowModel = ModelManager.findModel(slice, pos, state, random);
            }
        }

        if (snowModel != null) {
            original = false;
            this.type = RenderType.cutoutMipped();
            ((FabricBakedModel)snowModel).emitBlockQuads(this.level, state, pos, this.randomSupplier, this);
        }
        return original;
    }

}
