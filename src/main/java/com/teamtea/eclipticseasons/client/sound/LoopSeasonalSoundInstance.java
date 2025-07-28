package com.teamtea.eclipticseasons.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import java.lang.ref.WeakReference;
import java.util.Set;

public class LoopSeasonalSoundInstance extends AbstractTickableSoundInstance {
    private final WeakReference<Set<LoopSeasonalSoundInstance>> loopSounds;
    private int fadeDirection;
    private int fade;
    private long lastTickTime;

    public LoopSeasonalSoundInstance(SoundEvent soundEvent, Set<LoopSeasonalSoundInstance> loopSounds) {
        super(soundEvent, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
        this.looping = true;
        // loop need delay bigger than 0
        this.delay = 0;
        this.volume = 0.5F;
        this.relative = true;
        // this.fade=40;
        this.lastTickTime = System.currentTimeMillis();
        this.loopSounds = new WeakReference<>(loopSounds);
    }

    public void tick() {
        if (isStopped()) return;
        Set<LoopSeasonalSoundInstance> loopSeasonalSoundInstances = loopSounds.get();
        if (loopSeasonalSoundInstances != null && !loopSeasonalSoundInstances.contains(this)) {
            this.fadeDirection = -1;
        }
        if (this.fade < 0) {
            this.stop();
            this.fadeDirection = 0;
        }
        this.fade += this.fadeDirection;
        this.volume = Mth.clamp((float) this.fade / 40.0F, 0.0F, 1.0F);
        this.lastTickTime = System.currentTimeMillis();
    }


    public void fadeOut() {
        this.fade = Math.min(this.fade, 40);
        this.fadeDirection = -1;
        checkIfForceStop();
    }

    public void fadeIn() {
        this.fade = Math.max(0, this.fade);
        if (this.fade < 40)
            this.fadeDirection = 1;
        else this.fadeDirection = 0;
        checkIfForceStop();
    }

    public void checkIfForceStop() {
        if (lastTickTime - System.currentTimeMillis() > 1000 * 5) {
            this.stop();
        }
    }

}
