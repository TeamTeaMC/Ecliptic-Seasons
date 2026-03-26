package com.teamtea.eclipticseasons.api.constant.crop;


import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public enum CropSeasonType {
    SPRING(new CropSeasonInfo(1), EclipticSeasons.rl("crops/spring")),
    SUMMER(new CropSeasonInfo(2), EclipticSeasons.rl("crops/summer")),
    AUTUMN(new CropSeasonInfo(4), EclipticSeasons.rl("crops/autumn")),
    WINTER(new CropSeasonInfo(8), EclipticSeasons.rl("crops/winter")),
    SP_SU(new CropSeasonInfo(3), EclipticSeasons.rl("crops/spring_summer")),
    SP_AU(new CropSeasonInfo(5), EclipticSeasons.rl("crops/spring_autumn")),
    SP_WI(new CropSeasonInfo(9), EclipticSeasons.rl("crops/spring_winter")),
    SU_AU(new CropSeasonInfo(6), EclipticSeasons.rl("crops/summer_autumn")),
    SU_WI(new CropSeasonInfo(10), EclipticSeasons.rl("crops/summer_winter")),
    AU_WI(new CropSeasonInfo(12), EclipticSeasons.rl("crops/autumn_winter")),
    SP_SU_AU(new CropSeasonInfo(7), EclipticSeasons.rl("crops/spring_summer_autumn")),
    SP_SU_WI(new CropSeasonInfo(11), EclipticSeasons.rl("crops/spring_summer_winter")),
    SP_AU_WI(new CropSeasonInfo(13), EclipticSeasons.rl("crops/spring_autumn_winter")),
    SU_AU_WI(new CropSeasonInfo(14), EclipticSeasons.rl("crops/summer_autumn_winter")),
    ALL(new CropSeasonInfo(15), EclipticSeasons.rl("crops/all_seasons"));

    private final CropSeasonInfo info;
    private final Identifier res;

    CropSeasonType(CropSeasonInfo info, Identifier res) {
        this.info = info;
        this.res = res;
    }

    public CropSeasonInfo getInfo() {
        return info;
    }

    public Identifier getRes() {
        return res;
    }

    public TagKey<Item> getTag() {
        return TagKey.create(Registries.ITEM, res);
    }

    public TagKey<Block> getBlockTag() {
        return TagKey.create(Registries.BLOCK, res);
    }

    private static final CropSeasonType[] CROP_SEASON_TYPES = CropSeasonType.values();

    public static CropSeasonType[] collectValues() {
        return CROP_SEASON_TYPES;
    }
}
