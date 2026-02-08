package com.teamtea.eclipticseasons.compat.voxy.helper;

import net.minecraft.world.level.Level;

import java.lang.ref.WeakReference;

public interface IVoxyLevelProvider {

    default Level getLevelBind() {
        return getLevelReference() != null ?
                getLevelReference().get() : null;
    }

    default void release() {
        setLevelReference(null);
    }

    WeakReference<Level> getLevelReference();

    void setLevelReference(Level levelReference);
}
