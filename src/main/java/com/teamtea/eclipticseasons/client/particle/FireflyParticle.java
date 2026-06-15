package com.teamtea.eclipticseasons.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class FireflyParticle extends TextureSheetParticle {


    private final SpriteSet spriteSet;
    private boolean isBlink;
    private Vec3 nextPos;

    public FireflyParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.lifetime = 800;
        this.gravity = 1E-4f;
        this.spriteSet = spriteSet;

        this.isBlink = false;
        setSpriteFromAge(this.spriteSet);

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float patialTicks) {
        super.render(vertexConsumer, camera, patialTicks);
    }

    // 萤火虫是头部发光，但是不好看
    @Override
    protected void renderRotatedQuad(VertexConsumer pBuffer, Quaternionf pQuaternion, float pX, float pY, float pZ, float pPartialTicks) {
        float quadSize1 = this.getQuadSize(pPartialTicks);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int i = this.getLightColor(pPartialTicks);
        i = 15728880;
        if (this.age >= this.lifetime * 0.8) {
            i = this.getLightColor(pPartialTicks);
        }

        double crossY = 0;
        if (Minecraft.getInstance().getCameraEntity() != null) {
            var viewVec = Minecraft.getInstance().getCameraEntity().getLookAngle();
            double vx = viewVec.x;
            double vz = viewVec.z;
            crossY = vx * zd - vz * xd;
            if (crossY < 0.01f) {
                float ut = u0;
                u0 = u1;
                u1 = ut;
            }
        }

        // 如果想左右旋转粒子，那我们可以调换u0和u1
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, 1.0F, -1.0F, quadSize1, u1, v1, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, 1.0F, 1.0F, quadSize1, u1, v0, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, -1.0F, 1.0F, quadSize1, u0, v0, i, 1f);
        this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, -1.0F, -1.0F, quadSize1, u0, v1, i, 1f);

        if (isBlink) {
            var sp1 = spriteSet.get(1, 1);
            u0 = sp1.getU0();
            u1 = sp1.getU1();
            v0 = sp1.getV0();
            v1 = sp1.getV1();

            if (crossY < 0.01f) {
                float ut = u0;
                u0 = u1;
                u1 = ut;
            }
            this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, 1.0F, -1.0F, quadSize1, u1, v1, i, 0.5f);
            this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, 1.0F, 1.0F, quadSize1, u1, v0, i, 0.5f);
            this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, -1.0F, 1.0F, quadSize1, u0, v0, i, 0.5f);
            this.renderVertex(pBuffer, pQuaternion, pX, pY, pZ, -1.0F, -1.0F, quadSize1, u0, v1, i, 0.5f);
        }
    }

    protected void renderVertex(
            VertexConsumer pBuffer,
            Quaternionf pQuaternion,
            float pX,
            float pY,
            float pZ,
            float pXOffset,
            float pYOffset,
            float pQuadSize,
            float pU,
            float pV,
            int pPackedLight,
            float alpha
    ) {
        Vector3f vector3f = new Vector3f(pXOffset, pYOffset, 0.0F)
                // .rotateY(180*Mth.DEG_TO_RAD)
                .rotate(pQuaternion).mul(pQuadSize).add(pX, pY, pZ);
        pBuffer.addVertex(vector3f.x(), vector3f.y(), vector3f.z())
                .setUv(pU, pV)
                .setColor(this.rCol, this.gCol, this.bCol, alpha)
                // .setNormal(0,-1,0)
                .setLight(pPackedLight);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || onGround) {
            this.remove();
        } else {
            // Ecliptic.logger(spriteSet.get(this.age, this.lifetime));
            isBlink = this.age % 8 > 4 && this.age < this.lifetime * 0.8;
            // setSprite(spriteSet.get(isBlink ? 0 : 1, 1));
            var nowPos = new Vec3(x, y, z);
            var targetPosition = BlockPos.containing(x + xd, y + yd, z + zd);

            Vec3 vec3 = Entity.collideBoundingBox((Entity) null, new Vec3(xd, yd, zd), this.getBoundingBox(), this.level, List.of());
            if (this.nextPos != null &&
                    (!NaturalSpawner.isValidEmptySpawnBlock(level, targetPosition, level.getBlockState(targetPosition), level.getFluidState(targetPosition), EntityType.BAT)
                            || targetPosition.getY() <= level.getMinBuildHeight()
                            || Math.abs(vec3.y) < (double) 1.0E-5F
                            || this.onGround
                            || level.getNearestPlayer(x + xd, y + yd, z + zd, 1f, false) != null
                    )) {
                this.nextPos = null;
                // this.stoppedByCollision=false;
            }
            if (nextPos == null || nextPos.closerThan(nowPos, 1f) || nextPos.distanceTo(nowPos) > 100) {
                this.nextPos = findNextPosition(2.0F).getCenter();
                var re = nextPos.subtract(nowPos).multiply(0.02d, 0.02d, 0.02d);
                this.xd = re.x;
                this.yd = re.y;
                this.zd = re.z;
            } else {
                var re = nextPos.subtract(nowPos).multiply(0.02d, 0.02d, 0.02d);
                this.xd = 0.78 * this.xd + 0.3 * Math.abs(random.nextGaussian()) * re.x;
                this.yd = 0.8 * this.yd + 0.25 * Math.abs(random.nextGaussian()) * re.y;
                this.zd = 0.78 * this.zd + 0.3 * Math.abs(random.nextGaussian()) * re.z;
            }

            var pos = BlockPos.containing(x, y - 0.1f, z);
            if (!NaturalSpawner.isValidEmptySpawnBlock(level, pos, level.getBlockState(pos), level.getFluidState(pos), EntityType.BAT)) {
                this.yd = 0.05f;
            }

            this.move(xd, yd, zd);
        }
    }

    protected BlockPos findNextPosition(float yRange) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int tries = 0; tries < 8; tries++) {
            double i = this.x + this.random.nextGaussian() * 4.0D;
            double j = this.y + this.random.nextGaussian() * yRange;
            double k = this.z + this.random.nextGaussian() * 4.0D;

            mutable.set(i, j, k);

            if (NaturalSpawner.isValidEmptySpawnBlock(
                    this.level,
                    mutable,
                    this.level.getBlockState(mutable),
                    this.level.getFluidState(mutable),
                    EntityType.BAT
            )) {
                return mutable.immutable();
            }
        }

        return BlockPos.containing(this.x, this.y + 0.5D, this.z);
    }
}
