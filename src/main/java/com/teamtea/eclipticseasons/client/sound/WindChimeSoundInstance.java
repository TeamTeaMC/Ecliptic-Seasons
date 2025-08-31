package com.teamtea.eclipticseasons.client.sound;

import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.block.blockentity.WindChimesBlockEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;

public class WindChimeSoundInstance extends AbstractTickableSoundInstance {

    private final WeakReference<WindChimesBlockEntity> windChimes;

    public WindChimeSoundInstance(WindChimesBlockEntity windChimes, SoundEvent soundEvent, SoundSource source, RandomSource random) {
        super(soundEvent, source, random);
        this.windChimes = new WeakReference<>(windChimes);
        this.x = (float) windChimes.getBlockPos().getX();
        this.y = (float) windChimes.getBlockPos().getY();
        this.z = (float) windChimes.getBlockPos().getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0F;
        this.relative = true;
    }

    private static final float VOLUME_MIN = 0.0F;
    private static final float VOLUME_MAX = 1.2F;
    private static final float PITCH_MIN = 0.0F;

    @Override
    public void tick() {
        WindChimesBlockEntity windChimesBlockEntity = this.windChimes.get();
        if (windChimesBlockEntity!=null
                && !windChimesBlockEntity.isRemoved()) {
            // this.x = (float) windChimes.getBlockPos().getX();
            // this.y = (float) windChimes.getBlockPos().getY();
            // this.z = (float) windChimes.getBlockPos().getZ();
            float f = windChimesBlockEntity.isShaking() ? 0.7f : 0.1f;
            if (isRelative() && ClientCon.agent.getCameraEntity() != null) {
                Vec3 cameraPos = ClientCon.agent.getCameraEntity().getPosition(1);
                Vec3 soundPos = windChimesBlockEntity.getBlockPos().getCenter();
                double distanceSq = cameraPos.distanceToSqr(soundPos);
                double maxDistanceSq = 64 * 64 * f;
                if (distanceSq > maxDistanceSq && maxDistanceSq > 0) {
                    f *= 0.0F;
                } else {
                    f *= 1.0F - (float) (distanceSq / maxDistanceSq);
                }
            }
            if (f >= 0.001F) {
                this.pitch = Mth.lerp(Mth.clamp(f, this.getMinPitch(), this.getMaxPitch()), this.getMinPitch(), this.getMaxPitch());
                this.volume = Mth.lerp(Mth.clamp(f, 0.0F, 0.5F), 0.0F, 1.2F);
            } else {
                this.pitch = 0.0F;
                this.volume = 0.0F;
            }
        } else {
            this.stop();
        }
    }

    private float getMinPitch() {
        return 0.2F;
    }

    private float getMaxPitch() {
        return 1.1F;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}
