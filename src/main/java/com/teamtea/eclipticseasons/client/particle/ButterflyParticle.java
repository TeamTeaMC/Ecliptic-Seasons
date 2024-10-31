package com.teamtea.eclipticseasons.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
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
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    // 注意这样子可能有模组改镜头滚转角
    @Override
    protected void renderRotatedQuad(VertexConsumer pBuffer, Quaternionf pQuaternion, float pX, float pY, float pZ, float pPartialTicks) {
        float f = this.getQuadSize(pPartialTicks);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int i = this.getLightColor(pPartialTicks);
        // i = 15728880;
        // if (this.age >= this.lifetime * 0.8) {
        //     i = this.getLightColor(pPartialTicks);
        // }

        float ff = System.currentTimeMillis() % 4000;

        ff = 1 - (Math.abs((ff - 2000) / 2000f));


        // pQuaternion=pQuaternion.rotateXYZ(0,0,-Mth.DEG_TO_RAD*90);
        // 主要问题在于枢纽点，后续更换纹理需要更新xoffse和yoffeset
        // pQuaternion = pQuaternion.rotateAxis(ff*45*Mth.DEG_TO_RAD,1,0,0);

        float pXOffset1 = 1.f;
        float pXOffset0 = -1.f;
        float pYOffset1 = 1.f;
        float pYOffset0 = -1.f;
        // pQuaternion=new Quaternionf();

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

        pQuaternion =
                revex ?
                        pQuaternion.rotateAxis(ff * 70 * Mth.DEG_TO_RAD, 1, 1, 0)
                        : pQuaternion.rotateAxis(ff * 70 * Mth.DEG_TO_RAD, -1, 1, 0);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset1, pYOffset0, f, u1, v1, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset1, pYOffset1, f, u1, v0, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset0, pYOffset1, f, u0, v0, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset0, pYOffset0, f, u0, v1, i, 1f);


        pQuaternion = revex ?
                pQuaternion.rotateAxis(ff * -140 * Mth.DEG_TO_RAD, 1, 1, 0)
                :  pQuaternion.rotateAxis(ff * -140 * Mth.DEG_TO_RAD, -1, 1, 0);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset1, pYOffset0, f, u1, v1, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset1, pYOffset1, f, u1, v0, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset0, pYOffset1, f, u0, v0, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset0, pYOffset0, f, u0, v1, i, 1f);
// super.renderRotatedQuad(pBuffer, pQuaternion, pX, pY, pZ, pPartialTicks);
    }

    // TODO:这里存在贴图法向量问题，偶尔会看不到，也可能是面剔除机制？
    @Override
    protected void renderVertex(VertexConsumer pBuffer, Quaternionf pQuaternion, float pX, float pY, float pZ, float pXOffset, float pYOffset, float pQuadSize, float pU, float pV, int pPackedLight, float alpha) {
        super.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset, pYOffset, pQuadSize, pU, pV, pPackedLight, alpha);
        // Vector3f vector3f = new Vector3f(pXOffset, pYOffset, 0.0F).rotate(pQuaternion).mul(pQuadSize).add(pX, pY, pZ);
        // pBuffer.addVertex(vector3f.x(), vector3f.y(), vector3f.z())
        //         .setUv(pU, pV)
        //         .setColor(this.rCol, this.gCol, this.bCol, alpha)
        //         .setNormal(0, 0, 1F)
        //         .setLight(pPackedLight);
        // pBuffer.addVertex(vector3f.x(), vector3f.y(), vector3f.z())
        //         .setUv(pU, pV)
        //         .setColor(this.rCol, this.gCol, this.bCol, alpha)
        //         .setNormal(0, 0, -1F)
        //         .setLight(pPackedLight);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
