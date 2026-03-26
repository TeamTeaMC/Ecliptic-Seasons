package com.teamtea.eclipticseasons.api.constant.game;


import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public enum BreedSeasonType {
    SPRING(new BreedSeasonInfo(1), EclipticSeasons.rl("breed/spring")),
    SUMMER(new BreedSeasonInfo(2), EclipticSeasons.rl("breed/summer")),
    AUTUMN(new BreedSeasonInfo(4), EclipticSeasons.rl("breed/autumn")),
    WINTER(new BreedSeasonInfo(8), EclipticSeasons.rl("breed/winter")),
    SP_SU(new BreedSeasonInfo(3), EclipticSeasons.rl("breed/spring_summer")),
    SP_AU(new BreedSeasonInfo(5), EclipticSeasons.rl("breed/spring_autumn")),
    SP_WI(new BreedSeasonInfo(9), EclipticSeasons.rl("breed/spring_winter")),
    SU_AU(new BreedSeasonInfo(6), EclipticSeasons.rl("breed/summer_autumn")),
    SU_WI(new BreedSeasonInfo(10), EclipticSeasons.rl("breed/summer_winter")),
    AU_WI(new BreedSeasonInfo(12), EclipticSeasons.rl("breed/autumn_winter")),
    SP_SU_AU(new BreedSeasonInfo(7), EclipticSeasons.rl("breed/spring_summer_autumn")),
    SP_SU_WI(new BreedSeasonInfo(11), EclipticSeasons.rl("breed/spring_summer_winter")),
    SP_AU_WI(new BreedSeasonInfo(13), EclipticSeasons.rl("breed/spring_autumn_winter")),
    SU_AU_WI(new BreedSeasonInfo(14), EclipticSeasons.rl("breed/summer_autumn_winter")),
    ALL(new BreedSeasonInfo(15), EclipticSeasons.rl("breed/all_seasons"));

    private final BreedSeasonInfo info;
    private final Identifier res;

    BreedSeasonType(BreedSeasonInfo info, Identifier res) {
        this.info = info;
        this.res = res;
    }

    public BreedSeasonInfo getInfo() {
        return info;
    }

    public Identifier getRes() {
        return res;
    }

    public TagKey<EntityType<?>> getTag() {
        return TagKey.create(Registries.ENTITY_TYPE, res);
    }
}
