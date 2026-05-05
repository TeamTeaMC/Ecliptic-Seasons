package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.google.common.annotations.Beta;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.SeasonRenderDispatcher;
import com.teamtea.eclipticseasons.client.model.block.ISnowyReplaceModel;
import com.teamtea.eclipticseasons.api.misc.client.ISpriteChecker;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Beta
@Mixin({AbstractBlockRenderContext.class})
public abstract class MixinAbstractBlockRenderContext {
    @Shadow
    protected RandomSource random;

    @Shadow
    protected BlockAndTintGetter level;

    @Shadow
    protected BlockPos pos;

    @Shadow
    protected BlockState state;

    @ModifyExpressionValue(
           remap = false,
           method = "bufferDefaultModel",
           at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformModelAccess;getQuads(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModelPart;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;)Ljava/util/List;")
    )
    private List<BakedQuad> eclipticseasons$bufferDefaultModel_getQuads(
           List<BakedQuad> original,
           @Local(argsOnly = true) BlockStateModelPart part,
           @Local Direction side) {
       if (this instanceof SodiumStatus sodiumStatus && sodiumStatus.getSnowModel() != null)
           return SeasonRenderDispatcher.cancelTop(part, level, state, pos, side, random, original, sodiumStatus.getCacheBakeQuad());
       return original;
    }

    @Inject(
           remap = false,
           method = "bufferDefaultModel",
           at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/model/MutableQuadViewImpl;fromBakedQuad(Lnet/minecraft/client/resources/model/geometry/BakedQuad;)Lnet/caffeinemc/mods/sodium/client/render/model/MutableQuadViewImpl;")
    )
    private void eclipticseasons$bufferDefaultModel_cache(
            BlockStateModelPart part,
            Predicate<Direction> cullTest,
            Consumer<MutableQuadViewImpl> emitter,
            CallbackInfo ci,
            @Local(name = "q") BakedQuad bakedQuad,
            @Local(name = "cullFace") Direction side) {
       if (this instanceof SodiumStatus sodiumStatus
               && sodiumStatus.getSnowModel() != null
               && !(ISnowyReplaceModel.isInvalid(part))
               && !sodiumStatus.shouldCollect()) {
           try {
               if (bakedQuad.materialInfo().sprite() instanceof ISpriteChecker iSpriteChecker
                       && iSpriteChecker.isCTMSprite()) {
                   sodiumStatus.setShouldCollect(true);
               }
           } catch (Exception exception) {
               EclipticSeasons.logger(exception);
           }
       }
    }

    @ModifyExpressionValue(
           method = "shouldDrawSide",
           at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockAndTintGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState eclipticseasons$skip_if_fake_snow(BlockState original,
                                                        @Local(name = "neighborPos") BlockPos.MutableBlockPos otherPos,
                                                        @Local(argsOnly = true) Direction facing) {
       return SeasonRenderDispatcher.getFakeBlockState(original, state, level, otherPos, pos, facing);
    }


}
