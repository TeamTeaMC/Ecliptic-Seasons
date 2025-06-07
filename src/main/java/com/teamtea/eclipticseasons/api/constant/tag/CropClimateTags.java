package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.tags.TagKey;

public class CropClimateTags {

    public static final TagKey<AgroClimaticZone> ALL = create("all");
    public static final TagKey<AgroClimaticZone> OVERWORLD = create("overworld");


    public static TagKey<AgroClimaticZone> create(String s) {
        return TagKey.create(ESRegistries.AGRO_CLIMATE, EclipticSeasons.rl(s));
    }

}
