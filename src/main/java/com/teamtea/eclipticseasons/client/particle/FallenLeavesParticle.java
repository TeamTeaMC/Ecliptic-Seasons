package com.teamtea.eclipticseasons.client.particle;

import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FallenLeavesParticle extends TextureSheetParticle {

    private static final float ACCELERATION_SCALE = 0.0025F;
    private static final int INITIAL_LIFETIME = 300;
    private static final int CURVE_ENDPOINT_TIME = 300;
    private static final float FALL_ACC = 0.25F;
    private static final float WIND_BIG = 2.0F;
    private float rotSpeed;
    private final float particleRandom;
    private final float spinAcceleration;
    protected boolean wasOnGround;

    public FallenLeavesParticle(ClientLevel clientLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, ColorParticleOptions colorParticleOption, SpriteSet spriteSet) {
        super(clientLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.rotSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -30.0D : 30.0D);
        this.particleRandom = this.random.nextFloat();
        this.spinAcceleration = (float) Math.toRadians(this.random.nextBoolean() ? -5.0D : 5.0D);
        this.lifetime = 300;
        this.gravity = 7.5E-4F;
        float f = this.random.nextBoolean() ? 0.15F : 0.175F;
        this.quadSize = f;
        this.setSize(f, f);
        this.friction = 1.0F;
        this.rCol = colorParticleOption.getColor().x;
        this.gCol = colorParticleOption.getColor().y;
        this.bCol = colorParticleOption.getColor().z;
        this.setSprite(spriteSet.get(this.random));
    }

    public FallenLeavesParticle(ClientLevel clientLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, ColorParticleOptions colorParticleOption, TextureAtlasSprite sprite) {
        super(clientLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.rotSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -30.0D : 30.0D);
        this.particleRandom = this.random.nextFloat();
        this.spinAcceleration = (float) Math.toRadians(this.random.nextBoolean() ? -5.0D : 5.0D);
        this.lifetime = 300;
        this.gravity = 7.5E-4F;
        float f = this.random.nextBoolean() ? 0.15F : 0.175F;
        this.quadSize = f;
        this.setSize(f, f);
        this.friction = 1.0F;
        this.rCol = colorParticleOption.getColor().x;
        this.gCol = colorParticleOption.getColor().y;
        this.bCol = colorParticleOption.getColor().z;
        this.setSprite(sprite);
    }

    @Override
    public void setSprite(TextureAtlasSprite sprite) {
        super.setSprite(sprite);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        }

        if (this.onGround) {
            if (!ClientConfig.Particle.fallenLeavesGroundFade.get()) {
                this.remove();
            } else {
                if (!this.wasOnGround) {
                    this.lifetime = Math.min(this.lifetime, this.age + 40);
                }
                this.setColor(this.rCol * 0.99F, this.gCol * 0.99F, this.bCol * 0.99F);
                this.setAlpha(this.alpha * 0.94F);
            }
            this.wasOnGround = this.onGround;
        } else if (!this.removed) {
            float f = (float) (300 - this.lifetime);
            float f1 = Math.min(f / 300.0F, 1.0F);
            double d0 = Math.cos(Math.toRadians(this.particleRandom * 60.0F)) * 2.0D * Math.pow(f1, 1.25D);
            double d1 = Math.sin(Math.toRadians(this.particleRandom * 60.0F)) * 2.0D * Math.pow(f1, 1.25D);
            this.xd += d0 * (double) 0.0025F;
            this.zd += d1 * (double) 0.0025F;
            this.yd -= this.gravity;
            this.rotSpeed += this.spinAcceleration / 20.0F;
            this.oRoll = this.roll;
            this.roll += this.rotSpeed / 20.0F;

            this.move(this.xd, this.yd, this.zd);
            // if (this.onGround || this.lifetime < 299 && (this.xd == 0.0D || this.zd == 0.0D)) {
            //     this.remove();
            // }

            if (!this.removed) {
                this.xd *= this.friction;
                this.yd *= this.friction;
                this.zd *= this.friction;
            }
        }
    }

    private boolean stoppedByCollision = false;
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);

    @Override
    public void move(double pX, double pY, double pZ) {
        // if (!this.stoppedByCollision)
        {
            double d0 = pX;
            double d1 = pY;
            double d2 = pZ;
            // BlockPos nextPos=BlockPos.containing(getPos().add(d0,d1,d2));
            // BlockState nextState=level.getBlockState(nextPos);
            if (this.hasPhysics
                    && (pX != 0.0 || pY != 0.0 || pZ != 0.0)
                    && pX * pX + pY * pY + pZ * pZ < MAXIMUM_COLLISION_VELOCITY_SQUARED
                // && !(nextState.getBlock() instanceof LeavesBlock)
            ) {
                Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(pX, pY, pZ), this.getBoundingBox(), this.level, List.of());
                pX = vec3.x;
                pY = vec3.y;
                pZ = vec3.z;
            }

            if (pX != 0.0 || pY != 0.0 || pZ != 0.0) {
                this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
                this.setLocationFromBoundingbox();
            }

            if (Math.abs(d1) >= 1.0E-5F && Math.abs(pY) < 1.0E-5F) {
                this.stoppedByCollision = true;
            }

            // 现在检测
            this.onGround = d1 != pY && d1 < 0.0
            // &&!(nextState.getBlock() instanceof LeavesBlock)
            ;
            if (d0 != pX) {
                this.xd = 0.0;
            }

            if (d2 != pZ) {
                this.zd = 0.0;
            }
        }
    }
}
