package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.common.block.blockentity.WindChimesBlockEntity;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
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

    default void setChunkDirty(SectionPos chunkPos) {
    }

    default void setAllChunkDirty() {
    }

    default void setAllRendererChanged() {
    }

    default void setChange(boolean change) {
        setSnowChange(change);
        setTermChange(change);
    }

    default boolean isChange() {
        return isSnowChange() || isTermChange();
    }

    default void setSnowChange(boolean change) {
    }

    default boolean isSnowChange() {
        return false;
    }

    default void setTermChange(boolean change) {
    }

    default boolean isTermChange() {
        return false;
    }


    default String getCurrentWorldName() {
        return "world";
    }
}
