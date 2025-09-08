package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricModelDelayChecker;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({BlockRenderContext.class})
public abstract class MixinBlockRenderContext implements FabricModelDelayChecker {

    @Unique
    BakedModel eclipticseasons$isFabricModelYet = null;

    @Override
    public BakedModel isLastFabric() {
        return eclipticseasons$isFabricModelYet;
    }

    @Override
    public void updateIsLastFabric(BakedModel is) {
        this.eclipticseasons$isFabricModelYet = is;
    }


}
