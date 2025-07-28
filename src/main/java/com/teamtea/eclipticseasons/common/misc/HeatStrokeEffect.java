package com.teamtea.eclipticseasons.common.misc;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HeatStrokeEffect extends MobEffect {


    public HeatStrokeEffect(MobEffectCategory neutral, int i) {
        super(neutral, i);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // return duration % 60 == 0;
        return false;
    }


    @Override
    public boolean applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
        // if (entityLivingBaseIn.getHealth() > 0.1F)
        {
            // need a damage tag is bypasses_armor
            // entityLivingBaseIn.hurt(entityLivingBaseIn.damageSources().generic(), Math.minTime(entityLivingBaseIn.getHealth() * 0.025f,0.1f) + 0.001f);
        }
        return true;
    }


}
