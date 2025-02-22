package com.teamtea.eclipticseasons.mixin.compat.rubidium;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import com.teamtea.eclipticseasons.compat.fabric_renderer_indigo.FabricModelDelayChecker;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.embeddedt.embeddium.render.frapi.FRAPIRenderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin({BlockRenderContext.class})
public abstract class MixinBlockRenderContext implements FabricModelDelayChecker {

    @Unique
    boolean ecliptic$isFabricModelYet = false;


    @Override
    public boolean isLastFabric() {
        return ecliptic$isFabricModelYet;
    }

    @Override
    public void updateIsLastFabric(boolean is) {
        this.ecliptic$isFabricModelYet = is;
    }
}
