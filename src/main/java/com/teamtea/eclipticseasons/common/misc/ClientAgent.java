package com.teamtea.eclipticseasons.common.misc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public interface ClientAgent {

    @Nullable
    default Entity getCameraEntity() {
        return null;
    }

    @Nullable
    default HitResult getHitResult() {
        return null;
    }
}
