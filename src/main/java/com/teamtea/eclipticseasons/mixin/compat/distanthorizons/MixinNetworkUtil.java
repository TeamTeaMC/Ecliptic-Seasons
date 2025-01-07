package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.teamtea.eclipticseasons.common.network.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.NetworkUtil;
import com.teamtea.eclipticseasons.compat.distanthorizons.DHTool;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin({NetworkUtil.class})
public abstract class MixinNetworkUtil {

    @Inject(remap = false,
            at = {@At("HEAD")}, method = {"processEmptyMessage"})
    private static void ecliptic$processEmptyMessage(EmptyMessage emptyMessage, Supplier<NetworkEvent.Context> context, CallbackInfoReturnable<Boolean> cir) {
        context.get().enqueueWork(() -> {
            if (ClientConfig.Renderer.enhancementChunkRenderUpdate.get() && ClientConfig.Renderer.forceChunkRenderUpdate.get()) {
                DHTool.forceReloadAll();
            } else {
                DHTool.clearRenderCache();
            }
        });
    }
}
