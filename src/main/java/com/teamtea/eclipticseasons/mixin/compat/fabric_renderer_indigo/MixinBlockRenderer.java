package com.teamtea.eclipticseasons.mixin.compat.fabric_renderer_indigo;


import com.mojang.blaze3d.vertex.PoseStack;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.TerrainRenderContextLevelGetter;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.embeddedt.embeddium.render.frapi.IndigoBlockRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
