package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.compat.voxy.IVoxyLevelProvider;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import me.cortex.voxy.common.voxelization.VoxelizedSection;
import me.cortex.voxy.common.world.service.VoxelIngestService;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ConcurrentLinkedDeque;

@Mixin({VoxelIngestService.class})
public abstract class MixinVoxelIngestService {

    @Shadow(remap = false)
    @Final
    private static ThreadLocal<VoxelizedSection> SECTION_CACHE;

    @ModifyExpressionValue(
            remap = false,
            method = "processJob",
            at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ConcurrentLinkedDeque;pop()Ljava/lang/Object;")
    )
    private <E> E eclipticseasons$processJob(
            E original,
            @Share("voxy_level_provider") LocalRef<IVoxyLevelProvider> iVoxyLevelProviderLocalRef) {
        if (VoxyTool.isVoxyTest() && original instanceof IVoxyLevelProvider iVoxyLevelProvider) {
            iVoxyLevelProviderLocalRef.set(iVoxyLevelProvider);
        }
        return original;
    }

    @Inject(
            remap = false,
            method = "processJob",
            at = @At(value = "INVOKE", target = "Lme/cortex/voxy/common/voxelization/WorldConversionFactory;convert(Lme/cortex/voxy/common/voxelization/VoxelizedSection;Lme/cortex/voxy/common/world/other/Mapper;Lnet/minecraft/world/level/chunk/PalettedContainer;Lnet/minecraft/world/level/chunk/PalettedContainerRO;Lme/cortex/voxy/common/voxelization/ILightingSupplier;)Lme/cortex/voxy/common/voxelization/VoxelizedSection;")
    )
    private void eclipticseasons$processJob_in(
            CallbackInfo ci,
            @Share("voxy_level_provider") LocalRef<IVoxyLevelProvider> iVoxyLevelProviderLocalRef) {
        if (VoxyTool.isVoxyTest()) {
            IVoxyLevelProvider iVoxyLevelProvider1 = iVoxyLevelProviderLocalRef.get();
            if (iVoxyLevelProvider1 != null) {
                ((IVoxyLevelProvider) SECTION_CACHE.get()).setLevelReference(iVoxyLevelProvider1.getLevelBind());
            }
        }
    }

    @WrapOperation(
            remap = false,
            method = "enqueueIngest",
            at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ConcurrentLinkedDeque;add(Ljava/lang/Object;)Z")
    )
    private <E> boolean eclipticseasons$enqueueIngest(
            ConcurrentLinkedDeque<E> instance, E e, Operation<Boolean> original,
            @Local(argsOnly = true) LevelChunk chunk) {
        if (VoxyTool.isVoxyTest() && e instanceof IVoxyLevelProvider levelProvider) {
            levelProvider.setLevelReference(chunk.getLevel());
        }
        return original.call(instance, e);
    }
}
