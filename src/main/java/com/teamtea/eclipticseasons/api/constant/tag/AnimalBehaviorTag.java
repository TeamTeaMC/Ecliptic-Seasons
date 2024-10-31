package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class AnimalBehaviorTag {
    public static final TagKey<EntityType<?>> DAY = create("habit/day");
    public static final TagKey<EntityType<?>> NIGHT = create("habit/night");
    public static final TagKey<EntityType<?>> ALL_TIME = create("habit/all_time");

    public static TagKey<EntityType<?>> create(String s) {
        return TagKey.create(Registries.ENTITY_TYPE, EclipticSeasons.rl(s));
    }
}
