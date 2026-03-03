package com.teamtea.eclipticseasons.mixin.compat.voxy;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.cortex.voxy.client.core.model.ModelFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.locks.ReentrantLock;

@Mixin(ModelFactory.class)
public interface MixinAccessorModelFactory {

    @Accessor("idMappings")
    int[] getIdMappings();

    @Accessor("modelTexture2id")
    Object2IntOpenHashMap<Object> getModelTexture2id();


    @Accessor("blockStatesInFlightLock")
    ReentrantLock getBlockStatesInFlightLock();
}
