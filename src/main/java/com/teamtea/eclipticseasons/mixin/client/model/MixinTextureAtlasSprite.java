package com.teamtea.eclipticseasons.mixin.client.model;


import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.compat.ctm.CTMSpriteChecker;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlasSprite.class)
public abstract class MixinTextureAtlasSprite implements CTMSpriteChecker {

    @Unique
    boolean eclipticseasons$isCTMSprite = false;

    @Unique
    boolean eclipticseasons$hasCheck = false;

    @Override
    public boolean isCTMSprite() {
        if (!eclipticseasons$hasCheck) {
            eclipticseasons$isCTMSprite = ModelManager.isSpecialCTMSprite((TextureAtlasSprite) (Object) this);
            eclipticseasons$hasCheck = true;
        }
        return this.eclipticseasons$isCTMSprite;
    }
}
