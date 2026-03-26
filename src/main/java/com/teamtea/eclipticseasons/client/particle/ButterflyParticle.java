package com.teamtea.eclipticseasons.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class ButterflyParticle extends FireflyParticle {


    private final SpriteSet spriteSet;
    private boolean isBlink;
    private Vec3 nextPos;

    public ButterflyParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z, spriteSet);
        this.lifetime = 800;
        this.gravity = 1E-4f;
        this.spriteSet = spriteSet;

        this.isBlink = false;
        // setSpriteFromAge(this.spriteSet);
        setSprite(spriteSet.get(level.getRandom()));
    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }

    // 注意这样子可能有模组改镜头滚转角
    @Override
    protected void extractRotatedQuad(QuadParticleRenderState particleTypeRenderState, Quaternionf rotation, float x, float y, float z, float partialTickTime) {
        float f = this.getQuadSize(partialTickTime);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int i = this.getLightCoords(partialTickTime);

        float ff = System.currentTimeMillis() % 4000;

        ff = 1 - (Math.abs((ff - 2000) / 2000f));


        // rotation=rotation.rotateXYZ(0,0,-Mth.DEG_TO_RAD*90);
        // 主要问题在于枢纽点，后续更换纹理需要更新xoffse和yoffeset
        // rotation = rotation.rotateAxis(ff*45*Mth.DEG_TO_RAD,1,0,0);

        float pXOffset1 = 1.f;
        float pXOffset0 = -1.f;
        float pYOffset1 = 1.f;
        float pYOffset0 = -1.f;
        // rotation=new Quaternionf();

        boolean revex = true;
        if (Minecraft.getInstance().getCameraEntity() != null) {
            var viewVec = Minecraft.getInstance().getCameraEntity().getLookAngle();
            double vx = viewVec.x;
            double vz = viewVec.z;
            double crossY = vx * zd - vz * xd;

            // 浮点数要防抖
            if (crossY < 0.01f) {
                float ut = u0;
                u0 = u1;
                u1 = ut;
                revex = false;
            }
        }

        rotation = revex ?
                        rotation.rotateAxis(ff * 70 * Mth.DEG_TO_RAD, 1, 1, 0)
                        : rotation.rotateAxis(ff * 70 * Mth.DEG_TO_RAD, -1, 1, 0);
        this.renderVertex(particleTypeRenderState, rotation, x, y, z, pXOffset1, pYOffset0, f,
                u0, v0, u1, v1,
                i, 1f, partialTickTime);
        rotation = revex ?
                rotation.rotateAxis(ff * -140 * Mth.DEG_TO_RAD, 1, 1, 0)
                : rotation.rotateAxis(ff * -140 * Mth.DEG_TO_RAD, -1, 1, 0);
        this.renderVertex(particleTypeRenderState, rotation, x, y, z, pXOffset1, pYOffset0, f,
                u0, v0, u1, v1,
                i, 1f, partialTickTime);
    }


    @Override
    public void tick() {
        super.tick();
    }
}
