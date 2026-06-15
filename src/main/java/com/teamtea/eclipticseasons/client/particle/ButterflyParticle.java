package com.teamtea.eclipticseasons.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.List;

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

    protected boolean reverseByView = false;
    protected int recheck = 0;

    @Override
    protected void extractRotatedQuad(
            QuadParticleRenderState state,
            Quaternionf rotation,
            float x, float y, float z,
            float partialTickTime
    ) {
        float size = this.getQuadSize(partialTickTime);

        float ageF = this.age + partialTickTime;
        float fadeStart = this.lifetime * 0.8f;

        if (ageF > fadeStart) {
            float k = 1.0f - (ageF - fadeStart) / (this.lifetime - fadeStart);
            k = Mth.clamp(k, 0.0f, 1.0f);
            size *= k;
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightCoords(partialTickTime);

        float t = (this.age + partialTickTime) * 0.45f;
        float s = Mth.sin(t);
        float flap = (0.15f + 0.85f * s * s)
                * 55.0f * Mth.DEG_TO_RAD;

        if (Minecraft.getInstance().getCameraEntity() != null && recheck <= 0) {
            recheck = 10;

            Vec3 viewVec = Minecraft.getInstance().getCameraEntity().getLookAngle();
            double crossY = viewVec.x * this.zd - viewVec.z * this.xd;

            if (!reverseByView && crossY < -0.01f) {
                reverseByView = true;
            } else if (reverseByView && crossY > 0.01f) {
                reverseByView = false;
            }
        }

        if (recheck > 0) {
            recheck--;
        }

        boolean reverse = reverseByView;

        if (reverse) {
            float tmp = u0;
            u0 = u1;
            u1 = tmp;
        }

        float bodyOffsetX;
        float bodyOffsetY = 0.0f;
        float axisX;
        float axisY;

        if (reverse) {
            bodyOffsetX = -0.15625f;
            axisX = -0.70710677f;
            axisY = 0.70710677f;
        } else {
            bodyOffsetX = 0.15625f;
            axisX = 0.70710677f;
            axisY = 0.70710677f;
        }

        Quaternionf rotA = new Quaternionf(rotation);
        Quaternionf rotB = new Quaternionf(rotation);

        rotA.rotateAxis(flap, axisX, axisY, 0.0f);
        rotB.rotateAxis(-flap, axisX, axisY, 0.0f);

        this.renderVertex(
                state,
                rotA,
                x, y, z,
                bodyOffsetX, bodyOffsetY,
                size,
                u0, v0, u1, v1,
                light,
                1.0f,
                partialTickTime
        );

        this.renderVertex(
                state,
                rotB,
                x, y, z,
                bodyOffsetX, bodyOffsetY,
                size,
                u0, v0, u1, v1,
                light,
                1.0f,
                partialTickTime
        );
    }

    // @Override
    // public void tick() {
    //     super.tick();
    // }

    // private Vec3 nextPos;
    // protected float wingPhase;

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
                        || targetPosition.getY() <= this.level.getMinY()
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
