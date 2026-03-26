package com.teamtea.eclipticseasons.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class FireflyParticle extends SingleQuadParticle {


    private final SpriteSet spriteSet;
    private boolean isBlink;
    private Vec3 nextPos;

    public FireflyParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z, spriteSet.first());
        this.lifetime = 800;
        this.gravity = 1E-4f;
        this.spriteSet = spriteSet;

        this.isBlink = false;
        setSpriteFromAge(this.spriteSet);
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected void extractRotatedQuad(QuadParticleRenderState particleTypeRenderState, Quaternionf rotation, float x, float y, float z, float partialTickTime) {
        float quadSize1 = this.getQuadSize(partialTickTime);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int i = LightCoordsUtil.FULL_BRIGHT;
        if (this.age >= this.lifetime * 0.8) {
            i = this.getLightCoords(partialTickTime);
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
        this.renderVertex(particleTypeRenderState, rotation, x, y, z, 1.0F, -1.0F, quadSize1, u0, v0, u1, v1, i, 1f, partialTickTime);
        // this.renderVertex(particleTypeRenderState, rotation, x, y, z, 1.0F, 1.0F, quadSize1, u1, v0, i, 1f, partialTickTime);
        // this.renderVertex(particleTypeRenderState, rotation, x, y, z, -1.0F, 1.0F, quadSize1, u0, v0, i, 1f, partialTickTime);
        // this.renderVertex(particleTypeRenderState, rotation, x, y, z, -1.0F, -1.0F, quadSize1, u0, v1, i, 1f, partialTickTime);

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
            this.renderVertex(particleTypeRenderState, rotation, x, y, z, 1.0F, -1.0F, quadSize1,
                    u0, v0,
                    u1, v1, i, 0.5f, partialTickTime);
            // this.renderVertex(particleTypeRenderState, rotation, x, y, z, 1.0F, 1.0F, quadSize1, u1, v0, i, 0.5f, partialTickTime);
            // this.renderVertex(particleTypeRenderState, rotation, x, y, z, -1.0F, 1.0F, quadSize1, u0, v0, i, 0.5f, partialTickTime);
            // this.renderVertex(particleTypeRenderState, rotation, x, y, z, -1.0F, -1.0F, quadSize1, u0, v1, i, 0.5f, partialTickTime);
        }
    }

    protected void renderVertex(
            QuadParticleRenderState particleTypeRenderState,
            Quaternionf rotation,
            float pX,
            float pY,
            float pZ,
            float pXOffset,
            float pYOffset,
            float pQuadSize,
            float pU0,
            float pV0,
            float pU1,
            float pV1,
            int pPackedLight,
            float alpha,
            float partialTickTime
    ) {
        Vector3f vector3f = new Vector3f(pXOffset, pYOffset, 0.0F)
                // .rotateY(180*Mth.DEG_TO_RAD)
                .rotate(rotation).mul(pQuadSize).add(pX, pY, pZ);
        // pBuffer.addVertex(vector3f.x(), vector3f.y(), vector3f.z())
        //        .setUv(pU, pV)
        //        .setColor(this.rCol, this.gCol, this.bCol, alpha)
        //        // .setNormal(0,-1,0)
        //        .setLight(pPackedLight);

        particleTypeRenderState.add(
                this.getLayer(),
                vector3f.x(),
                vector3f.y(),
                vector3f.z(),
                rotation.x,
                rotation.y,
                rotation.z,
                rotation.w,
                this.getQuadSize(partialTickTime),
                pU0,
                pU1,
                pV0,
                pV1,
                ARGB.colorFromFloat(alpha, this.rCol, this.gCol, this.bCol),
                pPackedLight
        );
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
                            || targetPosition.getY() <= level.getMinY()
                            || Math.abs(vec3.y) < (double) 1.0E-5F
                            || this.onGround
                            || level.getNearestPlayer(x + xd, y + yd, z + zd, 1f, false) != null
                    )) {
                this.nextPos = null;
                // this.stoppedByCollision=false;
            }
            if (nextPos == null || nextPos.closerThan(nowPos, 1f) || nextPos.distanceTo(nowPos) > 100) {
                this.nextPos = findNextPosition().getCenter();
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

    protected BlockPos findNextPosition() {

        var blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

        do {
            // int b = random.nextGaussian()*7;
            double i = x + random.nextGaussian() * 4;
            double j = y + random.nextGaussian() * 2;
            double k = z + random.nextGaussian() * 4;
            blockpos$mutableblockpos.set(i, j, k);
            // Ecliptic.logger(blockpos$mutableblockpos);
        }
        while (!NaturalSpawner.isValidEmptySpawnBlock(level, blockpos$mutableblockpos, level.getBlockState(blockpos$mutableblockpos), level.getFluidState(blockpos$mutableblockpos), EntityType.BAT));


        return blockpos$mutableblockpos;
    }

}
