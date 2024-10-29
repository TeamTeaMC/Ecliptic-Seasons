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
    public void render(VertexConsumer vertexConsumer, Camera camera, float patialTicks) {

        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(patialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(patialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(patialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            try {
                quaternionf = (Quaternionf) camera.rotation().clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        } else {
            quaternionf = new Quaternionf(camera.rotation());
            quaternionf.rotateZ(Mth.lerp(patialTicks, this.oRoll, this.roll));
        }

        float f3 = this.getQuadSize(patialTicks);

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(patialTicks);

        boolean revex = true;
        if (Minecraft.getInstance().getCameraEntity() != null) {
            var viewVec = Minecraft.getInstance().getCameraEntity().getLookAngle();
            double vx = viewVec.x;
            double vz = viewVec.z;
            double crossY = vx * zd - vz * xd;

            // 浮点数要防抖
            if (crossY < 0.01f) {
                float ut = f7;
                f7 = f6;
                f6 = ut;
                revex = false;
            }
        }
        float ff = System.currentTimeMillis() % 4000;

        ff = 1 - (Math.abs((ff - 2000) / 2000f));
        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)};
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }
        vertexConsumer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexConsumer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexConsumer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexConsumer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();

    }

    @Override
    public void tick() {
        super.tick();
    }
}
