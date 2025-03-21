package com.teamtea.eclipticseasons.client.particle;

import com.teamtea.eclipticseasons.client.util.ColorHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.FastColor;

public class GreenHouseParticle extends SimpleAnimatedParticle {
    public GreenHouseParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ColorParticleOptions particleType, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0.0125F);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize *= 0.75F;
        this.lifetime = 60 + this.random.nextInt(12);
        this.setSpriteFromAge(sprites);

        int base = FastColor.ARGB32.color(255, (int) (particleType.getColor().x * 255), (int) (particleType.getColor().y * 255), (int) (particleType.getColor().z * 255));
        setColor(base);
        int fade = ColorHelper.simplyMixColor(base, 0.5f, 15916745, 0.5f);
        setFadeColor(fade);
    }

    public void move(double pX, double pY, double pZ) {
        this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
        this.setLocationFromBoundingbox();
    }
}
