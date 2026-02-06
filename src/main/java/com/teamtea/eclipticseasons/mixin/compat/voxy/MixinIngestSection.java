package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.teamtea.eclipticseasons.compat.voxy.IVoxyLevelProvider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;

@Mixin(targets = {"me.cortex.voxy.common.world.service.VoxelIngestService$IngestSection"})
public abstract class MixinIngestSection implements IVoxyLevelProvider {

    @Unique
    WeakReference<Level> eclipticseasons$level = new WeakReference<>(null);

    @Override
    public void setLevelReference(Level levelReference) {
        this.eclipticseasons$level = new WeakReference<>(levelReference);
    }

    @Override
    public WeakReference<Level> getLevelReference() {
        return eclipticseasons$level;
    }
}
