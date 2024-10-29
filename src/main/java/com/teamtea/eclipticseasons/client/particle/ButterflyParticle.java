package com.teamtea.eclipticseasons.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    @Override
    public void render(VertexConsumer pBuffer, Camera camera, float patialTicks) {
        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = camera.rotation();
        } else {
            quaternionf = new Quaternionf(camera.rotation());
            quaternionf.rotateZ(Mth.lerp(patialTicks, this.oRoll, this.roll));
        }
        this.renderRotatedQuad(pBuffer, camera, quaternionf, patialTicks);
    }

    protected void renderRotatedQuad(VertexConsumer pBuffer, Camera pCamera, Quaternionf pQuaternion, float pPartialTicks) {
        Vec3 vec3 = pCamera.getPosition();
        float f = (float)(Mth.lerp((double)pPartialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)pPartialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)pPartialTicks, this.zo, this.z) - vec3.z());
        this.renderRotatedQuad(pBuffer, pQuaternion, f, f1, f2, pPartialTicks);
    }


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


    protected void renderVertex(VertexConsumer pBuffer, Quaternionf pQuaternion, float pX, float pY, float pZ, float pXOffset, float pYOffset, float pQuadSize, float pU, float pV, int pPackedLight, float alpha) {
        Vector3f vector3f = new Vector3f(pXOffset, pYOffset, 0.0F)
                // .rotateY(180*Mth.DEG_TO_RAD)
                .rotate(pQuaternion).mul(pQuadSize).add(pX, pY, pZ);
        pBuffer.vertex(vector3f.x(), vector3f.y(), vector3f.z())
                .uv(pU, pV)
                .color(this.rCol, this.gCol, this.bCol, alpha)
                // .setNormal(0,-1,0)
                .uv2(pPackedLight)
                .endVertex();
    }

    @Override
    public void tick() {
        super.tick();
    }
}
