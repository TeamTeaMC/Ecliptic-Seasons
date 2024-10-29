package com.teamtea.eclipticseasons.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float patialTicks) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(patialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(patialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(patialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = camera.rotation();
        } else {
            quaternionf = new Quaternionf(camera.rotation());
            quaternionf.rotateZ(Mth.lerp(patialTicks, this.oRoll, this.roll));
        }

        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(patialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(patialTicks);
        j = 15728880;
        if (this.age >= this.lifetime * 0.8) {
            j = this.getLightColor(patialTicks);
        }
        ;
        int combinedLightIn = 15728880;
        int light1 = combinedLightIn & '\uffff';
        int light2 = combinedLightIn >> 16 & '\uffff';

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

        vertexConsumer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexConsumer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexConsumer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        vertexConsumer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();

        if (isBlink) {
            f6 = spriteSet.get(1, 1).getU0();
            f7 = spriteSet.get(1, 1).getU1();
            f4 = spriteSet.get(1, 1).getV0();
            f5 = spriteSet.get(1, 1).getV1();
            if(!revex){
                float ut = f7;
                f7 = f6;
                f6 = ut;
            }
            vertexConsumer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, 0.5f).uv2(j).endVertex();
            vertexConsumer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, 0.5f).uv2(j).endVertex();
            vertexConsumer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, 0.5f).uv2(j).endVertex();
            vertexConsumer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, 0.5f).uv2(j).endVertex();
        }


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
