package com.teamtea.eclipticseasons.mixin.client.model;


import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.api.misc.client.ISpriteChecker;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TextureAtlasSprite.class)
public abstract class MixinTextureAtlasSprite implements ISpriteChecker {


    @Unique
    boolean eclipticseasons$isCTMSprite = false;

    @Unique
    boolean eclipticseasons$hasCheck = false;

    @Override
    public boolean isCTMSprite() {
        if (!eclipticseasons$hasCheck) {
            eclipticseasons$isCTMSprite = ExtraModelManager.isSpecialCTMSprite((TextureAtlasSprite) (Object) this);
            eclipticseasons$hasCheck = true;
        }
        return this.eclipticseasons$isCTMSprite;
    }

    @Unique
    boolean eclipticseasons$isSnowySprite = false;

    @Unique
    boolean eclipticseasons$hasCheckSnowy = false;

    @Override
    public boolean isSnowyTexture() {
        if (!eclipticseasons$hasCheckSnowy) {
            eclipticseasons$isSnowySprite = ExtraModelManager.isSpecialSnowySprite((TextureAtlasSprite) (Object) this);
            eclipticseasons$hasCheckSnowy = true;
        }
        return this.eclipticseasons$isSnowySprite;
    }
}
