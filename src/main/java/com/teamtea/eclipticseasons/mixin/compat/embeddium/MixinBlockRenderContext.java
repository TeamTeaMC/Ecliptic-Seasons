package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricModelDelayChecker;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({BlockRenderContext.class})
public abstract class MixinBlockRenderContext implements FabricModelDelayChecker {

    @Unique
    boolean eclipticseasons$isFabricModelYet = false;


    @Override
    public boolean isLastFabric() {
        return eclipticseasons$isFabricModelYet;
    }

    @Override
    public void updateIsLastFabric(boolean is) {
        this.eclipticseasons$isFabricModelYet = is;
    }
}
