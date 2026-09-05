package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModelPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ESFabricRender {
    public static void emitQuads(
            List<BlockStateModelPart> parts,
            QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
        final boolean cutoutLeaves = Minecraft.getInstance().options.cutoutLeaves().get();
        final boolean forceOpaque = ModelBlockRenderer.forceOpaque(cutoutLeaves, state);

        if (forceOpaque) {
            emitter.pushTransform(quad -> {
                quad.chunkLayer(ChunkSectionLayer.SOLID);
                return true;
            });
        }

        final int partCount = parts.size();

        for (int i = 0; i < partCount; i++) {
            if (parts.get(i) instanceof FabricBlockStateModelPart part)
                part.emitQuads(emitter, cullTest);
        }

        if (forceOpaque) {
            emitter.popTransform();
        }
    }
}
