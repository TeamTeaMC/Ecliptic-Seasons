package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.climate.CropClimateType;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.tags.TagKey;

public class CropClimateTags {

    public static final TagKey<CropClimateType> ALL = create("all");

    public static TagKey<CropClimateType> create(String s) {
        return TagKey.create(ESRegistries.CROP_CLIMATE, EclipticSeasons.rl(s));
    }

}
