package com.teamtea.eclipticseasons.common.core.crop;

import com.teamtea.eclipticseasons.api.constant.solar.Season;

public class GreenHouseCoreProvider {

    private final Season season;
    protected int availCost;

    public GreenHouseCoreProvider(
            Season season,
            int availCost
    ) {
        this.season = season;
        this.availCost = availCost;
    }

    public int getAvailCost() {
        return availCost;
    }

    public void setAvailCost(int availCost) {
        this.availCost = availCost;
    }

    public Season getSeason() {
        return season;
    }

    public void addAvailCost(int attach) {
        this.availCost += attach;
    }

    public void costAvailCost(int cost) {
        this.availCost -= cost;
    }
}
