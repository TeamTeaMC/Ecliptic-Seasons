package com.teamtea.eclipticseasons.mixin.compat.fabric_renderer_indigo;


import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.TerrainRenderContextLevelGetter;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.embeddedt.embeddium.render.frapi.IndigoBlockRenderContext;
import org.spongepowered.asm.mixin.Mixin;

// need sodium load
@Mixin({IndigoBlockRenderContext.class})
public abstract class MixinBlockRenderer extends AbstractBlockRenderContext implements TerrainRenderContextLevelGetter {
    @Override
    public BlockAndTintGetter eclipticSeasons$get() {
        return blockInfo.blockView;
    }

    @Override
    public BlockPos eclipticSeasons$getPos() {
        return blockInfo.blockPos;
    }
}
