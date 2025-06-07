package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class ESMobEffectTags {
    public static final TagKey<MobEffect> HEATSTROKE_RESISTANT = create("heatstroke_resistant");

    public static TagKey<MobEffect> create(String s) {
        return TagKey.create(Registries.MOB_EFFECT, EclipticSeasons.rl(s));
    }

}
