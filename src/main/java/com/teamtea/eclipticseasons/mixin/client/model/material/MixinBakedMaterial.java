package com.teamtea.eclipticseasons.mixin.client.model.material;

import com.teamtea.eclipticseasons.client.model.block.quad.IExtendedMaterial;
import net.minecraft.client.resources.model.sprite.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = {Material.Baked.class, Material.class})
public abstract class MixinBakedMaterial implements IExtendedMaterial {

    @Unique
    private boolean eclipticseasons$forceCutout;

    @Override
    public boolean eclipticseasons$forceCutout() {
        return eclipticseasons$forceCutout;
    }

    @Override
    public void eclipticseasons$setForceCutout(boolean value) {
        eclipticseasons$forceCutout = value;
    }
}
