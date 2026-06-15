package com.teamtea.eclipticseasons.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.NaturalSpawner;
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

        float ageF = this.age + pPartialTicks;
        float fadeStart = this.lifetime * 0.8f;

        if (ageF > fadeStart) {
            float k = 1.0f - (ageF - fadeStart) / (this.lifetime - fadeStart);
            k = Mth.clamp(k, 0.0f, 1.0f);
            f *= k;
        }

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
        // super.tick();
        // if(true)return;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime || this.onGround) {
            this.remove();
            return;
        }

        isBlink = this.age % 8 > 4 && this.age < this.lifetime * 0.8;

        Vec3 nowPos = new Vec3(this.x, this.y, this.z);
        BlockPos targetPosition = BlockPos.containing(
                this.x + this.xd,
                this.y + this.yd,
                this.z + this.zd
        );

        // Vec3 vec3 = Entity.collideBoundingBox(
        //         null,
        //         new Vec3(this.xd, this.yd, this.zd),
        //         this.getBoundingBox(),
        //         this.level,
        //         List.of()
        // );

        if (this.nextPos != null &&
                (!NaturalSpawner.isValidEmptySpawnBlock(
                        this.level,
                        targetPosition,
                        this.level.getBlockState(targetPosition),
                        this.level.getFluidState(targetPosition),
                        EntityType.BAT
                )
                        || targetPosition.getY() <= this.level.getMinBuildHeight()
                        // || Math.abs(vec3.y) < 1.0E-5D
                        || this.onGround
                        || this.level.getNearestPlayer(
                        this.x + this.xd,
                        this.y + this.yd,
                        this.z + this.zd,
                        1.0D,
                        false
                ) != null
                )) {
            this.nextPos = null;
        }

        if (this.nextPos == null
                || this.nextPos.closerThan(nowPos, 0.45D)
                || this.nextPos.distanceToSqr(nowPos) > 100.0D) {
            this.nextPos = findNextPosition(3.0F).getCenter();
        }

        Vec3 toTarget = this.nextPos.subtract(nowPos);
        double lenSqr = toTarget.lengthSqr();

        if (lenSqr > 1.0E-4D) {
            double inv = Mth.fastInvSqrt(lenSqr);

            double speed = 0.045D;
            double bob = Mth.sin(this.age * 0.32F) * 0.006D;

            double targetXd = toTarget.x * inv * speed;
            double targetYd = toTarget.y * inv * speed * 0.45D + bob;
            double targetZd = toTarget.z * inv * speed;

            this.xd = this.xd * 0.88D + targetXd * 0.12D;
            this.yd = this.yd * 0.90D + targetYd * 0.10D;
            this.zd = this.zd * 0.88D + targetZd * 0.12D;
        }

        BlockPos belowPos = BlockPos.containing(this.x, this.y - 0.1D, this.z);
        if (!NaturalSpawner.isValidEmptySpawnBlock(
                this.level,
                belowPos,
                this.level.getBlockState(belowPos),
                this.level.getFluidState(belowPos),
                EntityType.BAT
        )) {
            this.yd = Math.max(this.yd, 0.04D);
        }

        this.move(this.xd, this.yd, this.zd);
    }
}
