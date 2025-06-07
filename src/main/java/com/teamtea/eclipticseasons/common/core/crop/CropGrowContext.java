package com.teamtea.eclipticseasons.common.core.crop;

import com.teamtea.eclipticseasons.api.constant.solar.Season;

import java.util.List;

public record CropGrowContext(
        List<Season> likeSeasons
) {


}
