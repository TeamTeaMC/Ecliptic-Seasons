package com.teamtea.eclipticseasons.client.particle;

import com.teamtea.eclipticseasons.client.util.ColorHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.ARGB;

public class GreenHouseParticle extends EndRodParticle {
    public GreenHouseParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ColorParticleOption particleType, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        int base = ARGB.colorFromFloat(1f, particleType.getRed(), particleType.getGreen(), particleType.getBlue());
        setColor(base);
        int fade = ColorHelper.simplyMixColor(base, 0.5f, 15916745, 0.5f);
        setFadeColor(fade);
    }
}
