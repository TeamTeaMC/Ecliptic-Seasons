package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.api.data.client.BiomeColor;

public interface IBiomeColorHolder {
    BiomeColor.Instance getBiomeColor();

    void setBiomeColor(BiomeColor.Instance biomeColor);

    void setSeasonChanged();
}
