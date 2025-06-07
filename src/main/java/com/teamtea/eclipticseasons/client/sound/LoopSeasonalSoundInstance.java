package com.teamtea.eclipticseasons.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class LoopSeasonalSoundInstance extends AbstractTickableSoundInstance {
    private int fadeDirection;
    private int fade;

    public LoopSeasonalSoundInstance(SoundEvent soundEvent) {
        super(soundEvent, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
        this.looping = true;
        // loop need delay bigger than 0
        this.delay = 0;
        this.volume = 0.5F;
        this.relative = true;
        // this.fade=40;
    }

    public void tick() {
        if (this.fade < 0) {
            this.stop();
        }
        // EclipticSeasons.logger(this, this.fade);
        this.fade += this.fadeDirection;
        this.volume = Mth.clamp((float) this.fade / 40.0F, 0.0F, 1.0F);

    }

    public void fadeOut() {
        this.fade = Math.min(this.fade, 40);
        if (this.fade >= -1)
            this.fadeDirection = -1;
        else this.fadeDirection = 0;
    }

    public void fadeIn() {
        this.fade = Math.max(0, this.fade);
        if (this.fade < 40)
            this.fadeDirection = 1;
        else this.fadeDirection = 0;
    }

}
