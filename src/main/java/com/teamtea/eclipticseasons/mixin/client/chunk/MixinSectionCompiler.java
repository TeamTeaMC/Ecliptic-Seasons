package com.teamtea.eclipticseasons.mixin.client.chunk;


import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(SectionCompiler.class)
public abstract class MixinSectionCompiler {

    @Shadow
    @Final
    private boolean cutoutLeaves;

    @Inject(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(value = "HEAD"
            )
    )
    private void eclipticseasons$compile_init_chunk(SectionPos sectionPos, RenderSectionRegion region, VertexSorting vertexSorting, SectionBufferBuilderPack builders, List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers, CallbackInfoReturnable<SectionCompiler.Results> cir) {
        ((IMapSlice) region).forceMapSliceUpdate();
    }
    @Inject(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(value = "INVOKE",
                    // shift = At.Shift.AFTER,
                    // ordinal = 1,
                    target = "Lnet/minecraft/client/renderer/block/FluidRenderer;tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V")
    )
    private void eclipticseasons$renderFrozenWaterIce(
            SectionPos sectionPos,
            RenderSectionRegion region,
            VertexSorting vertexSorting,
            SectionBufferBuilderPack builders,
            List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers,
            CallbackInfoReturnable<SectionCompiler.Results> cir,
            @Local(name = "pos") BlockPos pos,
            @Local(name = "blockState") BlockState blockState,
            @Local(name = "fluidState") FluidState fluidState,
            @Local(name = "quadOutput") BlockQuadOutput quadOutput,
            @Local(name = "opaqueQuadOutput") BlockQuadOutput opaqueQuadOutput,
            @Local(name = "blockRenderer") ModelBlockRenderer blockRenderer
    ) {

        if (!IceKeeper.notFrozen(region, pos, blockState, fluidState)) {
            blockRenderer.tesselateBlock(
                    ModelBlockRenderer.forceOpaque(this.cutoutLeaves, blockState) ? opaqueQuadOutput : quadOutput,
                    (float)SectionPos.sectionRelative(pos.getX()),
                    (float)SectionPos.sectionRelative(pos.getY()),
                    (float)SectionPos.sectionRelative(pos.getZ()),
                    region,
                    pos,
                    Blocks.ICE.defaultBlockState(),
                    IceKeeper.getIceModel(blockState, fluidState),
                    blockState.getSeed(pos));
        }
    }
}
