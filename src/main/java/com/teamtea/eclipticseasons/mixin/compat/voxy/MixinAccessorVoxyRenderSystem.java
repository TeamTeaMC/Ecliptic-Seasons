package com.teamtea.eclipticseasons.mixin.compat.voxy;

import me.cortex.voxy.client.core.VoxyRenderSystem;
import me.cortex.voxy.client.core.model.ModelBakerySubsystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(VoxyRenderSystem.class)
public interface MixinAccessorVoxyRenderSystem {

    @Accessor("modelService")
    ModelBakerySubsystem getModelBakerySubsystem();
}
