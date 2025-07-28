package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.common.block.blockentity.WindChimesBlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public interface ClientAgent {
    default void loadWindChime(WindChimesBlockEntity windChimesBlockEntity) {
    }

    @Nullable
    default Entity getCameraEntity() {
        return null;
    }

    @Nullable
    default HitResult getHitResult() {
        return null;
    }
}
