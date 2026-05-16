package com.teamtea.eclipticseasons.mixin.compat.iris;


import com.google.common.annotations.Beta;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.iris.IrisAttachSnowyShader;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.sodium.terrain.VertexEncoderInterface;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Beta
@Mixin(value = {AbstractBlockRenderContext.class}, priority = 1200)
public abstract class MixinAbstractBlockRenderContext implements IrisAttachSnowyShader {

    @Shadow
    protected BlockPos pos;
    @Shadow
    protected BlockAndTintGetter level;

    @Shadow
    protected BlockState state;

    @Inject(
            method = {"bufferDefaultModel"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelAccess;getQuads(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModelPart;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;)Ljava/util/List;"
            )}
    )
    private void eclipticseasons$bufferDefaultModel_aftergetBlockAppearance(
            BlockStateModelPart part,
            Predicate<Direction> cullTest,
            Consumer<MutableQuadViewImpl> emitter, CallbackInfo ci,
            @Local(name = "cullFace") Direction cullFace) {
        if ((Object) this instanceof BlockRenderer r) {
            if (this instanceof SodiumStatus sodiumStatus
                    && sodiumStatus.getSnowModel() != null
                    && CompatModule.ClientConfig.unifiedSnowyBlockShading.isTrue()) {
                if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null && cullFace != null) {
                    if (CompatModule.ClientConfig.unifiedSnowyBlockSides.isFalse() && cullFace != Direction.UP)
                        return;
                    if (ExtraModelManager.renderAsSnowInShader(state, level, pos)) {
                        // ((BlockSensitiveBufferBuilder) ((BlockRendererAccessor) r).getBuffers()).overrideBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(Blocks.SNOW_BLOCK.defaultBlockState()));
                        ((VertexEncoderInterface) r).overrideBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(Blocks.SNOW_BLOCK.defaultBlockState()));
                    }
                }
            }
        }

    }


    @Override
    public void es$setSnowyBlockState(BlockState state) {
        if (this instanceof VertexEncoderInterface vertexEncoderInterface
                && WorldRenderingSettings.INSTANCE.getBlockStateIds() != null) {
            vertexEncoderInterface.overrideBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(state));
        }
    }
}
