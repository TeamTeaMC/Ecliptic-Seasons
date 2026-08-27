package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.compat.voxy.IVoxyMapper;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.cortex.voxy.common.world.other.Mapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.locks.ReentrantLock;

@Mixin({Mapper.class})
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public abstract class MixinMapping implements IVoxyMapper {

    @Shadow(remap = false)
    @Final
    private ReentrantLock biomeLock;
    @Shadow(remap = false)
    @Final
    private ObjectArrayList<Mapper.BiomeEntry> biomeId2biomeEntry;
    @Unique
    private static final Mapper.StateEntry ECLIPTICSEASONS_VIRTUAL_ICE = new Mapper.StateEntry(
            VoxyTool.VIRTUAL_ICE_BLOCK_ID, BlockRegistry.thinIce.get().defaultBlockState()
    );

    @WrapOperation(
            remap = false,
            method = "getBlockStateOpacity(I)I",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;get(I)Ljava/lang/Object;")
    )
    private <K> K eclipticseasons$getBlockStateOpacity_fixId(ObjectArrayList<K> instance, int index, Operation<K> original) {
        if (VoxyTool.isVirtualIceId(index)) return (K) ECLIPTICSEASONS_VIRTUAL_ICE;
        return original.call(instance, VoxyTool.fixId((Mapper) (Object) this, index));
    }

    @WrapOperation(
            remap = false,
            method = "getBlockStateFromBlockId",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;get(I)Ljava/lang/Object;")
    )
    private <K> K eclipticseasons$getBlockStateFromBlockId_fixId(ObjectArrayList<K> instance, int index, Operation<K> original) {
        if (VoxyTool.isVirtualIceId(index)) return (K) ECLIPTICSEASONS_VIRTUAL_ICE;
        return original.call(instance, VoxyTool.fixId((Mapper) (Object) this, index));
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void eclipticseasons$close(CallbackInfo ci) {
        VoxyTool.clearBiomeCache();
    }

    @Override
    public Mapper.BiomeEntry eclipticseasons$getBiomeEntry(int biomeId) {
        biomeLock.lock();
        try {
            return biomeId >= 0 && biomeId < biomeId2biomeEntry.size()
                    ? biomeId2biomeEntry.get(biomeId)
                    : null;
        } finally {
            biomeLock.unlock();
        }
    }
}
