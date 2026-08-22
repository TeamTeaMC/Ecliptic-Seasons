package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.compat.voxy.client.IVoxySectionUpdateRouter;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import me.cortex.voxy.client.core.rendering.SectionUpdateRouter;
import me.cortex.voxy.common.world.WorldEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.locks.StampedLock;

/**
 * Reuses an already registered mixin filename so no mixin-json edit is needed.
 */
@Mixin(SectionUpdateRouter.class)
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public abstract class MixinAccessorModelFactory implements IVoxySectionUpdateRouter {

    @Shadow
    @Final
    private Long2ByteOpenHashMap[] slices;

    @Shadow
    @Final
    private StampedLock[] locks;

    @Override
    public LongArrayList eclipticseasons$getWatchedSections() {
        LongArrayList result = new LongArrayList();

        for (int i = 0; i < slices.length; i++) {
            long stamp = locks[i].readLock();

            try {
                for (Long2ByteMap.Entry entry : slices[i].long2ByteEntrySet()) {
                    if ((entry.getByteValue() & WorldEngine.UPDATE_TYPE_BLOCK_BIT) != 0) {
                        result.add(entry.getLongKey());
                    }
                }
            } finally {
                locks[i].unlockRead(stamp);
            }
        }

        return result;
    }
}
