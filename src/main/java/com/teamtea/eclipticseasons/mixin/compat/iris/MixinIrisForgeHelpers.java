package com.teamtea.eclipticseasons.mixin.compat.iris;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.model.ISnowyReplaceModel;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext;
import net.irisshaders.iris.compat.sodium.mixin.BlockRendererAccessor;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {AbstractBlockRenderContext.class}, priority = 1200)
public abstract class MixinIrisForgeHelpers {

    @Shadow
    protected BlockPos pos;
    @Shadow
    protected BlockAndTintGetter level;


    @Inject(
            method = {"bufferDefaultModel"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelAccess;getQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/minecraft/client/renderer/RenderType;Lnet/caffeinemc/mods/sodium/client/services/SodiumModelData;)Ljava/util/List;"
            )}
    )
    private void eclipticseasons$bufferDefaultModel_aftergetBlockAppearance(BakedModel model, BlockState state, CallbackInfo ci, @Local Direction cullFace) {
        if ((Object) this instanceof BlockRenderer r) {
            if (this instanceof SodiumStatus sodiumStatus
                    && sodiumStatus.getSnowModel() instanceof ISnowyReplaceModel snowyBakedModelWrapper) {
                if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null && cullFace != null) {
                    // BlockState appearance = null;
                    // appearance = IrisPlatformHelpers.getInstance().getBlockAppearance(this.level, state, cullFace, this.pos);
                    // if (EclipticSeasonsApi.getInstance().isSnowyBlock(Minecraft.getInstance().level, state, pos)) {
                    //     appearance = Blocks.SNOW.defaultBlockState();
                    // }

                    if (ExtraModelManager.renderAsSnowInShader(state, level, pos)) {
                        ((BlockSensitiveBufferBuilder) ((BlockRendererAccessor) r).getBuffers()).overrideBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(Blocks.SNOW.defaultBlockState()));
                    }
                }
            }
        }

    }

    // @Inject(
    //         method = {"bufferDefaultModel"},
    //         at = {@At("TAIL")}
    // )
    // private void eclipticseasons$bufferDefaultModel_endgetBlockAppearance(BakedModel model, BlockState state, CallbackInfo ci) {
    //     if ((Object) this instanceof BlockRenderer r) {
    //         if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null) {
    //             ((BlockSensitiveBufferBuilder) ((BlockRendererAccessor) r).getBuffers()).restoreBlock();
    //         }
    //     }
    //
    // }

    // private void eclipticseasons$getBlockAppearance_check(BlockAndTintGetter level, BlockState state, Direction cullFace, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
    //     if (EclipticSeasonsApi.getInstance().isSnowyBlock(Minecraft.getInstance().level, state, pos)) {
    //         cir.setReturnValue(Blocks.SNOW.defaultBlockState());
    //     }
    // }
}
