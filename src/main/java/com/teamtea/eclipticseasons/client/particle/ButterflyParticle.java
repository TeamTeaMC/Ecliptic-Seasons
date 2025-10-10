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
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        Quaternionf quaternionf = new Quaternionf();
//        this.getFacingCameraMode().setRotation(quaternionf, renderInfo, partialTicks);
//        quaternionf.set(renderInfo.rotation());
        if (this.roll != 0.0F) {
            quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }

        this.renderRotatedQuad(buffer, renderInfo, quaternionf, partialTicks);
    }

    protected void renderRotatedQuad(VertexConsumer buffer, Camera camera, Quaternionf quaternion, float partialTicks) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        this.renderRotatedQuad(buffer, quaternion, f, f1, f2, partialTicks);
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
                : pQuaternion.rotateAxis(ff * -140 * Mth.DEG_TO_RAD, -1, 1, 0);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset1, pYOffset0, f, u1, v1, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset1, pYOffset1, f, u1, v0, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset0, pYOffset1, f, u0, v0, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, pXOffset0, pYOffset0, f, u0, v1, i, 1f);
// super.renderRotatedQuad(pBuffer, pQuaternion, pX, pY, pZ, pPartialTicks);
    }

    private void renderVertex(
            VertexConsumer buffer,
            Quaternionf quaternion,
            float x,
            float y,
            float z,
            float xOffset,
            float yOffset,
            float quadSize,
            float u,
            float v,
            int packedLight,
            float alpha
    ) {
        Vector3f vector3f = new Vector3f(xOffset, yOffset, 0.0F)
                .rotate(quaternion).mul(quadSize).add(x, y, z);
        buffer.vertex(vector3f.x(), vector3f.y(), vector3f.z())
                .uv(u, v)
                .color(rCol, gCol, bCol, alpha)
                .uv2(packedLight)
                .endVertex();
    }

    @Override
    public void tick() {
        super.tick();
    }
}
