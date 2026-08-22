package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.compat.voxy.client.IVoxyGeometryCache;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import me.cortex.voxy.client.core.rendering.GeometryCache;
import me.cortex.voxy.client.core.rendering.building.BuiltSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.locks.ReentrantLock;

@Mixin(GeometryCache.class)
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public abstract class MixinGeometryCache implements IVoxyGeometryCache {

    @Shadow
    @Final
    private ReentrantLock lock;

    @Shadow
    private long currentSize;

    @Shadow
    @Final
    private Long2ObjectLinkedOpenHashMap<BuiltSection> cache;

    @Override
    public void eclipticseasons$clearForSeasonChange() {
        lock.lock();

        try {
            cache.values().forEach(BuiltSection::free);
            cache.clear();
            currentSize = 0L;
        } finally {
            lock.unlock();
        }
    }
}
