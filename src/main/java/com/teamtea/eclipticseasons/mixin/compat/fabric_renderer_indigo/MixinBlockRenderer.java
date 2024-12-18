package com.teamtea.eclipticseasons.mixin.compat.fabric_renderer_indigo;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.TerrainRenderContextLevelGetter;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import com.teamtea.eclipticseasons.compat.yuushya.YuushyaChecker;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin({TerrainRenderContext.class})
public abstract class MixinBlockRenderer extends AbstractBlockRenderContext implements TerrainRenderContextLevelGetter {


    @Inject(
            remap = false,
            method = "tessellateBlock",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/client/resources/model/BakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V")
    )
    private void eclipticseasons$tessellateBlock(
            BlockState blockState, BlockPos blockPos, BakedModel model, PoseStack matrixStack, ModelData modelData, RenderType renderType, CallbackInfo ci) {

    }


    @Override
    public BlockAndTintGetter eclipticSeasons$get() {
        return blockInfo.blockView;
    }

    @Override
    public BlockPos eclipticSeasons$getPos() {
        return blockInfo.blockPos;
    }
}
